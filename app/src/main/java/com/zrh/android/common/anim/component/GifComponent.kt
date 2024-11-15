package com.zrh.android.common.anim.component

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.zrh.android.common.anim.AnimResource
import com.zrh.android.common.anim.AnimationComponent
import com.zrh.android.common.anim.AnimationDownloader
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable

class GifComponent : AnimationComponent(){
    private var mImageView: ImageView? = null
    private val animCallback = AnimationListener {
        Log.d("GifComponent", "loopCount: $it")
        if(mLoops <= 0) return@AnimationListener
        if (it == mLoops - 1){
            notifyComplete {
                val drawable = mImageView?.drawable as? GifDrawable
                drawable?.stop()
                mImageView?.isVisible = false
            }
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

        AnimationDownloader.download(
            imageView.context,
            resource.resourceUrl,
            onError = this::notifyError
        ) {
            val drawable = GifDrawable(it)
            runOnUiThread {
                imageView.setImageDrawable(drawable)
                drawable.loopCount = mLoops
                drawable.addAnimationListener(animCallback)
                drawable.start()
                mImageView?.isVisible = true
            }
        }
    }

    override fun onRestart(resource: AnimResource) {
        val drawable = mImageView?.drawable as? GifDrawable
        if (drawable == null){
            setRunning(false)
            return
        }
        drawable.reset()
        mImageView?.isVisible = true
    }

    override fun onStop() {
        val drawable = mImageView?.drawable as? GifDrawable
        drawable?.stop()
    }

    override fun onDestroy() {
        val drawable = mImageView?.drawable as? GifDrawable
        drawable?.let {
            it.removeAnimationListener(animCallback)
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