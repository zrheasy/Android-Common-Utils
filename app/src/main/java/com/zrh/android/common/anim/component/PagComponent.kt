package com.zrh.android.common.anim.component

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.zrh.android.common.anim.AnimResource
import com.zrh.android.common.anim.AnimationComponent
import com.zrh.android.common.anim.AnimationDownloader
import org.libpag.PAGImageView
import org.libpag.PAGScaleMode

class PagComponent : AnimationComponent(), PAGImageView.PAGImageViewListener {
    private var mPagView: PAGImageView? = null

    override fun attachToParent(parent: ViewGroup) {
        if (mPagView == null) {
            mPagView = PAGImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                parent.addView(this)
            }
        }
    }

    override fun onStart(resource: AnimResource) {
        if (mPagView == null){
            setRunning(false)
            return
        }
        val pagView = mPagView!!
        pagView.setRepeatCount(mLoops)
        pagView.setScaleMode(PAGScaleMode.LetterBox)
        pagView.addListener(this)

        AnimationDownloader.download(pagView.context, resource.resourceUrl, onError = this::notifyError) {
            runOnUiThread {
                pagView.setPath(it.path)
                pagView.play()
                pagView.isVisible = true
            }
        }
    }

    override fun onRestart(resource: AnimResource) {
        if (mPagView == null){
            setRunning(false)
            return
        }
        val pagView = mPagView!!
        AnimationDownloader.download(pagView.context, resource.resourceUrl, onError = this::notifyError) {
            runOnUiThread {
                pagView.setPath(it.path)
                pagView.play()
                pagView.isVisible = true
            }
        }
    }

    override fun onStop() {
        mPagView?.pause()
    }

    override fun onDestroy() {
        mPagView?.pause()
        mPagView?.removeListener(this)
        (mPagView?.parent as? ViewGroup)?.removeView(mPagView!!)
        mPagView = null
    }

    override fun getType(): String {
        return AnimResource.TYPE_PAG
    }

    override fun onAnimationStart(p0: PAGImageView?) {
    }

    override fun onAnimationEnd(p0: PAGImageView?) {
        notifyComplete {
            mPagView?.isVisible = false
        }
    }

    override fun onAnimationCancel(p0: PAGImageView?) {
        notifyComplete {
            mPagView?.isVisible = false
        }
    }

    override fun onAnimationRepeat(p0: PAGImageView?) {

    }

    override fun onAnimationUpdate(p0: PAGImageView?) {

    }
}