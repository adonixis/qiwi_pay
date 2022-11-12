package com.qiwi.pay.payer.model

import com.google.gson.annotations.SerializedName

data class QR(
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("paymentToken")
    val paymentToken: String
)