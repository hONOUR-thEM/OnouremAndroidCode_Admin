package com.onourem.android.activity.ui.utils.moods_charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.onourem.android.activity.R

class LineArrowXAxisRenderer(
    context: Context,
    viewPortHandler: ViewPortHandler?,
    xAxis: XAxis?,
    trans: Transformer?
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    private val arrowRightIcon: Drawable?
    private val arrowWidth: Int
    private val arrowHeight: Int

    init {
        arrowRightIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_up)
        arrowWidth = arrowRightIcon!!.intrinsicWidth
        arrowHeight = arrowRightIcon.intrinsicHeight
    }

    override fun renderAxisLine(c: Canvas) {

        if (!mXAxis.isDrawAxisLineEnabled || !mXAxis.isEnabled) return

        mAxisLinePaint.color = mXAxis.axisLineColor
        mAxisLinePaint.strokeWidth = mXAxis.axisLineWidth
        mAxisLinePaint.pathEffect = mXAxis.axisLineDashPathEffect

        if (mXAxis.position == XAxis.XAxisPosition.TOP ||
            mXAxis.position == XAxis.XAxisPosition.TOP_INSIDE ||
            mXAxis.position == XAxis.XAxisPosition.BOTH_SIDED
        ) {

            //draw the top X axis line
            c.drawLine(
                mViewPortHandler.contentLeft(),
                mViewPortHandler.contentTop(),
                mViewPortHandler.contentRight(),
                mViewPortHandler.contentTop(),
                mAxisLinePaint
            )

            //draw the arrowRightIcon on the right side of top X axis
            arrowRightIcon!!.bounds =
                Rect(
                    mViewPortHandler.contentRight().toInt() - arrowWidth / 2,
                    mViewPortHandler.contentTop().toInt() - arrowHeight / 2,
                    mViewPortHandler.contentRight().toInt() + arrowWidth / 2,
                    mViewPortHandler.contentTop().toInt() + arrowHeight / 2
                )
            arrowRightIcon.draw(c)
        }

        if (mXAxis.position == XAxis.XAxisPosition.BOTTOM ||
            mXAxis.position == XAxis.XAxisPosition.BOTTOM_INSIDE ||
            mXAxis.position == XAxis.XAxisPosition.BOTH_SIDED
        ) {

            //draw the bottom X axis line
            c.drawLine(
                mViewPortHandler.contentLeft(),
                mViewPortHandler.contentBottom(),
                mViewPortHandler.contentRight(),
                mViewPortHandler.contentBottom(),
                mAxisLinePaint
            )

            //draw the arrowRightIcon on the right side of bottom X axis
            arrowRightIcon!!.bounds =
                Rect(
                    mViewPortHandler.contentRight().toInt() - arrowWidth / 2,
                    mViewPortHandler.contentBottom().toInt() - arrowHeight / 2,
                    mViewPortHandler.contentRight().toInt() + arrowWidth / 2,
                    mViewPortHandler.contentBottom().toInt() + arrowHeight / 2
                )
            arrowRightIcon.draw(c)
        }
    }

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val line = formattedLabel.split("\n").toTypedArray()
        Utils.drawXAxisValue(
            c,
            line[0], x, y, mAxisLabelPaint, anchor, angleDegrees
        )
        Utils.drawXAxisValue(
            c,
            line[1],
            x + mAxisLabelPaint.textSize,
            y + mAxisLabelPaint.textSize,
            mAxisLabelPaint,
            anchor,
            angleDegrees
        )
    }
}