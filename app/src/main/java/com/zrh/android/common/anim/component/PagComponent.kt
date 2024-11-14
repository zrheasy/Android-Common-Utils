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

    override fun start(resource: AnimResource) {
        val pagView = mPagView ?: return
        pagView.setRepeatCount(mLoops)
        pagView.setScaleMode(PAGScaleMode.LetterBox)
        pagView.addListener(this)

        // 资源相同并且已在播放则返回
        if (mResource == resource && isRunning){
            return
        }

        isRunning = true
        AnimationDownloader.download(pagView.context, resource.resourceUrl, onError = { code, msg ->
            mHandler.post {
                isRunning = false
                mCallback?.onError(code, msg)
            }
        }) {
            mHandler.post {
                pagView.setPath(it.path)
                pagView.play()
                pagView.isVisible = true
            }
        }
    }

    override fun stop() {
        isRunning = false
        mPagView?.pause()
    }

    override fun destroy() {
        isRunning = false
        mPagView?.pause()
        mPagView?.removeListener(this)
        (mPagView?.parent as? ViewGroup)?.removeView(mPagView!!)
        mPagView = null

        mHandler.removeCallbacksAndMessages(null)
    }

    override fun getType(): String {
        return AnimResource.TYPE_PAG
    }

    override fun onAnimationStart(p0: PAGImageView?) {
    }

    override fun onAnimationEnd(p0: PAGImageView?) {
        mHandler.post {
            isRunning = false
            mCallback?.onComplete()
            mPagView?.isVisible = false
        }
    }

    override fun onAnimationCancel(p0: PAGImageView?) {
        mHandler.post {
            isRunning = false
            mCallback?.onComplete()
            mPagView?.isVisible = false
        }
    }

    override fun onAnimationRepeat(p0: PAGImageView?) {

    }

    override fun onAnimationUpdate(p0: PAGImageView?) {

    }
}