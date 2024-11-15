package com.zrh.android.common.anim.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        vapView.setFetchResource(object : IFetchResource {
            override fun fetchImage(res: Resource, result: (Bitmap?) -> Unit) {
                val image = resource.elements?.find { res.tag == it.key }
                if (image == null) {
                    result.invoke(null)
                } else {
                    AnimationDownloader.download(
                        vapView.context,
                        image.value,
                        onError = { _, _ -> result.invoke(null) }) {
                        val options = BitmapFactory.Options()
                        options.inScaled = false
                        result.invoke(BitmapFactory.decodeFile(it.path, options))
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

        AnimationDownloader.download(
            vapView.context,
            resource.resourceUrl,
            onError = this::notifyError
        ) {
            runOnUiThread { vapView.startPlay(it) }
        }
    }

    override fun onRestart(resource: AnimResource) {
        if (mVapView == null) {
            setRunning(false)
            return
        }
        val vapView = mVapView!!
        AnimationDownloader.download(
            vapView.context,
            resource.resourceUrl,
            onError = this::notifyError
        ) {
            runOnUiThread { vapView.startPlay(it) }
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

    override fun getType(): String {
        return AnimResource.TYPE_VAP
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
}