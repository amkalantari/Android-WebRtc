package me.axon.webrtc.rtcCallback

/**
 * Created by aMiir on 10/2/21
 * Drunk, Fix Later
 */
interface WebRtcCallBack {

    fun onCreateOfferOrAnswer(type: String, sdp: String)

    fun onIceCandidate(label: Int, id: String, candidate: String)

}