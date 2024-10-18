package com.onourem.android.activity.dao

import javax.inject.Singleton
import androidx.lifecycle.LiveData
import androidx.room.*
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.push.NotificationCounter
import com.onourem.android.activity.push.NotificationSilentCounter
import com.onourem.android.activity.push.MessageNotificationCounter
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.dao.NotificationCounterDao
import com.onourem.android.activity.dao.AudioPlayBackHistoryDao
import com.onourem.android.activity.dao.QuestionContactsDao
import java.util.ArrayList

@Singleton
@Dao
abstract class AudioPlayBackHistoryDao {
    @Query("SELECT * FROM AudioPlaybackHistory where userId=:userId")
    abstract fun getAllAudioPlayBackHistory(userId: String?): LiveData<List<AudioPlaybackHistory>>

    @Query("SELECT * FROM AudioPlaybackHistory where audioId=:audioId")
    abstract fun getAudioPlayBackHistory(audioId: String?): AudioPlaybackHistory

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(audioPlaybackHistory: AudioPlaybackHistory)

    @Query("DELETE FROM AudioPlaybackHistory")
    abstract fun reset()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(audioPlaybackHistory: AudioPlaybackHistory)

    @Delete
    abstract fun delete(audioPlaybackHistory: AudioPlaybackHistory)

    fun deleteByList(audioPlaybackHistories: ArrayList<AudioPlaybackHistory>) {
        if (audioPlaybackHistories.size > 0) {
            for (item in audioPlaybackHistories) {
                delete(item)
            }
        }
    }
}