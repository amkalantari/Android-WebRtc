package com.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.core.R
import com.core.utils.glide.GlideApp


class ImageViewCustom : AppCompatImageView {

    var isSquare = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.ImageViewCustom)
        val resource = typed.getInt(R.styleable.ImageViewCustom_imageResource, -1)
        isSquare = typed.getBoolean(R.styleable.ImageViewCustom_is_square, false)
        if (resource > -1) {
            setResource(resource)
        }
    }

    fun setImage(url: String, isRoundedCorners: Boolean = false) {
        val builder = GlideApp.with(context).load(url)
        if (isRoundedCorners) {
            builder.transform(CenterCrop(), RoundedCorners(10))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .into(this)
        } else {
            builder.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .into(this)
        }
    }

    fun setImage(@DrawableRes id: Int, isRoundedCorners: Boolean = false) {
        val builder = GlideApp.with(context).load(id)
        if (isRoundedCorners) {
            builder.transform(CenterCrop(), RoundedCorners(10))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .into(this)
        } else {
            builder.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .into(this)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, if (isSquare) widthMeasureSpec else heightMeasureSpec)
    }

    override fun setEnabled(enabled: Boolean) {
        alpha = when {
            enabled -> 1.0f
            else -> 0.5f
        }
        super.setEnabled(enabled)
    }

    private fun setResource(resource: Int) {
        //add placeholder in case resource is null
        this.setImageResource(resource)
    }

    companion object {

        @JvmStatic
        @BindingAdapter("image_url")
        fun ImageViewCustom.setImageUrl(value: String?) {
            if (!value.isNullOrEmpty())
                setImage(value)
        }

        @JvmStatic
        @BindingAdapter("circle_image_url")
        fun ImageViewCustom.setCircleImageUrl(value: String?) {
            if (!value.isNullOrEmpty()) {
                GlideApp.with(context).load(value).circleCrop().into(this)
            }
        }
    }
}