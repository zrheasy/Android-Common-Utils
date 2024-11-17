package com.anim.core

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * 一个聚合的动画组件，支持播放svga、vap、pag、gif
 */
class AnimationView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    // 位于底部的图片
    private var mImageView = ImageView(context)
    private var mComponent: AnimationComponent? = null

    private var mCallback: AnimationCallback? = null
    private var mLoops: Int = 1
    private var mClearsAfterDetached: Boolean = true
    private var mScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
    private var mFillMode: FillMode = FillMode.Clear

    private var assetsResource: AnimResource? = null

    init {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.AnimationView, 0, 0)

        typedArray.getString(R.styleable.AnimationView_scaleType)?.let { scaleType ->
            when (scaleType) {
                "0" -> mScaleType = ImageView.ScaleType.CENTER_CROP
                "1" -> mScaleType = ImageView.ScaleType.CENTER
                "2" -> mScaleType = ImageView.ScaleType.FIT_XY
            }
        }

        mLoops = typedArray.getInt(R.styleable.AnimationView_loopCount, 1)
        mClearsAfterDetached =
            typedArray.getBoolean(R.styleable.AnimationView_clearsAfterDetached, true)
        val source = typedArray.getString(R.styleable.AnimationView_source)

        if (source != null) {
            val type = typedArray.getString(R.styleable.AnimationView_type) ?: ""
            val resType = when (type) {
                "0" -> AnimationType.GIF
                "1" -> AnimationType.SVGA
                "2" -> AnimationType.PAG
                "3" -> AnimationType.VAP
                else -> getTypeBySuffix(source)
            }
            assetsResource = AnimResource(resType, "file:///android_asset/$source")
        }

        typedArray.recycle()

        addView(mImageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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

    fun setFillMode(fillMode: FillMode) {
        mFillMode = fillMode
    }

    fun setCallback(callback: AnimationCallback?) {
        mCallback = callback
    }

    fun setCallback(
        onLoading: () -> Unit = {},
        onStart: () -> Unit = {},
        onError: ((code: Int, msg: String) -> Unit)? = null,
        onComplete: () -> Unit = {},
    ) {
        setCallback(object : AnimationCallback {
            override fun onLoading() {
                onLoading.invoke()
            }

            override fun onStart() {
                onStart.invoke()
            }

            override fun onComplete() {
                onComplete.invoke()
            }

            override fun onError(code: Int, message: String) {
                onError?.invoke(code, message)
            }
        })
    }

    /**
     * 加载assets文件资源
     */
    fun startWithAssets(
        fileName: String?,
        type: AnimationType = AnimationType.SVGA,
        elements: List<AnimElement>? = null
    ) {
        var name = fileName ?: return
        if (!name.contains(".")) {
            val suffix = when (type) {
                AnimationType.GIF -> ".gif"
                AnimationType.SVGA -> ".svga"
                AnimationType.VAP -> ".mp4"
                AnimationType.PAG -> ".pag"
                else -> ""
            }
            name = "${name}$suffix"
        }
        val resource = AnimResource(type, "file:///android_asset/$name", elements)
        start(resource)
    }

    /**
     * 根据url后缀名推断类型
     */
    fun start(link: String?, elements: List<AnimElement>? = null) {
        val url = link ?: return
        val type = getTypeBySuffix(url)
        start(AnimResource(type, url, elements))
    }

    private fun getTypeBySuffix(url: String): AnimationType {
        return when {
            url.endsWith(".gif", ignoreCase = true) -> AnimationType.GIF
            url.endsWith(".svga", ignoreCase = true) -> AnimationType.SVGA
            url.endsWith(".mp4", ignoreCase = true) -> AnimationType.VAP
            url.endsWith(".pag") -> AnimationType.PAG
            else -> AnimationType.NONE
        }
    }

    fun start(resource: AnimResource) {
        mImageView.setImageDrawable(null)

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
        val component = AnimationManager.createComponent(type)
        component?.let {
            it.setLoops(mLoops)
            it.setScaleType(mScaleType)
            it.setCallback(mCallback)
            it.setFillMode(mFillMode)
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
        if (mClearsAfterDetached) clear()
    }

    fun stop() {
        mComponent?.stop()
    }

    fun setImageResource(resId: Int) {
        mComponent?.destroy()
        mImageView.setImageResource(resId)
    }

    fun getImageView(): ImageView = mImageView

    fun clear() {
        mComponent?.destroy()
        mComponent = null

        mCallback = null
    }

}

