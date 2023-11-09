package com.zrh.android.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.zrh.android.common.R
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body

/**
 *
 * @author zrh
 * @date 2023/8/20
 *
 */

fun View.onClick(interval: Long = 300, onClick: () -> Unit) {
    setOnClickListener {
        val tagId = R.id.view_click
        val now = System.currentTimeMillis()
        val lastClickTime = getTag(tagId) as? Long ?: 0
        if (now - lastClickTime > interval) {
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

@SuppressLint("WrongConstant")
fun TextView.setExpandText(
    content: String,
    maxLine: Int,
    expandText: String,
    expandTextColor: Int? = null,
    onExpandClick: (() -> Unit)? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
    }

    val ellipsis = "..."
    post {
        val staticLayout =
            StaticLayout(content, paint, width, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, includeFontPadding)
        if (staticLayout.lineCount <= maxLine) {
            text = content
            return@post
        }
        val lineWith = staticLayout.getLineWidth(maxLine - 1)
        var replaceStart = staticLayout.getLineEnd(maxLine - 1)
        val endText = ellipsis + expandText
        val endTextWidth = paint.measureText(endText)
        var exceededWidth = lineWith + endTextWidth - width
        while (exceededWidth > 0) {
            replaceStart--
            val replaceText = content.substring(replaceStart, replaceStart+1)
            exceededWidth -= paint.measureText(replaceText)
        }
        val displayText = content.substring(0, replaceStart) + endText
        val displaySpan = SpannableString(displayText)
        if (expandTextColor != null) {
            val start = displayText.indexOf(expandText)
            val end = start + expandText.length
            displaySpan.setSpan(ForegroundColorSpan(expandTextColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (onExpandClick != null) {
            val clickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onExpandClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }
            val start = displayText.indexOf(expandText)
            val end = start + expandText.length
            displaySpan.setSpan(clickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            movementMethod = LinkMovementMethod.getInstance()
        }
        text = displaySpan
    }
}

@SuppressLint("WrongConstant")
fun TextView.setCloseText(content: String, closeText: String, closeTextColor: Int? = null, onCloseClick: (() -> Unit)? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
    }
    post {
        val displayText = "$content $closeText"
        val displaySpan = SpannableString(displayText)
        if (closeTextColor != null) {
            val start = displayText.indexOf(closeText)
            val end = start + closeText.length
            displaySpan.setSpan(ForegroundColorSpan(closeTextColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (onCloseClick != null) {
            val clickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onCloseClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }
            val start = displayText.indexOf(closeText)
            val end = start + closeText.length
            displaySpan.setSpan(clickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            movementMethod = LinkMovementMethod.getInstance()
        }
        text = displaySpan
    }
}

/**
 * 随手机的转动，施加相应的矢量
 * @param x x 轴方向的分量
 * @param y y 轴方向的分量
 */
fun ViewGroup.onSensorChanged(x: Float, y: Float) {
    val impulse = Vec2(x, y)
    for (i in 0..this.childCount) {
        val view: View? = this.getChildAt(i)
        val body = view?.getTag(R.id.physics_layout_body_tag) as? Body
        body?.applyLinearImpulse(impulse, body.position)
    }
}

