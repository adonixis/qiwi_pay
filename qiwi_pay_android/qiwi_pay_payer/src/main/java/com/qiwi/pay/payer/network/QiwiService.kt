package com.qiwi.pay.payer.network

import com.qiwi.pay.payer.model.PhoneRequest
import com.qiwi.pay.payer.model.PhoneResponse
import com.qiwi.pay.payer.model.SmsCodeRequest
import com.qiwi.pay.payer.model.SmsCodeResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface QiwiService {

    @Headers("Content-Type: application/json")
    @POST("payin-tokenization-api/v1/sites/{siteId}/token-requests")
    fun sendPhone(@Path("siteId") siteId: String, @Body phoneRequest: PhoneRequest): Observable<PhoneResponse>

    @Headers("Content-Type: application/json")
    @POST("payin-tokenization-api/v1/sites/{siteId}/token-requests/complete")
    fun sendSmsCode(@Path("siteId") siteId: String, @Body smsCodeRequest: SmsCodeRequest): Observable<SmsCodeResponse>
}