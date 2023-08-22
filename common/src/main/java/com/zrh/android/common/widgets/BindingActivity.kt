package com.zrh.android.common.widgets

import android.os.Bundle
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
        setContentView(binding.root)
    }

    protected fun onCreateViewBinding(): VB {
        return ViewBindingHelper.binding(this, layoutInflater)
    }
}