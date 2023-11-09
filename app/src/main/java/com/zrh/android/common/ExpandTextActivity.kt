package com.zrh.android.common

import android.graphics.Color
import android.os.Bundle
import com.zrh.android.common.utils.databinding.ActivityExpandTextBinding
import com.zrh.android.common.utils.setCloseText
import com.zrh.android.common.utils.setExpandText
import com.zrh.android.common.widgets.BindingActivity

/**
 *
 * @author zrh
 * @date 2023/11/9
 *
 */
class ExpandTextActivity : BindingActivity<ActivityExpandTextBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text =
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789456789012345678901234567890123456789"
        setExpandText(text)
    }

    private fun setExpandText(text: String) {
        binding.expandTextView.setExpandText(text, 2, "展开", Color.BLUE) {
            binding.expandTextView.setCloseText(text, "收起", Color.BLUE) {
                setExpandText(text)
            }
        }
    }
}