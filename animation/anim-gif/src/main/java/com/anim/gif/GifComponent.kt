package com.anim.gif

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.anim.core.AnimResource
import com.anim.core.AnimationComponent
import com.anim.core.AnimationType
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable
import java.io.File

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

        download(imageView.context, resource.resourceUrl)
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

    override fun onDownloadSuccess(file: File) {
        val drawable = GifDrawable(file)
        runOnUiThread {
            if (mImageView == null){
                setRunning(false)
                return@runOnUiThread
            }
            mImageView?.setImageDrawable(drawable)
            drawable.loopCount = mLoops
            drawable.addAnimationListener(animCallback)
            drawable.start()
            mImageView?.isVisible = true
        }
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

    override fun getType(): AnimationType {
        return AnimationType.GIF
    }
}