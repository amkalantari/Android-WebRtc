package com.axon.webrtc.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.axon.webrtc.R
import com.axon.webrtc.databinding.FragmentRegisterBinding
import com.axon.webrtc.ui.login.viewModel.LoginViewModel
import com.axon.webrtc.ui.main.MainActivity
import com.axon.webrtc.util.KeyBoardHelper
import com.core.parent.ParentSharedFragment


class RegisterFragment : ParentSharedFragment<LoginViewModel, FragmentRegisterBinding>() {

    companion object {
        const val Login_Extra = "userName"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val keyBoard = KeyBoardHelper(requireActivity())

        binding.toolbar.title = getString(R.string.login_and_register)

        binding.textLayout.setOnClickListener {
            keyBoard.closeKeyBoard()
            finish()
            startActivity(
                Intent(requireActivity(), MainActivity::class.java).putExtra(
                    Login_Extra,
                    binding.phoneLayout.getText()
                )
            )
        }

        binding.phoneLayout.editText?.addTextChangedListener {
            binding.phoneLayout.error = null
        }

    }

    override fun showProgress(tag: String) {
        super.showProgress(tag)
        binding.textLayout.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
    }

    override fun hideProgress(tag: String) {
        super.hideProgress(tag)
        binding.textLayout.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
    }

    override fun showError(tag: String, error: String) {
        super.showError(tag, error)
        binding.phoneLayout.error = error
        binding.phoneLayout.boxStrokeErrorColor =
            ContextCompat.getColorStateList(requireContext(), R.color.red)
    }

    override fun getSkeletonLayoutId(): View? = null

    override fun getViewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java

    override fun getResourceLayoutId(): Int = R.layout.fragment_register

}