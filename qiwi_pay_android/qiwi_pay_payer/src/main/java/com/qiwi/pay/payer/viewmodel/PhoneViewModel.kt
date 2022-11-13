package com.qiwi.pay.payer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qiwi.pay.payer.R
import com.qiwi.pay.payer.model.PhoneRequest
import com.qiwi.pay.payer.model.PhoneResponse
import com.qiwi.pay.payer.network.ServiceFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException


class PhoneViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "PhoneViewModel"
    }

    private var phoneLiveData: MutableLiveData<PhoneResponse>? = null
    private var errorLiveData: MutableLiveData<String>? = null

    fun getPhoneLiveData(): LiveData<PhoneResponse> {
        phoneLiveData = MutableLiveData()
        return phoneLiveData as MutableLiveData<PhoneResponse>
    }

    fun getErrorLiveData(): LiveData<String> {
        errorLiveData = MutableLiveData()
        return errorLiveData as MutableLiveData<String>
    }

    fun sendPhone(siteId: String, requestId: String, phone: String, accountId: String) {
        val phoneRequest = PhoneRequest(
            requestId = requestId,
            phone = phone,
            accountId = accountId
        )
        val service = ServiceFactory.getQiwiService()
        service!!.sendPhone(siteId, phoneRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResult, this::handleError)
    }

    private fun handleResult(phoneResponse: PhoneResponse) {
        phoneLiveData!!.value = phoneResponse
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