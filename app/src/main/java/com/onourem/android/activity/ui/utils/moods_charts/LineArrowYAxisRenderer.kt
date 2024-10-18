package com.onourem.android.activity.ui.utils.moods_charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.onourem.android.activity.R

class LineArrowYAxisRenderer(
    context: Context,
    viewPortHandler: ViewPortHandler?,
    yAxis: YAxis?,
    trans: Transformer?
) : YAxisRenderer(viewPortHandler, yAxis, trans) {

    private val arrowUpIcon: Drawable?
    private val arrowWidth: Int
    private val arrowHeight: Int

    init {
        arrowUpIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_up)
        arrowWidth = arrowUpIcon!!.intrinsicWidth
        arrowHeight = arrowUpIcon.intrinsicHeight
    }

    override fun renderAxisLine(c: Canvas) {

        if (!mYAxis.isEnabled || !mYAxis.isDrawAxisLineEnabled) return

        mAxisLinePaint.color = mYAxis.axisLineColor
        mAxisLinePaint.strokeWidth = mYAxis.axisLineWidth

        if (mYAxis.axisDependency == YAxis.AxisDependency.LEFT) {

            //draw the left Y line axis
            c.drawLine(
                mViewPortHandler.contentLeft(),
                mViewPortHandler.contentTop(),
                mViewPortHandler.contentLeft(),
                mViewPortHandler.contentBottom(),
                mAxisLinePaint
            )

            //draw the arrowUp on top of the left Y axis
            arrowUpIcon!!.bounds =
                Rect(
                    mViewPortHandler.contentLeft().toInt() - arrowWidth / 2,
                    mViewPortHandler.contentTop().toInt() - arrowHeight / 2,
                    mViewPortHandler.contentLeft().toInt() + arrowWidth / 2,
                    mViewPortHandler.contentTop().toInt() + arrowHeight / 2
                )
            arrowUpIcon.draw(c)
        } else {

            //draw the right Y line axis
            c.drawLine(
                mViewPortHandler.contentRight(),
                mViewPortHandler.contentTop(),
                mViewPortHandler.contentRight(),
                mViewPortHandler.contentBottom(),
                mAxisLinePaint
            )

            //draw the arrowUp on top of the right Y axis
            arrowUpIcon!!.bounds =
                Rect(
                    mViewPortHandler.contentRight().toInt() - arrowWidth / 2,
                    mViewPortHandler.contentTop().toInt() - arrowHeight / 2,
                    mViewPortHandler.contentRight().toInt() + arrowWidth / 2,
                    mViewPortHandler.contentTop().toInt() + arrowHeight / 2
                )
            arrowUpIcon.draw(c)
        }
    }
}