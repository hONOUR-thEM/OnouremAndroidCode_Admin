package com.onourem.android.activity.ui.utils.media

/**
 * Created by Kedar Labde on 12-July-2021
 * Onourem Social Games
 */
interface FFmpegCallBack {
    fun process(logMessage: LogMessage) {}
    fun statisticsProcess(statistics: Statistics) {}
    fun success() {}
    fun cancel() {}
    fun failed() {}
}