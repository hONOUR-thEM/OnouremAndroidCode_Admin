package com.onourem.android.activity.ui.audio_exo.internal.di

import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AudioPlayerServiceModule {

    @ContributesAndroidInjector
    internal abstract fun contributeAudioPlayerService(): AudioPlayerService
}
