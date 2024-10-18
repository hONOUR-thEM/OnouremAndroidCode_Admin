package com.onourem.android.activity.di.module.service

import com.onourem.android.activity.push.OnouremFirebaseService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeOnouremFirebaseService(): OnouremFirebaseService

//    @ContributesAndroidInjector
//    abstract fun contributeMusicService(): MusicService
//
//    @ContributesAndroidInjector
//    abstract fun contributeAudioPlayerService(): AudioPlayerService
}