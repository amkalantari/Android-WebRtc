package com.axon.webrtc.ui.call

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.axon.webrtc.App
import com.axon.webrtc.BuildConfig
import com.axon.webrtc.R
import com.axon.webrtc.databinding.ActivityCallBinding
import com.axon.webrtc.ui.main.MainActivity
import com.axon.webrtc.ui.main.viewModel.MainViewModel
import com.axon.webrtc.ui.main.viewModel.MainViewModelFactory
import com.core.parent.ParentActivity
import me.axon.webrtc.SignalingSocket
import me.axon.webrtc.WebRtc
import me.axon.webrtc.rtcCallback.WebRtcCallBack
import me.axon.webrtc.signalCallback.RtcActivityCallBack
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.util.*
import javax.inject.Inject

class CallActivity : ParentActivity<MainViewModel, ActivityCallBinding>() {

    private var mRtcLocalRender: VideoRenderer.Callbacks? = null

    private var mRtcRemoteRender: VideoRenderer.Callbacks? = null

    @Inject
    lateinit var factory: MainViewModelFactory

    @Inject
    lateinit var signalingSocket: SignalingSocket

    private lateinit var webRtc: WebRtc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.glview.preserveEGLContextOnPause = true
        binding.glview.keepScreenOn = true
        VideoRendererGui.setView(binding.glview) {
            setRtcWrapper()
        }
        setVideoRender()

        binding.micButton.setOnClickListener {
            webRtc.enableAudio()
        }

        binding.videoButton.setOnClickListener {
            webRtc.enableVideo()
        }

        binding.endCallButton.setOnClickListener {
            try {
                signalingSocket.emit("exit", "yes")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            webRtc.exitSession()
            val intent = Intent(this@CallActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setVideoRender() {
        mRtcLocalRender = VideoRendererGui.create(
            0, 0, 100, 100,
            RendererCommon.ScalingType.SCALE_ASPECT_FILL, true
        )
        mRtcRemoteRender = VideoRendererGui.create(
            0, 0, 100, 100,
            RendererCommon.ScalingType.SCALE_ASPECT_FILL, false
        )
    }

    private fun setRtcWrapper() {
        webRtc = WebRtc.Builder(this)
            .socketListener(object : WebRtcCallBack {
                override fun onCreateOfferOrAnswer(type: String, sdp: String) {
                    try {
                        signalingSocket.emit(type, sdp)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onIceCandidate(label: Int, id: String, candidate: String) {
                    try {
                        val msg = JSONObject()
                        msg.put("source", signalingSocket.uid)
                        msg.put("target", signalingSocket.target)
                        msg.put("label", label)
                        msg.put("mid", id)
                        msg.put("candidate", candidate)
                        signalingSocket.emit("candidate", msg)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
            .eglContext(VideoRendererGui.getEglBaseContext())
            .serverUrl(BuildConfig.SOCKET_URL)
            .mRtcLocalRender(mRtcLocalRender)
            .mRtcRemoteRender(mRtcRemoteRender)
            .build()

        signalingSocket.setRtcActivity(webRtc, intent.extras!!.getString("status")!!, false,
            object : RtcActivityCallBack {
                override fun onDisconnect() {
                    runOnUiThread {
                        showMessage("can't connect to server")
                    }
                }

                override fun onExit() {
                    startActivity(Intent(this@CallActivity, MainActivity::class.java))
                    finish()
                }
            })

    }

    override fun getResourceLayoutId(): Int = R.layout.activity_call

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun inject() {
        (application as App).getDagger().inject(this)
    }

}
