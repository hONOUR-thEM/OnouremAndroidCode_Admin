package com.onourem.android.activity.ui.dashboard

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterDataSet
import com.onourem.android.activity.R

class MyScatterDataSet(yVals: List<Entry?>?, label: String?, fragmentContext: Context?) :
    ScatterDataSet(yVals, label) {
    override fun getColor(index: Int): Int {
        return quadrant(getEntryForIndex(index).x, getEntryForIndex(index).y, mColors)
    }

    companion object {
        // Function to check quadrant
        fun quadrant(x: Float, y: Float, mColors: List<Int>): Int {
            return if (x >= 0 && y >= 0) {
                println("lies in First quadrant")
                mColors[0]
            } else if (x >= 0 && y < 0) {
                println("lies in Second quadrant")
                mColors[1]
            } else if (x < 0 && y >= 0) {
                println("lies in Fourth quadrant")
                mColors[2]
            } else if (x < 0 && y < 0) {
                println("lies in Third quadrant")
                mColors[3]
            } else {
                mColors[3]
            }
        }
    }

    init {
        setColors(
            ContextCompat.getColor(fragmentContext!!, R.color.color_red),
            ContextCompat.getColor(fragmentContext, R.color.color_blue),
            ContextCompat.getColor(fragmentContext, R.color.color_green),
            ContextCompat.getColor(fragmentContext, R.color.color_bg_dashboard)
        )
    }
}