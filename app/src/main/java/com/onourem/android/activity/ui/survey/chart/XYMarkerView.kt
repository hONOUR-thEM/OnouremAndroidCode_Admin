package com.onourem.android.activity.ui.survey.chart

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.onourem.android.activity.R
import java.text.DecimalFormat

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
class XYMarkerView(context: Context?, private val xAxisValueFormatter: ValueFormatter) :
    MarkerView(context, R.layout.custom_marker_view) {
    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val format: DecimalFormat = DecimalFormat("###.0")

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        tvContent.text = String.format(
            "x: %s, y: %s",
            xAxisValueFormatter.getFormattedValue(e.x),
            format.format(e.y.toDouble())
        )
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width.toFloat() / 2), (-height).toFloat())
    }

    init {


        //super(context, findViewById(R.layout.custom_marker_view));
    }
}