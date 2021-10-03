package me.axon.webrtc

import android.content.Context
import me.axon.webrtc.rtcCallback.RtcListener
import me.axon.webrtc.rtcCallback.WebRtcCallBack
import me.axon.webrtc.wrapper.WebRTCWrapper
import org.webrtc.*

/**
 * Created by aMiir on 9/28/21
 * Drunk, Fix Later
 */
class WebRtc(
    builder: Builder,
    var context: Context
) : WebRTCWrapper(
    builder.serverUrl,
    builder.eglContext,
    builder.msgListener
), RtcListener {

    private var mRtcLocalRender: VideoRenderer.Callbacks? = builder.mRtcLocalRender

    private var mRtcRemoteRender: VideoRenderer.Callbacks? = builder.mRtcRemoteRender

    private var webRtcCallBack: WebRtcCallBack = builder.socketListener

    init {
        init(context, this)
    }

    override fun onLocalStream(mediaStream: MediaStream) {
        if (eglContext != null) {
            mediaStream.videoTracks[0].addRenderer(VideoRenderer(mRtcLocalRender))
            VideoRendererGui.update(
                mRtcLocalRender, 0, 50, 100, 50,
                RendererCommon.ScalingType.SCALE_ASPECT_FILL, true
            )
        }
    }

    override fun onAddRemoteStream(mediaStream: MediaStream) {
        if (eglContext != null) {
            mediaStream.videoTracks[0].addRenderer(VideoRenderer(mRtcRemoteRender))
            VideoRendererGui.update(
                mRtcRemoteRender, 0, 0, 100, 50,
                RendererCommon.ScalingType.SCALE_ASPECT_FILL, true
            )
            VideoRendererGui.update(
                mRtcLocalRender, 0, 50, 100, 50,
                RendererCommon.ScalingType.SCALE_ASPECT_FILL, true
            )
        }
    }

    override fun onRemoveRemoteStream(mediaStream: MediaStream) {
        if (eglContext != null) {
            VideoRendererGui.update(
                mRtcLocalRender, 0, 50, 100, 50,
                RendererCommon.ScalingType.SCALE_ASPECT_FILL, true
            )
        }
    }

    override fun onCreateOfferOrAnswer(type: String, sdp: String) {
        webRtcCallBack.onCreateOfferOrAnswer(type, sdp)
    }

    override fun onIceCandidate(label: Int, id: String, candidate: String) {
        webRtcCallBack.onIceCandidate(label, id, candidate)
    }

    class Builder(var context: Context) {
        lateinit var socketListener: WebRtcCallBack
            private set

        lateinit var serverUrl: String
            private set


        var mRtcLocalRender: VideoRenderer.Callbacks? = null
            private set

        var mRtcRemoteRender: VideoRenderer.Callbacks? = null
            private set

        var eglContext: EglBase.Context? = null
            private set

        var msgListener: ((String) -> Unit)? = null
            private set

        fun socketListener(socketListener: WebRtcCallBack) =
            apply { this.socketListener = socketListener }

        fun serverUrl(serverUrl: String) = apply { this.serverUrl = serverUrl }

        fun eglContext(eglContext: EglBase.Context?) = apply { this.eglContext = eglContext }

        fun msgListener(msgListener: ((String) -> Unit)) = apply { this.msgListener = msgListener }

        fun mRtcLocalRender(mRtcLocalRender: VideoRenderer.Callbacks?) = apply { this.mRtcLocalRender = mRtcLocalRender }
        fun mRtcRemoteRender(mRtcRemoteRender: VideoRenderer.Callbacks?) = apply { this.mRtcRemoteRender = mRtcRemoteRender }

        fun build() = WebRtc(this, context)
    }

}