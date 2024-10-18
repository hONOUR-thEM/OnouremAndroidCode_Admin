package com.onourem.android.activity.ui.utils.moods_charts;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {

    private final int month;
    private final int year;

    public LineChartXAxisValueFormatter(int month, int year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public String getFormattedValue(float value) {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long emissionsMilliSince1970Time = ((long) value) * 1000;

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM", Locale.getDefault());
//        dateTimeFormat.format(date.getTime())


        Calendar date = Calendar.getInstance();
        date.set(Calendar.DATE, (int) value);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.YEAR, year);
        date.setTimeZone(TimeZone.getDefault());

        return date.get(Calendar.DATE) + "\n" + dateTimeFormat.format(date.getTime());
    }

}