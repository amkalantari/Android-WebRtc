package com.axon.webrtc.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.axon.webrtc.App
import com.axon.webrtc.BuildConfig
import com.axon.webrtc.R
import com.axon.webrtc.databinding.ActivityChatBinding
import com.axon.webrtc.ui.main.MainActivity
import com.core.parent.ParentActivity
import me.axon.webrtc.SignalingSocket
import me.axon.webrtc.WebRtc
import me.axon.webrtc.rtcCallback.RtcListener
import me.axon.webrtc.rtcCallback.WebRtcCallBack
import me.axon.webrtc.signalCallback.RtcActivityCallBack
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * Created by aMiir on 9/21/21
 * Drunk, Fix Later
 */
class ChatActivity : ParentActivity<ChatViewModel, ActivityChatBinding>() {

    private val adapter: RvAdapterChat by lazy {
        RvAdapterChat()
    }

    @Inject
    lateinit var factory: ChatViewModelFactory

    @Inject
    lateinit var socketWrapper: SignalingSocket

    private lateinit var webRtc: WebRtc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recyclerView.adapter = adapter

        binding.toolbar.title = getString(R.string.chat_title)
        binding.toolbar.setNavigationIcon(com.core.R.drawable.ic_back_black)
        binding.toolbar.setNavigationOnClickListener {
            onBackClicked()
        }

        binding.sendImageButton.setOnClickListener {
            val text = binding.responseEditText.text.toString()
            val success = webRtc.sendMessage(text)
            if (success) {
                adapter.setData(MsgDto(text, true))
                binding.responseEditText.setText("")
            }
        }

        setRtcWrapper()

    }

    private fun onBackClicked() {
        try {
            socketWrapper.emit("exit", "yes")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webRtc.exitSession()
        val intent = Intent(this@ChatActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun setRtcWrapper() {
        webRtc = WebRtc.Builder(this).socketListener(object : WebRtcCallBack {
            override fun onCreateOfferOrAnswer(type: String, sdp: String) {
                try {
                    socketWrapper.emit(type, sdp)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onIceCandidate(label: Int, id: String, candidate: String) {
                try {
                    val msg = JSONObject()
                    msg.put("source", socketWrapper.uid)
                    msg.put("target", socketWrapper.target)
                    msg.put("label", label)
                    msg.put("mid", id)
                    msg.put("candidate", candidate)
                    socketWrapper.emit("candidate", msg)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        }).serverUrl(BuildConfig.SOCKET_URL).msgListener {
            runOnUiThread {
                adapter.setData(MsgDto(it, false))
            }
        }.build()

        socketWrapper.setRtcActivity(
            webRtc,
            intent.extras!!.getString("status")!!,
            true,
            object : RtcActivityCallBack {
                override fun onDisconnect() {
                    runOnUiThread {
                        showMessage("can't connect to server")
                    }
                }

                override fun onExit() {

                }
            })

    }

    override fun inject() {
        (application as App).getDagger().inject(this)
    }

    override fun getResourceLayoutId(): Int = R.layout.activity_chat

    override fun getViewModelClass(): Class<ChatViewModel> = ChatViewModel::class.java

    override fun getFactory(): ViewModelProvider.Factory = factory
}