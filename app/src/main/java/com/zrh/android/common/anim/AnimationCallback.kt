package com.zrh.android.common.anim

interface AnimationCallback {
    fun onComplete()

    fun onError(code:Int, message:String)
}