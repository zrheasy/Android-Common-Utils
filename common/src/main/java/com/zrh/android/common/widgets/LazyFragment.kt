package com.zrh.android.common.widgets

import androidx.fragment.app.Fragment

abstract class LazyFragment : Fragment() {
    protected var isLoaded = false
        private set

    /**
     * 页面首次加载回调
     */
    abstract fun onLazy()

    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            isLoaded = true
            onLazy()
        }
    }
}