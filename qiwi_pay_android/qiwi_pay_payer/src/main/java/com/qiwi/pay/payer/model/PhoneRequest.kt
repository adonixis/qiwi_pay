package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class PhoneRequest(
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("accountId")
    val accountId: String,
)