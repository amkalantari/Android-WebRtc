package me.axon.webrtc.rtcCallback

import org.webrtc.MediaStream

/**
 * Created by aMiir on 9/21/21
 * Drunk, Fix Later
 */

interface RtcListener {
    fun onLocalStream(mediaStream: MediaStream)

    fun onAddRemoteStream(mediaStream: MediaStream)

    fun onRemoveRemoteStream(mediaStream: MediaStream)

    fun onCreateOfferOrAnswer(type: String, sdp: String)

    fun onIceCandidate(label: Int, id: String, candidate: String)
}