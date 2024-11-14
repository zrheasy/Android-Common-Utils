package com.zrh.android.common.anim

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.zrh.android.common.anim.component.GifComponent
import com.zrh.android.common.anim.component.PagComponent
import com.zrh.android.common.anim.component.SVGAComponent
import com.zrh.android.common.anim.component.VapComponent
import com.zrh.android.common.utils.R

/**
 * 一个聚合的动画组件，包含svga、vap、pag三种组件
 */
class AnimationView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var mComponent: AnimationComponent? = null

    private var mCallback: AnimationCallback? = null
    private var mLoops: Int = 1
    private var mClearsAfterDetached: Boolean = false
    private var mScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP

    private var assetsResource: AnimResource? = null

    init {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.AnimationView, 0, 0)
        mLoops = typedArray.getInt(R.styleable.AnimationView_loopCount, 1)
        mClearsAfterDetached =
            typedArray.getBoolean(R.styleable.AnimationView_clearsAfterDetached, false)
        val source = typedArray.getString(R.styleable.AnimationView_source)
        val type = typedArray.getInt(R.styleable.AnimationView_type, -1)

        if (source != null && type != -1) {
            val resType = when (type) {
                0 -> AnimResource.TYPE_GIF
                1 -> AnimResource.TYPE_SVGA
                2 -> AnimResource.TYPE_PAG
                3 -> AnimResource.TYPE_VAP
                else -> AnimResource.TYPE_NONE
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

    private fun onCreateComponent(type: String): AnimationComponent? {
        val component = when (type) {
            AnimResource.TYPE_SVGA -> SVGAComponent()
            AnimResource.TYPE_GIF -> GifComponent()
            AnimResource.TYPE_VAP -> VapComponent()
            AnimResource.TYPE_PAG -> PagComponent()
            else -> null
        }
        component?.let {
            it.setLoops(mLoops)
            it.setScaleType(mScaleType)
            it.setCallback(mCallback)
        }
        return component
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        assetsResource?.let { start(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mClearsAfterDetached) clear()
    }

    fun clear() {
        mComponent?.destroy()
        mComponent = null

        mCallback = null
    }

}

