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

@Singleton
@Dao
abstract class QuestionContactsDao {
    @Query("SELECT * FROM QuestionForContacts where pagerPosition=:position")
    abstract fun getAllContactsByPagerPosition(position: Int?): LiveData<List<QuestionForContacts>>

    @Query("SELECT * FROM QuestionForContacts where pagerPosition=:position")
    abstract fun getContactsByPagerPosition(position: Int?): List<QuestionForContacts>

    @Query("SELECT * FROM QuestionForContacts where selected=:selected")
    abstract fun getSelectedContactsForQuestions(selected: Boolean): LiveData<List<QuestionForContacts>>

    @Query("SELECT * FROM QuestionForContacts where selected=:selected")
    abstract fun getSelectedFinalContactsForQuestions(selected: Boolean): List<QuestionForContacts>

    @Query("SELECT * FROM QuestionForContacts where pagerPosition=:position AND selected=:selected")
    abstract fun getSelectedContactsForQuestionsByPagerPosition(
        selected: Boolean,
        position: Int?
    ): LiveData<List<QuestionForContacts>>

    @Insert
    abstract fun insert(questionForContacts: QuestionForContacts)

    @Query("DELETE FROM QuestionForContacts")
    abstract fun reset()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(questionForContacts: QuestionForContacts)

    @Delete
    abstract fun delete(questionForContacts: QuestionForContacts)
}