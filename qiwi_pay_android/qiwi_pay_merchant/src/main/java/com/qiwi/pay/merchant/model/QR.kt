package com.qiwi.pay.merchant.model

import com.google.gson.annotations.SerializedName

data class QR(
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("paymentToken")
    val paymentToken: String
)