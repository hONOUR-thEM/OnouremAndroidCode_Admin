package com.onourem.android.activity.ui.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R

/**
 * Adds margin to the left and right sides of the RecyclerView item.
 * Adapted from https://stackoverflow.com/a/27664023/4034572
 * @param horizontalMarginInDp the margin resource, in dp.
 */
class VerticalMarginItemDecoration(context: Context) :
    RecyclerView.ItemDecoration() {
    val verticalMargin = context.resources.getDimension(R.dimen.viewpager_current_item_vertical_margin)

    //    private val horizontalMarginInPx: Int =
//        context.resources.getDimension(horizontalMarginInDp.toInt()).toInt()
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
//        outRect.top = verticalMargin.toInt()
//        outRect.bottom = verticalMargin.toInt()
        if (parent.getChildAdapterPosition(view) == 0) {
            //        outRect.top = verticalMargin.toInt()
            outRect.bottom = verticalMargin.toInt()
        }

    }
}