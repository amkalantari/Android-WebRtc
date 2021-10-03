package me.axon.webrtc.signalCallback

import me.axon.webrtc.dto.Agent

/**
 * Created by aMiir on 10/2/21
 * Drunk, Fix Later
 */
interface ContactActivityCallBack {
    fun userListUpdate(list: ArrayList<Agent>)
    fun onDisconnect()
    fun jumpToActivityCallBack(status : String, isChat:Boolean)
}