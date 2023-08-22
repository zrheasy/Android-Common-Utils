package com.zrh.android.common

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.zrh.android.common.utils.databinding.DialogBottomBinding
import com.zrh.android.common.utils.databinding.DialogTextBinding
import com.zrh.android.common.widgets.BindingDialog

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
class BottomTextDialog(context: Context) : BindingDialog<DialogBottomBinding>(context, R.style.DefaultDialog) {

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