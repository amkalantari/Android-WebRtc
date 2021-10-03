package com.core.base

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.core.R
import com.core.dto.ErrorType
import com.core.dto.NetworkState

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
abstract class BaseFragment<E : ViewDataBinding> : Fragment() {

    val permissionRequest: Int = 12000

    var granted: PermissionGranted? = null

    var denied: PermissionDenied? = null

//    var skeletonScreen: ViewSkeletonScreen? = null

    lateinit var binding: E

    abstract fun getResourceLayoutId(): Int

    abstract fun getSkeletonLayoutId(): View?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            getResourceLayoutId(),
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
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

    open fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

    open fun initToolbar(toolbar: Toolbar, @StringRes title: Int?, isDarkTheme: Boolean = false) {
        if (isDarkTheme)
            toolbar.setNavigationIcon(R.drawable.ic_back_white)
        else
            toolbar.setNavigationIcon(R.drawable.ic_back_black)
        title?.let {
            toolbar.title = getString(title)
        }
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    open fun initToolbar(toolbar: Toolbar, title: String, isDarkTheme: Boolean = false) {
        if (isDarkTheme) {
            toolbar.setNavigationIcon(R.drawable.ic_back_white)
            toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_back_black)
            toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        toolbar.title = title
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
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
        if (isAdded) {
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .show()
        }
    }

    open fun showProgress(tag: String) {
//        if (!tag.contains("getAllOrder")) {
//            skeletonScreen?.show()
//        }
    }

    open fun hideProgress(tag: String) {
//        if (!tag.contains("getAllOrder")) {
//            skeletonScreen?.hide()
//        }
    }

    open fun backStack(tag: String) {}

    open fun showError(tag: String, error: String) {
        hideProgress(tag)
    }

    open fun onBackPressed() {
        findNavController().popBackStack()
    }

    open fun finish() {
        requireActivity().finish()
    }

    open fun inject() {}

    open fun setStatusBarColorResource(@ColorRes color: Int) {
        setBaseStatusColor(ContextCompat.getColor(requireContext(), color))
    }

    open fun inflateToolbarMenu(
        toolbar: Toolbar,
        @MenuRes menuRes: Int,
        onItemSelected: ((MenuItem) -> Boolean)? = null
    ) {
        toolbar.inflateMenu(menuRes)
        toolbar.setOnMenuItemClickListener { menuItem ->
            return@setOnMenuItemClickListener onItemSelected?.invoke(menuItem) ?: false
        }
    }

    open fun handleSearchView(menuItem: MenuItem, onTextChanged: (String) -> Unit) {
        val searchView = menuItem.actionView as SearchView?
        var timer: CountDownTimer? = null
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false
            override fun onQueryTextChange(newText: String): Boolean {
                timer?.cancel()
                timer = object : CountDownTimer(1000, 1500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        onTextChanged(newText)
                    }
                }.start()
                return true
            }
        })
    }

    open fun handleAppbarLayout(
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        toolbar: Toolbar?,
        title: String? = "",
        isCollapsed: (Boolean) -> Unit
    ) {
        val collapsingTitle = when (title) {
            "" -> {
                collapsingToolbarLayout.title.toString()
            }
            else -> {
                title!!
            }
        }
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            isCollapsed(
                when {
                    Math.abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                        collapsingToolbarLayout.title = collapsingTitle
                        toolbar?.let {
                            initToolbar(toolbar, null, false)
                        }
                        true
                    }
                    else -> {
                        collapsingToolbarLayout.title = " "
                        toolbar?.let {
                            initToolbar(toolbar, null, true)
                        }
                        false
                    }
                }
            )
        })
    }

    open fun handleFailureStatus(status: NetworkState, onShowMessage: (String) -> Unit) {
        hideProgress(status.tag)
        if (isAdded)
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

    private fun parseColor(colorCode: String): Int {
        var color = colorCode
        if (!colorCode.contains("#"))
            color = "#$colorCode"

        return Color.parseColor(color)
    }

    private fun setBaseStatusColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

}