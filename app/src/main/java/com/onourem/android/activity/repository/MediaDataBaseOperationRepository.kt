package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory

interface MediaDataBaseOperationRepository {
    fun getAllAudioPlayBackHistory(userId: String): LiveData<List<AudioPlaybackHistory>>
    fun reset()
    fun insert(audioPlaybackHistory: AudioPlaybackHistory)
    fun update(audioPlaybackHistory: AudioPlaybackHistory)
    fun delete(audioPlaybackHistory: AudioPlaybackHistory)
    fun deleteByList(audioPlaybackHistories: ArrayList<AudioPlaybackHistory>)
}