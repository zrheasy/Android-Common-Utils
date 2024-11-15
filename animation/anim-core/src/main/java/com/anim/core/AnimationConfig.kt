package com.anim.core

import android.content.Context
import java.io.File

object AnimationConfig {

    private val mFactories = mutableMapOf<AnimationType, AnimationComponentFactory>()
    private var mDownloader: AnimationDownloader? = null

    fun registerFactory(type: AnimationType, factory: AnimationComponentFactory) {
        mFactories[type] = factory
    }

    fun registerDownloader(downloader: AnimationDownloader) {
        mDownloader = downloader
    }

    internal fun createComponent(type: AnimationType): AnimationComponent? {
        val factory = mFactories[type]
        if (factory != null) {
            return factory.createComponent()
        }
        return null
    }

    internal fun download(
        context: Context,
        url: String,
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onSuccess: (file: File) -> Unit
    ) {
        if (mDownloader == null) {
            onError?.invoke(400, "Downloader not found!")
            return
        }
        mDownloader!!.download(context, url, onError, onSuccess)
    }

}