package com.onourem.android.activity.ui.utils.background

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.onourem.android.activity.ui.utils.background.TaskService

class TaskWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    private val TAG = "***TaskWorker"
    override fun doWork(): Result {
        Log.d(TAG, "doWork called for: " + this.id)
        Log.d(TAG, "Service Running: " + TaskService.isServiceRunning)
        if (!TaskService.isServiceRunning) {
            Log.d(TAG, "starting service from doWork")
            val intent = Intent(context, TaskService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }

    override fun onStopped() {
        Log.d(TAG, "onStopped called for: " + this.id)
        super.onStopped()
    }
}