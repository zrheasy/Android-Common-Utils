package com.zrh.android.common

import android.os.Bundle
import android.view.View
import com.zrh.android.common.utils.databinding.ActivityScrollViewBinding
import com.zrh.android.common.widgets.BindingActivity

/**
 *
 * @author zrh
 * @date 2024/4/2
 *
 */
class ScrollViewActivity : BindingActivity<ActivityScrollViewBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}