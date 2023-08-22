package com.zrh.android.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils

/**
 *
 * @author zrh
 * @date 2023/8/21
 *
 */
object ClipboardUtils {
    fun copyText(context: Context, text: String) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("Label", text))
    }

    fun getText(context: Context): String {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (manager.primaryClip != null && manager.primaryClip!!.itemCount > 0) {
            val text: CharSequence? = manager.primaryClip!!.getItemAt(0).text
            if (text != null) return text.toString()
        }
        return ""
    }

    fun clear(context: Context) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("", ""))
    }
}