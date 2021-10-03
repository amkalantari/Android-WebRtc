package com.core.base

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.core.R
import com.core.dto.ErrorType
import com.core.dto.NetworkState
import com.google.android.material.snackbar.Snackbar

typealias PermissionGranted = (Array<String>) -> Unit
typealias PermissionDenied = (Array<String>) -> Unit

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
abstract class BaseActivity<E : ViewDataBinding> : AppCompatActivity() {

    private val permissionRequest: Int = 12000

    private var granted: PermissionGranted? = null

    var denied: PermissionDenied? = null

    lateinit var binding: E

    abstract fun getResourceLayoutId(): Int

    open fun inject() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        binding = DataBindingUtil.setContentView(this, getResourceLayoutId())
        setStatusBarColorResource(R.color.colorAccent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getNavController(@IdRes idRes: Int) : NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(idRes) as NavHostFragment
        return navHostFragment.navController
    }

    protected fun showFragment(
        @IdRes layoutId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(layoutId, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }
        transaction.commit()
    }

    protected fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun showMessage(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    open fun initToolbar(toolbar: Toolbar, title: String? = null) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title ?: ""
    }

    open fun showProgress(tag: String) {}

    open fun hideProgress(tag: String) {}

    open fun showError(tag: String, error: String) {
        hideProgress(tag)
    }

    open fun isTablet(): Boolean {
        val screen = resources.configuration.screenLayout
        return screen and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequest) {
            if (permissions.size == grantResults.size) {
                if (granted != null)
                    granted!!(arrayOf(*permissions))
            } else {
                denied!!(arrayOf(*permissions))
            }
        }
    }

    open fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            )
                return false
        }
        return true
    }

    open fun permissionsRequest(
        permissions: Array<String>,
        granted: PermissionGranted? = null,
        denied: PermissionDenied? = null
    ) {
        var hasPermission = true
        val deniedPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                hasPermission = false
                deniedPermissions.add(permission)
            }
        }
        if (hasPermission) {
            granted?.let { it(permissions) }
        } else {
            denied?.let { it(deniedPermissions.toTypedArray()) }
            this.granted = granted
            this.denied = denied
            ActivityCompat.requestPermissions(this, permissions, permissionRequest)
        }
    }

    open fun setStatusBarColorResource(@ColorRes color: Int) {
        setStatusBarColor(ContextCompat.getColor(this, color))
    }

    open fun handleFailureStatus(status: NetworkState, onShowMessage: (String) -> Unit) {
        hideProgress(status.tag)
        when (status.type) {
            ErrorType.Forbidden -> {
                (application as BaseApp).logoutAndRestart()
            }
            else -> {
                onShowMessage(
                    if (status.msg.isEmpty())
                        if (status.type.resource != 0) {
                            getString(status.type.resource)
                        } else {
                            ""
                        }
                    else
                        status.msg
                )
            }
        }
    }

    private fun setStatusBarColor(color: Int) {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

}