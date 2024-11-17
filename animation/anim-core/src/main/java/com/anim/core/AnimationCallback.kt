package com.anim.core

interface AnimationCallback {
    fun onLoading()

    fun onStart()

    fun onComplete()

    fun onError(code: Int, message: String)
}