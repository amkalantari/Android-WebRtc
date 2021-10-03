package com.core.parent


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.core.base.BaseFragment
import com.core.base.BaseViewModel
import com.core.dto.Status

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
abstract class ParentFragment<T : BaseViewModel, E : ViewDataBinding> : BaseFragment<E>() {

    lateinit var viewModel: T

    abstract fun getViewModelClass(): Class<T>

    abstract fun getFactory(): ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, getFactory()).get(getViewModelClass())
        viewModel.getNetworkStatus().observe(this) {
            when (it.status) {
                Status.RUNNING -> showProgress(it.tag)
                Status.SUCCESS -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        hideProgress(it.tag)
                    }, 500)
                }
                else -> {
                    handleFailureStatus(it) { error ->
                        showError(it.tag, error)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

}