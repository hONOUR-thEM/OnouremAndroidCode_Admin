package com.onourem.android.activity.ui.utils.media

import com.arthenica.mobileffmpeg.Level

/**
 * Created by Kedar Labde on 12-July-2021
 * Onourem Social Games
 */
class LogMessage(val executionId: Long, val level: Level, val text: String) {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("LogMessage{")
        stringBuilder.append("executionId=")
        stringBuilder.append(executionId)
        stringBuilder.append(", level=")
        stringBuilder.append(level)
        stringBuilder.append(", text=")
        stringBuilder.append("\'")
        stringBuilder.append(text)
        stringBuilder.append('\'')
        stringBuilder.append('}')
        return stringBuilder.toString()
    }
}