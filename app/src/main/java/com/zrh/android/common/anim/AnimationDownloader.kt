package com.zrh.android.common.anim

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zrh.android.common.glide.GlideApp
import java.io.File

object AnimationDownloader {

    fun download(
        context: Context,
        url: String,
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onSuccess: (file: File) -> Unit
    ) {
        GlideApp.with(context).asFile()
            .listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    error: GlideException?,
                    p1: Any?,
                    p2: Target<File>,
                    p3: Boolean
                ): Boolean {
                    onError?.invoke(-1, "download error: $url")
                    return false
                }

                override fun onResourceReady(
                    file: File,
                    p1: Any,
                    p2: Target<File>?,
                    p3: DataSource,
                    p4: Boolean
                ): Boolean {
                    onSuccess(file)
                    return false
                }
            })
            .load(url)
            .submit()
    }

    fun downloadGif(
        context: Context,
        url: String,
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onSuccess: (drawable: GifDrawable) -> Unit
    ) {
        GlideApp.with(context).asGif()
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<GifDrawable>,
                    p3: Boolean
                ): Boolean {
                    onError?.invoke(-1, "download error: $url")
                    return false
                }

                override fun onResourceReady(
                    drawable: GifDrawable,
                    p1: Any,
                    p2: Target<GifDrawable>?,
                    p3: DataSource,
                    p4: Boolean
                ): Boolean {
                    onSuccess(drawable)
                    return false
                }

            })
            .load(url)
            .submit()
    }

    fun downloadBitmap(
        context: Context,
        url: String,
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onSuccess: (bitmap: Bitmap) -> Unit
    ) {
        GlideApp.with(context).asBitmap()
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<Bitmap>,
                    p3: Boolean
                ): Boolean {
                    onError?.invoke(-1, "download error: $url")
                    return false
                }

                override fun onResourceReady(
                    bitmap: Bitmap,
                    p1: Any,
                    p2: Target<Bitmap>?,
                    p3: DataSource,
                    p4: Boolean
                ): Boolean {
                    onSuccess(bitmap)
                    return false
                }

            })
            .load(url)
            .submit()
    }
}