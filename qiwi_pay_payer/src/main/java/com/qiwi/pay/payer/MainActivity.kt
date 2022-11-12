package com.qiwi.pay.payer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qiwi.pay.payer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startPhoneActivity()
    }

    private fun startPhoneActivity() {
        val intent = Intent(this@MainActivity, PhoneActivity::class.java)
        startActivity(intent)
    }
}