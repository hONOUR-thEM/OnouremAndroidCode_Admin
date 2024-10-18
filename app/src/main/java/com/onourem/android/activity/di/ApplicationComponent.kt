package com.onourem.android.activity.di

import com.onourem.android.activity.OnouremApp
import com.onourem.android.activity.di.module.ApplicationModule
import com.onourem.android.activity.di.module.activity.ActivityModule
import com.onourem.android.activity.di.module.di_exo.MainFeatureContributorModule
import com.onourem.android.activity.di.module.service.ServiceBuilderModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ApplicationModule::class, ActivityModule::class, ServiceBuilderModule::class, MainFeatureContributorModule::class])
interface ApplicationComponent : AndroidInjector<OnouremApp> {
    override fun inject(application: OnouremApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: OnouremApp): Builder
        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun build(): ApplicationComponent
    }
}