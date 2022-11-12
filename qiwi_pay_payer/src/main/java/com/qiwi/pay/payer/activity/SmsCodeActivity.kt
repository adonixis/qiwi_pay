package com.qiwi.pay.payer.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.qiwi.pay.payer.R
import com.qiwi.pay.payer.databinding.ActivitySmsCodeBinding
import com.qiwi.pay.payer.model.SmsCodeResponse
import com.qiwi.pay.payer.utils.Constants.PAYMENT_TOKEN_KEY
import com.qiwi.pay.payer.utils.Constants.REQUEST_ID_KEY
import com.qiwi.pay.payer.utils.Constants.SITE_ID
import com.qiwi.pay.payer.utils.Constants.STATUS_CREATED
import com.qiwi.pay.payer.utils.Constants.STATUS_WAITING_SMS
import com.qiwi.pay.payer.utils.TextWatcherImpl
import com.qiwi.pay.payer.utils.showErrorSnackbar
import com.qiwi.pay.payer.viewmodel.SmsCodeViewModel


class SmsCodeActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SmsCodeActivity"
        const val SMS_CODE_LENGTH = 4
    }

    private val smsCodeViewModel: SmsCodeViewModel by viewModels()
    private lateinit var binding: ActivitySmsCodeBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var siteId: String
    private lateinit var requestId: String

    private val maskTextWatcher: TextWatcher = object : TextWatcherImpl() {
        override fun afterTextChanged(s: Editable) {
            binding.btnSendSmsCode.isEnabled = s.length == SMS_CODE_LENGTH
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsCodeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.etSmsCode.addTextChangedListener(maskTextWatcher)

        settings = PreferenceManager.getDefaultSharedPreferences(this)
        editor = settings.edit()

        initProgressDialog()

        requestId = intent.getStringExtra(REQUEST_ID_KEY) ?: ""

        smsCodeViewModel.getSmsCodeLiveData().observe(this) { onSendSmsCode(it) }
        smsCodeViewModel.getErrorLiveData().observe(this) { onSendSmsCodeError(it) }

        siteId = SITE_ID

        binding.btnSendSmsCode.setOnClickListener { sendSmsCode() }
    }

    private fun sendSmsCode() {
        val smsCode = binding.etSmsCode.text.toString().replace("[^\\d]".toRegex(), "")
        progressDialog.show()
        binding.btnSendSmsCode.isEnabled = false
        smsCodeViewModel.sendSmsCode(
            siteId = siteId,
            requestId = requestId,
            smsCode = smsCode
        )
    }

    private fun onSendSmsCodeError(error: String) {
        progressDialog.dismiss()
        binding.btnSendSmsCode.isEnabled = true
        showErrorSnackbar(binding.root, error)
    }

    private fun onSendSmsCode(smsCodeResponse: SmsCodeResponse?) {
        progressDialog.dismiss()
        binding.btnSendSmsCode.isEnabled = true
        if (smsCodeResponse?.status?.value == STATUS_WAITING_SMS ||
            smsCodeResponse?.status?.value == STATUS_CREATED) {
            val paymentToken = smsCodeResponse.token.value
            editor.putString(PAYMENT_TOKEN_KEY, paymentToken)
            editor.apply()
            startMainActivity(paymentToken)
        } else {
            onSendSmsCodeError(getString(R.string.error))
        }
    }

    private fun startMainActivity(paymentToken: String) {
        val intent = Intent(this@SmsCodeActivity, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(PAYMENT_TOKEN_KEY, paymentToken)
        startActivity(intent)
        finish()
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.progress_loading))
    }
}