package com.anim.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import java.io.File
import java.lang.ref.WeakReference

abstract class AnimationComponent {

    private val mHandler = Handler(Looper.getMainLooper())

    private var isRunning: Boolean = false
    private var mCallback: AnimationCallback? = null

    protected var mResource: AnimResource? = null
    protected var mLoops: Int = 1
    protected var mScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP

    fun isRunning(): Boolean = isRunning

    fun setRunning(running: Boolean) {
        isRunning = running
    }

    fun setLoops(loops: Int) {
        mLoops = loops
    }

    fun setScaleType(scaleType: ImageView.ScaleType) {
        mScaleType = scaleType
    }

    fun setCallback(callback: AnimationCallback?) {
        mCallback = callback
    }

    fun notifyError(code: Int, msg: String) {
        mHandler.post {
            Log.d("AnimationComponent", "Error: $code $msg $mResource")
            isRunning = false
            mCallback?.onError(code, msg)
        }
    }

    fun notifyComplete(action: () -> Unit = {}) {
        mHandler.post {
            Log.d("AnimationComponent", "onComplete: $mResource")
            isRunning = false
            mCallback?.onComplete()
            action()
        }
    }

    fun runOnUiThread(action: () -> Unit) {
        mHandler.post { action() }
    }

    abstract fun attachToParent(parent: ViewGroup)

    fun start(resource: AnimResource) {
        if (this.mResource == resource) {
            if (!isRunning) {
                isRunning = true
                onRestart(resource)
            }
        } else {
            mResource = resource
            isRunning = true
            onStart(resource)
        }
    }

    abstract fun onRestart(resource: AnimResource)

    abstract fun onStart(resource: AnimResource)

    fun stop() {
        isRunning = false
        onStop()
    }

    abstract fun onStop()

    fun destroy() {
        isRunning = false
        mHandler.removeCallbacksAndMessages(null)
        mCallback = null
        onDestroy()
    }

    abstract fun onDestroy()

    abstract fun getType(): AnimationType

    protected fun download(
        context: Context,
        url: String
    ) {
        AnimationConfig.download(
            context,
            url,
            SafeOnDownloadError(this),
            SafeOnDownloadSuccess(this)
        )
    }

    abstract fun onDownloadSuccess(file: File)
    open fun onDownloadFail(code: Int, msg: String){
        notifyError(code, msg)
    }
}

class SafeOnDownloadError(component: AnimationComponent) : OnDownloadError {
    private val weakRef = WeakReference(component)
    override fun invoke(code: Int, msg: String) {
        weakRef.get()?.onDownloadFail(code, msg)
    }
}

class SafeOnDownloadSuccess(component: AnimationComponent) : OnDownloadSuccess {
    private val weakRef = WeakReference(component)
    override fun invoke(file: File) {
        weakRef.get()?.onDownloadSuccess(file)
    }

}

