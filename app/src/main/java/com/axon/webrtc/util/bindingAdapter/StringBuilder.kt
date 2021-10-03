package com.axon.webrtc.util.bindingAdapter

operator fun StringBuilder.plus(text: String?) {
    append(text)
}