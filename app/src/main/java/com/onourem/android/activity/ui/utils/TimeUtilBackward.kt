package com.onourem.android.activity.ui.utils

import android.text.format.DateUtils
import com.onourem.android.activity.ui.audio.fragments.removeFractionalSeconds
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


object TimeUtilBackward {
    private const val APP_DISPLAY_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val APP_DISPLAY_DATE_FORMAT = "dd-MMM-yyyy"


    @JvmStatic
    fun getRelatedTime(serverTime: String): String {
        try {
            val dateFormat: DateFormat = SimpleDateFormat(APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone(getServerTimeZone(serverTime.removeFractionalSeconds()))

            val serverDate = dateFormat.parse(serverTime.removeFractionalSeconds())

            if (serverDate != null && serverDate.before(Date())) {
                val duration = Calendar.getInstance().timeInMillis - serverDate.time

                // Use DateUtils for relative time calculation
                val relativeTime = DateUtils.getRelativeTimeSpanString(
                    serverDate.time,
                    Calendar.getInstance().timeInMillis,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
                )

                // Show day of the week only if the date is within the last 7 days
                val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)
                var time = relativeTime.toString()
                if (time.equals("0 min. ago", ignoreCase = true)) {
                    time = "Just Now"
                } else {
                    if (duration <= sevenDaysMillis) {
//                        time = new SimpleDateFormat("EEEE", Locale.getDefault()).format(serverDate);

                        val res = StringBuilder()

                        val oneDayMillis = TimeUnit.DAYS.toMillis(1)
                        val dayDiff = duration / oneDayMillis

                        if (dayDiff > 0) {
                            val calendar = Calendar.getInstance()
                            calendar.time = serverDate

                            if (dayDiff <= 7) {
                                res.append(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US))
                            } else {
                                res.append(getDateString(serverDate))
                            }
                        } else {
                            val oneMinuteMillis = TimeUnit.MINUTES.toMillis(1)
                            val minuteDiff = duration / oneMinuteMillis
                            val hourDiff = minuteDiff / 60
                            val minThreshold = 3
                            if (minuteDiff < 60 && minuteDiff > minThreshold) {
                                res.append(minuteDiff).append(" minutes")
                            } else if (hourDiff >= 1 && hourDiff <= 24) {
                                if (hourDiff == 1L) {
                                    res.append(hourDiff).append(" hour")
                                } else {
                                    res.append(hourDiff).append(" hours")
                                }
                            } else if (minuteDiff < minThreshold) {
                                res.append("Just now")
                            }
                        }

                        return if ("" == res.toString()) "Just now"
                        else res.toString()
                    } else {
                        time = relativeTime.toString()
                    }
                }

                return time
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun getServerTimeZone(serverTime: String): String {
        // Replace this with your logic to extract the time zone from the server time string
        return "UTC"
    }

    private fun getDateString(date: Date): String {
        val sf = SimpleDateFormat(APP_DISPLAY_DATE_FORMAT, Locale.getDefault())
        return sf.format(date)
    }
}
