package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class SmsCodeRequest(
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("smsCode")
    val smsCode: String
)