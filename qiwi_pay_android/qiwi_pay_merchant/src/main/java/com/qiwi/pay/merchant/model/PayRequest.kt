package com.qiwi.pay.merchant.model

import com.google.gson.annotations.SerializedName

data class PayRequest(
    @SerializedName("amount")
    val amount: Amount,
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethod,
    @SerializedName("customer")
    val customer: Customer
)