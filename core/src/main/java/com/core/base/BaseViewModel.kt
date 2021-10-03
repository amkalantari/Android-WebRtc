package com.core.base


import androidx.lifecycle.*
import com.core.dto.NetworkState

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
abstract class BaseViewModel() : ViewModel(), LifecycleObserver, BaseObserver {

    private val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    open fun getNetworkStatus(): LiveData<NetworkState> = networkState

    open fun showingProgress(tag: String) {
        networkState.postValue(NetworkState.loading(tag))
    }

    open fun hideProgress(tag: String) {
        networkState.postValue(NetworkState.loaded(tag))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreated() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {

    }

}