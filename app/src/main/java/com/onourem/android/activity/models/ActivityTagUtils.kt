package com.onourem.android.activity.models

import androidx.annotation.ColorRes
import com.onourem.android.activity.R

object ActivityTagUtils {
    @JvmStatic
    @ColorRes
    fun getTagColor(activity: LoginDayActivityInfoList): Int {

        var count = 0
        if (activity.friendCount != null) {
            count = activity.friendCount!!.toInt()
        }

        val tagColor = if (count == 1) {
            R.color.color_green
        } else if (count > 1) {
            R.color.color_green
        } else {
            R.color.color_transparent
        }

        return tagColor
    }

    @JvmStatic
    fun getTagInfoText(activity: LoginDayActivityInfoList): String {
        val infoText: String
        var count = 0
        if (activity.friendCount != null) {
            count = activity.friendCount!!.toInt()
        }

        infoText = if (count == 1) {
            "You have one new answer from a friend."
        } else if (count > 1) {
            "You have $count new friends' answers."
        } else {
            ""
        }

        return infoText
    }
}