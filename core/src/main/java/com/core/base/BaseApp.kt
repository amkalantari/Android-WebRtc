package com.core.base

import android.app.Application
import com.core.BuildConfig
import timber.log.Timber

abstract class BaseApp : Application(), BaseObserver {

    abstract fun logoutAndRestart()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}