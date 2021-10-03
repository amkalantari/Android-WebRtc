package com.axon.webrtc.ui.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.axon.webrtc.App
import com.axon.webrtc.di.DaggerAppComponent
import com.axon.webrtc.R
import com.axon.webrtc.databinding.ActivityLoginBinding
import com.axon.webrtc.ui.login.viewModel.LoginViewModel
import com.axon.webrtc.ui.login.viewModel.LoginViewModelFactory
import com.core.parent.ParentActivity
import com.core.utils.SettingManager
import javax.inject.Inject

/**
 * Created by aMiir on 10/2/20
 * Drunk, Fix Later
 */
open class LoginActivity : ParentActivity<LoginViewModel, ActivityLoginBinding>() {

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var factory: LoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.setHomeButtonEnabled(true)
    }

    override fun getResourceLayoutId(): Int = R.layout.activity_login

    override fun getViewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun inject() {
        (application as App).getDagger().inject(this)
    }


}