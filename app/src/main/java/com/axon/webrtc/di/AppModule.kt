package com.axon.webrtc.di

import android.app.Application
import com.core.db.AppDatabase
import com.core.utils.*
import com.github.nkzawa.socketio.client.IO
import dagger.Module
import dagger.Provides
import me.axon.webrtc.SignalingSocket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
class AppModule {

    @Provides
    fun provideDatabase(app: Application): AppDatabase {
        return AppDatabase.getInstance(app.applicationContext, testMode = false)
    }

    @Provides
    fun provideSecurityHelper(): SecurityHelper {
        return SecurityHelperImpl()
    }

    @Singleton
    @Provides
    fun provideSocketWrapper(): SignalingSocket {
        return SignalingSocket.Builder()
            .serverUrl(IO.socket("http://172.17.12.61:1234/"))
//            .serverUrl("http://${BuildConfig.SOCKET_URL}:1234/")
//            .serverUrl("https://172.17.12.61:1234/")
            .build()
    }

    @Provides
    fun providePreference(app: Application, securityHelper: SecurityHelper): Preference {
        return PreferenceImpl(app, securityHelper)
    }

    @Provides
    fun provideSettingManager(preference: Preference): SettingManager {
        return SettingManagerImpl(preference)
    }
}
