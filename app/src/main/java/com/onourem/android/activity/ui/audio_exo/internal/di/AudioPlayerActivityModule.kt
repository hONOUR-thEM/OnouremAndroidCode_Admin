package com.onourem.android.activity.ui.audio_exo.internal.di

import android.annotation.SuppressLint
import androidx.core.app.ComponentActivity
import com.onourem.android.activity.OnouremApp
import com.onourem.android.activity.ui.audio_exo.AudioPlayerServiceManager
import com.onourem.android.activity.ui.audio_exo.AudioPlayerStateListener
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerServiceManagerImpl
import com.onourem.android.activity.ui.audio_exo.internal.factory.AudioPlayerServiceConnectionFactory
import com.onourem.android.activity.ui.audio_exo.internal.factory.AudioPlayerServiceIntentFactory
import dagger.Module
import dagger.Provides

@Suppress("unused")
@SuppressLint("JvmStaticProvidesInObjectDetector")
@Module
object AudioPlayerActivityModule {

    @Provides
    @JvmStatic
    internal fun provideAudioPlayerServiceManager(
        activity: ComponentActivity,
        audioPlayerServiceConnectionFactory: AudioPlayerServiceConnectionFactory,
        audioPlayerServiceIntentFactory: AudioPlayerServiceIntentFactory,
        stateListener: AudioPlayerStateListener
    ): AudioPlayerServiceManager =
        AudioPlayerServiceManagerImpl(
            activity,
            audioPlayerServiceConnectionFactory,
            audioPlayerServiceIntentFactory,
            stateListener
        )

    @Provides
    @JvmStatic
    internal fun provideAudioPlayerServiceConnectionFactory(): AudioPlayerServiceConnectionFactory =
        AudioPlayerServiceConnectionFactory()

    @Provides
    @JvmStatic
    internal fun provideAudioPlayerServiceIntentFactory(application: OnouremApp): AudioPlayerServiceIntentFactory =
        AudioPlayerServiceIntentFactory(application)
}
