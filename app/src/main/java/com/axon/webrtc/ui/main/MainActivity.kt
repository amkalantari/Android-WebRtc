package com.axon.webrtc.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.axon.webrtc.App
import com.axon.webrtc.R
import com.axon.webrtc.databinding.ActivityMainBinding
import com.axon.webrtc.ui.call.CallActivity
import com.axon.webrtc.ui.chat.ChatActivity
import com.axon.webrtc.ui.login.RegisterFragment.Companion.Login_Extra
import com.axon.webrtc.ui.main.viewModel.MainViewModel
import com.axon.webrtc.ui.main.viewModel.MainViewModelFactory
import com.core.parent.ParentActivity
import me.axon.webrtc.SignalingSocket
import me.axon.webrtc.signalCallback.ContactActivityCallBack
import me.axon.webrtc.dto.Agent
import java.util.*
import javax.inject.Inject

class MainActivity : ParentActivity<MainViewModel, ActivityMainBinding>() {

    @Inject
    lateinit var factory: MainViewModelFactory

    @Inject
    lateinit var signalingSocket: SignalingSocket

    private val adapter: RvAdapterUsers by lazy {
        RvAdapterUsers(callListener = {
            signalingSocket.target = it.id()
            signalingSocket.inviteCall()
        }, chatListener = {
            signalingSocket.target = it.id()
            signalingSocket.inviteChat()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signalingSocket.setContactActivity(this, object : ContactActivityCallBack {
            override fun userListUpdate(agents: ArrayList<Agent>) {
                adapter.submitList(mutableListOf())
                val temp = mutableListOf<Agent>()
                for (i in agents.indices) {
                    if (agents[i].id() != signalingSocket.uid) {
                        temp.add(agents[i])
                    }
                }
                adapter.submitList(temp)
            }

            override fun onDisconnect() {
                runOnUiThread {
                    showMessage(getString(R.string.server_connection_error))
                }
            }

            override fun jumpToActivityCallBack(status: String, isChat: Boolean) {
                val intent = if (isChat)
                    Intent(this@MainActivity, ChatActivity::class.java)
                else
                    Intent(this@MainActivity, CallActivity::class.java)

                intent.putExtra("status", status)
                startActivity(intent)
            }
        })

        binding.recyclerView.adapter = adapter

        if (intent.hasExtra(Login_Extra)) {
            signalingSocket.register(intent.getStringExtra(Login_Extra)!!)
        }

        signalingSocket.updateRemoteAgent()

    }

    override fun getResourceLayoutId(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun inject() {
        (application as App).getDagger().inject(this)
    }

    override fun showError(tag: String, error: String) {
        super.showError(tag, error)
        showMessage(error)
    }

}