package com.axon.webrtc.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.axon.webrtc.BuildConfig
import com.core.utils.SettingManager
import okhttp3.*
import org.webrtc.MediaStream
import timber.log.Timber
import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String get() = "عدم اتصال به اینترنت"
}

class ConnectivityInterceptor(
    val app: Application,
    private var settingManager: SettingManager
) : Interceptor {

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val originalRequest = chain.request().newBuilder()
            if (!isNetworkAvailable(app)) {
                throw NoConnectivityException()
            }
            val request = requestHeader(originalRequest, settingManager)
            return chain.proceed(request)
        }
    }

    companion object {
        fun requestHeader(request: Request.Builder, settingManager: SettingManager): Request {

            return request
                .addHeader("Accept", "application/json")
                .addHeader("Content-type", "application/json")
                .addHeader("app-version", BuildConfig.VERSION_NAME)
                .build()

        }
    }

}
