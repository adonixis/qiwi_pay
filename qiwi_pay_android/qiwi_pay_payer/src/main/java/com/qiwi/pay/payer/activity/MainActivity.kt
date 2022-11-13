package com.qiwi.pay.payer.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qiwi.pay.payer.databinding.ActivityMainBinding
import com.qiwi.pay.payer.model.QR
import com.qiwi.pay.payer.utils.Constants
import com.qiwi.pay.payer.utils.Constants.PAYMENT_TOKEN_KEY


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        if (needLogin()) {
            startPhoneActivity()
            super.onCreate(savedInstanceState)
            finish()
        } else {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

            settings = PreferenceManager.getDefaultSharedPreferences(this)
            val accountId = settings.getString(Constants.ACCOUNT_ID_KEY, "")

            var paymentToken = intent.getStringExtra(PAYMENT_TOKEN_KEY)
            if (paymentToken == null) {
                paymentToken = settings.getString(PAYMENT_TOKEN_KEY, "")
            }

            if (paymentToken != null && accountId != null) {
                val qr = QR(accountId = accountId, paymentToken = paymentToken)
                val gson = Gson()
                val jsonQr = gson.toJson(qr)
                val qrCodeBitmap = generateQrCodeBitmap(jsonQr)
                qrCodeBitmap?.let {
                    binding.ivQr.setImageBitmap(qrCodeBitmap)
                }
            }
        }
    }

    private fun startPhoneActivity() {
        val intent = Intent(this@MainActivity, PhoneActivity::class.java)
        startActivity(intent)
    }

    private fun needLogin(): Boolean {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        return settings.getString(PAYMENT_TOKEN_KEY, "")!!.isEmpty()
    }

    private fun generateQrCodeBitmap(paymentToken: String): Bitmap? {
        val size = 512
        var bitmap: Bitmap? = null
        try {
            val barcodeEncoder = BarcodeEncoder()
            bitmap = barcodeEncoder.encodeBitmap(
                paymentToken,
                BarcodeFormat.QR_CODE, size, size
            )
        } catch (e: WriterException) {
            Log.e("generateQR()", e.message ?: "")
        }
        return bitmap
    }
}