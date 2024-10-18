package com.onourem.android.activity.ui.utils.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class TaskReceiver : BroadcastReceiver() {
    private val TAG = "***TaskReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called")

        // We are starting MyService via a worker and not directly because since Android 7
        // (but officially since Lollipop!), any process called by a BroadcastReceiver
        // (only manifest-declared receiver) is run at low priority and hence eventually
        // killed by Android.
        val workManager = WorkManager.getInstance(context)
        val startServiceRequest = OneTimeWorkRequest.Builder(TaskWorker::class.java)
            .build()
        workManager.enqueue(startServiceRequest)
    }
}