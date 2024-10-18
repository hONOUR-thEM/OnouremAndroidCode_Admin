package com.onourem.android.activity.ui.audio_exo.internal.factory

import com.onourem.android.activity.ui.audio_exo.AudioPlayerStateListener
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerServiceConnection

internal class AudioPlayerServiceConnectionFactory {

    internal fun createConnection(stateListener: AudioPlayerStateListener): AudioPlayerServiceConnection =
        AudioPlayerServiceConnection(stateListener)
}
