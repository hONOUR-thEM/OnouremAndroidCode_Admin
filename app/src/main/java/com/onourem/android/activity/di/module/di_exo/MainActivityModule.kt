package com.onourem.android.activity.di.module.di_exo

import android.annotation.SuppressLint
import androidx.core.app.ComponentActivity
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio_exo.AudioPlayerStateListener
import com.onourem.android.activity.ui.audio_exo.internal.di.AudioPlayerActivityModule
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("unused")
@SuppressLint("JvmStaticProvidesInObjectDetector,ModuleCompanionObjects")
@Module(includes = [AudioPlayerActivityModule::class])
abstract class MainActivityModule {

    @Binds
    abstract fun bindComponentActivity(activity: DashboardActivity): ComponentActivity

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun provideAudioPlayerStateListener(activity: DashboardActivity): AudioPlayerStateListener =
            activity.audioPlayerStateListener

    }
}
