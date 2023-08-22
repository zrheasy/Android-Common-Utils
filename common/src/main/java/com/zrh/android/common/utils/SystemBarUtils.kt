package com.zrh.android.common.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
object SystemBarUtils {

    fun setSystemBar(
        window: Window,
        isFitSystemBar: Boolean = false,
        statusBarColor: Int = Color.TRANSPARENT,
        navigationBarColor: Int = Color.TRANSPARENT,
        isDarkStatusFont: Boolean = true,
        isDarkNavigationFont: Boolean = true
    ) {
        WindowCompat.setDecorFitsSystemWindows(window, isFitSystemBar)
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = navigationBarColor
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isDarkStatusFont
            isAppearanceLightNavigationBars = isDarkNavigationFont
        }
    }

    // call after window attached
    fun getStatusBarHeight(window: Window): Int {
        val windowInsets: WindowInsetsCompat? = ViewCompat.getRootWindowInsets(window.decorView)
        return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.bottom ?: 0
    }

    // call after window attached
    fun getNavigationBarHeight(window: Window): Int {
        val windowInsets: WindowInsetsCompat? = ViewCompat.getRootWindowInsets(window.decorView)
        return windowInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
    }
}

fun Activity.getStatusBarHeight(): Int {
    return SystemBarUtils.getStatusBarHeight(window)
}

fun Activity.getNavigationBarHeight(): Int {
    return SystemBarUtils.getNavigationBarHeight(window)
}

fun Activity.setSystemBar(
    isFitSystemBar: Boolean = false,
    statusBarColor: Int = Color.TRANSPARENT,
    navigationBarColor: Int = Color.TRANSPARENT,
    isDarkStatusFont: Boolean = true,
    isDarkNavigationFont: Boolean = true
) {
    SystemBarUtils.setSystemBar(window, isFitSystemBar, statusBarColor, navigationBarColor, isDarkStatusFont, isDarkNavigationFont)
}

fun Dialog.setSystemBar(
    isFitSystemBar: Boolean = false,
    statusBarColor: Int = Color.TRANSPARENT,
    navigationBarColor: Int = Color.TRANSPARENT,
    isDarkStatusFont: Boolean = true,
    isDarkNavigationFont: Boolean = true
) {
    SystemBarUtils.setSystemBar(window!!, isFitSystemBar, statusBarColor, navigationBarColor, isDarkStatusFont, isDarkNavigationFont)
}

