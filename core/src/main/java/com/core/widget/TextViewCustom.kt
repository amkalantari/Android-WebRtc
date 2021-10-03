package com.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.core.R
import com.core.utils.Utils
import java.util.*

class TextViewCustom : AppCompatTextView {

    private var isSquare = false
    private var confirmLength = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.TextViewCustom, defStyleAttr, 0)
        isSquare = typed.getBoolean(R.styleable.TextViewCustom_is_square, false)
        confirmLength = typed.getInt(R.styleable.TextViewCustom_confirm_length, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, (if (isSquare) widthMeasureSpec else heightMeasureSpec))
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (confirmLength > 0 && text?.length ?: 0 == confirmLength){
            setCompoundDrawablesRelative(null, null, ContextCompat.getDrawable(context, R.drawable.ic_confirm_lenght), null)
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    @SuppressLint("SimpleDateFormat")
    fun setDateTime(date: Date?) {
        date?.let {
            text = generateDate(it)
        }
    }

    fun setTimer(time: Long?) {
        time?.let {
            val stringBuilder = StringBuilder()
            text = stringBuilder.append(" ").append(Utils.convertMillisecondToTime(it))
                .append(" ثانیه")
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun generateDate(date: Date): String {
        return DateFormat.format("dd.MM.yyyy - HH:mm", date) as String
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