package com.onourem.android.activity.ui.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class XAxisTimeFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return SimpleDateFormat("HH:MM", Locale.getDefault()).format(Date(value.toLong()))
    }
}