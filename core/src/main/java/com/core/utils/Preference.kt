package com.core.utils

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


interface Preference {
    fun put(key: String, value: String)
    fun put(key: String, value: Boolean)
    fun put(key: String, value: Long)
    fun put(key: String, value: Int)

    fun putEncrypt(key: String, value: String)

    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun getInt(key: String): Int

    fun getDecryptedString(key: String): String
}

class PreferenceImpl(
        application: Application,
        private val securityHelper: SecurityHelper
) : Preference {

    private val stringValues : HashMap<String, String> = hashMapOf()
    private val longValues   : HashMap<String, Long> = hashMapOf()
    private val intValues    : HashMap<String, Int> = hashMapOf()
    private val booleanValues: HashMap<String, Boolean> = hashMapOf()

    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    override fun put(key: String, value: String) {
        synchronized(this) {
            stringValues[key] = value
            preferences.edit().putString(key, value).apply()
        }
    }

    override fun put(key: String, value: Boolean) {
        synchronized(this) {
            booleanValues[key] = value
            preferences.edit().putBoolean(key, value).apply()
        }
    }

    override fun put(key: String, value: Long) {
        synchronized(this) {
            longValues[key] = value
            preferences.edit().putLong(key, value).apply()
        }
    }

    override fun put(key: String, value: Int) {
        synchronized(this) {
            intValues[key] = value
            preferences.edit().putInt(key, value).apply()
        }
    }

    override fun putEncrypt(key: String, value: String) {
        synchronized(this) {
            val encrypted = securityHelper.encrypt(value)
            stringValues[key] = encrypted
            preferences.edit().putString(key, encrypted).apply()
        }
    }

    override fun getString(key: String): String {
        synchronized(this) {
            return if (stringValues.containsKey(key))
                stringValues[key] ?: ""
            else
                preferences.getString(key, "") ?: ""
        }
    }

    override fun getBoolean(key: String): Boolean {
        synchronized(this) {
            return if (booleanValues.containsKey(key))
                booleanValues[key] ?: false
            else
                preferences.getBoolean(key, false)
        }
    }

    override fun getLong(key: String): Long {
        synchronized(this) {
            return if (longValues.containsKey(key))
                longValues[key] ?: 0
            else
                preferences.getLong(key, 0)
        }
    }

    override fun getInt(key: String): Int {
        synchronized(this) {
            return if (intValues.containsKey(key))
                intValues[key] ?: 0
            else
                preferences.getInt(key, 0)
        }
    }

    override fun getDecryptedString(key: String): String {
        synchronized(this) {
            val value = if (stringValues.containsKey(key))
                stringValues[key] ?: ""
            else
                preferences.getString(key, "") ?: ""
            return securityHelper.decrypt(value)
        }
    }
}