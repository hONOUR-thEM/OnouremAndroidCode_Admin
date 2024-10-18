package com.onourem.android.activity.di.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.arch.dagger.ViewModelKey
import com.onourem.android.activity.arch.helper.BaseViewModelFactory
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.appreciation.viewmodel.AppreciationViewModel
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.contactus.ContactUsViewModel
import com.onourem.android.activity.ui.counselling.CounsellingViewModel
import com.onourem.android.activity.ui.friends.FriendsViewModel
import com.onourem.android.activity.ui.games.viewmodel.*
import com.onourem.android.activity.ui.invite.InviteFriendsViewModel
import com.onourem.android.activity.ui.message.ConversationsViewModel
import com.onourem.android.activity.ui.notifications.NotificationsViewModel
import com.onourem.android.activity.ui.people.UserListViewModel
import com.onourem.android.activity.ui.profile.ProfileViewModel
import com.onourem.android.activity.ui.report.ReportProblemViewModel
import com.onourem.android.activity.ui.reviewus.ReviewUsViewModel
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.splash.SplashViewModel
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.viewmodel.DashboardViewModel
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {
    @Binds
    abstract fun bindViewModelFactory(factory: BaseViewModelFactory?): ViewModelProvider.Factory?

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    abstract fun bindOnboardingViewModel(onboardingViewModel: OnboardingViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(ContactUsViewModel::class)
    abstract fun bindContactUsViewModel(contactUsViewModel: ContactUsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(FriendsViewModel::class)
    abstract fun bindFriendsViewModel(friendsViewModel: FriendsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(InviteFriendsViewModel::class)
    abstract fun bindInviteFriendsViewModel(inviteFriendsViewModel: InviteFriendsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindNotificationsViewModel(notificationsViewModel: NotificationsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(ReportProblemViewModel::class)
    abstract fun bindReportProblemViewModel(reportProblemViewModel: ReportProblemViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(ReviewUsViewModel::class)
    abstract fun bindReviewUsViewModel(reviewUsViewModel: ReviewUsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(SurveyViewModel::class)
    abstract fun bindSurveyViewModel(surveyViewModel: SurveyViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(splashViewModel: SplashViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindDashboardViewModel(dashboardViewModel: DashboardViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(QuestionGamesViewModel::class)
    abstract fun bindQuestionGamesViewModel(questionGamesViewModel: QuestionGamesViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(AppreciationViewModel::class)
    abstract fun bindAppreciationViewModel(appreciationViewModel: AppreciationViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(AnswerQuestionViewModel::class)
    abstract fun bindAnswerQuestionViewModel(answerQuestionViewModel: AnswerQuestionViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(MediaPickerViewModel::class)
    abstract fun bindMediaPickerViewModel(mediaPickerViewModel: MediaPickerViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(SelectPrivacyViewModel::class)
    abstract fun bindSelectPrivacyViewModel(selectPrivacyViewModel: SelectPrivacyViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(CommentsViewModel::class)
    abstract fun bindCommentsViewModel(commentsViewModel: CommentsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(UserListViewModel::class)
    abstract fun bindUserListViewModel(userListViewModel: UserListViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(GamesReceiverViewModel::class)
    abstract fun bindGamesReceiverViewModel(viewModel: GamesReceiverViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(UserActionViewModel::class)
    abstract fun bindUserActionViewModel(viewModel: UserActionViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(OneToManyViewModel::class)
    abstract fun bindOneToManyViewModel(viewModel: OneToManyViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(DToOneViewModel::class)
    abstract fun bindDToOneViewModel(viewModel: DToOneViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(OneToOneViewModel::class)
    abstract fun bindOneToOneViewModel(viewModel: OneToOneViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(TaskAndMessageViewModel::class)
    abstract fun bindTaskAndMessageViewModel(viewModel: TaskAndMessageViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(MediaOperationViewModel::class)
    abstract fun bindMediaOperationViewModel(viewModel: MediaOperationViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(ConversationsViewModel::class)
    abstract fun bindConversationsViewModel(viewModel: ConversationsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(AdminViewModel::class)
    abstract fun bindAdminViewModel(viewModel: AdminViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(FriendCircleGameViewModel::class)
    abstract fun bindCircleViewModel(viewModel: FriendCircleGameViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(CounsellingViewModel::class)
    abstract fun bindCounsellingViewModel(viewModel: CounsellingViewModel?): ViewModel?
}