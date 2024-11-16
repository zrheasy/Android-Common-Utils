package com.anim.pag

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.anim.core.AnimResource
import com.anim.core.AnimationComponent
import com.anim.core.AnimationDownloader
import com.anim.core.AnimationType
import org.libpag.PAGImageView
import org.libpag.PAGScaleMode
import java.io.File

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

        download(pagView.context, resource.resourceUrl)
    }

    override fun onRestart(resource: AnimResource) {
        if (mPagView == null){
            setRunning(false)
            return
        }
        val pagView = mPagView!!
        download(pagView.context, resource.resourceUrl)
    }

    override fun onDownloadSuccess(file: File) {
        runOnUiThread {
            if (mPagView == null){
                setRunning(false)
                return@runOnUiThread
            }
            val pagView = mPagView!!
            pagView.setPath(file.path)
            pagView.play()
            pagView.isVisible = true
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

    override fun getType(): AnimationType {
        return AnimationType.PAG
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