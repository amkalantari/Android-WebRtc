package com.core.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.core.R
import com.core.dto.ErrorType
import com.core.dto.NetworkState
import com.core.widget.ImageViewCustom
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*


/**
 * Dialog which can be open like Drawer from START or END
 * In the phone this Dialog will be showing full screen
 * In the tablet is not like that and will be opening like a dialog and base on [Gravity]
 * In the [Gravity.NO_GRAVITY] status it will be opening like normal dialog in the center of screen
 * In the other hand it can be open from side in [tabletGravity] function.
 */
abstract class BaseDialogFragment<E : ViewDataBinding> : DialogFragment() {

    val permissionRequest: Int = 12000

    var granted: PermissionGranted? = null
    var denied: PermissionDenied? = null
    var getStatusBarColor: String = ""

    lateinit var binding: E

    abstract fun getSkeletonLayoutId(): View?

    abstract fun getResourceLayoutId(): Int

    open fun initToolbar(
        toolbar: Toolbar,
        @SuppressLint("SupportAnnotationUsage") @StringRes title: Int,
        isDarkTheme: Boolean = false
    ) {
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.menu.findItem(R.id.close_menu)?.let {
            val view = it.actionView
            val image = view.findViewById<ImageViewCustom>(R.id.close_imageview)
            image.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
        toolbar.title = getString(title)
    }

    /**
     * because of stupid ui design for close icon required in left screen in rtl layout
     * we have to use this inflater which by default we have an extra close icon as menu
     */
    open fun initToolbarWithMenu(
        toolbar: Toolbar,
        title: String,
        isDarkTheme: Boolean = false,
        @MenuRes menuRes: Int
    ) {
        toolbar.inflateMenu(menuRes)
        toolbar.menu.findItem(R.id.close_menu)?.let {
            val view = it.actionView
            val image = view.findViewById<ImageViewCustom>(R.id.close_imageview)
            image.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
        toolbar.setOnMenuItemClickListener {
            onMenuItemSelected(it)
            return@setOnMenuItemClickListener true
        }
        toolbar.title = title
    }

    open fun onMenuItemSelected(menuItem: MenuItem) {}

    open fun initToolbar(
        toolbar: Toolbar,
        title: String,
        isDarkTheme: Boolean = false,
        @DrawableRes icon: Int = R.drawable.ic_back_black
    ) {
        toolbar.title = title
        toolbar.setNavigationIcon(icon)
        toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(
                requireContext(),
                if (isDarkTheme) R.color.white else R.color.black
            )
        )
        toolbar.setNavigationOnClickListener { dismissAllowingStateLoss() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun getTheme(): Int = R.style.AppTheme_Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            getResourceLayoutId(),
            container,
            false
        )
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_Dialog)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.setDimAmount(0.5.toFloat())
        return binding.root
    }

    open fun hideKeyboard() {
        try {
            val imm =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = requireActivity().currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
    }

    open fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun isTablet(): Boolean {
        val screen = resources.configuration.screenLayout
        return screen and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    open fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_DENIED
            )
                return false
        }
        return true
    }

    open fun permissionsRequest(
        permissions: Array<String>,
        granted: PermissionGranted,
        denied: PermissionDenied
    ) {
        var hasPermission: Boolean = true
        val deniedPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                hasPermission = false
                deniedPermissions.add(permission)
            }
        }
        if (hasPermission) {
            granted(permissions)
        } else {
            denied(deniedPermissions.toTypedArray())
            this.granted = granted
            this.denied = denied
            requestPermissions(permissions, permissionRequest)
        }
    }

    open fun showMessage(msg: String) {
        if (isResumed) {
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .show()
        }
    }

    open fun showDialogProgress(tag: String) {
//        if (!tag.contains("getAllOrder")) {
//            skeletonScreen?.show()
//        }
    }

    open fun hideDialogProgress(tag: String) {
//        if (!tag.contains("getAllOrder")) {
//            skeletonScreen?.hide()
//        }
    }

    open fun backStack(tag: String) {}

    open fun showDialogError(tag: String, error: String) {
        hideDialogProgress(tag)
    }

    open fun inject() {}

    private fun statusBarHeight(): Int {
        return try {
            val resource =
                requireContext().resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resource > 0) {
                requireContext().resources.getDimensionPixelSize(resource)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    open fun setDialogViewPercent(width: Double, height: Double) {
        dialog?.window?.let { window ->
            val statusBarHeight = statusBarHeight()
            val size = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = window.windowManager.currentWindowMetrics
                val screenWidth = metrics.bounds.left - metrics.bounds.right
                val screenHeight = metrics.bounds.top - metrics.bounds.bottom - statusBarHeight
                window.setLayout((screenWidth * width).toInt(), (screenHeight * height).toInt())
            } else {
                window.windowManager.defaultDisplay.getSize(size)
                val screenWidth: Int = size.x
                val screenHeight: Int = size.y - statusBarHeight
                window.setLayout((screenWidth * width).toInt(), (screenHeight * height).toInt())
            }
            window.setGravity(Gravity.CENTER)
        }
    }

    private fun parseColor(colorCode: String): Int {
        var color = colorCode
        if (!colorCode.contains("#"))
            color = "#$colorCode"

        return Color.parseColor(color)
    }

    protected fun setBaseStatusColor(color: String) {
        getStatusBarColor = color
        setStatusBarColor(parseColor(getStatusBarColor))
    }

    override fun onResume() {
        super.onResume()
        if (getStatusBarColor.isEmpty()) {
            setStatusBarColor(parseColor("360266"))
        } else {
            setStatusBarColor(parseColor(getStatusBarColor))
        }

        super.onResume()
//        getSkeletonLayoutId()?.let {
//            skeletonScreen = Skeleton.bind(it)
//                .load(R.layout.layout_skeleton)
//                .shimmer(true)
//                .angle(20)
//                .duration(1000)
//                .build()
//        }

    }

    private fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequest) {
            if (permissions.size == grantResults.size) {
                granted!!(arrayOf(*permissions))
            } else {
                denied!!(arrayOf(*permissions))
            }
        }
    }

    open fun handleFailureStatus(status: NetworkState, onShowMessage: (String) -> Unit) {
        hideDialogProgress(status.tag)
        when (status.type) {
            ErrorType.Forbidden -> {
                (requireActivity().application as BaseApp).logoutAndRestart()
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
}