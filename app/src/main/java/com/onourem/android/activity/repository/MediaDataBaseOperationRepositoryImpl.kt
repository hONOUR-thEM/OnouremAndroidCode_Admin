package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.AppExecutors
import com.onourem.android.activity.dao.AudioPlayBackHistoryDao
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory
import javax.inject.Inject

class MediaDataBaseOperationRepositoryImpl @Inject internal constructor(
    private val appExecutors: AppExecutors,
    private val audioPlayBackHistoryDao: AudioPlayBackHistoryDao
) : MediaDataBaseOperationRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getAllAudioPlayBackHistory(userId: String): LiveData<List<AudioPlaybackHistory>> {
        return audioPlayBackHistoryDao.getAllAudioPlayBackHistory(userId)
    }

    override fun reset() {
        appExecutors.diskIO().execute { audioPlayBackHistoryDao.reset() }
    }

    override fun insert(audioPlaybackHistory: AudioPlaybackHistory) {
        appExecutors.diskIO().execute { audioPlayBackHistoryDao.insert(audioPlaybackHistory) }
    }

    override fun update(audioPlaybackHistory: AudioPlaybackHistory) {
        appExecutors.diskIO().execute { audioPlayBackHistoryDao.update(audioPlaybackHistory) }
    }

    override fun delete(audioPlaybackHistory: AudioPlaybackHistory) {
        appExecutors.diskIO().execute { audioPlayBackHistoryDao.delete(audioPlaybackHistory) }
    }

    override fun deleteByList(audioPlaybackHistories: ArrayList<AudioPlaybackHistory>) {
        appExecutors.diskIO()
            .execute { audioPlayBackHistoryDao.deleteByList(audioPlaybackHistories) }
    }
}