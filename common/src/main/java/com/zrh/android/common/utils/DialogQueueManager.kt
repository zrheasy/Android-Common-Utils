package com.zrh.android.common.utils

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.util.PriorityQueue

/**
 *
 * @author zrh
 * @date 2024/5/7
 *
 */
class DialogQueueManager(private val activity: FragmentActivity) {
    private val queue = PriorityQueue<DialogNode>(8) { o1, o2 -> o2.priority - o1.priority }
    private var visibleCount = 0
    private val callback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            if (f is DialogFragment) visibleCount--
            tryShowNext()
        }
    }

    init {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(callback, false)
    }

    fun showDialog(priority: Int, dialog: DialogFragment) {
        queue.add(DialogNode(priority, dialog))
        tryShowNext()
    }

    private fun tryShowNext() {
        if (visibleCount > 0) return
        if (queue.isEmpty()) return
        val node = queue.poll() ?: return
        node.dialog.show(activity.supportFragmentManager, "")
        visibleCount++
    }

    class DialogNode(val priority: Int, val dialog: DialogFragment)
}