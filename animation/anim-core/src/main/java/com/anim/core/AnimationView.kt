package com.anim.core

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * 一个聚合的动画组件，支持播放svga、vap、pag、gif
 */
class AnimationView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var mComponent: AnimationComponent? = null

    private var mCallback: AnimationCallback? = null
    private var mLoops: Int = 1
    private var mClearsAfterDetached: Boolean = true
    private var mScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP

    private var assetsResource: AnimResource? = null

    init {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.AnimationView, 0, 0)

        val scaleType = typedArray.getInt(R.styleable.AnimationView_scaleType, 0)
        when (scaleType) {
            0 -> mScaleType = ImageView.ScaleType.CENTER_CROP
            1 -> mScaleType = ImageView.ScaleType.CENTER
            2 -> mScaleType = ImageView.ScaleType.FIT_XY
        }

        mLoops = typedArray.getInt(R.styleable.AnimationView_loopCount, 1)
        mClearsAfterDetached =
            typedArray.getBoolean(R.styleable.AnimationView_clearsAfterDetached, true)
        val source = typedArray.getString(R.styleable.AnimationView_source)
        val type = typedArray.getInt(R.styleable.AnimationView_type, -1)

        if (source != null && type != -1) {
            val resType = when (type) {
                0 -> AnimationType.GIF
                1 -> AnimationType.SVGA
                2 -> AnimationType.PAG
                3 -> AnimationType.VAP
                else -> AnimationType.SVGA
            }
            assetsResource = AnimResource(resType, "file:///android_asset/$source")
        }

        typedArray.recycle()
    }

    fun setLoops(loops: Int) {
        this.mLoops = loops
    }

    fun setClearsAfterDetached(clearsAfterDetached: Boolean) {
        this.mClearsAfterDetached = clearsAfterDetached
    }

    fun setScaleType(scaleType: ImageView.ScaleType) {
        this.mScaleType = scaleType
    }

    fun setCallback(callback: AnimationCallback?) {
        mCallback = callback
    }

    /**
     * 加载assets文件资源
     */
    fun startWithAssets(name: String, type: AnimationType = AnimationType.SVGA) {
        val resource = AnimResource(type, "file:///android_asset/$name")
        start(resource)
    }

    /**
     * 根据url后缀名推断类型
     */
    fun start(url: String) {
        val type = when {
            url.endsWith(".gif") -> AnimationType.GIF
            url.endsWith(".svga") -> AnimationType.SVGA
            url.endsWith(".mp4") -> AnimationType.VAP
            url.endsWith(".pag") -> AnimationType.PAG
            else -> AnimationType.NONE
        }
        start(AnimResource(type, url))
    }

    fun start(resource: AnimResource) {

        if (mComponent == null) {
            mComponent = onCreateComponent(resource.type)
            mComponent?.attachToParent(this)
        } else if (resource.type != mComponent?.getType()) {
            mComponent?.destroy()
            mComponent = onCreateComponent(resource.type)
            mComponent?.attachToParent(this)
        }

        mComponent?.start(resource)
    }

    private fun onCreateComponent(type: AnimationType): AnimationComponent? {
        val component = AnimationConfig.createComponent(type)
        component?.let {
            it.setLoops(mLoops)
            it.setScaleType(mScaleType)
            it.setCallback(mCallback)
        }
        return component
    }

    fun isRunning(): Boolean = mComponent?.isRunning() ?: false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        assetsResource?.let { start(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mClearsAfterDetached) destroy()
    }

    fun stop() {
        mComponent?.stop()
    }

    fun destroy() {
        mComponent?.destroy()
        mComponent = null

        mCallback = null
    }

}

