package com.zrh.android.common.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

class SpacingDecoration(
    private val hSpacing: Int = 0,
    private val vSpacing: Int = 0,
    private val includeEdge: Boolean = false) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager = parent.layoutManager as GridLayoutManager
            val spanCount = layoutManager.spanCount
            val column = position % spanCount
            getGridItemOffsets(outRect, position, column, spanCount)
        } else if (parent.layoutManager is StaggeredGridLayoutManager) {
            val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
            val spanCount = layoutManager.spanCount
            val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
            val column = lp.spanIndex
            getGridItemOffsets(outRect, position, column, spanCount)
        } else if (parent.layoutManager is LinearLayoutManager) {
            val layoutManager = parent.layoutManager as LinearLayoutManager
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                outRect.left = hSpacing
                outRect.right = hSpacing
                if (includeEdge) {
                    outRect.top = if (position == 0) vSpacing else 0
                    outRect.bottom = vSpacing
                } else {
                    outRect.top = if (position == 0) 0 else vSpacing
                    outRect.bottom = 0
                }
            } else {
                outRect.top = vSpacing
                outRect.bottom = vSpacing
                if (includeEdge) {
                    outRect.left = if (position == 0) vSpacing else 0
                    outRect.right = vSpacing
                } else {
                    outRect.left = if (position == 0) 0 else vSpacing
                    outRect.right = 0
                }
            }
        }
    }

    private fun getGridItemOffsets(outRect: Rect, position: Int, column: Int, spanCount: Int) {
        if (includeEdge) {
            outRect.left = hSpacing * (spanCount - column) / spanCount
            outRect.right = hSpacing * (column + 1) / spanCount
            if (position < spanCount) {
                outRect.top = vSpacing
            }
            outRect.bottom = vSpacing
        } else {
            outRect.left = hSpacing * column / spanCount
            outRect.right = hSpacing * (spanCount - 1 - column) / spanCount
            if (position >= spanCount) {
                outRect.top = vSpacing
            }
        }
    }
}