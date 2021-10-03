package com.core.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.core.R
import com.core.utils.Utils
import com.tuyenmonkey.mkloader.MKLoader


class ButtonCustom : RelativeLayout {

    private var loading: Boolean = false

    lateinit var loader: MKLoader

    lateinit var textView: TextViewCustom

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayout(context, attrs)
    }

    fun setText(text: CharSequence?) {
        textView.text = text
    }

    fun setTextColor(@ColorRes res: Int) {
        textView.setTextColor(ContextCompat.getColor(context, res))
    }

    fun setBackground(@DrawableRes res: Int) {
        textView.background = ContextCompat.getDrawable(context, res)
    }

    private fun initLayout(context: Context, attrs: AttributeSet) {
        val values = IntArray(4)
        val typed = context.obtainStyledAttributes(attrs, values)

        val size = Utils.dpToPx(context, 32f)

        textView = TextViewCustom(context, attrs)
        textView.id = 1.toInt()
        textView.gravity = Gravity.CENTER

        val typeface = ResourcesCompat.getFont(context, R.font.iranyekan_bold)
        textView.typeface = typeface

        textView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpToPx(context, 48f)).apply {
                this.addRule(CENTER_IN_PARENT, TRUE)
            }
        this.addView(textView)

        loader = MKLoader(context)
        loader.layoutParams = LayoutParams(size, size).apply {
            this.addRule(CENTER_HORIZONTAL, TRUE)
            this.addRule(CENTER_VERTICAL, TRUE)
        }

        this.addView(loader)

        setLoading()
        typed.recycle()
    }

    fun setLoading(isLoading: Boolean = false) {
        this.loading = isLoading
        loader.visibility = if (isLoading) {
            isEnabled = false
            View.VISIBLE
        } else {
            isEnabled = true
            View.GONE
        }
        if (isLoading) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
        }
    }

    fun setEnable(state: Boolean = true){
        if (state){
            isEnabled = true
            isClickable = true
            loader.visibility = View.GONE
            textView.visibility = View.VISIBLE
            textView.isEnabled = true
            textView.background = ContextCompat.getDrawable(context, R.drawable.background_button_common)
        }else{
            isEnabled = false
            isClickable = false
            loader.visibility = View.GONE
            textView.visibility = View.VISIBLE
            textView.isEnabled = false
            textView.background = ContextCompat.getDrawable(context, R.drawable.background_button_disable)
        }
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

    companion object {
        @JvmStatic
        @BindingAdapter("loading")
        fun ButtonCustom.showLoading(loading: Boolean? = false) {
            setLoading(loading ?: false)
        }

        @JvmStatic
        @BindingAdapter("enable")
        fun ButtonCustom.setEnable(state: Boolean? = true) {
            setEnable(state ?: true)
        }
    }
}