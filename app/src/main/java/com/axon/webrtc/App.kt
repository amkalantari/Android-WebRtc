package com.axon.webrtc

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.axon.webrtc.di.AppComponent
import com.axon.webrtc.di.DaggerAppComponent
import com.axon.webrtc.ui.splash.SplashActivity
import com.core.base.BaseApp
import com.core.utils.SettingManager
import io.reactivex.plugins.RxJavaPlugins
import me.axon.webrtc.SignalingSocket
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.system.exitProcess

/**
 * Created by aMiir on 9/31/21
 * Drunk, Fix Later
 */
open class App : BaseApp() {

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var signalingSocket: SignalingSocket

    private lateinit var dagger: AppComponent

    open fun getDagger(): AppComponent {
        return dagger
    }

    private fun handleUncaughtExceptionHandler(e: Throwable) {
        try {
            var exceprionMessage = "****************************************************\n"
            exceprionMessage += "exception ClassName    : ${e.javaClass.simpleName} ".trimIndent()
            exceprionMessage += "\n"
            if (e.stackTrace.isNotEmpty()) {
                exceprionMessage += "exception MethodName   : ${e.stackTrace[0].methodName}".trimIndent()
                exceprionMessage += "\n"
                exceprionMessage += "exception LineNumber   : ${e.stackTrace[0].lineNumber}".trimIndent()
            }
            exceprionMessage += "\n"
            exceprionMessage += "exception Message      : ${e.message}".trimIndent()
            exceprionMessage += "\n"
            exceprionMessage += "****************************************************\n"
            Timber.e("Exception $exceprionMessage")
        } catch (ex: java.lang.Exception) {
            Timber.e("Exception ${ex.message}")
        }
    }

    override fun logoutAndRestart() {
        settingManager.setRegister(false)
        Handler(Looper.getMainLooper()).postDelayed({
            val registerActivity = Intent(applicationContext, SplashActivity::class.java)
            registerActivity.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            applicationContext.startActivity(registerActivity)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }, 1000)
    }

    override fun onCreate() {
        super.onCreate()

        inject()

        RxJavaPlugins.setErrorHandler { throwable: Throwable? ->
            println("exception check ${throwable?.message}")
            if (throwable != null)
                handleUncaughtExceptionHandler(throwable)
        }

        val mAndroidCrashHandler = Thread.getDefaultUncaughtExceptionHandler()

        val mUncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, exception ->
            try {
                handleUncaughtExceptionHandler(exception)
            } finally {
                mAndroidCrashHandler?.uncaughtException(thread, exception)
            }
        }

        Thread.setDefaultUncaughtExceptionHandler(mUncaughtExceptionHandler)

    }

    fun inject() {
        dagger = DaggerAppComponent.builder().app(this).build()
        dagger.inject(this)
    }

}