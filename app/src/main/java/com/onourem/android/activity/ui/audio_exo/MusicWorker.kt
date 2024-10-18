package com.onourem.android.activity.ui.audio_exo

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService

class MusicWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val serviceIntent = Intent(applicationContext, AudioPlayerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(serviceIntent)
        }
        //call methods to perform background task
        return Result.success()
    }

    companion object {
        private const val TAG = "BackupWorker"
    }
}