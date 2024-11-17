package com.anim.vap

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.anim.core.AnimResource
import com.anim.core.AnimationComponent
import com.anim.core.AnimationManager
import com.anim.core.AnimationType
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.inter.IFetchResource
import com.tencent.qgame.animplayer.mix.Resource
import com.tencent.qgame.animplayer.util.ScaleType
import java.io.File
import java.lang.ref.WeakReference

class VapComponent : AnimationComponent(), IAnimListener {
    private var mVapView: AnimView? = null

    override fun attachToParent(parent: ViewGroup) {
        if (mVapView == null) {
            mVapView = AnimView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                parent.addView(this)
            }
        }
    }

    override fun onStart(resource: AnimResource) {
        if (mVapView == null) {
            setRunning(false)
            return
        }
        val vapView = mVapView!!
        vapView.setLoop(mLoops)
        when (mScaleType) {
            ImageView.ScaleType.CENTER_CROP -> vapView.setScaleType(ScaleType.CENTER_CROP)
            ImageView.ScaleType.CENTER -> vapView.setScaleType(ScaleType.FIT_CENTER)
            ImageView.ScaleType.FIT_XY -> vapView.setScaleType(ScaleType.FIT_XY)
            else -> {
                vapView.setScaleType(ScaleType.CENTER_CROP)
            }
        }
        vapView.setAnimListener(this)
        vapView.setFetchResource(SafeFetchSource(resource, vapView.context))

        download(vapView.context, resource.resourceUrl)
        notifyLoading()
    }

    override fun onRestart(resource: AnimResource) {
        if (mVapView == null) {
            setRunning(false)
            return
        }
        val vapView = mVapView!!
        download(vapView.context, resource.resourceUrl)
        notifyLoading()
    }

    override fun onDownloadSuccess(file: File) {
        runOnUiThread {
            if (mVapView == null) {
                setRunning(false)
                return@runOnUiThread
            }
            val vapView = mVapView!!
            vapView.startPlay(file)
            notifyStart()
        }
    }

    override fun onStop() {
        mVapView?.stopPlay()
    }

    override fun onDestroy() {
        mVapView?.setAnimListener(null)
        mVapView?.stopPlay()
        (mVapView?.parent as? ViewGroup)?.removeView(mVapView!!)
        mVapView = null
    }

    override fun getType(): AnimationType {
        return AnimationType.VAP
    }

    override fun onFailed(errorType: Int, errorMsg: String?) {
        notifyError(errorType, errorMsg ?: "vap play error")
    }

    override fun onVideoComplete() {
        notifyComplete()
    }

    override fun onVideoDestroy() {
        notifyComplete()
    }

    override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {

    }

    override fun onVideoStart() {

    }


    class SafeFetchSource(private val animResource: AnimResource, context: Context) :
        IFetchResource {
        private val weakRef = WeakReference(context)

        override fun fetchImage(resource: Resource, result: (Bitmap?) -> Unit) {
            val context = weakRef.get()
            if (context == null) {
                result.invoke(null)
                return
            }

            val image = animResource.elements?.find { resource.tag == it.key }
            if (image == null) {
                result.invoke(null)
            } else {
                AnimationManager.downloadImage(
                    context,
                    image.value,
                    onError = { _, _ -> result.invoke(null) },
                    onComplete = { result.invoke(it) }
                )
            }
        }

        override fun fetchText(resource: Resource, result: (String?) -> Unit) {
            val text = animResource.elements?.find { resource.tag == it.key }
            if (text == null) {
                result.invoke(null)
            } else {
                result.invoke(text.value)
            }
        }

        override fun releaseResource(resources: List<Resource>) {
            resources.forEach { it.bitmap?.recycle() }
        }
    }
}