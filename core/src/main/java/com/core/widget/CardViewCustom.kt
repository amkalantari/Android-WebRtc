package com.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.cardview.widget.CardView

class CardViewCustom : CardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setEnabled(enabled: Boolean) {
        alpha = when {
            enabled -> 1.0f
            else -> 0.5f
        }
        super.setEnabled(enabled)
    }

    var listener: OnClickListener? = null

    fun setOnDelayClickListener(delayListener: ((View) -> Unit)? = null) {
        if (delayListener == null) {
            this.setOnClickListener(null)
        } else {
            this.setOnDelayClickListener(delayListener, 1000)
        }
    }

    fun setOnDelayClickListener(delayListener: (View) -> Unit, delay: Long) {
        this.listener = OnClickListener { view ->
            this.setOnClickListener(null)
            delayListener(view)
            Handler(Looper.getMainLooper()).postDelayed({
                this.setOnClickListener(listener)
            }, delay)
        }
        this.setOnClickListener(listener)
    }
}