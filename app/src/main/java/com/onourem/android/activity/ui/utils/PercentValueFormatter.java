package com.onourem.android.activity.ui.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

public class PercentValueFormatter extends ValueFormatter {
    private final float totalSurveyVoters;

    public PercentValueFormatter(float totalSurveyVoters) {
        this.totalSurveyVoters = totalSurveyVoters;
    }

    @Override
    public String getFormattedValue(float value) {
        int percent = Math.round((value / totalSurveyVoters) * 100);
        return String.format(Locale.getDefault(), "%d%%", percent);
    }
}
