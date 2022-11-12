package com.qiwi.pay.payer.network

import com.qiwi.pay.payer.model.PhoneRequest
import com.qiwi.pay.payer.model.PhoneResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface QiwiService {

    @Headers("Content-Type: application/json")
    @POST("payin-tokenization-api/v1/sites/{siteId}/token-requests")
    fun sendPhone(@Path("siteId") siteId: String, @Body phoneRequest: PhoneRequest): Observable<PhoneResponse>
}