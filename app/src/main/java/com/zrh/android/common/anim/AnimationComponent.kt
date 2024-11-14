package com.zrh.android.common.anim

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView

abstract class AnimationComponent {

    protected val mHandler = Handler(Looper.getMainLooper())

    protected var mResource: AnimResource? = null
    protected var isRunning: Boolean = false
    protected var mCallback: AnimationCallback? = null
    protected var mLoops: Int = 1
    protected var mScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP

    fun setLoops(loops: Int) {
        mLoops = loops
    }

    fun setScaleType(scaleType: ImageView.ScaleType) {
        mScaleType = scaleType
    }

    fun setCallback(callback: AnimationCallback?) {
        mCallback = callback
    }

    abstract fun attachToParent(parent:ViewGroup)

    abstract fun start(resource:AnimResource)

    abstract fun stop()

    abstract fun destroy()

    abstract fun getType():String
}