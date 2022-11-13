package com.qiwi.pay.merchant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qiwi.pay.merchant.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}