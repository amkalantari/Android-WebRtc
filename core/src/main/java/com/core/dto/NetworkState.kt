package com.core.dto

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.core.R
import retrofit2.HttpException


@Keep
enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

@Keep
enum class ErrorType(@StringRes val resource: Int) {

    //api error type
    EOFException(R.string.eofException),
    Forbidden(R.string.ioException),
    IOException(R.string.ioException),
    InternetConnection(R.string.internetConnection),
    SSLHandShake(R.string.sslException),
    Authorization(R.string.authorization),
    HttpException(R.string.undefine),
    NullPointException(R.string.app_name),
    Undefine(R.string.undefine)
}

data class NetworkState(
    var status: Status,
    var type: ErrorType = ErrorType.Undefine,
    var tag: String = "",
    var msg: String = "",
    val debuggingMessage: String = "",
    var code: Int = 200
) {

    companion object {

        fun loaded(tag: String) = NetworkState(Status.SUCCESS, tag = tag)

        fun loading(tag: String) = NetworkState(Status.RUNNING, tag = tag)

        fun error(type: ErrorType, tag: String, msg: String = "", code: Int = 200) =
            NetworkState(status = Status.FAILED, type = type, tag = tag, msg = msg, code = code)

        fun httpError(throwable: HttpException, msg: String): NetworkState {
            val url = throwable.response()?.raw()?.request?.url?.toUrl().toString()
            val body = throwable.response()?.raw()?.request?.body?.toString()
            var exceprionMessage = "****************************************************\n"
            exceprionMessage += "exception during calling API           \n".trimIndent()
            exceprionMessage += "$url \n".trimIndent()
            exceprionMessage += "$body\n".trimIndent()
            exceprionMessage += "exception code : ${throwable.code()}     ".trimIndent()
            exceprionMessage += "exception Message      : $msg".trimIndent()
            exceprionMessage += "****************************************************\n"
            return when (throwable.code()) {
                401 -> NetworkState(
                    status = Status.FAILED,
                    type = ErrorType.Authorization,
                    msg = msg,
                    debuggingMessage = exceprionMessage,
                    code = throwable.code()
                )
                403 -> NetworkState(
                    status = Status.FAILED,
                    type = ErrorType.Forbidden,
                    msg = msg,
                    debuggingMessage = exceprionMessage,
                    code = throwable.code()
                )
                else -> NetworkState(
                    status = Status.FAILED,
                    type = ErrorType.HttpException,
                    msg = msg,
                    debuggingMessage = exceprionMessage,
                    code = throwable.code()
                )
            }
        }
    }
}