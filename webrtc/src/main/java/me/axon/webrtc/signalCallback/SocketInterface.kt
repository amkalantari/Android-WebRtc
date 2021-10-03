package me.axon.webrtc.signalCallback

import me.axon.webrtc.dto.Agent
import java.util.*

/**
 * Created by aMiir on 9/21/21
 * Drunk, Fix Later
 */
interface SocketInterface {

    fun onUserAgentsUpdate(agents: ArrayList<Agent>)

    fun onDisConnect()

    fun onRemoteEventMsg(source: String, target: String, type: String, value: String)

    fun onRemoteCandidate(label: Int, mid: String, candidate: String)

}