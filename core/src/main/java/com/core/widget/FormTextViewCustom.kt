package com.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.core.R
import com.core.databinding.LayoutFormTextViewCustomBinding

@SuppressLint("Recycle")
class FormTextViewCustom(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    var binding: LayoutFormTextViewCustomBinding

    init {
        context.obtainStyledAttributes(attrs, R.styleable.FormTextViewCustom, 0, 0).apply {
            try {
                val inflater = LayoutInflater.from(context)
                binding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.layout_form_text_view_custom,
                    this@FormTextViewCustom,
                    true
                )
                setTitle(
                    context.getString(
                        getResourceId(
                            R.styleable.FormTextViewCustom_titleText,
                            -1
                        )
                    )
                )
                descText(getString(R.styleable.FormTextViewCustom_descText).toString())
                setTitleColor(getColor(R.styleable.FormTextViewCustom_titleColor, 0))
                setDescColor(getColor(R.styleable.FormTextViewCustom_descColor, 0))
            } finally {
                recycle()
            }
        }
    }

    fun setTitle(text: String?) {
        text?.let {
            binding.titleTv.text = it
        }
    }

    fun descText(text: String?) {
        text?.let {
            binding.descriptionTv.text = it
        }
    }

    fun setTitleColor(colorId: Int) {
        binding.titleTv.setTextColor(colorId)
    }

    fun setDescColor(colorId: Int) {
        binding.descriptionTv.setTextColor(colorId)
    }

}