package com.core.utils.glide

import android.content.Context
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.caverock.androidsvg.SVG
import java.io.InputStream


@GlideModule
class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry
            .register(
                SVG::class.java, PictureDrawable::class.java,
                SvgDrawableTranscoder()
            )
            .append(
                InputStream::class.java, SVG::class.java,
                SvgDecoder()
            )

//        registry.replace(
//            GlideUrl::class.java,
//            InputStream::class.java,
//            OkHttpUrlLoader.Factory(okHttpClient)
//        )
        super.registerComponents(context, glide, registry)
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions()
//            .fallback(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

        builder.setDefaultRequestOptions(requestOptions)

        super.applyOptions(context, builder)
    }

    // Disable manifest parsing to avoid adding similar modules twice.
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
