package com.zrh.android.common.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.zrh.android.common.utils.ViewBindingHelper
import com.zrh.android.common.utils.onClick

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
abstract class BindingDialog<VB : ViewBinding>(context: Context, theme: Int) : Dialog(context, theme) {
    private var rootLayout: FrameLayout
    private var maskView: View
    private var binding: VB
    private var cancelable = true

    init {
        rootLayout = FrameLayout(context).apply {
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setContentView(this)
        }

        maskView = View(context).apply {
            layoutParams =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            background = ColorDrawable(Color.BLACK)
            alpha = getMaskAlpha()
            rootLayout.addView(this)
        }
        maskView.onClick {
            if (cancelable) dismiss()
        }

        binding = onCreateViewBinding(layoutInflater, rootLayout)
        rootLayout.addView(binding.root)

        window?.apply {
            val p = attributes
            p.width = WindowManager.LayoutParams.MATCH_PARENT
            p.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    protected fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup): VB {
        return ViewBindingHelper.binding(this, layoutInflater, parent, false)
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        cancelable = flag
    }

    override fun show() {
        super.show()
        maskView.startAnimation(getMaskInAnimation())
        binding.root.startAnimation(getContentInAnimation())
    }

    override fun dismiss() {
        maskView.startAnimation(getMaskOutAnimation())

        val listener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                superDismiss()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        }
        val animation = getContentOutAnimation()
        animation.setAnimationListener(listener)
        binding.root.startAnimation(animation)
    }

    protected fun superDismiss() = super.dismiss()

    protected open fun getAnimationDuration(): Long = 250

    protected open fun getMaskAlpha(): Float = 0.3f

    private fun getMaskInAnimation(): Animation {
        return AlphaAnimation(0.0f, 1f).apply {
            duration = getAnimationDuration()
        }
    }

    private fun getMaskOutAnimation(): Animation {
        return AlphaAnimation(1f, 0.0f).apply {
            duration = getAnimationDuration()
        }
    }

    protected open fun getContentInAnimation(): Animation {
        return AlphaAnimation(0.0f, 1f).apply {
            duration = getAnimationDuration()
        }
    }

    protected open fun getContentOutAnimation(): Animation {
        return AlphaAnimation(1f, 0.0f).apply {
            duration = getAnimationDuration()
        }
    }
}