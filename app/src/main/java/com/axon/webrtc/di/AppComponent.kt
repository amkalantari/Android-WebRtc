package com.axon.webrtc.di

import android.app.Application
import com.axon.webrtc.App
import com.axon.webrtc.ui.call.CallActivity
import com.axon.webrtc.ui.chat.ChatActivity
import com.axon.webrtc.ui.login.LoginActivity
import com.axon.webrtc.ui.main.MainActivity
import com.axon.webrtc.ui.splash.SplashActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RepositoryProvider::class,
        ViewModelFactory::class
    ]
)
interface AppComponent {

    fun inject(app: App)

    //Activity
    fun inject(app: MainActivity)
    fun inject(app: CallActivity)
    fun inject(app: SplashActivity)
    fun inject(app: LoginActivity)
    fun inject(app: ChatActivity)

    //Fragment

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(app: Application): Builder

        fun build(): AppComponent
    }

}
