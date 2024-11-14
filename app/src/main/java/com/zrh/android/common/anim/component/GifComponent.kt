package com.zrh.android.common.anim.component

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.zrh.android.common.anim.AnimResource
import com.zrh.android.common.anim.AnimationComponent
import com.zrh.android.common.anim.AnimationDownloader

class GifComponent : AnimationComponent() {
    private var mImageView: ImageView? = null
    private val animCallback = object : AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            mCallback?.onComplete()
            isRunning = false
        }
    }

    override fun attachToParent(parent: ViewGroup) {
        if (mImageView == null) {
            mImageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                parent.addView(this)
            }
        }
    }

    override fun start(resource: AnimResource) {
        val imageView = mImageView ?: return
        imageView.scaleType = mScaleType

        if (this.mResource == resource) {
            if (!isRunning) {
                val drawable = mImageView?.drawable as? GifDrawable
                drawable?.start()
                isRunning = true
            }
        } else {
            isRunning = true
            AnimationDownloader.downloadGif(
                imageView.context,
                resource.resourceUrl,
                onError = { code, msg ->
                    mHandler.post {
                        isRunning = false
                        mCallback?.onError(code, msg)
                    }
                }) {
                mHandler.post {
                    imageView.setImageDrawable(it)
                    it.setLoopCount(mLoops)
                    it.registerAnimationCallback(animCallback)
                    it.start()
                }
            }
        }
    }

    override fun stop() {
        isRunning = false
        val drawable = mImageView?.drawable as? GifDrawable
        drawable?.stop()
    }

    override fun destroy() {
        isRunning = false
        val drawable = mImageView?.drawable as? GifDrawable
        drawable?.let {
            it.unregisterAnimationCallback(animCallback)
            it.stop()
        }
        (mImageView?.parent as? ViewGroup)?.removeView(mImageView!!)
        mImageView?.setImageDrawable(null)
        mImageView = null

        mHandler.removeCallbacksAndMessages(null)
    }

    override fun getType(): String {
        return AnimResource.TYPE_GIF
    }
}