package com.qiwi.pay.merchant.model

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("value")
    val value: String,
    @SerializedName("changedDateTime")
    val changedDateTime: String
)