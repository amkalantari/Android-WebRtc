package com.axon.webrtc.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.axon.webrtc.App
import com.axon.webrtc.Coordinator
import com.axon.webrtc.R
import com.axon.webrtc.databinding.ActivitySplashBinding
import com.axon.webrtc.ui.splash.viewModel.SplashViewModel
import com.axon.webrtc.ui.splash.viewModel.SplashViewModelFactory
import com.core.parent.ParentActivity
import com.core.utils.SettingManager
import javax.inject.Inject


/**
 * Created by aMiir on 10/2/20
 * Drunk, Fix Later
 */

class SplashActivity : ParentActivity<SplashViewModel, ActivitySplashBinding>() {

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var factory: SplashViewModelFactory

    private var isDelayPassed = false

    @Suppress("DEPRECATION")
    private fun hideStatusBar() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setStatusBarColorResource(R.color.colorAccent)
        animateContent(binding.root)

        val a = arrayOfNulls<String>(2)
        a[0] = Manifest.permission.CAMERA
        a[1] = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, a, 101)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                isDelayPassed = true
                checkReadyToGo()
            }, 2000)
        }
    }

    private fun animateContent(view: View) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(resources.getInteger(R.integer.default_animation_duration).toLong())
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun checkReadyToGo() {
        if (isDelayPassed) {
            val intent = Coordinator.getNextIntent(this, settingManager)
            startActivity(intent)
            finish()
        }
    }

    override fun getResourceLayoutId(): Int = R.layout.activity_splash

    override fun getViewModelClass(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun inject() {
        (application as App).getDagger().inject(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (permissions.size == grantResults.size) {
                isDelayPassed = true
                checkReadyToGo()
            } else {
                finish()
            }
        }
    }

}