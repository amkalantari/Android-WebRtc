package com.core.base

import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import com.core.dto.ErrorType
import com.core.dto.NetworkState
import com.core.dto.ResultDto
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


@Keep
data class ResponseData<T>(
    @Keep val tag: String,
    @Keep val data: MutableLiveData<T>,
    @Keep var status: MutableLiveData<NetworkState> = MutableLiveData()
) {

    fun loaded() {
        status.postValue(NetworkState.loaded(tag))
    }

    fun loading() {
        status.postValue(NetworkState.loading(tag))
    }

    fun handlerResultAsync(result: ResultDto<T>) {
        loaded()
        if (result.code == 200) {
            val data = result.result
            if (data != null) {
                this.data.postValue(data)
                loaded()
            } else {
                status.postValue(NetworkState.error(ErrorType.NullPointException, tag = tag, result.message ?: ""))
            }
        } else {
            if (!result.message.isNullOrEmpty()) {
                status.postValue(NetworkState.error(ErrorType.Undefine, tag = tag, result.message ?: ""))
            } else {
                status.postValue(when (result.code) {
                    401 -> NetworkState.error(ErrorType.Authorization, tag = tag)
                    403 -> NetworkState.error(ErrorType.Forbidden, tag = tag)
                    else -> NetworkState.error(ErrorType.Undefine, tag = tag)
                })
            }
        }
    }

    fun handleError(t: Throwable) {
        status.postValue(when (t) {
            /**
             * server exception
             */
            is EOFException -> NetworkState.error(ErrorType.EOFException, tag = tag)
            is SSLHandshakeException -> NetworkState.error(ErrorType.SSLHandShake, tag = tag)
            is SocketTimeoutException,
            is UnknownHostException,
            is IOException -> NetworkState.error(ErrorType.InternetConnection, tag = tag)
            is HttpException -> NetworkState.httpError(t, getErrorMessage(t.response()?.errorBody()))
            else -> {
                NetworkState.error(ErrorType.Undefine, tag = tag)
            }
        })
    }

    /**
     *
     * [getErrorMessage] is responsible for fetching error message from error body
     * which is happening in the response of api call.
     * Error Format : {Code:"",Description:"",Stack:""}
     */
    private fun getErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val jsonObject = JSONObject(errorBody?.string() ?: "")
            when {
                jsonObject.has("Description") -> {
                    jsonObject.getString("Description")
                }
                else -> {
                    ""
                }
            }
        } catch (e: Exception) {
            ""
        }
    }
}