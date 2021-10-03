package me.axon.webrtc.dto

class Agent(private val mID: String, private val mName: String, private val mType: String) {
    fun id(): String {
        return mID
    }

    fun name(): String {
        return mName
    }

    fun type(): String {
        return mType
    }

}