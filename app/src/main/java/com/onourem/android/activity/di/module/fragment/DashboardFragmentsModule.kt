package com.onourem.android.activity.di.module.fragment

import com.onourem.android.activity.ui.ProgressDialogFragment
import com.onourem.android.activity.ui.appreciation.fragments.AppreciateDialogFragment
import com.onourem.android.activity.ui.audio.fragments.*
import com.onourem.android.activity.ui.bottomsheet.ActionBottomDialogFragment
import com.onourem.android.activity.ui.bottomsheet.PickerBottomSheetDialogFragment
import com.onourem.android.activity.ui.bottomsheet.SearchActionBottomSheetDialogFragment
import com.onourem.android.activity.ui.bottomsheet.UserActionBottomSheetDialogFragment
import com.onourem.android.activity.ui.circle.fragments.*
import com.onourem.android.activity.ui.contactus.ContactUsFragment
import com.onourem.android.activity.ui.counselling.AskCounsellingFragment
import com.onourem.android.activity.ui.counselling.showcase.CounsellingInfoDialogFragment
import com.onourem.android.activity.ui.counselling.offline.InstituteOfflineFragment
import com.onourem.android.activity.ui.counselling.online.InstituteOnlineFragment
import com.onourem.android.activity.ui.counselling.onourem.OnouremOnlineCounsellingFragment
import com.onourem.android.activity.ui.dashboard.*
import com.onourem.android.activity.ui.dashboard.guest.*
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestFavouriteVocalsFragment
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestFriendsVocalsFragment
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestMyVocalsFragment
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestTrendingVocalsFragment
import com.onourem.android.activity.ui.dashboard.landing.FriendsPlayingFragment
import com.onourem.android.activity.ui.dashboard.landing.MyQuestionsFragment
import com.onourem.android.activity.ui.dashboard.landing.QuestionPlayedFragment
import com.onourem.android.activity.ui.dashboard.landing.ShareSomethingDialogFragment
import com.onourem.android.activity.ui.dashboard.mood.MoodReasonSheetFragment
import com.onourem.android.activity.ui.dashboard.subscription.*
import com.onourem.android.activity.ui.dashboard.subscription.ccavenue.activity.CCAvenueWebFragment
import com.onourem.android.activity.ui.dashboard.watchlist.EditAddWatchListDialogFragment
import com.onourem.android.activity.ui.dashboard.watchlist.WatchListDialogFragment
import com.onourem.android.activity.ui.dialog.MediaShareDialogFragment
import com.onourem.android.activity.ui.dialog.MediaViewDialogFragment
import com.onourem.android.activity.ui.dialog.TimePickerDialogFragment
import com.onourem.android.activity.ui.dynamic.DynamicBannerDialogFragment
import com.onourem.android.activity.ui.friends.FriendsFragment
import com.onourem.android.activity.ui.games.WebExternalContentFragment
import com.onourem.android.activity.ui.games.dialogs.CreateOwnQuestionDialogFragment
import com.onourem.android.activity.ui.games.dialogs.EditQuestionDialogFragment
import com.onourem.android.activity.ui.games.dialogs.PhoneCallDialogFragment
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.ui.games.fragments.*
import com.onourem.android.activity.ui.intro.OnouremIntroFragment
import com.onourem.android.activity.ui.invite.InviteFriendsFragment
import com.onourem.android.activity.ui.invite.InviteSheetFragment
import com.onourem.android.activity.ui.message.ConversationDetailsFragment
import com.onourem.android.activity.ui.message.ConversationFragment
import com.onourem.android.activity.ui.message.FriendPickerFragment
import com.onourem.android.activity.ui.message.share.ShareContentSheetFragment
import com.onourem.android.activity.ui.notifications.NotificationExternalPostDetailsFragment
import com.onourem.android.activity.ui.notifications.NotificationPostMessageDetailsFragment
import com.onourem.android.activity.ui.notifications.NotificationsFragment
import com.onourem.android.activity.ui.people.FriendOfFriendSearchFragment
import com.onourem.android.activity.ui.people.PeopleSearchFragment
import com.onourem.android.activity.ui.profile.*
import com.onourem.android.activity.ui.report.ReportProblemFragment
import com.onourem.android.activity.ui.reviewus.ReviewUsFragment
import com.onourem.android.activity.ui.settings.fragments.*
import com.onourem.android.activity.ui.survey.fragments.AllSurveysFragment
import com.onourem.android.activity.ui.survey.fragments.AnonymousSurveyFragment
import com.onourem.android.activity.ui.survey.fragments.StatisticsSurveyFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DashboardFragmentsModule {

    //users

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeContactUsFragment(): ContactUsFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsFragment(): FriendsFragment

    @ContributesAndroidInjector
    abstract fun contributeInviteFriendsFragment(): InviteFriendsFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeReportProblemFragment(): ReportProblemFragment

    @ContributesAndroidInjector
    abstract fun contributeReviewUsFragment(): ReviewUsFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAllSurveysFragment(): AllSurveysFragment

    @ContributesAndroidInjector
    abstract fun contributeAnonymousSurveyFragment(): AnonymousSurveyFragment

    @ContributesAndroidInjector
    abstract fun contributeStatisticsSurveyFragment(): StatisticsSurveyFragment

    @ContributesAndroidInjector
    abstract fun contributePrivacyGroupsFragment(): PrivacyGroupsFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodsDialogFragment(): MoodsDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeProgressDialogFragment(): ProgressDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeQuestionGamesFragment(): QuestionGamesFragment

    @ContributesAndroidInjector
    abstract fun contributeActionBottomDialogFragment(): ActionBottomDialogFragment

    @ContributesAndroidInjector
    abstract fun contributePlaySoloGamesFragment(): PlayGamesFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateOwnQuestionDialogFragment(): CreateOwnQuestionDialogFragment

    @ContributesAndroidInjector
    abstract fun contributePlayGroupMemberFragment(): PlayGroupMemberFragment

    @ContributesAndroidInjector
    abstract fun contributeAddGroupMemberFragment(): AddGroupMemberFragment

    @ContributesAndroidInjector
    abstract fun contributePrivacyGroupsMemberFragment(): PrivacyGroupsMemberFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchListDialogFragment(): WatchListDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeEditAddWatchListDialogFragment(): EditAddWatchListDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAppreciateDialogFragment(): AppreciateDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAnswerQuestionDialogFragment(): AnswerQuestionDialogFragment

    @ContributesAndroidInjector
    abstract fun contributePickerBottomSheetDialogFragment(): PickerBottomSheetDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectPrivacyDialogFragment(): SelectPrivacyDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeMediaViewDialogFragment(): MediaViewDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeComposeMessageDialogFragment(): AppreciateMessageComposeDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeSendNowOrSendLaterDialogFragment(): SendNowOrSendLaterDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTaskAndMessageComposeDialogFragment(): TaskAndMessageComposeDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeWriteCommentDialogFragment(): WriteCommentDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeQuestionDetailsFragment(): QuestionDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributePeopleSearchFragment(): PeopleSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeAskFriendsBottomSheetFragment(): AskFriendsBottomSheetFragment

    @ContributesAndroidInjector
    abstract fun contributeUserActionBottomSheetDialogFragment(): UserActionBottomSheetDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeOneToManyQuestionDetailsFragment(): OneToManyQuestionDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeDToOneQuestionDetailsFragment(): DToOneQuestionDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeOneToOneQuestionDetailsFragment(): OneToOneQuestionDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeEditPrivacyDialogFragment(): EditPrivacyDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCommentListFragment(): CommentListFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectPlayGroupDialogFragment(): SelectPlayGroupDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTaskMessageQuestionDetailsFragment(): TaskMessageQuestionDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationPostMessageDetailsFragment(): NotificationPostMessageDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeWebContentFragment(): WebContentFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileSettingsFragment(): ProfileSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileImageEditorFragment(): ProfileImageEditorFragment

    @ContributesAndroidInjector
    abstract fun contributeBlockListFragment(): BlockListFragment

    @ContributesAndroidInjector
    abstract fun contributeDeleteAccountFragment(): DeleteAccountFragment

    @ContributesAndroidInjector
    abstract fun contributeTimePickerDialogFragment(): TimePickerDialogFragment

    @ContributesAndroidInjector
    abstract fun contributePushNotificationSettingsFragment(): PushNotificationSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendOfFriendSearchFragment(): FriendOfFriendSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchQuestionFragment(): SearchQuestionFragment

    @ContributesAndroidInjector
    abstract fun contributePlayGamesOClubFragment(): PlayGamesOClubFragment

    @ContributesAndroidInjector
    abstract fun contributeDynamicBannerDialogFragment(): DynamicBannerDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeEditQuestionDialogFragment(): EditQuestionDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDashboardNewFragment(): DashboardNewFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodsTodayFragment(): MoodsTodayFragment

    @ContributesAndroidInjector
    abstract fun contributeRecorderSettingsFragment(): RecorderSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeDeleteAccountReasonsDialogFragment(): DeleteAccountReasonsDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeMediaShareDialogFragment(): MediaShareDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFunCardsFragment(): FunCardsFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioGamesFragment(): AudioGamesFragment

    @ContributesAndroidInjector
    abstract fun contributeOnouremIntroFragment(): OnouremIntroFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioCategoryFragment(): AudioCategoryFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioLanguageFragment(): AudioLanguageFragment

    @ContributesAndroidInjector
    abstract fun contributeRecorderFragment(): RecorderFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioPrivacyFragment(): AudioPrivacyFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioPickerFragment(): AudioPickerFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioBackgroundFragment(): AudioBackgroundFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchVocalFragment(): SearchVocalFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioStatsFragment(): AudioStatsFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioGamesPreviousListFragment(): AudioGamesPreviousListFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioEditorFragment(): AudioEditorFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioCommentDialogFragment(): AudioCommentDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAudioCommentListFragment(): AudioCommentListFragment

    @ContributesAndroidInjector
    abstract fun contributeConversationFragment(): ConversationFragment

    @ContributesAndroidInjector
    abstract fun contributeConversationDetailsFragment(): ConversationDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendPickerFragment(): FriendPickerFragment

    @ContributesAndroidInjector
    abstract fun contributeVocalsFragment(): VocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeTrendingVocalsFragment(): TrendingVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsVocalsFragment(): FriendsVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyVocalsFragment(): MyVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteVocalsFragment(): FavoriteVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeOthersVocalsFragment(): OthersVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeInviteSheetFragment(): InviteSheetFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodMappingFragment(): MoodMappingFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodMappingLineFragment(): MoodHistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchVocalsByTitleFragment(): SearchVocalsByTitleFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchActionBottomSheetDialogFragment(): SearchActionBottomSheetDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsPlayingFragment(): FriendsPlayingFragment

    @ContributesAndroidInjector
    abstract fun contributeQuestionPlayedFragment(): QuestionPlayedFragment

    @ContributesAndroidInjector
    abstract fun contributeMyQuestionsFragment(): MyQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestFriendsPlayingFragment(): GuestFriendsPlayingFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestQuestionPlayedFragment(): GuestQuestionPlayedFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestMyQuestionsFragment(): GuestMyQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodReasonSheetFragment(): MoodReasonSheetFragment

    @ContributesAndroidInjector
    abstract fun contributeMoodsInfoSheetFragment(): MoodsInfoSheetFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestDummyFragment(): GuestDummyFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestOneToManyQuestionDetailsFragment(): GuestOneToManyQuestionDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestStatisticsSurveyFragment(): GuestStatisticsSurveyFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestTrendingVocalsFragment(): GuestTrendingVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestFriendsVocalsFragment(): GuestFriendsVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestMyVocalsFragment(): GuestMyVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeGuestFavouriteVocalsFragment(): GuestFavouriteVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeWebExternalContentFragment(): WebExternalContentFragment

    @ContributesAndroidInjector
    abstract fun contributePhoneCallDialogFragment(): PhoneCallDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsCircleAddNumberFragment(): FriendsCircleAddNumberFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsCircleMainFragment(): FriendsCircleMainFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsCircleSelectContactsFragment(): FriendsCircleSelectContactsFragment

    @ContributesAndroidInjector
    abstract fun contributeOTPDialogFragment(): OTPDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectContactsDialogFragment(): SelectContactsDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsCircleQuestionViewPagerFragment(): FriendsCircleQuestionViewPagerFragment

    @ContributesAndroidInjector
    abstract fun contributeWellDoneDialogFragment(): WellDoneDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsThoughtsFragment(): FriendsThoughtsFragment

    @ContributesAndroidInjector
    abstract fun contributeShowTaggedFriendsDialogFragment(): ShowTaggedFriendsDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeShareSomethingDialogFragment(): ShareSomethingDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAmbassadorFragment(): AmbassadorFragment

    @ContributesAndroidInjector
    abstract fun contributeFilterListFragment(): FilterListFragment

    @ContributesAndroidInjector
    abstract fun contributeMyQuestionPlayedFragment(): MyQuestionPlayedFragment

    @ContributesAndroidInjector
    abstract fun contributeMyOwnQuestionsFragment(): MyOwnQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileMyVocalsFragment(): ProfileMyVocalsFragment

    @ContributesAndroidInjector
    abstract fun contributeSubscriptionPurchaseFragment(): SubscriptionPurchaseFragment

    @ContributesAndroidInjector
    abstract fun contributeMySubscriptionFragment(): MySubscriptionFragment

    @ContributesAndroidInjector
    abstract fun contributeShareCodeFragment(): ShareCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeShareContentSheetFragment(): ShareContentSheetFragment

    @ContributesAndroidInjector
    abstract fun contributePurchaseStatusSheet(): PurchaseStatusSheet

    @ContributesAndroidInjector
    abstract fun contributeFreePurchaseStatusSheet(): FreePurchaseStatusSheet

    @ContributesAndroidInjector
    abstract fun contributeCCAvenueWebFragment(): CCAvenueWebFragment

    @ContributesAndroidInjector
    abstract fun contributeOnlineCounsellingFragment(): OnouremOnlineCounsellingFragment

    @ContributesAndroidInjector
    abstract fun contributeOnlineAskCounsellingFragment(): AskCounsellingFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationExternalPostDetailsFragment(): NotificationExternalPostDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeInstituteOfflineFragment(): InstituteOfflineFragment


    @ContributesAndroidInjector
    abstract fun contributeInstituteOnlineFragment(): InstituteOnlineFragment

    @ContributesAndroidInjector
    abstract fun contributeCounsellingInfoDialogFragment(): CounsellingInfoDialogFragment
}