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
            notifyComplete()
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

    override fun onStart(resource: AnimResource) {
        if (mImageView == null){
            setRunning(false)
            return
        }
        val imageView = mImageView!!
        imageView.scaleType = mScaleType

        AnimationDownloader.downloadGif(
            imageView.context,
            resource.resourceUrl,
            onError = this::notifyError
        ) {
            runOnUiThread {
                imageView.setImageDrawable(it)
                it.setLoopCount(mLoops)
                it.registerAnimationCallback(animCallback)
                it.start()
            }
        }
    }

    override fun onRestart(resource: AnimResource) {
        val drawable = mImageView?.drawable as? GifDrawable
        if (drawable == null){
            setRunning(false)
            return
        }
        drawable.start()
    }

    override fun onStop() {
        val drawable = mImageView?.drawable as? GifDrawable
        drawable?.stop()
    }

    override fun onDestroy() {
        val drawable = mImageView?.drawable as? GifDrawable
        drawable?.let {
            it.unregisterAnimationCallback(animCallback)
            it.stop()
        }
        (mImageView?.parent as? ViewGroup)?.removeView(mImageView!!)
        mImageView?.setImageDrawable(null)
        mImageView = null
    }

    override fun getType(): String {
        return AnimResource.TYPE_GIF
    }
}