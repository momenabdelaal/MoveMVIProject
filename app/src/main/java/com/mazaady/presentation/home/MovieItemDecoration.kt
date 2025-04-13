package com.mazaady.presentation.home

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mazaady.R

class MovieItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val spacing = context.resources.getDimensionPixelSize(R.dimen.grid_spacing)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % 2

        outRect.left = if (column == 0) spacing else spacing / 2
        outRect.right = if (column == 1) spacing else spacing / 2
        outRect.bottom = spacing

        // Add top margin only for the first row
        if (position < 2) {
            outRect.top = spacing
        }
    }
}
