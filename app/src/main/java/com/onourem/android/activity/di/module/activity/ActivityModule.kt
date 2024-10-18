package com.onourem.android.activity.di.module.activity

import com.onourem.android.activity.di.module.di_exo.MainActivityModule
import com.onourem.android.activity.di.module.fragment.AdminDashboardFragmentsModule
import com.onourem.android.activity.di.module.fragment.DashboardFragmentsModule
import com.onourem.android.activity.di.module.fragment.OnboardingFragmentsModule
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.dashboard.subscription.ccavenue.activity.WebViewActivity
import com.onourem.android.activity.ui.onboarding.OnboardingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [OnboardingFragmentsModule::class])
    abstract fun contributeOnboardingActivity(): OnboardingActivity

    @ContributesAndroidInjector(modules = [DashboardFragmentsModule::class, AdminDashboardFragmentsModule::class, MainActivityModule::class])
    abstract fun contributeDashboardActivity(): DashboardActivity

    @ContributesAndroidInjector(modules = [DashboardFragmentsModule::class])
    abstract fun contributeWebViewActivity(): WebViewActivity

}