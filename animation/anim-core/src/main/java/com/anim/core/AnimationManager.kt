package com.anim.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@SuppressLint("StaticFieldLeak")
object AnimationManager {

    private val mFactories = mutableMapOf<AnimationType, AnimationComponentFactory>()
    private var mDownloader: AnimationDownloader? = null

    private lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context
    }

    fun release() {
        mFactories.values.forEach { it.onRelease() }
    }

    fun registerFactory(type: AnimationType, factory: AnimationComponentFactory) {
        mFactories[type] = factory
        factory.onInit(mContext)
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

    fun download(
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

    fun downloadImage(
        context: Context,
        url: String,
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onComplete: (Bitmap) -> Unit,
    ) {
        download(context, url, onError) {
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeFile(it.path, options)
            onComplete(bitmap)
        }
    }

    fun downloadImages(
        context: Context,
        urls: List<String>,
        onComplete: (result: Map<String, Bitmap>) -> Unit
    ) {
        val result = ConcurrentHashMap<String, Bitmap>()
        val finishedCount = AtomicInteger(0)
        urls.forEach { url ->
            downloadImage(context, url, onError = { _, _ ->
                finishedCount.getAndIncrement()
                if (finishedCount.get() == urls.size) {
                    onComplete(result)
                }
            }) { bitmap ->

                result[url] = bitmap
                finishedCount.getAndIncrement()
                if (finishedCount.get() == urls.size) {
                    onComplete(result)
                }
            }
        }
    }
}

typealias OnDownloadError = (code: Int, msg: String) -> Unit
typealias OnDownloadSuccess = (file: File) -> Unit
typealias OnDownloadComplete = (result: Map<String, Bitmap>) -> Unit