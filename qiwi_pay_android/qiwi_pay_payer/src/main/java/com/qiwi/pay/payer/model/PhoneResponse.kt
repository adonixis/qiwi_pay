package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class PhoneResponse(
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("status")
    val status: Status
)