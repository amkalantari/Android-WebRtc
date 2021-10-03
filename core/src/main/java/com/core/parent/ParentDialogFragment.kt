package com.core.parent


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.core.base.BaseDialogFragment
import com.core.base.BaseViewModel
import com.core.dto.Status

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
abstract class ParentDialogFragment<T : BaseViewModel, E : ViewDataBinding> : BaseDialogFragment<E>() {

    lateinit var viewModel: T

    abstract fun getViewModelClass(): Class<T>

    abstract fun getFactory(): ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, getFactory()).get(getViewModelClass())
        viewModel.getNetworkStatus().observe(this) {
            when (it.status) {
                Status.RUNNING -> showDialogProgress(it.tag)
                Status.SUCCESS -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        hideDialogProgress(it.tag)
                    }, 500)
                }
                else -> {
                    handleFailureStatus(it) { error ->
                        showDialogError(it.tag, error)
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