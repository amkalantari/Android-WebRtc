package com.axon.webrtc.ui.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.base.BaseViewModel

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
abstract class MainViewModel: BaseViewModel() {}


class MainViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModelImpl() as T
    }

}