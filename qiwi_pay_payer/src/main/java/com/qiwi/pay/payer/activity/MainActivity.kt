package com.qiwi.pay.payer.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.qiwi.pay.payer.databinding.ActivityMainBinding
import com.qiwi.pay.payer.utils.Constants.PAYMENT_TOKEN_KEY


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settings: SharedPreferences
    private var paymentToken: String? = null

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

            paymentToken = intent.getStringExtra(PAYMENT_TOKEN_KEY)
            if (paymentToken == null) {
                settings = PreferenceManager.getDefaultSharedPreferences(this)
                paymentToken = settings.getString(PAYMENT_TOKEN_KEY, "")
            }

            binding.tvToken.text = paymentToken
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
}