package com.zrh.android.common

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.zrh.android.common.utils.databinding.ActivityVerticalPagerBinding
import com.zrh.android.common.utils.databinding.ItemBannerBinding
import com.zrh.android.common.widgets.BindingActivity

/**
 *
 * @author zrh
 * @date 2024/5/11
 *
 */
class VerticalPagerActivity : BindingActivity<ActivityVerticalPagerBinding>() {
    private val mAdapter = ListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recyclerView.adapter = mAdapter

    }

    class ListAdapter : RecyclerView.Adapter<BannerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return BannerViewHolder(ItemBannerBinding.inflate(inflater, parent, false))
        }

        override fun getItemCount(): Int = 1

        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {

        }

        override fun onViewDetachedFromWindow(holder: BannerViewHolder) {
            super.onViewDetachedFromWindow(holder)
            holder.onDetach()
        }

        override fun onViewAttachedToWindow(holder: BannerViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder.onAttach()
        }
    }

    class BannerViewHolder(private val mBinding: ItemBannerBinding) :
        RecyclerView.ViewHolder(mBinding.root), Runnable {

        private val mHandler = Handler(Looper.getMainLooper())
        private val mAdapter = Adapter()

        init {
            mBinding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
            mBinding.viewPager.isUserInputEnabled = false
            mBinding.viewPager.adapter = mAdapter
        }

        override fun run() {
            val next = mBinding.viewPager.currentItem + 1
            mBinding.viewPager.setCurrentItem(next, true)
            mHandler.postDelayed(this, 3000)
        }

        fun onAttach(){
            mHandler.postDelayed(this, 3000)
        }

        fun onDetach() {
            mHandler.removeCallbacks(this)
        }
    }

    class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = View(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount(): Int = Int.MAX_VALUE

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val color = when (position % 4) {
                0 -> Color.RED
                1 -> Color.YELLOW
                2 -> Color.BLUE
                else -> Color.GREEN
            }
            holder.itemView.setBackgroundColor(color)
        }
    }
}