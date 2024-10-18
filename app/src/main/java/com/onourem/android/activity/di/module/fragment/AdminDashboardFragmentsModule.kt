package com.onourem.android.activity.di.module.fragment

import com.onourem.android.activity.ui.admin.activity_updates.ActivityUpdatesFragment
import com.onourem.android.activity.ui.admin.activity_updates.AllUpdatesFragment
import com.onourem.android.activity.ui.admin.activity_updates.OClubUpdatesFragment
import com.onourem.android.activity.ui.admin.activity_updates.SoloUpdatesFragment
import com.onourem.android.activity.ui.admin.bottomsheets.AdminPickerBottomSheetDialogFragment
import com.onourem.android.activity.ui.admin.create.CreateActivitiesFragment
import com.onourem.android.activity.ui.admin.create.card.ScheduleFutureCardsFragment
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalContentFragment
import com.onourem.android.activity.ui.admin.create.external_posts.ScheduleFutureExternalPostsFragment
import com.onourem.android.activity.ui.admin.create.external_posts.UpdateExternalContentFragment
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminCreateOwnQuestionDialogFragment
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminCreateTaskDialogFragment
import com.onourem.android.activity.ui.admin.create.question_schedule.ScheduleFutureQuestionsFragment
import com.onourem.android.activity.ui.admin.create.question_schedule.ScheduleOnouremActivityFragment
import com.onourem.android.activity.ui.admin.create.surveys.AddSurveyFragment
import com.onourem.android.activity.ui.admin.create.surveys.ScheduleFutureSurveysFragment
import com.onourem.android.activity.ui.admin.create.task.AdminAppreciateDialogFragment
import com.onourem.android.activity.ui.admin.create.task.AdminAppreciateMessageComposeDialogFragment
import com.onourem.android.activity.ui.admin.create.task.ScheduleFutureTaskFragment
import com.onourem.android.activity.ui.admin.details.ActivityDetailsFragment
import com.onourem.android.activity.ui.admin.main.ScheduledActivitiesFragment
import com.onourem.android.activity.ui.admin.admin_menu.AdminMenuDialogFragment
import com.onourem.android.activity.ui.admin.details.AutoTriggeredOclubActivitiesFragment
import com.onourem.android.activity.ui.admin.details.InactiveActivityFragment
import com.onourem.android.activity.ui.admin.main.delete.DeleteUserAccountFragment
import com.onourem.android.activity.ui.admin.main.email.EmailFragment
import com.onourem.android.activity.ui.admin.main.mood.MoodInfoListFragment
import com.onourem.android.activity.ui.admin.main.mood.UpdateMoodInfoFragment
import com.onourem.android.activity.ui.admin.main.other.AppReviewsListFragment
import com.onourem.android.activity.ui.admin.main.other.CreateReplyDialogFragment
import com.onourem.android.activity.ui.admin.main.other.UserQueryFragment
import com.onourem.android.activity.ui.admin.main.other.UserReportsFragment
import com.onourem.android.activity.ui.admin.main.portal.PortalSignUpFragment
import com.onourem.android.activity.ui.admin.main.portal.PortalUsersFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.AddInstitutionFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.AddSubscriptionDiscountFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.AddSubscriptionFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.InstituteFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.OClubSettingsFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.PackagesFragment
import com.onourem.android.activity.ui.admin.main.subscriptions.PaymentStatusByOrderIdFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.AllActivitiesFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.TriggerActivitiesInOclubFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.cards.AllCardsFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.external.AllExternalPostsFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.questions.AllQuestionsFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.surveys.AllAdminSurveysFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.task_message.AllTaskFragment
import com.onourem.android.activity.ui.admin.posts.LivePostsFragment
import com.onourem.android.activity.ui.admin.posts.PendingPostsFragment
import com.onourem.android.activity.ui.admin.posts.PostViewPagerFragment
import com.onourem.android.activity.ui.admin.posts.RejectedPostsFragment
import com.onourem.android.activity.ui.admin.random.GenerateRandomNumbersFragment
import com.onourem.android.activity.ui.admin.vocals.AdminVocalsFragment
import com.onourem.android.activity.ui.admin.vocals.RatingFragment
import com.onourem.android.activity.ui.admin.vocals.ScheduleVocalsFragment
import com.onourem.android.activity.ui.admin.vocals.playback.AdminSearchVocalFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AdminDashboardFragmentsModule {

    //admin

    @ContributesAndroidInjector
    abstract fun contributeExternalContentFragment(): ExternalContentFragment

    @ContributesAndroidInjector
    abstract fun contributeEmailFragment(): EmailFragment

    @ContributesAndroidInjector
    abstract fun contributeAddSurveyFragment(): AddSurveyFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleFutureQuestionsFragment(): ScheduleFutureQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleQuestionBottomSheetFragment(): ScheduleOnouremActivityFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminCreateOwnQuestionDialogFragment(): AdminCreateOwnQuestionDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminVocalsFragment(): AdminVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminSearchVocalFragment(): AdminSearchVocalFragment

    @ContributesAndroidInjector
    abstract fun contributeRatingFragment(): RatingFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminMenuDialogFragment(): AdminMenuDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeExternalPostsFragment(): ScheduleFutureExternalPostsFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateExternalContentFragment(): UpdateExternalContentFragment

    @ContributesAndroidInjector
    abstract fun contributePostViewPagerFragment(): PostViewPagerFragment

    @ContributesAndroidInjector
    abstract fun contributePendingPostsFragment(): PendingPostsFragment

    @ContributesAndroidInjector
    abstract fun contributeRejectedPostsFragment(): RejectedPostsFragment

    @ContributesAndroidInjector
    abstract fun contributeLivePostsFragment(): LivePostsFragment

    @ContributesAndroidInjector
    abstract fun contributeDeleteUserAccountFragment(): DeleteUserAccountFragment

    @ContributesAndroidInjector
    abstract fun contributeSubscriptionsFragment(): AddSubscriptionFragment

    @ContributesAndroidInjector
    abstract fun contributeAddInstitutionFragment(): AddInstitutionFragment


    @ContributesAndroidInjector
    abstract fun contributeAdminCreateTaskDialogFragment(): AdminCreateTaskDialogFragment


    @ContributesAndroidInjector
    abstract fun contributePaymentStatusByOrderIdFragment(): PaymentStatusByOrderIdFragment

    @ContributesAndroidInjector
    abstract fun contributeTaskListFragment(): ScheduleFutureTaskFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminAppreciateDialogFragment(): AdminAppreciateDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminAppreciateMessageComposeDialogFragment(): AdminAppreciateMessageComposeDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateActivitiesFragment(): CreateActivitiesFragment

    @ContributesAndroidInjector
    abstract fun contributeGenerateRandomNumbersFragment(): GenerateRandomNumbersFragment

    @ContributesAndroidInjector
    abstract fun contributeAddSubscriptionDiscountFragment(): AddSubscriptionDiscountFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleFutureCardsFragment(): ScheduleFutureCardsFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleFutureSurveysFragment(): ScheduleFutureSurveysFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminPickerBottomSheetDialogFragment(): AdminPickerBottomSheetDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeActivityDetailsFragment(): ActivityDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduledActivitiesFragment(): ScheduledActivitiesFragment

    @ContributesAndroidInjector
    abstract fun contributeActivityUpdatesFragment(): ActivityUpdatesFragment

    @ContributesAndroidInjector
    abstract fun contributeAllUpdatesFragment(): AllUpdatesFragment

    @ContributesAndroidInjector
    abstract fun contributeOClubUpdatesFragment(): OClubUpdatesFragment

    @ContributesAndroidInjector
    abstract fun contributeSoloUpdatesFragment(): SoloUpdatesFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleVocalsFragment(): ScheduleVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeOClubSettingsFragment(): OClubSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserReportsFragment(): UserReportsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserQueryFragment(): UserQueryFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateReplyDialogFragment(): CreateReplyDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateMoodInfoFragment(): UpdateMoodInfoFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodInfoListFragment(): MoodInfoListFragment

    @ContributesAndroidInjector
    abstract fun contributeAppReviewsListFragment(): AppReviewsListFragment

    @ContributesAndroidInjector
    abstract fun contributePortalSignUpFragment(): PortalSignUpFragment

    @ContributesAndroidInjector
    abstract fun contributePortalUsersFragment(): PortalUsersFragment

    @ContributesAndroidInjector
    abstract fun contributeAutoTriggeredOclubActivitiesFragment(): AutoTriggeredOclubActivitiesFragment

    @ContributesAndroidInjector
    abstract fun contributeAllActivitiesFragment(): AllActivitiesFragment

    @ContributesAndroidInjector
    abstract fun contributeAllQuestionsFragment(): AllQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contributeAllCardsFragment(): AllCardsFragment

    @ContributesAndroidInjector
    abstract fun contributeAllTaskFragment(): AllTaskFragment

    @ContributesAndroidInjector
    abstract fun contributeAllExternalPostsFragment(): AllExternalPostsFragment

    @ContributesAndroidInjector
    abstract fun contributeAllAdminSurveysFragment(): AllAdminSurveysFragment

    @ContributesAndroidInjector
    abstract fun contributeTriggerActivitiesInOclubFragment(): TriggerActivitiesInOclubFragment

    @ContributesAndroidInjector
    abstract fun contributeInactiveActivityFragment(): InactiveActivityFragment

    @ContributesAndroidInjector
    abstract fun contributePackagesFragment(): PackagesFragment

    @ContributesAndroidInjector
    abstract fun contributeInstituteFragment(): InstituteFragment
}