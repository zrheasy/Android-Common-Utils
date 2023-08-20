package com.zrh.android.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 *
 * @author zrh
 * @date 2023/8/20
 *
 */
object KeyboardHelper {

    const val KEYBOARD_VISIBLE_THRESHOLD_DP = 100

    fun showKeyboard(editText: EditText, delay: Int = 0) {
        if (delay > 0) {
            editText.postDelayed({ showKeyboard(editText) }, delay.toLong())
        } else {
            showKeyboard(editText)
        }
    }

    private fun showKeyboard(editText: EditText) {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun isKeyboardVisible(activity: Activity): Boolean {
        val r = Rect()
        val contentView = activity.getContentView()
        contentView.getWindowVisibleDisplayFrame(r)

        val thresholds = activity.dp2px(KEYBOARD_VISIBLE_THRESHOLD_DP.toFloat())
        val heightDiff = contentView.rootView.height - r.height()

        return heightDiff > thresholds
    }

    fun monitor(activity: Activity, listener: KeyboardVisibilityListener): KeyboardVisibilityMonitor {
        return KeyboardVisibilityMonitor(activity, listener)
    }
}

class KeyboardVisibilityMonitor(activity: Activity, private val listener: KeyboardVisibilityListener) :
    ViewTreeObserver.OnGlobalLayoutListener {
    private val contentView = activity.getContentView()
    private var isVisible = KeyboardHelper.isKeyboardVisible(activity)
    private val thresholds = activity.dp2px(KeyboardHelper.KEYBOARD_VISIBLE_THRESHOLD_DP.toFloat())

    init {
        contentView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    fun dispose() {
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val r = Rect()
        contentView.getWindowVisibleDisplayFrame(r)
        val heightDiff = contentView.rootView.height - r.height()
        val isKeyboardVisible = heightDiff > thresholds
        if (isKeyboardVisible == isVisible) return
        isVisible = isKeyboardVisible
        if (isVisible) {
            listener.onShow(heightDiff)
        } else {
            listener.onHide()
        }
    }
}

interface KeyboardVisibilityListener {
    fun onShow(height: Int)
    fun onHide()
}