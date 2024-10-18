package com.onourem.android.activity.ui.utils.range_seekbar;

/**
 * Callback that returns the start and the end range values of RangeSeekBar
 */
public interface OnRangeSeekBarListener {
    void onRangeValues(RangeSeekBar rangeSeekBar, int start, int end);
}
