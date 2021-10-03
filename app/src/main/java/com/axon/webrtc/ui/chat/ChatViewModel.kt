package com.axon.webrtc.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axon.webrtc.ui.main.viewModel.MainViewModel
import com.core.base.BaseViewModel

abstract class ChatViewModel : BaseViewModel() {}


class ChatViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ChatViewModelImpl() as T
    }

}

class ChatViewModelImpl() : MainViewModel() {}