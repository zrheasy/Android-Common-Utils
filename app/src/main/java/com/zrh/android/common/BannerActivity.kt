package com.zrh.android.common

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.zrh.android.common.utils.databinding.ActivityBannerBinding
import com.zrh.android.common.utils.dp2px
import com.zrh.android.common.widgets.BindingActivity
import kotlin.math.abs

/**
 *
 * @author zrh
 * @date 2024/6/19
 *
 */
class BannerActivity:BindingActivity<ActivityBannerBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewHomeHotBanner.setAdapter { bgaBanner, view, any, i ->
            view.setBackgroundColor(Color.RED)
        }
        binding.viewHomeHotBanner.setData(mutableListOf(1), null)
        binding.viewHomeHotBanner.viewPager.clipChildren = false
        binding.viewHomeHotBanner.viewPager.offscreenPageLimit = 1
        binding.viewHomeHotBanner.viewPager.pageMargin = -dp2px(8f)
        binding.viewHomeHotBanner.viewPager.setPageTransformer(true, ZoomPageTransformer())
    }

    class ZoomPageTransformer : ViewPager.PageTransformer {
        private var mMinScale = 0.85f

        override fun transformPage(view: View, position: Float) {
            // -2 -1 0 1 2
            when {
                position >= -1 && position < 0 -> {
                    handleTransform(view, abs(position))
                }

                position >= 0 && position < 1 -> {
                    handleTransform(view, position)
                }

                else -> {
                    handleTransform(view, 1f)
                }
            }
        }

        private fun handleTransform(view: View, percent: Float) {
            val range = 1 - mMinScale
            val scale = (1.0f - percent) * range + mMinScale
            view.scaleX = scale
            view.scaleY = scale
        }
    }
}