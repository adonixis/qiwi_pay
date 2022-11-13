package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("value")
    val value: String,
    @SerializedName("expiredDate")
    val expiredDate: String
)