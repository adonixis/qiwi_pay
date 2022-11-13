package com.qiwi.pay.merchant.model

import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @SerializedName("type")
    val type: String,
    @SerializedName("paymentToken")
    val paymentToken: String
)