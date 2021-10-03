package com.core.utils

interface SettingManager {

    fun setRegister(isRegister: Boolean)
    fun isRegister(): Boolean

}

class SettingManagerImpl(private val preference: Preference) : SettingManager {

    companion object {
        private const val isRegistered = "isRegistered"
    }

    override fun setRegister(isRegister: Boolean) {
        preference.put(isRegistered, isRegister)
    }

    override fun isRegister(): Boolean = preference.getBoolean(isRegistered)

}

