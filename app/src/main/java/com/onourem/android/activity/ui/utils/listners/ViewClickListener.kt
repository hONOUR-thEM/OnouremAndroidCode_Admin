package com.onourem.android.activity.ui.utils.listners

import android.os.SystemClock
import android.view.View

class ViewClickListener(private val onClickListener: View.OnClickListener) : View.OnClickListener {
    private val intervalTime: Long = 600
    private var mLastClickTime: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < intervalTime) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        onClickListener.onClick(v)

    }
}