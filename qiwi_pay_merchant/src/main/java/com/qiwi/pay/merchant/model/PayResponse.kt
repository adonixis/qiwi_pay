package com.qiwi.pay.merchant.model

import com.google.gson.annotations.SerializedName

data class PayResponse(
    @SerializedName("paymentId")
    val paymentId: String,
    @SerializedName("billId")
    val billId: String,
    @SerializedName("createdDateTime")
    val createdDateTime: String,
    @SerializedName("amount")
    val amount: Amount,
    @SerializedName("capturedAmount")
    val capturedAmount: Amount,
    @SerializedName("refundedAmount")
    val refundedAmount: Amount,
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethod,
    @SerializedName("customer")
    val customer: Customer,
    @SerializedName("status")
    val status: Status
)