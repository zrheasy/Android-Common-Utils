package com.zrh.android.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.*

/**
 *
 * @author zrh
 * @date 2023/11/5
 *
 */
class MarqueeLayout(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {

    private var viewFactory: (() -> View)? = null
    private var onBindView: ((Any, View) -> Unit)? = null
    private val viewPool: Queue<View> = LinkedList()
    private val data = mutableListOf<Any>()
    private var index = 0

    private var duration = 3000
    private var speed = 0f

    private val mChoreographer = Choreographer.getInstance()
    private val mFrameCallback: FrameCallback = object : FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            onFrame()
            mChoreographer.postFrameCallback(this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mChoreographer.postFrameCallback(mFrameCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mChoreographer.removeFrameCallback(mFrameCallback)
    }

    override fun measureChildWithMargins(child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int) {
        val lp = child.layoutParams as MarginLayoutParams

        val childWidthMeasureSpec = getMeasureSpec(parentWidthMeasureSpec,
                                                   paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
                                                           + widthUsed, lp.width)
        val childHeightMeasureSpec = getMeasureSpec(parentHeightMeasureSpec,
                                                    paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin
                                                            + heightUsed, lp.height)

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    private fun getMeasureSpec(spec: Int, padding: Int, childDimension: Int): Int {
        val specMode = MeasureSpec.getMode(spec)
        val specSize = MeasureSpec.getSize(spec)
        val size = Math.max(0, specSize - padding)
        var resultSize = 0
        var resultMode = 0

        if (childDimension >= 0) {
            resultSize = childDimension
            resultMode = MeasureSpec.EXACTLY
        } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
            // Child wants to be our size. So be it.
            resultSize = if (specMode == MeasureSpec.UNSPECIFIED) 0 else size
            resultMode = specMode
        } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // Child wants to determine its own size. It can't be bigger than us.
            resultSize = 0
            resultMode = MeasureSpec.UNSPECIFIED
        }
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        speed = 1f * width / duration
    }

    fun setScrollDuration(duration: Int) {
        this.duration = duration
    }

    fun setOnBindView(onBindView: (Any, View) -> Unit) {
        this.onBindView = onBindView
    }

    fun setViewFactory(factory: () -> View) {
        this.viewFactory = factory
    }

    fun setData(data: List<Any>) {
        this.data.apply {
            clear()
            addAll(data)
        }
    }

    private fun onFrame() {
        recycleViews()
        insertView()
        animViews()
    }

    private fun recycleViews() {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.translationX + view.width < 0) {
                viewPool.add(view)
            }
        }
        viewPool.filter { it.parent != null }.forEach { removeView(it) }
    }

    private fun animViews() {
        val now = System.currentTimeMillis()
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val startTime = view.tag as Long
            val stayTime = now - startTime
            val distance = stayTime * speed
            view.translationX = width - distance
        }
    }

    private fun insertView() {
        if (data.isEmpty()) return
        val item = data[index % data.size]
        var view: View? = null
        if (childCount == 0) {
            view = onCreateView()
        } else {
            val lastView = getChildAt(childCount - 1)
            if (lastView.translationX + lastView.width < width) {
                view = onCreateView()
            }
        }
        view?.let {
            onBindView?.invoke(item, it)
            index++
            it.translationX = width.toFloat()
            addView(it)
            // 记录开始时间
            it.tag = System.currentTimeMillis()
        }
    }

    private fun onCreateView(): View? {
        if (viewPool.isNotEmpty()) return viewPool.poll()
        return viewFactory?.invoke()
    }
}