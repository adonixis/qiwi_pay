package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("value")
    val value: String
)