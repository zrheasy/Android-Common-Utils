package com.zrh.android.common.widgets

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.zrh.android.common.utils.ViewBindingHelper

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
abstract class BindingActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = onCreateViewBinding()
        getFitsSystemView()?.fitsSystemWindows = true
        setContentView(binding.root)
    }

    protected open fun onCreateViewBinding(): VB {
        return ViewBindingHelper.binding(this, layoutInflater)
    }

    protected open fun getFitsSystemView(): View?{
        return binding.root
    }
}