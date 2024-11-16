package com.anim.svga

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextPaint
import android.util.TypedValue
import android.view.ViewGroup
import com.anim.core.AnimElement
import com.anim.core.AnimResource
import com.anim.core.AnimationComponent
import com.anim.core.AnimationConfig
import com.anim.core.AnimationType
import com.anim.core.ElementType
import com.anim.core.OnDownloadComplete
import com.opensource.svgaplayer.SVGACache
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.io.File
import java.io.FileInputStream
import java.lang.ref.WeakReference

class SvgaComponent : AnimationComponent(), SVGACallback {
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
            mPaint.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                12f,
                Resources.getSystem().displayMetrics
            )
        }
    }

    override fun onStart(resource: AnimResource) {
        if (mSvgaImageView == null) {
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
        download(svgaView.context, resource.resourceUrl)
        // 同时预加载占位图
        resource.elements?.filter { it.type == ElementType.IMAGE }?.forEach {
            AnimationConfig.download(svgaView.context, it.value) {}
        }
    }

    override fun onDownloadSuccess(file: File) {
        runOnUiThread {
            if (mSvgaImageView == null) {
                setRunning(false)
                return@runOnUiThread
            }
            internalStart(mSvgaImageView!!, file, mResource?.elements)
        }
    }

    override fun onRestart(resource: AnimResource) {
        if (mSvgaImageView == null) {
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
            SVGACache.buildCacheKey(file.path),
            callback = object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    val dynamicEntity = SVGADynamicEntity()
                    // 填充文字
                    elements?.filter { it.type == ElementType.TEXT }?.forEach {
                        dynamicEntity.setDynamicText(it.value, mPaint, it.key)
                    }
                    // 填充图片
                    val urls = elements?.filter { it.type == ElementType.IMAGE }?.map { it.value }
                    if (urls != null) {
                        // 有占位图则先下载占位图后再开启动画
                        AnimationConfig.downloadImages(
                            svgaView.context, urls, WeakOnSourceReady(
                                this@SvgaComponent,
                                elements,
                                videoItem,
                                dynamicEntity
                            )
                        )
                        return
                    }

                    onSourceReady(videoItem, dynamicEntity)
                }

                override fun onError() {
                    notifyError(-2, "svga play error")
                }
            },
            closeInputStream = true
        )
    }

    fun onSourceReady(videoItem: SVGAVideoEntity, dynamicEntity: SVGADynamicEntity) {
        runOnUiThread {
            mSvgaImageView?.setVideoItem(videoItem, dynamicEntity)
            mSvgaImageView?.startAnimation()
        }
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

    class WeakOnSourceReady(
        component: SvgaComponent,
        private val elements: List<AnimElement>?,
        private val videoItem: SVGAVideoEntity,
        private val dynamicEntity: SVGADynamicEntity
    ) : OnDownloadComplete {

        private val weakRef = WeakReference(component)
        override fun invoke(result: Map<String, Bitmap>) {
            val component = weakRef.get() ?: return

            val getKey = mutableMapOf<String, String>()
            elements?.forEach {
                getKey[it.value] = it.key
            }

            for (item in result) {
                dynamicEntity.setDynamicImage(item.value, getKey[item.key] ?: "")
            }

            component.onSourceReady(videoItem, dynamicEntity)
        }
    }
}