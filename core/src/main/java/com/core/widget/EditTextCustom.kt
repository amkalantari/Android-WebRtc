package com.core.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatEditText

class EditTextCustom : AppCompatEditText {


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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