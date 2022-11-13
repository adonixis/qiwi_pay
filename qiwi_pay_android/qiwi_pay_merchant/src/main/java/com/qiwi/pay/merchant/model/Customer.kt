package com.qiwi.pay.merchant.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("account")
    val account: String
)