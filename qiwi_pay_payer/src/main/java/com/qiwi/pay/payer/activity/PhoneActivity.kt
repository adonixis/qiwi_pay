package com.qiwi.pay.payer.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.qiwi.pay.payer.R
import com.qiwi.pay.payer.utils.TextWatcherImpl
import com.qiwi.pay.payer.databinding.ActivityPhoneBinding
import com.qiwi.pay.payer.model.PhoneResponse
import com.qiwi.pay.payer.utils.Constants.ACCOUNT_ID_KEY
import com.qiwi.pay.payer.utils.Constants.REQUEST_ID_KEY
import com.qiwi.pay.payer.utils.Constants.SITE_ID
import com.qiwi.pay.payer.utils.Constants.STATUS_CREATED
import com.qiwi.pay.payer.utils.Constants.STATUS_WAITING_SMS
import com.qiwi.pay.payer.utils.showErrorSnackbar
import com.qiwi.pay.payer.viewmodel.PhoneViewModel
import ru.tinkoff.decoro.MaskDescriptor
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.DescriptorFormatWatcher
import java.util.*


class PhoneActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PhoneActivity"
        const val PHONE_NUMBER_LENGTH = 18
    }

    private val phoneViewModel: PhoneViewModel by viewModels()
    private lateinit var binding: ActivityPhoneBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var accountId: String
    private lateinit var siteId: String
    private lateinit var requestId: String

    private val maskTextWatcher: TextWatcher = object : TextWatcherImpl() {
        override fun afterTextChanged(s: Editable) {
            binding.btnSendPhone.isEnabled = s.length == PHONE_NUMBER_LENGTH
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        installPhoneFormatter()

        settings = PreferenceManager.getDefaultSharedPreferences(this)
        editor = settings.edit()

        initProgressDialog()

        phoneViewModel.getPhoneLiveData().observe(this) { onSendPhone(it) }
        phoneViewModel.getErrorLiveData().observe(this) { onSendPhoneError(it) }

        siteId = SITE_ID

        binding.btnSendPhone.setOnClickListener { sendPhone() }
    }

    private fun sendPhone() {
        requestId = UUID.randomUUID().toString()
        val phone = binding.etPhone.text.toString().replace("[^\\d]".toRegex(), "")
        accountId = phone.hashCode().toString()
        progressDialog.show()
        binding.btnSendPhone.isEnabled = false
        phoneViewModel.sendPhone(
            siteId = siteId,
            requestId = requestId,
            phone = phone,
            accountId = accountId
        )
    }

    private fun onSendPhoneError(error: String) {
        progressDialog.dismiss()
        binding.btnSendPhone.isEnabled = true
        showErrorSnackbar(binding.root, error)
    }

    private fun onSendPhone(phoneResponse: PhoneResponse?) {
        progressDialog.dismiss()
        binding.btnSendPhone.isEnabled = true
        if (phoneResponse?.status?.value == STATUS_WAITING_SMS ||
            phoneResponse?.status?.value == STATUS_CREATED) {
            editor.putString(ACCOUNT_ID_KEY, accountId)
            editor.apply()
            startSmsCodeActivity(requestId)
        } else {
            onSendPhoneError(getString(R.string.error))
        }
    }

    private fun startSmsCodeActivity(requestId: String) {
        val intent = Intent(this, SmsCodeActivity::class.java)
        intent.putExtra(REQUEST_ID_KEY, requestId)
        startActivity(intent)
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.progress_loading))
    }

    private fun installPhoneFormatter() {
        val maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.RUS_PHONE_NUMBER)
        val formatWatcher = DescriptorFormatWatcher(MaskDescriptor.emptyMask().setTerminated(false))
        formatWatcher.installOn(binding.etPhone)
        formatWatcher.changeMask(maskDescriptor.setInitialValue(binding.etPhone.text.toString()))
        binding.etPhone.addTextChangedListener(maskTextWatcher)
    }
}