package com.qiwi.pay.payer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.qiwi.pay.payer.databinding.ActivityPhoneBinding
import ru.tinkoff.decoro.MaskDescriptor
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.DescriptorFormatWatcher


class PhoneActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PhoneActivity"
        const val PHONE_NUMBER_LENGTH = 18
    }

    private lateinit var binding: ActivityPhoneBinding

    private val maskTextWatcher: TextWatcher = object : TextWatcherImpl() {
        override fun afterTextChanged(s: Editable) {
            if (s.length == PHONE_NUMBER_LENGTH) {
                Log.d(TAG, "Number ok")
            } else {
                Log.d(TAG, "Number wrong")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        installPhoneFormatter()
    }

    private fun installPhoneFormatter() {
        val maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.RUS_PHONE_NUMBER)
        val formatWatcher = DescriptorFormatWatcher(MaskDescriptor.emptyMask().setTerminated(false))
        formatWatcher.installOn(binding.etPhone)
        formatWatcher.changeMask(maskDescriptor.setInitialValue(binding.etPhone.text.toString()))
        binding.etPhone.addTextChangedListener(maskTextWatcher)
    }
}