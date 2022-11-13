package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class SmsCodeResponse(
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("status")
    val status: Status,
    @SerializedName("token")
    val token: Token
)