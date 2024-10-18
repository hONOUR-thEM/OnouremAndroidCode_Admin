package com.onourem.android.activity.db

import javax.inject.Singleton
import androidx.room.Dao
import androidx.lifecycle.LiveData
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import androidx.room.OnConflictStrategy
import com.onourem.android.activity.push.NotificationCounter
import com.onourem.android.activity.push.NotificationSilentCounter
import com.onourem.android.activity.push.MessageNotificationCounter
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory
import androidx.room.Database
import com.onourem.android.activity.models.UserList
import androidx.room.RoomDatabase
import com.onourem.android.activity.dao.NotificationCounterDao
import com.onourem.android.activity.dao.AudioPlayBackHistoryDao
import com.onourem.android.activity.dao.QuestionContactsDao

@Database(
    entities = [UserList::class, NotificationCounter::class, NotificationSilentCounter::class, AudioPlaybackHistory::class, MessageNotificationCounter::class, QuestionForContacts::class],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationCounterDao(): NotificationCounterDao
    abstract fun audioPlayBackHistoryDao(): AudioPlayBackHistoryDao
    abstract fun questionContactsDao(): QuestionContactsDao

    companion object {
        const val DATABASE_NAME = "Ounourem.db"
    }
}