package com.zrh.android.common.widgets

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat

/**
 *
 * @author zrh
 * @date 2023/11/13
 *
 */
class HeartProgressView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {
    private var mSolidHeartBitmap: Bitmap? = null
    private var mFrameHeartBitmap: Bitmap? = null

    private var mProgress: Float = 0f

    private val mRect = RectF()
    private val mWavePath = Path()
    private var mWaveHeight = 20
    private var mWaveStartOffset = 0f

    private val mPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    private var mWaveAnim: Animator? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setBitmap(solidHeartBitmap: Bitmap, frameHeartBitmap: Bitmap) {
        mSolidHeartBitmap = solidHeartBitmap
        mFrameHeartBitmap = frameHeartBitmap
        invalidate()
    }

    fun setWaveHeight(height: Int) {
        mWaveHeight = height
        invalidate()
    }

    fun setBitmapResource(@DrawableRes solidHeartRes: Int, @DrawableRes frameHeartRes: Int) {
        val solidBitmap = ContextCompat.getDrawable(context, solidHeartRes)
        if (solidBitmap is BitmapDrawable) mSolidHeartBitmap = solidBitmap.bitmap
        val frameBitmap = ContextCompat.getDrawable(context, frameHeartRes)
        if (frameBitmap is BitmapDrawable) mFrameHeartBitmap = frameBitmap.bitmap
        invalidate()
    }

    fun setProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        mProgress = progress
        initWavePath()
        invalidate()
    }

    fun startAnim(duration: Long = 2000) {
        post {
            mWaveAnim?.cancel()
            val animator = ValueAnimator()
            animator.setFloatValues(0f, width.toFloat())
            animator.duration = duration
            animator.repeatCount = ValueAnimator.INFINITE
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener {
                mWaveStartOffset = it.animatedValue as Float
                initWavePath()
                invalidate()
            }

            animator.start()
            mWaveAnim = animator
        }
    }

    fun stopAnim() {
        mWaveAnim?.cancel()
        mWaveAnim = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnim()
    }

    private fun initWavePath() {
        mWavePath.reset()

        if (mProgress >= 1f) {
            mWavePath.moveTo(0f, 0f)
            mWavePath.lineTo(width.toFloat(), 0f)
            mWavePath.lineTo(width.toFloat(), height.toFloat())
            mWavePath.lineTo(0f, height.toFloat())
            return
        }

        val waveTop = height - mProgress * height
        // wave width range [-width, width]
        val waveLeft = (mWaveStartOffset - width)
        val pointOffset = width / 4f

        mWavePath.moveTo(waveLeft, waveTop)
        mWavePath.quadTo(waveLeft + pointOffset, waveTop - mWaveHeight, waveLeft + pointOffset * 2, waveTop)
        mWavePath.moveTo(waveLeft + pointOffset * 2, waveTop)
        mWavePath.quadTo(waveLeft + pointOffset * 3, waveTop + mWaveHeight, waveLeft + width, waveTop)
        mWavePath.moveTo(waveLeft + width, waveTop)
        mWavePath.quadTo(waveLeft + width + pointOffset, waveTop - mWaveHeight, waveLeft + width + pointOffset * 2, waveTop)
        mWavePath.moveTo(waveLeft + width + pointOffset * 2, waveTop)
        mWavePath.quadTo(waveLeft + width + pointOffset * 3, waveTop + mWaveHeight, waveLeft + width + width, waveTop)
        mWavePath.lineTo(waveLeft + width + width, height.toFloat())
        mWavePath.lineTo(waveLeft, height.toFloat())
        mWavePath.lineTo(waveLeft, waveTop)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRect.set(0f, 0f, width.toFloat(), height.toFloat())
        initWavePath()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mSolidHeartBitmap?.let {
            val saveId = canvas.saveLayer(mRect, mPaint)
            canvas.drawPath(mWavePath, mPaint)

            mPaint.xfermode = mXfermode

            canvas.drawBitmap(it, null, mRect, mPaint)

            mPaint.xfermode = null

            canvas.restoreToCount(saveId)
        }

        mFrameHeartBitmap?.let {
            canvas.drawBitmap(it, null, mRect, mPaint)
        }
    }
}