package com.qiwi.pay.payer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qiwi.pay.payer.R
import com.qiwi.pay.payer.model.SmsCodeRequest
import com.qiwi.pay.payer.model.SmsCodeResponse
import com.qiwi.pay.payer.network.ServiceFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException


class SmsCodeViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "SmsCodeViewModel"
    }

    private var smsCodeLiveData: MutableLiveData<SmsCodeResponse>? = null
    private var errorLiveData: MutableLiveData<String>? = null

    fun getSmsCodeLiveData(): LiveData<SmsCodeResponse> {
        smsCodeLiveData = MutableLiveData()
        return smsCodeLiveData as MutableLiveData<SmsCodeResponse>
    }

    fun getErrorLiveData(): LiveData<String> {
        errorLiveData = MutableLiveData()
        return errorLiveData as MutableLiveData<String>
    }

    fun sendSmsCode(siteId: String, requestId: String, smsCode: String) {
        val smsCodeRequest = SmsCodeRequest(
            requestId = requestId,
            smsCode = smsCode
        )
        val service = ServiceFactory.getQiwiService()
        service!!.sendSmsCode(siteId, smsCodeRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResult, this::handleError)
    }

    private fun handleResult(smsCodeResponse: SmsCodeResponse) {
        smsCodeLiveData!!.value = smsCodeResponse
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