package com.zrh.android.common.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.zrh.android.common.utils.ViewBindingHelper

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
typealias OnAction<T> = (type: Int, item: T, position: Int) -> Unit

abstract class BindingAdapter<VB : ViewBinding, T> : ListDiffAdapter<BindingViewHolder<VB>, T>() {

    private var onAction: OnAction<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewBindingHelper.binding<VB>(this, inflater, parent, false)
        return BindingViewHolder(binding)
    }

    fun setOnAction(action: OnAction<T>) {
        onAction = action
    }

    fun onAction(type: Int, item: T, position: Int) = onAction?.invoke(type, item, position)
}

class BindingViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)