package com.onourem.android.activity.di.module.di_exo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio_exo.CurrentContentIntentProvider
import com.onourem.android.activity.ui.audio_exo.internal.di.AudioPlayerServiceModule
import dagger.Module
import dagger.Provides

@Suppress("unused")
@SuppressLint("JvmStaticProvidesInObjectDetector,ModuleCompanionObjects")
@Module(includes = [AudioPlayerServiceModule::class])
abstract class MainFeatureContributorModule {

//    @ContributesAndroidInjector(modules = [MainActivityModule::class])
//    abstract fun contributeMainActivity(): DashboardActivity

//    @Binds
//    @IntoMap
//    @ViewModelKey(MainViewModel::class)
//    abstract fun bindMainViewModel(userViewModel: MainViewModel): ViewModel

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun provideCurrentContentIntentProvider(): CurrentContentIntentProvider =
            object : CurrentContentIntentProvider {
                override fun provideCurrentContentIntent(context: Context) =
                    Intent(context, DashboardActivity::class.java)
            }
    }
}
