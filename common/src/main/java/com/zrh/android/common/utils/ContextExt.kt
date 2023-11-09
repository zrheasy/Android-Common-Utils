package com.zrh.android.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
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
    val wms = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return intArrayOf(wms.maximumWindowMetrics.bounds.width(), wms.maximumWindowMetrics.bounds.height())
    }
    val displayMetrics = DisplayMetrics()
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

private var sToast: Toast? = null
fun Context.toast(text: CharSequence) {
    sToast?.cancel()
    sToast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).apply { show() }
}

fun Context.addSensorListener(type: Int, listener: SensorEventListener) {
    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor: Sensor = sensorManager.getDefaultSensor(type) ?: return
    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
}

fun Context.removeSensorListener(listener: SensorEventListener) {
    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sensorManager.unregisterListener(listener)
}