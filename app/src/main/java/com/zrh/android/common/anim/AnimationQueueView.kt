package com.zrh.android.common.anim

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import java.util.LinkedList

class AnimationQueueView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val mAnimView: AnimationView = AnimationView(context, attrs)

    private val mQueue = LinkedList<AnimResource>()

    init {
        addView(mAnimView)
        mAnimView.setCallback(object : AnimationCallback {
            override fun onComplete() {
                play()
            }

            override fun onError(code: Int, message: String) {
                play()
            }
        })
    }

    fun enqueue(resource: AnimResource) {
        mQueue.push(resource)
        if (!mAnimView.isRunning()) {
            play()
        }
    }

    private fun play() {
        if (mQueue.isEmpty()) return
        val res = mQueue.pop()
        mAnimView.start(res)
    }

    fun destroy() {
        mQueue.clear()
        mAnimView.destroy()
    }
}