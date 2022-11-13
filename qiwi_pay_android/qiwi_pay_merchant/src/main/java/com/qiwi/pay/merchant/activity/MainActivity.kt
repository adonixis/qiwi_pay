package com.qiwi.pay.merchant.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.qiwi.pay.merchant.R
import com.qiwi.pay.merchant.databinding.ActivityMainBinding
import com.qiwi.pay.merchant.model.PayResponse
import com.qiwi.pay.merchant.model.QR
import com.qiwi.pay.merchant.utils.Constants.PAYMENT_TYPE
import com.qiwi.pay.merchant.utils.Constants.RUB_CURRENCY
import com.qiwi.pay.merchant.utils.Constants.SITE_ID
import com.qiwi.pay.merchant.utils.Constants.STATUS_COMPLETED
import com.qiwi.pay.merchant.utils.TextWatcherImpl
import com.qiwi.pay.merchant.utils.showErrorSnackbar
import com.qiwi.pay.merchant.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var siteId: String
    private lateinit var amountCurrency: String
    private lateinit var paymentType: String
    private lateinit var paymentId: String

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            pay(result.contents)
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcherImpl() {
        override fun afterTextChanged(s: Editable) {
            binding.btnAcceptPayment.isEnabled = s.isNotEmpty()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.etAmount.addTextChangedListener(textWatcher)

        initProgressDialog()

        mainViewModel.getPayLiveData().observe(this) { onPay(it) }
        mainViewModel.getErrorLiveData().observe(this) { onPayError(it) }

        siteId = SITE_ID
        paymentType = PAYMENT_TYPE
        amountCurrency = RUB_CURRENCY

        binding.btnAcceptPayment.setOnClickListener { scanQr() }
    }

    private fun scanQr() {
        val scanOptions = ScanOptions()
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        scanOptions.setOrientationLocked(true)
        scanOptions.setPrompt(getString(R.string.scan_qr))
        scanOptions.setBeepEnabled(false)
        scanOptions.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(scanOptions)
    }

    private fun pay(qrJson: String) {
        val gson = Gson()
        try {
            val qr = gson.fromJson(qrJson, QR::class.java)
            val amount = binding.etAmount.text.toString().toLongOrNull() ?: 0L
            val amountString = String.format(Locale.US, "%.2f", amount.toFloat())
            paymentId = UUID.randomUUID().toString()
            progressDialog.show()
            binding.btnAcceptPayment.isEnabled = false
            mainViewModel.pay(
                siteId = siteId,
                paymentId = paymentId,
                amountCurrency = amountCurrency,
                amountValue = amountString,
                paymentType = paymentType,
                paymentToken = qr.paymentToken,
                accountId = qr.accountId
            )
        } catch (e: JsonSyntaxException) {
            showErrorSnackbar(binding.root, e.localizedMessage ?: "")
        }
    }

    private fun onPayError(error: String) {
        progressDialog.dismiss()
        binding.btnAcceptPayment.isEnabled = true
        showErrorSnackbar(binding.root, error)
    }

    private fun onPay(payResponse: PayResponse?) {
        progressDialog.dismiss()
        binding.btnAcceptPayment.isEnabled = true
        if (payResponse?.status?.value == STATUS_COMPLETED) {
            binding.etAmount.text?.clear()
            startSuccessActivity()
        } else {
            onPayError(getString(R.string.error))
        }
    }

    private fun startSuccessActivity() {
        val intent = Intent(this@MainActivity, SuccessActivity::class.java)
        startActivity(intent)
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.progress_loading))
    }

}