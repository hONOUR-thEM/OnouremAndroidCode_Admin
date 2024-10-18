package com.onourem.android.activity.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.onourem.android.activity.ui.audio.fragments.removeFractionalSeconds
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtil {
    private const val APP_DISPLAY_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getRelatedTime(serverTime: String): String {
        try {
            val formatter = DateTimeFormatter.ofPattern(APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault())

            AppUtilities.showLog("###", "Server Time: $serverTime")

            // Parse the server time string to Instant
            val serverInstant = LocalDateTime.parse(serverTime.removeFractionalSeconds(), formatter)
                .atZone(ZoneId.of(serverTimeZone))
                .toInstant()

            AppUtilities.showLog("###", "UTC Time: $serverInstant")

            // Convert server time to the device's local timezone
            val deviceTimeZone = ZoneId.systemDefault()

            val serverDateTimeLocal = LocalDateTime.ofInstant(serverInstant, deviceTimeZone)

            // Calculate time difference
            val ltd = LocalDateTime.now()

            val duration = Duration.between(serverDateTimeLocal, ltd)

            AppUtilities.showLog("###", "Device Local Time: $ltd")

            AppUtilities.showLog("###", "Server Date Local Time: $serverDateTimeLocal")

            AppUtilities.showLog("###", "Duration : $duration")


            val res = StringBuilder()

            val oneDayMillis = TimeUnit.DAYS.toMillis(1)

            val dayDiff = duration.toMillis() / oneDayMillis

            if (dayDiff > 0) {
                res.append(formatDayDifference(serverDateTimeLocal, dayDiff))
            } else {
                val oneMinuteMillis = TimeUnit.MINUTES.toMillis(1)
                val minuteDiff = duration.toMillis() / oneMinuteMillis
                val hourDiff = minuteDiff / 60
                val minThreshold = 3

                if (minuteDiff < 60 && minuteDiff > minThreshold) {
                    res.append(minuteDiff).append(" minutes")
                } else if (hourDiff >= 1 && hourDiff <= 24) {
                    res.append(formatHourDifference(hourDiff))
                } else if (minuteDiff < minThreshold) {
                    res.append("Just now")
                }
            }

            return res.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun formatDayDifference(serverDateTime: LocalDateTime, dayDiff: Long): String {
        if (dayDiff == 1L) {
            return "Yesterday"
        } else if (dayDiff <= 7) {
            return serverDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        } else {
            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
            return serverDateTime.format(formatter)
        }
    }

    private fun formatHourDifference(hourDiff: Long): String {
        return if (hourDiff == 1L) {
            "$hourDiff hour"
        } else {
            "$hourDiff hours"
        }
    }

    private val serverTimeZone: String
        get() = "UTC"
}
