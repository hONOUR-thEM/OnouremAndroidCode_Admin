package com.onourem.android.activity.ui.audio_exo

import android.media.MediaPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

interface AudioPlayerStateListener {

    fun onPlaybackStateUpdated(
        playWhenReady: Boolean,
        hasError: Boolean,
        playbackState: Int,
        currentMediaItem: MediaItem?,
        exoPlayer: ExoPlayer,
        mediaPlayer: MediaPlayer?
    ) {
    }

    fun onCurrentWindowUpdated(showNextAction: Boolean, showPreviousAction: Boolean) {}

    fun onMediaItemChanged(newMediaItemIndex: Int?)

}
