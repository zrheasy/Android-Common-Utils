package com.zrh.android.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

/**
 *
 * @author zrh
 * @date 2023/8/20
 *
 */

fun Context.dp2px(dp: Float): Int {
    return (resources.displayMetrics.density * dp).roundToInt()
}

fun Context.sp2px(sp: Float): Int {
    return (resources.displayMetrics.scaledDensity * sp).roundToInt()
}

fun Context.getScreenSize(): IntArray {
    val displayMetrics = DisplayMetrics()
    val wms = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wms.defaultDisplay.getRealMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels
    return intArrayOf(width, height)
}

fun Context.color(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.string(@StringRes id: Int, vararg args: Any): String {
    return getString(id, args)
}

fun Context.drawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Activity.getContentView(): View {
    return findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
}