package com.qiwi.pay.merchant.network

import com.qiwi.pay.merchant.model.PayRequest
import com.qiwi.pay.merchant.model.PayResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface QiwiService {
    @Headers("Content-Type: application/json")
    @PUT("payin/v1/sites/{siteId}/payments/{paymentId}")
    fun pay(@Path("siteId") siteId: String, @Path("paymentId") paymentId: String, @Body payRequest: PayRequest): Observable<PayResponse>
}