package com.onourem.android.activity.ui.audio_exo.internal.factory

import android.app.Application
import android.content.Intent
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService

internal class AudioPlayerServiceIntentFactory(private val application: Application) {

    internal fun createBaseIntent(): Intent = Intent(application, AudioPlayerService::class.java)

    internal fun createPlayNextIntent(): Intent = createBaseIntent().apply {
        putExtra(AudioPlayerService.INTENT_KEY_NEXT, true)
    }

    internal fun createMediaItemPositionIntent(position: Int): Intent = createBaseIntent().apply {
        putExtra(AudioPlayerService.INTENT_KEY_MEDIA_ITEM_POSITION_FOR_NEXT_PREV, position)
    }

    internal fun createPlayPreviousIntent(): Intent = createBaseIntent().apply {
        putExtra(AudioPlayerService.INTENT_KEY_PREVIOUS, true)
    }

    internal fun createChangePlaybackIntent(shouldPlay: Boolean): Intent =
        createBaseIntent().apply {
            putExtra(AudioPlayerService.INTENT_KEY_SHOULD_PLAY, shouldPlay)
        }

    internal fun createReleaseIntent(shouldRelease: Boolean): Intent =
        createBaseIntent().apply {
            putExtra(AudioPlayerService.INTENT_KEY_SHOULD_RELEASE, shouldRelease)
        }

//    internal fun createChangePlaybackPauseIntent(shouldPlay: Boolean): Intent = createBaseIntent().apply {
//        putExtra(AudioPlayerService.INTENT_KEY_SHOULD_NOT_PLAY, shouldPlay)
//    }

    internal fun createSetMediaListIntent(songs: ArrayList<Song>): Intent =
        createBaseIntent().apply {
            putParcelableArrayListExtra(AudioPlayerService.INTENT_KEY_SET_MEDIA_LIST, songs)
            //putExtra(AudioPlayerService.INTENT_KEY_SET_MEDIA_ITEM, song)
        }

    internal fun createAddMediaListIntent(songs: ArrayList<Song>): Intent =
        createBaseIntent().apply {
            putParcelableArrayListExtra(AudioPlayerService.INTENT_KEY_ADD_MEDIA_LIST, songs)
        }

    internal fun createClearMediaListIntent(): Intent = createBaseIntent().apply {
        putExtra(AudioPlayerService.INTENT_KEY_ADD_MEDIA_LIST, true)
    }
}
