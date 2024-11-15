package com.anim.core

import android.util.Log
import java.util.LinkedList

class AnimationQueue {
    private var mAnimView: AnimationView? = null
    private val mQueue = LinkedList<AnimResource>()
    private val mCallback = object : AnimationCallback {
        override fun onComplete() {
            play()
        }

        override fun onError(code: Int, message: String) {
            play()
        }
    }

    fun setView(view: AnimationView) {
        this.mAnimView = view
        view.setCallback(mCallback)
    }

    fun enqueue(resource: AnimResource) {
        mQueue.offer(resource)
        Log.d("AnimationQueueView", "enqueue: $resource")

        if (mAnimView != null && !mAnimView!!.isRunning()) {
            play()
        }
    }

    fun reset() {
        mAnimView?.stop()
        mQueue.clear()
    }

    private fun play() {
        if (mAnimView == null) return

        if (mQueue.isEmpty()) return

        val res = mQueue.pop()
        mAnimView?.start(res)
    }

    fun destroy() {
        mQueue.clear()
        mAnimView?.destroy()
    }
}