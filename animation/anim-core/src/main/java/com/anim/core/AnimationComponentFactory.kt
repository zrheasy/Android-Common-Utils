package com.anim.core

import android.content.Context

interface AnimationComponentFactory {
    fun createComponent(): AnimationComponent
    fun onInit(context: Context){}

    fun onRelease(){}
}