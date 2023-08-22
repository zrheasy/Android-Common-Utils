package com.zrh.android.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zrh.android.common.utils.LocaleUtils
import com.zrh.android.common.utils.databinding.ActivityLanguageBinding
import com.zrh.android.common.utils.databinding.ActivityListBinding
import com.zrh.android.common.utils.databinding.ActivityMainBinding
import com.zrh.android.common.utils.databinding.ItemStringBinding
import com.zrh.android.common.utils.dp2px
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.utils.toast
import com.zrh.android.common.widgets.BindingActivity
import com.zrh.android.common.widgets.BindingAdapter
import com.zrh.android.common.widgets.BindingViewHolder
import com.zrh.android.common.widgets.SpacingDecoration
import java.util.*

class ListActivity : BindingActivity<ActivityListBinding>() {

    private val mAdapter: StringListAdapter by lazy { StringListAdapter() }
    private val mList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recyclerView.apply {
            addItemDecoration(SpacingDecoration(vSpacing = dp2px(8f), includeEdge = true))
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        binding.btnInsert.onClick {
            mList.add(0, "New Item ${System.currentTimeMillis()}")
            mAdapter.submitList(mList)
        }
        binding.btnDelete.onClick {
            if (mList.isNotEmpty()) {
                mList.removeAt(0)
                mAdapter.submitList(mList)
            }
        }
        binding.btnUpdate.onClick {
            if (mList.isNotEmpty()) {
                mList[0] = "Update Item ${System.currentTimeMillis()}"
                mAdapter.submitList(mList)
            }
        }

        mList.apply {
            addAll(listOf("Android", "iOS", "Flutter", "React Native"))
            mAdapter.submitList(this)
        }

        mAdapter.setOnAction { _, item, _ ->
            toast("click item: $item")
        }
    }
}

class StringListAdapter : BindingAdapter<ItemStringBinding, String>() {
    override fun onBindViewHolder(holder: BindingViewHolder<ItemStringBinding>, position: Int) {
        val item = getItem(position)!!
        holder.binding.tvName.text = item
        holder.itemView.onClick { onAction(0, item, position) }
    }
}