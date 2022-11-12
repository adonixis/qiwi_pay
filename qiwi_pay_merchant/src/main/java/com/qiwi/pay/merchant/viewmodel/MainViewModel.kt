package com.qiwi.pay.merchant.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qiwi.pay.merchant.R
import com.qiwi.pay.merchant.model.*
import com.qiwi.pay.merchant.network.ServiceFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private var payLiveData: MutableLiveData<PayResponse>? = null
    private var errorLiveData: MutableLiveData<String>? = null

    fun getPayLiveData(): LiveData<PayResponse> {
        payLiveData = MutableLiveData()
        return payLiveData as MutableLiveData<PayResponse>
    }

    fun getErrorLiveData(): LiveData<String> {
        errorLiveData = MutableLiveData()
        return errorLiveData as MutableLiveData<String>
    }

    fun pay(siteId: String,
            paymentId: String,
            amountCurrency: String,
            amountValue: String,
            paymentType: String,
            paymentToken: String,
            accountId: String
    ) {
        val payRequest = PayRequest(
            amount = Amount(amountCurrency, amountValue),
            paymentMethod = PaymentMethod(paymentType, paymentToken),
            customer = Customer(accountId)
        )
        val service = ServiceFactory.getQiwiService()
        service!!.pay(siteId, paymentId, payRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResult, this::handleError)
    }

    private fun handleResult(smsCodeResponse: PayResponse) {
        payLiveData!!.value = smsCodeResponse
    }

    private fun handleError(e: Throwable) {
        val errorMessage = getApplication<Application>().getString(R.string.error)
        when (e) {
            is HttpException -> {
                errorLiveData!!.setValue("$errorMessage: ${e.code()} ${e.message()}")
            }
            is UnknownHostException -> {
                errorLiveData!!.setValue(getApplication<Application>().getString(R.string.error_network))
            }
            else -> {
                Log.e(TAG, "Unknown error: ", e)
                errorLiveData!!.setValue(errorMessage)
            }
        }
    }
}