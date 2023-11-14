package com.zrh.android.common

import android.os.Bundle
import com.zrh.android.common.utils.databinding.ActivityHeartViewBinding
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.utils.R

/**
 *
 * @author zrh
 * @date 2023/11/13
 *
 */
class HeartViewActivity : BindingActivity<ActivityHeartViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.heartProgressView.setBitmapResource(R.mipmap.img_solid_heart, R.mipmap.img_frame_heart)
        binding.heartProgressView.startAnim()
        binding.heartProgressView.setProgress(0.5f)
        binding.heartProgressView.setWaveHeight(20)
    }
}