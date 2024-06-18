package com.zrh.android.common.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

/**
 *
 * @author zrh
 * @date 2024/6/18
 *
 */
class OutlineLayout(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs, 0) {

    private var outlineRadius = 10f
    private var outlineColor = Color.WHITE
    private var outlineWidth = 4f
    private val cutoutPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outlineRect = RectF()
    private val cutoutRect = RectF()

    private var titleMarginStart = 64
    private val tvTitle: TextView = TextView(context)

    init {
        clipChildren = false
        initTitleView()
        initPaint()
    }

    private fun initPaint() {
        outlinePaint.color = outlineColor
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.strokeWidth = outlineWidth

        cutoutPaint.style = Paint.Style.FILL_AND_STROKE
        cutoutPaint.color = outlineColor
        cutoutPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OUT))

        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun initTitleView() {
        tvTitle.textSize = 14f
        tvTitle.setTextColor(Color.WHITE)
        tvTitle.setPadding(10, 0, 10, 0)
        tvTitle.layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                this.startToStart = 0
                this.topToTop = 0
                this.bottomToTop = 0
                this.marginStart = titleMarginStart
            }
        tvTitle.text = "Hello world!"
        addView(tvTitle)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val offset = outlineWidth / 2
        outlineRect.left = offset
        outlineRect.right = width - offset
        outlineRect.top = offset
        outlineRect.bottom = height - offset

        cutoutRect.left = tvTitle.left.toFloat()
        cutoutRect.right = tvTitle.right.toFloat()
        cutoutRect.top = tvTitle.top.toFloat()
        cutoutRect.bottom = tvTitle.bottom.toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawOutline(canvas)
        super.dispatchDraw(canvas)
    }

    private fun drawOutline(canvas: Canvas) {
        val layer = saveCanvasLayer(canvas)

        val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(bitmap)
        tempCanvas.drawRoundRect(outlineRect, outlineRadius, outlineRadius, outlinePaint)
        tempCanvas.drawRect(cutoutRect, cutoutPaint)
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)

        canvas.restoreToCount(layer)
    }

    private fun saveCanvasLayer(canvas: Canvas):Int {
        return canvas.saveLayer(
            0.0f,
            0.0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            null as Paint?
        )
    }
}