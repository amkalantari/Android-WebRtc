package me.axon.webrtc.wrapper

import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import me.axon.webrtc.signalCallback.SocketInterface
import me.axon.webrtc.dto.Agent
import org.json.JSONException
import org.json.JSONObject
import java.util.*

open class SocketWrapper(var mSignaling: Socket?) {

    private var mMsgProcessor: MessageProcessor = MessageProcessor()
    private val mListeners: ArrayList<SocketInterface> = ArrayList()

    var uid: String? = null
    var target: String? = null

    @Throws(Throwable::class)
    protected fun finalize() {
        destroy()
    }

    fun connectToURL() {
        setEvent()
        mSignaling?.connect()
    }

    @Synchronized
    fun addListener(delegate: SocketInterface) {
        mListeners.add(delegate)
    }

    @Synchronized
    fun removeListener(delegate: SocketInterface) {
        mListeners.remove(delegate)
    }

    private fun destroy() {
        mSignaling?.disconnect()
        mSignaling?.close()
    }

    @Throws(JSONException::class)
    fun emit(type: String?, value: String?) {
        val msg = JSONObject()
        msg.put("source", uid)
        msg.put("target", target)
        msg.put("type", type)
        msg.put("value", value)
        mSignaling?.emit("event", msg)
    }

    @Throws(JSONException::class)
    fun emit(event: String?, `object`: JSONObject?) {
        mSignaling?.emit(event, `object`)
    }

    fun keepAlive() {
        if (uid != null) {
            try {
                val msg = JSONObject()
                msg.put("source", uid)
                msg.put("target", "EastRTC Server")
                msg.put("type", "heart")
                msg.put("value", "yes")
                mSignaling?.emit("heart", msg)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun updateRemoteAgent() {
        try {
            val msg = JSONObject()
            msg.put("source", uid)
            msg.put("target", target)
            msg.put("type", "request_user_list")
            msg.put("value", "yes")
            mSignaling?.emit("request_user_list", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun register(name: String?) {
        try {
            val msg = JSONObject()
            msg.put("name", name)
            msg.put("type", "Android")
            mSignaling?.emit("get_id", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun inviteCall() {
        try {
            val msg = JSONObject()
            msg.put("source", uid)
            msg.put("target", target)
            msg.put("type", "invite_call")
            msg.put("value", "yes")
            mSignaling?.emit("event", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun inviteChat() {
        try {
            val msg = JSONObject()
            msg.put("source", uid)
            msg.put("target", target)
            msg.put("type", "invite_chat")
            msg.put("value", "yes")
            mSignaling?.emit("event", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun ack(accept: Boolean) {
        try {
            val msg = JSONObject()
            msg.put("source", uid)
            msg.put("target", target)
            msg.put("type", "ack_call")
            if (accept) {
                msg.put("value", "yes")
            } else {
                msg.put("value", "no")
            }
            mSignaling?.emit("event", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun ackChat(accept: Boolean) {
        try {
            val msg = JSONObject()
            msg.put("source", uid)
            msg.put("target", target)
            msg.put("type", "ack_chat")
            if (accept) {
                msg.put("value", "yes")
            } else {
                msg.put("value", "no")
            }
            mSignaling?.emit("event", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setEvent() {
        mSignaling?.on("connect", mMsgProcessor.onConnect)
        mSignaling?.on("event", mMsgProcessor.onEvent(this@SocketWrapper))
        mSignaling?.on("candidate", mMsgProcessor.onCandidate(this@SocketWrapper))
        mSignaling?.on("set_id", mMsgProcessor.onGet)
        mSignaling?.on("user_list", mMsgProcessor.onResponse(this@SocketWrapper))
        mSignaling?.on("disconnect", mMsgProcessor.onDisconnect(this@SocketWrapper))
    }

    private fun verify(type: String) {
        try {
            val msg = JSONObject()
            msg.put("source", uid)
            msg.put("target", "EasyRTC Server")
            msg.put("type", type)
            mSignaling?.emit("ack", msg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private inner class MessageProcessor {

        val onConnect = Emitter.Listener { }

        fun onEvent(socketWrapper: SocketWrapper) = Emitter.Listener { args: Array<Any> ->
            val data = args[0] as JSONObject
            try {
                val type = data.getString("type")
                val source = data.getString("source")
                val target = data.getString("target")
                val value = data.getString("value")
                synchronized(socketWrapper) {
                    for (delegate in socketWrapper.mListeners) {
                        delegate.onRemoteEventMsg(source, target, type, value)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            verify("event")
        }

        fun onCandidate(socketWrapper: SocketWrapper) = Emitter.Listener { args: Array<Any> ->
            val data = args[0] as JSONObject
            try {
                val label = data.getInt("label")
                val mid = data.getString("mid")
                val candidate = data.getString("candidate")
                synchronized(socketWrapper) {
                    for (delegate in socketWrapper.mListeners) {
                        delegate.onRemoteCandidate(label, mid, candidate)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            verify("candidate")
        }

        val onGet = Emitter.Listener { args ->
            val data = args[0] as JSONObject
            try {
                val value = data.getString("value")
                uid = value
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            verify("set_id")
        }

        fun onResponse(socketWrapper: SocketWrapper) = Emitter.Listener { args: Array<Any> ->
            val data = args[0] as JSONObject
            try {
                val scnt = data.getString("cnt")
                val cnt = scnt.toInt()
                val list = ArrayList<Agent>()
                for (i in 0 until cnt) {
                    val `object` = data[i.toString()] as JSONObject
                    val id = `object`.getString("id")
                    val name = `object`.getString("name")
                    val remoteType = `object`.getString("type")
                    list.add(Agent(id, name, remoteType))
                }
                synchronized(socketWrapper) {
                    for (delegate in socketWrapper.mListeners) {
                        delegate.onUserAgentsUpdate(list)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            verify("user_list")
        }

        fun onDisconnect(socketWrapper: SocketWrapper) = Emitter.Listener {
            for (delegate in socketWrapper.mListeners) {
                delegate.onDisConnect()
            }
        }
    }

}