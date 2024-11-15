package com.anim.svga

import android.graphics.Color
import android.text.TextPaint
import android.view.ViewGroup
import com.anim.core.AnimElement
import com.anim.core.AnimResource
import com.anim.core.AnimationComponent
import com.anim.core.AnimationType
import com.anim.core.ElementType
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.io.File
import java.io.FileInputStream

class SvgaComponent : AnimationComponent() , SVGACallback{
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

    override fun onStart(resource: AnimResource) {
        if (mSvgaImageView == null){
            setRunning(false)
            return
        }

        val svgaView = mSvgaImageView!!

        // 设置svga播放参数
        svgaView.loops = this@SvgaComponent.mLoops
        svgaView.scaleType = this@SvgaComponent.mScaleType
        svgaView.fillMode = SVGAImageView.FillMode.Clear

        // 监听回调
        svgaView.callback = this
        download(
            svgaView.context,
            resource.resourceUrl,
            onError = this::notifyError
        ) { file ->
            runOnUiThread {
                internalStart(svgaView, file, resource.elements)
            }
        }
    }

    override fun onRestart(resource: AnimResource) {
        if (mSvgaImageView == null){
            setRunning(false)
            return
        }

        val svgaView = mSvgaImageView!!
        svgaView.startAnimation()
    }

    private fun internalStart(svgaView: SVGAImageView, file: File, elements: List<AnimElement>?) {
        SVGAParser.shareParser().init(svgaView.context)
        SVGAParser.shareParser().decodeFromInputStream(
            FileInputStream(file),
            file.path,
            callback = object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    runOnUiThread {
                        val dynamicEntity = SVGADynamicEntity()
                        elements?.forEach {
                            when (it.type) {
                                ElementType.TEXT -> {
                                    dynamicEntity.setDynamicText(it.value, mPaint, it.key)
                                }
                                ElementType.IMAGE -> {
                                    dynamicEntity.setDynamicImage(it.value, it.key)
                                }
                            }
                        }
                        svgaView.setVideoItem(videoItem, dynamicEntity)
                        svgaView.startAnimation()
                    }
                }

                override fun onError() {
                    notifyError(-2, "svga play error")
                }
            },
            closeInputStream = true
        )
    }

    override fun onStop() {
        mSvgaImageView?.stopAnimation()
    }

    override fun onDestroy() {
        mSvgaImageView?.callback = null
        mSvgaImageView?.stopAnimation()
        mSvgaImageView?.clear()
        (mSvgaImageView?.parent as? ViewGroup)?.removeView(mSvgaImageView!!)
        mSvgaImageView = null
    }

    override fun getType(): AnimationType {
        return AnimationType.SVGA
    }

    override fun onFinished() {
        notifyComplete()
    }

    override fun onPause() {

    }

    override fun onRepeat() {

    }

    override fun onStep(frame: Int, percentage: Double) {

    }
}