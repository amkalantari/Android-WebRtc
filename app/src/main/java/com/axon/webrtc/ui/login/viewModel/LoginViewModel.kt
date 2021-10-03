package com.axon.webrtc.ui.login.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.base.BaseViewModel

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */

abstract class LoginViewModel : BaseViewModel() {}

class LoginViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModelImpl() as T
    }
}

class LoginViewModelImpl() : LoginViewModel() {
}