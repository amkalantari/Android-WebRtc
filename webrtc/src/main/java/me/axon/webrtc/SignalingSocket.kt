package me.axon.webrtc

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.github.nkzawa.socketio.client.Socket
import me.axon.webrtc.dto.Agent
import me.axon.webrtc.signalCallback.ContactActivityCallBack
import me.axon.webrtc.signalCallback.RtcActivityCallBack
import me.axon.webrtc.signalCallback.SocketInterface

import me.axon.webrtc.wrapper.SocketWrapper
import java.util.*

/**
 * Created by aMiir on 9/29/21
 * Drunk, Fix Later
 */
class SignalingSocket(builder: Builder) : SocketWrapper(builder.socketIO), SocketInterface {

    private lateinit var mAlertDialogCall: AlertDialog
    private lateinit var mAlertDialogChat: AlertDialog
    private lateinit var context: Activity
    private var contactActivityCallBack: ContactActivityCallBack? = null
    private var rtcActivityCallBack: RtcActivityCallBack? = null
    private lateinit var webRtc: WebRtc

    init {
        connectToURL()
        initSocketHeartBeat()
        addListener(this)
    }

    fun setContactActivity(context: Activity, signalingCallBack: ContactActivityCallBack) {
        this.context = context
        this.contactActivityCallBack = signalingCallBack
        setCallAlertDialog()
        setChatAlertDialog()
    }

    fun setRtcActivity(webRtc: WebRtc, status: String,isChat:Boolean, rtcCallBack: RtcActivityCallBack) {
        this.rtcActivityCallBack = rtcCallBack
        this.webRtc = webRtc
        createOfferOrAck(status,isChat)
    }

    private fun createOfferOrAck(mCallStatus: String,isChat: Boolean) {
        if (mCallStatus == "send") {
            webRtc.createOffer()
        } else if (mCallStatus == "recv") {
            if (isChat)
            ackChat(true)
            else
                ack(true)
        }
    }

    private fun initSocketHeartBeat() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                keepAlive()
            }
        }, 0, 2000)
    }

    private fun setCallAlertDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.you_have_call_request))
        builder.setPositiveButton(context.getString(R.string.accept)) { _, _ ->
            contactActivityCallBack?.jumpToActivityCallBack("recv", false)
        }
        builder.setNegativeButton(context.getString(R.string.reject)) { _, _ ->
            ack(false)
        }
        mAlertDialogCall = builder.create()
    }

    private fun setChatAlertDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.you_have_chat_request))
        builder.setPositiveButton(context.getString(R.string.accept)) { _, _ ->
            contactActivityCallBack?.jumpToActivityCallBack("recv", true)
        }
        builder.setNegativeButton(context.getString(R.string.reject)) { _, _ ->
            ack(false)
        }
        mAlertDialogChat = builder.create()
    }

    override fun onUserAgentsUpdate(agents: ArrayList<Agent>) {
        contactActivityCallBack?.userListUpdate(agents)
    }

    override fun onDisConnect() {
        contactActivityCallBack?.onDisconnect()
        rtcActivityCallBack?.onDisconnect()
    }

    private fun processSignal(source: String, target: String, type: String, value: String) {
        if (target == uid) {
            if (type == "invite_call") {
                this.target = source
                context.runOnUiThread {
                    mAlertDialogCall.show()
                }
            }
            if (type == "invite_chat") {
                this.target = source
                context.runOnUiThread {
                    mAlertDialogChat.show()
                }
            }
            if (type == "ack_call") {
                if (value == "yes") {
                    contactActivityCallBack?.jumpToActivityCallBack("send", false)
                }
            }
            if (type == "ack_chat") {
                if (value == "yes") {
                    contactActivityCallBack?.jumpToActivityCallBack("send", true)
                }
            }
            if (type == "offer") {
                webRtc.setRemoteSdp(type, value)
                webRtc.createAnswer()
            }
            if (type == "answer") {
                webRtc.setRemoteSdp(type, value)
            }
            if (type == "exit") {
                webRtc.exitSession()
                rtcActivityCallBack?.onExit()
            }

        }
    }

    override fun onRemoteEventMsg(source: String, target: String, type: String, value: String) {
        processSignal(source, target, type, value)
    }

    override fun onRemoteCandidate(label: Int, mid: String, candidate: String) {
        webRtc.setCandidate(label, mid, candidate)
    }

    class Builder {

        lateinit var socketIO: Socket
            private set

        fun serverUrl(socketIO: Socket) =
            apply { this.socketIO = socketIO }

        fun build() = SignalingSocket(this)
    }

}