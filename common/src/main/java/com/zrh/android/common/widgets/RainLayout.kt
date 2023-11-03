package com.zrh.android.common.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener
import com.zrh.android.common.utils.onClick
import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random

/**
 *
 * @author zrh
 * @date 2023/11/2
 *
 */
class RainLayout(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {
    // 雨滴下落时间ms
    private var fallingDuration: Int = 3_000

    // 总的持续时间
    private var duration: Int = 8_000

    // 雨点的高度
    private var itemHeight: Int = 200

    // 雨点的宽度
    private var itemWidth: Int = 100

    // 雨点的速度，在Layout测量完后赋值
    private var speed: Float = 0f

    private val random = Random.Default

    private var mAnimator: ValueAnimator? = null

    // 雨点集合
    private val rainItems = mutableListOf<RainItem>()

    private var onItemClick: ((View, Any) -> Unit)? = null
    private var onAnimEnd: (() -> Unit)? = null
    private var viewFactory: ((Any, FrameLayout) -> View)? = null

    private val recyclePool: Queue<View> = LinkedList()

    fun attach(parentView: ViewGroup) {
        if (parent != null) return
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        parentView.addView(this)
    }

    fun setupWithBuilder(builder: Builder) {
        fallingDuration = builder.fallingDuration
        duration = builder.duration
        itemHeight = builder.itemHeight
        itemWidth = builder.itemWidth
        onItemClick = builder.onItemClick
        onAnimEnd = builder.onAnimEnd
        viewFactory = builder.viewFactory
    }

    fun start(items: List<Any>) {
        reset()
        post { internalStart(items) }
    }

    fun pauseOrResume() {
        mAnimator?.apply {
            if (isStarted && !isPaused) pause()
            else if (isPaused) resume()
        }
    }

    private fun reset() {
        mAnimator?.cancel()
        rainItems.clear()
        removeAllViews()
    }

    private fun internalStart(items: List<Any>) {
        speed = 1f * (height + itemHeight) / fallingDuration
        items.forEach { rainItems.add(buildItem(it, items.size)) }
        val animator = ValueAnimator.ofInt(duration)
        animator.interpolator = LinearInterpolator()
        animator.duration = duration.toLong()
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            val distance = value * speed
            rainItems.filter { item -> item.y + distance + itemHeight >= 0 }.forEach { item ->
                addRainItemView(item)
            }
            animViews(distance.toInt())
        }
        animator.addListener(onEnd = {
            removeAllViews()
            onAnimEnd?.invoke()
        })
        animator.start()
        mAnimator = animator
    }

    private val children: List<View>
        get() {
            return mutableListOf<View>().apply {
                for (i in 0 until childCount) {
                    add(getChildAt(i))
                }
            }
        }

    private fun animViews(distance: Int) {
        children.forEach { view ->
            val item = view.tag as RainItem
            if (item.isActive) {
                val y = (item.y + distance).toFloat()
                view.translationY = y
                if (y > height) {
                    // 回收view
                    item.isActive = false
                    removeView(view)
                    recyclePool.add(view)
                }
            }
        }
    }

    private fun addRainItemView(item: RainItem) {
        val view: View = onCreateView(item)
        view.translationX = item.x.toFloat()
        view.translationY = item.y.toFloat()
        addView(view)
        item.isActive = true
        rainItems.remove(item)
    }

    private fun onCreateView(item: RainItem): View {
        //复用view
        val view = if (recyclePool.isEmpty()) {
            FrameLayout(context).apply {
                layoutParams = LayoutParams(itemWidth, itemHeight)
                viewFactory?.invoke(item.data, this)?.let { this.addView(it) }
            }
        } else {
            recyclePool.poll()
        }

        view.tag = item
        view.isClickable = true
        view.onClick {
            view.isClickable = false
            item.isActive = false
            onItemClick?.invoke(view, item.data)
        }
        return view
    }

    private fun buildItem(it: Any, total: Int): RainItem {
        val safeWidth = width - itemWidth
        val safeHeight = (duration - fallingDuration) * speed
        val preItemDistance = (safeHeight / total).toInt()

        var x = random.nextInt(safeWidth)
        var y = -itemHeight

        if (rainItems.isNotEmpty()) {
            y = rainItems.last().y - random.nextInt(preItemDistance)
            val rect = Rect(x, y, x + itemWidth, y + itemHeight)
            // 找到所有可能有交集的item
            val intersectList = rainItems.filter { rect.bottom > it.y }.sortedBy { it.x }
            var startX = 0
            var validX = false
            for (item in intersectList) {
                if (item.x - startX > safeWidth) {
                    x = startX + random.nextInt(item.x - startX - safeWidth)
                    validX = true
                    break
                }
                startX = item.x + itemWidth
            }
            // 未找到合法的x则调整y
            if (!validX) {
                y = rainItems.last().y - itemHeight - 10
            }
        }

        return RainItem(x, y, it)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimator?.removeAllListeners()
        mAnimator?.removeAllUpdateListeners()
        mAnimator?.cancel()
    }

    data class RainItem(
        val x: Int = 0,
        val y: Int = 0,
        val data: Any,
        var isActive: Boolean = false
    )

    class Builder(val context: Context) {
        var fallingDuration: Int = 3_000
        var duration: Int = 8_000
        var itemHeight: Int = 200
        var itemWidth: Int = 100
        var onItemClick: ((View, Any) -> Unit)? = null
        var onAnimEnd: (() -> Unit)? = null
        var viewFactory: ((Any, FrameLayout) -> View)? = null

        fun setFallingDuration(duration: Int): Builder {
            this.fallingDuration = duration
            return this
        }

        fun setDuration(duration: Int): Builder {
            this.duration = duration
            return this
        }

        fun setItemHeight(height: Int): Builder {
            this.itemHeight = height
            return this
        }

        fun setItemWidth(width: Int): Builder {
            this.itemWidth = width
            return this
        }

        fun setOnItemClick(onClick: (View, Any) -> Unit): Builder {
            this.onItemClick = onClick
            return this
        }

        fun setOnAnimEnd(onEnd: () -> Unit): Builder {
            this.onAnimEnd = onEnd
            return this
        }

        fun setViewFactory(factory: (Any, FrameLayout) -> View): Builder {
            this.viewFactory = factory
            return this
        }

        fun build(): RainLayout {
            return RainLayout(context, null).apply { setupWithBuilder(this@Builder) }
        }
    }
}