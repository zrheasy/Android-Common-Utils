package com.zrh.android.common.anim.component

import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.inter.IFetchResource
import com.tencent.qgame.animplayer.mix.Resource
import com.tencent.qgame.animplayer.util.ScaleType
import com.zrh.android.common.anim.AnimResource
import com.zrh.android.common.anim.AnimationComponent
import com.zrh.android.common.anim.AnimationDownloader

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

    override fun start(resource: AnimResource) {
        val vapView = mVapView ?: return
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
        vapView.setFetchResource(object : IFetchResource {
            override fun fetchImage(res: Resource, result: (Bitmap?) -> Unit) {
                val image = resource.elements?.find { res.tag == it.key }
                if (image == null) {
                    result.invoke(null)
                } else {
                    AnimationDownloader.downloadBitmap(
                        vapView.context,
                        image.value,
                        onError = { _, _ ->
                            mHandler.post { result.invoke(null) }
                        }) {
                        mHandler.post { result.invoke(it) }
                    }
                }
            }

            override fun fetchText(res: Resource, result: (String?) -> Unit) {
                val text = resource.elements?.find { res.tag == it.key }
                if (text == null) {
                    result.invoke(null)
                } else {
                    result.invoke(text.value)
                }
            }

            override fun releaseResource(list: List<Resource>) {
                list.forEach { it.bitmap?.recycle() }
            }

        })

        // 资源相同并且已在播放则返回
        if (mResource == resource && isRunning){
            return
        }

        isRunning = true
        AnimationDownloader.download(vapView.context, resource.resourceUrl, onError = { code, msg ->
            mHandler.post {
                isRunning = false
                mCallback?.onError(code, msg)
            }
        }) {
            mHandler.post { vapView.startPlay(it) }
        }
    }

    override fun stop() {
        isRunning = false
        mVapView?.stopPlay()
    }

    override fun destroy() {
        isRunning = false

        mVapView?.setAnimListener(null)
        mVapView?.stopPlay()
        (mVapView?.parent as? ViewGroup)?.removeView(mVapView!!)
        mVapView = null

        mHandler.removeCallbacksAndMessages(null)
    }

    override fun getType(): String {
        return AnimResource.TYPE_VAP
    }

    override fun onFailed(errorType: Int, errorMsg: String?) {
        mHandler.post {
            isRunning = false
            mCallback?.onError(errorType, errorMsg ?: "play error")
        }
    }

    override fun onVideoComplete() {
        mHandler.post {
            isRunning = false
            mCallback?.onComplete()
        }
    }

    override fun onVideoDestroy() {
        mHandler.post {
            isRunning = false
            mCallback?.onComplete()
        }
    }

    override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {

    }

    override fun onVideoStart() {

    }
}