package com.zrh.android.common

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.anim.core.AnimationConfig
import com.anim.core.AnimationDownloader
import com.anim.core.AnimationType
import com.anim.gif.GifComponentFactory
import com.anim.pag.PagComponentFactory
import com.anim.svga.SvgaComponentFactory
import com.anim.vap.VapComponentFactory
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.didichuxing.doraemonkit.DoKit
import com.zrh.android.common.env.AppEnvManager
import com.zrh.android.common.glide.GlideApp
import com.zrh.android.common.utils.LocaleUtils
import com.zrh.android.common.utils.setSystemBar
import java.io.File

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
class App : Application(), ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()

        LocaleUtils.init(this, getSharedPreferences("setting", Context.MODE_PRIVATE))

        registerActivityLifecycleCallbacks(this)

        AppEnvManager.init(this, BuildConfig.DEBUG)

        DoKit.Builder(this)
            .alwaysShowMainIcon(false)
            .build()

        AnimationConfig.registerFactory(AnimationType.GIF, GifComponentFactory())
        AnimationConfig.registerFactory(AnimationType.SVGA, SvgaComponentFactory())
        AnimationConfig.registerFactory(AnimationType.VAP, VapComponentFactory())
        AnimationConfig.registerFactory(AnimationType.PAG, PagComponentFactory())
        AnimationConfig.registerDownloader(object : AnimationDownloader {
            override fun download(
                context: Context,
                url: String,
                onError: ((code: Int, msg: String) -> Unit)?,
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
                            Log.d("Downloader", "onError: $error")
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
                            Log.d("Downloader", "onSuccess: ${file.path}")
                            onSuccess(file)
                            return false
                        }

                    })
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA))
                    .load(url)
                    .submit()
            }
        })
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.setSystemBar()
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}