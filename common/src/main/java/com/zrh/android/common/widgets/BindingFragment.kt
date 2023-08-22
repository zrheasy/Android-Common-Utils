package com.zrh.android.common.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zrh.android.common.utils.ViewBindingHelper

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
abstract class BindingFragment<VB : ViewBinding>:LazyFragment() {

    protected lateinit var binding:VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = onCreateViewBinding(inflater, container)
        return binding.root
    }

    protected open fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?):VB{
        return ViewBindingHelper.binding(this, layoutInflater, container)
    }

    override fun onLazy() {}
}