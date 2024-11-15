package com.anim.core

interface AnimationCallback {
    fun onComplete()

    fun onError(code:Int, message:String)
}