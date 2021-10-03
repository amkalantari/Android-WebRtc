package com.axon.webrtc

import android.app.Activity
import android.content.Intent
import com.axon.webrtc.ui.call.CallActivity
import com.axon.webrtc.ui.login.LoginActivity
import com.axon.webrtc.ui.login.RegisterFragment.Companion.Login_Extra
import com.axon.webrtc.ui.main.MainActivity
import com.core.dto.LoginResponse
import com.core.dto.User
import com.core.utils.SettingManager


object Coordinator {

    fun getNextIntent(activity: Activity, settingManager: SettingManager): Intent {

        return if (settingManager.isRegister()) {
            Intent(activity, LoginActivity::class.java)
        } else {
            Intent(activity, LoginActivity::class.java)
        }

    }

}