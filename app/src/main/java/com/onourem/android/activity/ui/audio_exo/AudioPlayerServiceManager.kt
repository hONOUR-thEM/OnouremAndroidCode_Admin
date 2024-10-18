package com.onourem.android.activity.ui.audio_exo

import com.onourem.android.activity.ui.audio.playback.Song

interface AudioPlayerServiceManager {

    fun changePlayback(shouldPlay: Boolean)

    fun next()

    fun previous()

    fun release()

    fun pause()

    fun reset()

    fun clearMediaList()

    fun setMediaItemPos(pos: Int)

    fun setMediaList(songs: ArrayList<Song>)

    fun addMediaList(songs: ArrayList<Song>)
}
