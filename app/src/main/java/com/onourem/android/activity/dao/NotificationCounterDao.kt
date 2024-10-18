package com.onourem.android.activity.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.onourem.android.activity.push.MessageNotificationCounter
import com.onourem.android.activity.push.NotificationCounter
import com.onourem.android.activity.push.NotificationSilentCounter
import javax.inject.Singleton

@Singleton
@Dao
abstract class NotificationCounterDao {
    @get:Query("SELECT count FROM NotificationCounter")
    abstract val notificationCount: LiveData<Int>

    @Query("SELECT * FROM NotificationCounter where userId=:userId")
    abstract fun getNotificationCounter(userId: String): NotificationCounter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(notificationCounter: NotificationCounter)

    @Query("DELETE FROM NotificationCounter")
    abstract fun reset()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(notificationCounter: NotificationCounter)

    @get:Query("SELECT countSilent FROM NotificationSilentCounter")
    abstract val notificationSilentCount: LiveData<String>

    @Query("SELECT * FROM NotificationSilentCounter where id=:id")
    abstract fun getNotificationSilentCounter(id: String): NotificationSilentCounter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(notificationSilentCounter: NotificationSilentCounter)

    @Query("DELETE FROM NotificationSilentCounter")
    abstract fun resetSilent()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(notificationCounter: NotificationSilentCounter)

    @get:Query("SELECT count FROM MessageNotificationCounter")
    abstract val messageCount: LiveData<String>

    @Query("SELECT * FROM MessageNotificationCounter where userId=:userId")
    abstract fun getMessageNotificationCounter(userId: String): MessageNotificationCounter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(notificationCounter: MessageNotificationCounter)

    @Delete
    abstract fun delete(messageNotificationCounter: MessageNotificationCounter)

    //
    //    @Query("DELETE FROM MessageNotificationCounter")
    //    public abstract void resetMessage();
    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(notificationCounter: MessageNotificationCounter)
    fun increaseCount(userId: String) {
        val notificationCounter = getNotificationCounter(userId)
        if (notificationCounter != null) {
            notificationCounter.count = notificationCounter.count + 1
            update(notificationCounter)
        }
    }

    fun increaseSilentCount(userId: String) {
        val notificationSilentCounter = getNotificationSilentCounter(userId)
        if (notificationSilentCounter != null) {
            notificationSilentCounter.countSilent =
                (notificationSilentCounter.countSilent.toInt() + 1).toString()
            update(notificationSilentCounter)
        }
    }

    fun increaseMessageCount(userId: String, conversationId: String) {
        val messageNotificationCounter = getMessageNotificationCounter(userId)
        if (messageNotificationCounter != null) {
            messageNotificationCounter.count =
                (messageNotificationCounter.count.toInt() + 1).toString()
            update(messageNotificationCounter)
        }

    }
}