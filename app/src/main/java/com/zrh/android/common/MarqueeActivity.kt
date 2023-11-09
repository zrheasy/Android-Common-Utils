package com.zrh.android.common

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.zrh.android.common.utils.databinding.ActivityMarqueeBinding
import com.zrh.android.common.utils.dp2px
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity

/**
 *
 * @author zrh
 * @date 2023/11/9
 *
 */
class MarqueeActivity:BindingActivity<ActivityMarqueeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.marqueeLayout.setData(getTextList())
        binding.marqueeLayout.setOnBindView { data, view ->
            val textView = view as TextView
            textView.text = data.toString()
        }
        binding.marqueeLayout.setViewFactory {
            val textView = AppCompatTextView(this)
            textView.layoutParams =
                ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.MATCH_PARENT)
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

        binding.btnPlay.onClick {
            if (binding.marqueeLayout.isRunning()){
                binding.marqueeLayout.pause()
            }else{
                binding.marqueeLayout.resume()
            }
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

}