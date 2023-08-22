package com.zrh.android.common.widgets

import android.content.Context
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.viewbinding.ViewBinding

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
abstract class BottomDialog<VB : ViewBinding>(context: Context) : BindingDialog<VB>(context) {

    init {
        setCancelable(true)
    }

    override fun getContentInAnimation(): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
        ).apply {
            duration = getAnimationDuration()
        }
    }

    override fun getContentOutAnimation(): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
        ).apply {
            duration = getAnimationDuration()
        }
    }
}