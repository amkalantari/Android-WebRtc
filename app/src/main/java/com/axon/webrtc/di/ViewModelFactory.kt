package com.axon.webrtc.di

import com.axon.webrtc.ui.chat.ChatViewModelFactory
import com.axon.webrtc.ui.login.viewModel.LoginViewModelFactory
import com.axon.webrtc.ui.main.viewModel.MainViewModelFactory
import com.axon.webrtc.ui.splash.viewModel.SplashViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelFactory {

    @Provides
    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory()
    }

    @Provides
    fun provideSplashViewModelFactory(): SplashViewModelFactory {
        return SplashViewModelFactory()
    }

    @Provides
    fun provideChatViewModelFactoryFactory(): ChatViewModelFactory {
        return ChatViewModelFactory()
    }

    @Provides
    fun provideLoginViewModelFactory(

    ): LoginViewModelFactory {
        return LoginViewModelFactory()
    }

}