package com.zrh.android.common.anim.component

import android.graphics.Color
import android.text.TextPaint
import android.view.ViewGroup
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.zrh.android.common.anim.AnimElement
import com.zrh.android.common.anim.AnimResource
import com.zrh.android.common.anim.AnimationComponent
import com.zrh.android.common.anim.AnimationDownloader
import java.io.File
import java.io.FileInputStream

class SVGAComponent : AnimationComponent() , SVGACallback{
    private var mSvgaImageView: SVGAImageView? = null
    private lateinit var mPaint: TextPaint

    override fun attachToParent(parent: ViewGroup) {
        if (mSvgaImageView == null) {
            mSvgaImageView = SVGAImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                parent.addView(this)
            }


            mPaint = TextPaint()
            mPaint.color = Color.WHITE
            mPaint.isAntiAlias = true
            mPaint.textSize = 14f
        }
    }

    override fun start(resource: AnimResource) {
        val svgaView = mSvgaImageView ?: return

        // 设置svga播放参数
        svgaView.loops = this@SVGAComponent.mLoops
        svgaView.scaleType = this@SVGAComponent.mScaleType
        svgaView.fillMode = SVGAImageView.FillMode.Clear

        // 监听回调
        svgaView.callback = this

        if (this.mResource == resource) {
            if (!isRunning) {
                svgaView.startAnimation()
                isRunning = true
            }
        } else {
            isRunning = true
            AnimationDownloader.download(
                svgaView.context,
                resource.resourceUrl,
                onError = { code, msg ->
                    mHandler.post {
                        isRunning = false
                        mCallback?.onError(code, msg)
                    }
                }
            ) { file ->
                mHandler.post {
                    internalStart(svgaView, file, resource.elements)
                }
            }
        }
    }

    private fun internalStart(svgaView: SVGAImageView, file: File, elements: List<AnimElement>?) {
        SVGAParser.shareParser().init(svgaView.context)
        SVGAParser.shareParser().decodeFromInputStream(
            FileInputStream(file),
            file.path,
            callback = object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    mHandler.post {
                        val dynamicEntity = SVGADynamicEntity()
                        elements?.forEach {
                            when (it.type) {
                                AnimResource.ELEMENT_TEXT -> {
                                    dynamicEntity.setDynamicText(it.value, mPaint, it.key)
                                }
                                AnimResource.ELEMENT_IMAGE -> {
                                    dynamicEntity.setDynamicImage(it.value, it.key)
                                }
                            }
                        }
                        svgaView.setVideoItem(videoItem, dynamicEntity)
                        svgaView.startAnimation()
                    }
                }

                override fun onError() {
                    mHandler.post {
                        isRunning = false
                        mCallback?.onError(-2, "svga play error")
                    }
                }
            },
            closeInputStream = true
        )
    }

    override fun stop() {
        isRunning = false
        mSvgaImageView?.stopAnimation()
    }

    override fun destroy() {
        isRunning = false

        mSvgaImageView?.callback = null
        mSvgaImageView?.stopAnimation()
        mSvgaImageView?.clear()
        (mSvgaImageView?.parent as? ViewGroup)?.removeView(mSvgaImageView!!)
        mSvgaImageView = null

        mHandler.removeCallbacksAndMessages(null)
    }

    override fun getType(): String {
        return AnimResource.TYPE_SVGA
    }

    override fun onFinished() {
        mHandler.post {
            isRunning = false
            mCallback?.onComplete()
        }
    }

    override fun onPause() {

    }

    override fun onRepeat() {

    }

    override fun onStep(frame: Int, percentage: Double) {

    }
}