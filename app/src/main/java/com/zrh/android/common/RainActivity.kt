package com.zrh.android.common

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.zrh.android.common.utils.databinding.ActivityRainBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.utils.toast
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.widgets.RainLayout

/**
 *
 * @author zrh
 * @date 2023/11/9
 *
 */
class RainActivity : BindingActivity<ActivityRainBinding>() {

    private lateinit var mRainLayout: RainLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRainLayout = RainLayout.Builder(this)
            .setItemWidth(100)
            .setItemHeight(200)
            .setOnItemClick { view, any ->
                view.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(200).apply {
                    setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            view.isVisible = false
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                        }
                    })
                    start()
                }
                toast("抢到红包:$any")
            }
            .setOnAnimEnd {
                toast("红包雨结束")
            }
            .setViewFactory { data, parent ->
                val view = TextView(parent.context)
                view.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                view.text = data.toString()
                view.textSize = 14f
                view.gravity = Gravity.CENTER
                view.setBackgroundColor(Color.RED)
                return@setViewFactory view
            }
            .build()
        mRainLayout.attach(window.decorView as ViewGroup)

        binding.btnRedPacket.onClick {
            val list = mutableListOf<Any>().apply {
                for (i in 0 until 20) {
                    add(i)
                }
            }
            mRainLayout.start(list)
        }
        binding.btnRedPacketPause.onClick { mRainLayout.pauseOrResume() }
    }
}