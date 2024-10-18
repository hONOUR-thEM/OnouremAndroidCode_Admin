package com.onourem.android.activity.ui.dashboard

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class HeightWrappingViewPager : ViewPager {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val firstChild = getChildAt(0)
        if (firstChild != null) {
            firstChild.measure(widthMeasureSpec, heightMeasureSpec)
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(firstChild.measuredHeight, MeasureSpec.EXACTLY)
            )
        }
    }
}