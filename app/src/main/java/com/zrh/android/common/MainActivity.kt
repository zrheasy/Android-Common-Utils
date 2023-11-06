package com.zrh.android.common

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.zrh.android.common.event.CounterEvent
import com.zrh.android.common.utils.*
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.widgets.RainLayout

class MainActivity : BindingActivity<ActivityMainBinding>() {

    private lateinit var mRainLayout: RainLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnLanguage.onClick {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        binding.btnList.onClick {
            startActivity(Intent(this, ListActivity::class.java))
        }

        binding.btnDialog.onClick {
            TextDialog(this).show()
        }

        binding.btnBottomDialog.onClick {
            BottomTextDialog(this).show()
        }

        binding.btnBus.onClick {
            startActivity(Intent(this, CounterActivity::class.java))
        }

        AndroidBus.receiver(this) {
            subscribe(CounterEvent::class.java) {
                toast("收到事件通知：$it")
            }
        }

        val text = "01234567890123456789012345678901234567890123456789012345678901234567890123456789"
        setExpandText(text)

        mRainLayout = RainLayout.Builder(this)
            .setItemWidth(100)
            .setItemHeight(200)
            .setOnItemClick { view, any ->
                view.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(200).apply {
                    setListener(object : AnimatorListener {
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


        binding.btnRedPacket.onClick {
            val list = mutableListOf<Any>().apply {
                for (i in 0 until 20) {
                    add(i)
                }
            }
            mRainLayout.attach(window.decorView as ViewGroup)
            mRainLayout.start(list)
        }
        binding.btnRedPacketPause.onClick { mRainLayout.pauseOrResume() }

        binding.marqueeLayout.setData(getTextList())
        binding.marqueeLayout.setOnBindView { data, view ->
            val textView = view as TextView
            textView.text = data.toString()
        }
        binding.marqueeLayout.setViewFactory {
            val textView = AppCompatTextView(this)
            textView.layoutParams =
                MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT)
                    .apply {
                        marginEnd = dp2px(10f)
                        marginStart = dp2px(10f)
                    }
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.textSize = 14f
            textView.setTextColor(Color.BLACK)
            textView.maxLines = 1
            return@setViewFactory textView
        }
    }

    private fun getTextList(): List<String> {
        val text = "Start0123456789012345678901234567845678901234567890123456789End"
        return mutableListOf<String>().apply {
            for (i in 0..1) {
                add(text)
            }
        }
    }

    private fun setExpandText(text: String) {
        binding.expandTextView.setExpandText(text, 2, "More>>", Color.BLUE) {
            binding.expandTextView.setCloseText(text, "Close", Color.BLUE) {
                setExpandText(text)
            }
        }
    }
}