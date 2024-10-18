package com.onourem.android.activity.repository

import android.content.Context
import androidx.lifecycle.LiveData

interface NotificationCounterRepository {
    fun notificationCount(): LiveData<Int>
    fun notificationSilentCount(): LiveData<String>
    fun messageNotificationCount(): LiveData<String>
    fun reset()
    fun resetSilent()
    fun increaseCount(userId: String)
    fun increaseSilentCount(userId: String)
    fun increaseMessageNotificationCount(userId: String, conversationId: String)
    fun updateFirebaseToken(registrationId: String, app: Context)
}