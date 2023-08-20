package com.zrh.android.common.utils

import android.content.Context
import android.graphics.Typeface
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

/**
 *
 * @author zrh
 * @date 2023/8/20
 *
 */

fun View.onClick(interval: Long = 300, onClick: () -> Unit) {
    setOnClickListener {
        val tagId = 0x777
        val now = System.currentTimeMillis()
        val lastClickTime = getTag(tagId) as? Long ?: 0
        if (lastClickTime - now > interval) {
            setTag(tagId, now)
            onClick()
        }
    }
}

fun View.measure(lp: ViewGroup.LayoutParams = layoutParams): Array<Int> {
    measure(measureWidth(lp, context), measureHeight(lp, context))
    return arrayOf(measuredWidth, measuredHeight)
}

private fun measureWidth(lp: ViewGroup.LayoutParams, context: Context): Int {
    if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
        return View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
    } else if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
        val screenWidth = context.getScreenSize()[0]
        return View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY)
    }
    return View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY)
}

private fun measureHeight(lp: ViewGroup.LayoutParams, context: Context): Int {
    if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
        return View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
    } else if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
        val screenHeight = context.getScreenSize()[1]
        return View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.EXACTLY)
    }
    return View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY)
}

fun TextView.setTextBold() {
    this.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
}

fun TextView.setTextNormal() {
    this.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
}

fun EditText.isPassword(): Boolean = transformationMethod is PasswordTransformationMethod

fun EditText.setPassword(isPassword: Boolean) {
    transformationMethod = if (isPassword) {
        PasswordTransformationMethod.getInstance()
    } else {
        HideReturnsTransformationMethod.getInstance()
    }
}
