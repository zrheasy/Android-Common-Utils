package com.zrh.android.common.widgets

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
abstract class ListDiffAdapter<VH : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<VH>() {

    private var mList: List<T> = emptyList()

    override fun getItemCount(): Int = mList.size

    fun getList(): List<T> = mList

    fun getItem(position: Int): T? {
        if (position >= 0 && position < mList.size) {
            return mList[position]
        }
        return null
    }

    fun submitList(list: List<T>) {
        val oldList = mList
        mList = ArrayList(list)

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return mList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem: T = oldList[oldItemPosition]
                val newItem: T = mList[newItemPosition]
                return areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem: T = oldList[oldItemPosition]
                val newItem: T = mList[newItemPosition]
                return areContentsTheSame(oldItem, newItem)
            }
        })
        result.dispatchUpdatesTo(this)
    }

    protected open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    protected open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}