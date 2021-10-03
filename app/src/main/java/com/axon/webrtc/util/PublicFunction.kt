package com.axon.webrtc.util


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri


object PublicFunction {

    private const val CLIP_DATA_LABEL = "clipData"

    @JvmStatic
    fun copyToClipBoard(context: Context, text: CharSequence) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(CLIP_DATA_LABEL, text)
        clipboard.setPrimaryClip(clip)
    }

    @JvmStatic
    fun shareLinkDialog(context: Context, text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    @JvmStatic
    fun openUrlInBrowser(context: Context, url: String) {
        var webUrl = url
        if (!webUrl.startsWith("http://") && !webUrl.startsWith("https://"))
            webUrl = "http://$webUrl"

        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        context.startActivity(browserIntent)
    }

    @JvmStatic
    fun actionCall(context: Context, phoneNumber: String) {
        context.startActivity(
            Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", phoneNumber, null)
            )
        )
    }
}