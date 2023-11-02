package com.zrh.android.common.widgets

import android.content.Context
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 *
 * @author zrh
 * @date 2023/10/31
 *
 */
class ExpandTextView(context: Context, attributeSet: AttributeSet?) : AppCompatTextView(context, attributeSet) {
    private val ellipsis = "..."
    private var content: String = ""
    private var expandText: String = ""
    private var expandTextColor: Int? = null

    fun setExpandText(text: String, color: Int? = null) {
        expandTextColor = color
        expandText = text
        setText(content)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val content = text?.toString() ?: ""

        if (width == 0) {
            post { setText(content) }
            return
        }

        val staticLayout =
            StaticLayout(content, paint, width, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, includeFontPadding)
        if (staticLayout.lineCount <= maxLines) {
            super.setText(content, type)
            return
        }
        val lineWith = staticLayout.getLineWidth(maxLines - 1)
        val replaceEnd = staticLayout.getLineEnd(maxLines - 1)
        var replaceStart = replaceEnd
        val endText = ellipsis + expandText
        val endTextWidth = paint.measureText(endText)
        var exceededWidth = lineWith + endTextWidth - width
        while (exceededWidth > 0) {
            replaceStart--
            val replaceText = content.substring(replaceStart, replaceEnd)
            exceededWidth -= paint.measureText(replaceText)
        }
        val displayText = content.substring(0, replaceStart-1) + endText

        if (expandTextColor != null) {
            val span = SpannableString(displayText)
            val start = displayText.indexOf(expandText)
            val end = start + expandText.length
            span.setSpan(ForegroundColorSpan(expandTextColor!!), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            super.setText(span, type)
        } else {
            super.setText(displayText, type)
        }
    }
}