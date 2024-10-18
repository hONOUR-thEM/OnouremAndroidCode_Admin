package com.onourem.android.activity.di.module.fragment

import com.onourem.android.activity.ui.ProgressDialogFragment
import com.onourem.android.activity.ui.dialog.DatePickerDialogFragment
import com.onourem.android.activity.ui.dialog.MediaViewDialogFragment
import com.onourem.android.activity.ui.onboarding.fragments.*
import com.onourem.android.activity.ui.settings.fragments.WebContentFragment
import com.onourem.android.activity.ui.splash.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OnboardingFragmentsModule {
    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeSignUpFragment(): SignUpFragment

    @ContributesAndroidInjector
    abstract fun contributeOnboardingFragment(): OnboardingFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

    @ContributesAndroidInjector
    abstract fun contributeProgressDialogFragment(): ProgressDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDatePickerDialogFragment(): DatePickerDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeVerificationFragment(): VerificationFragment

    @ContributesAndroidInjector
    abstract fun contributeDemoDialogFragment(): DemoDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeWebContentFragment(): WebContentFragment

    @ContributesAndroidInjector
    abstract fun contributeMediaViewDialogFragment(): MediaViewDialogFragment
}