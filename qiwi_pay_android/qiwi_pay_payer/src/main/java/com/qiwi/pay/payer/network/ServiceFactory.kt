package com.qiwi.pay.payer.network

import com.qiwi.pay.payer.utils.Constants.AUTH_TOKEN
import com.qiwi.pay.payer.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.logging.HttpLoggingInterceptor

object ServiceFactory {
    private var qiwiService: QiwiService? = null

    fun getQiwiService(): QiwiService? {
        if (qiwiService == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $AUTH_TOKEN")
                        .build()
                    chain.proceed(newRequest)
                 }
                .addInterceptor(loggingInterceptor)
                .build()
            qiwiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QiwiService::class.java)
        }
        return qiwiService
    }
}