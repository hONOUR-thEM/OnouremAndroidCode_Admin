package com.onourem.android.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.*
import com.onourem.android.activity.ui.audio.playback.Song
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    dashboardRepository: DashboardRepositoryImpl,
    onboardingRepository: OnboardingRepositoryImpl,
    mediaDataBaseOperationRepository: MediaDataBaseOperationRepositoryImpl,
    notificationCounterRepository: NotificationCounterRepositoryImpl,
    questionGamesRepository: QuestionGamesRepositoryImpl
) : ViewModel(), DashboardRepository {
    private val dashboardRepository: DashboardRepository
    private val onboardingRepository: OnboardingRepositoryImpl
    private val notificationCounterRepository: NotificationCounterRepositoryImpl
    private val questionGamesRepository: QuestionGamesRepositoryImpl
    private val expressionListLiveData = MutableLiveData<UserExpressionList>()
    private val counsellingLiveData = MutableLiveData<UserExpressionList?>()
    val showHomeBadge = MutableLiveData<Boolean>()
    val updateWatchlistResponse = MutableLiveData<GetWatchListResponse>()
    val showVocalsFragment = MutableLiveData<Boolean>()
    val dismissMoodsDialog = MutableLiveData<String?>()
    val removeSurveyItem = MutableLiveData<String?>()
    val dismissMoodsReasonDialog = MutableLiveData<String?>()
    val updateWatchList = MutableLiveData<Boolean>()
    val showInAppReview = MutableLiveData<Boolean>()
    val callShowBadges = MutableLiveData<Boolean>()
    val isMoodsDialogShowing = MutableLiveData<Boolean>()
    val moodsDialogShowingAfterOneDay = MutableLiveData<Boolean>()
    val showFunCards = MutableLiveData<Boolean>()
    val showAudioVocals = MutableLiveData<Boolean>()
    val updateTrendingData = MutableLiveData<Boolean>()
    val showCreateFab = MutableLiveData<Boolean>()
    val notificationInfoListMutableLiveData = MutableLiveData<String>()
    val updateUI = MutableLiveData<Song>()
    val updatePaymentStatus = MutableLiveData<PaymentStatus>()
    val updateFreePaymentStatus = MutableLiveData<String>()
    val updateScrollPosition = MutableLiveData<Song>()

    private var loginDayActivityInfoList = MutableLiveData<MutableList<LoginDayActivityInfoList>>()
    private var loginDayActivityInfo = MutableLiveData<LoginDayActivityInfoList>()
    private var addItemsToLoginDayActivityInfoList = MutableLiveData<MutableList<LoginDayActivityInfoList>>()

    override fun moodExpressions(): LiveData<ApiResponse<ExpressionDataResponse>> {
        return dashboardRepository.moodExpressions()
    }

    override fun updateUserMood(expressionId: String, timeSpentOnScreen: String): LiveData<ApiResponse<UpdateMoodResponse>> {
        return dashboardRepository.updateUserMood(expressionId, timeSpentOnScreen)
    }

    override fun logout(): LiveData<ApiResponse<LogoutResponse>>? {
        dashboardRepository.logout()
            ?.observeForever { }

        return null
    }

    override fun getUserWatchList(notificationById: String): LiveData<ApiResponse<GetWatchListResponse>> {
        return dashboardRepository.getUserWatchList(notificationById)
    }

    override fun applyDiscountCode(code: String): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>> {
        return dashboardRepository.applyDiscountCode(code)
    }

    override fun getOrderInfo(amount: String, currency: String, packageCode: String, discountCode: String): LiveData<ApiResponse<GetOrderInfoRes>> {
        return dashboardRepository.getOrderInfo(amount, currency,packageCode, discountCode)
    }

    override fun getFreeSubscriptions(
        packageId: String,
        activationDate: String
    ): LiveData<ApiResponse<GetFreeSubscriptionsResponse>> {
        return dashboardRepository.getFreeSubscriptions(packageId, activationDate)
    }

    override fun updateUserSubscriptionDetails(packageId: String, discountCode: String): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateUserSubscriptionDetails(packageId, discountCode)
    }

    override fun updateOrderStatus(
        id: String,
        orderResult: String,
        packageId: String,
        discountCode: String,
        orderStatus: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateOrderStatus(id, orderResult, packageId, discountCode, orderStatus)
    }

    override fun cancelWatchListPendingRequest(id: String): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.cancelWatchListPendingRequest(id)
    }

    override fun acceptPendingWatchRequest(id: String): LiveData<ApiResponse<AcceptPendingWatchResponse>> {
        return dashboardRepository.acceptPendingWatchRequest(id)
    }

    override fun cancelWatchListRequest(id: String): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.cancelWatchListRequest(id)
    }

    override fun watchFriendList(): LiveData<ApiResponse<GetWatchFriendListResponse>> {
        return dashboardRepository.watchFriendList()
    }

    override fun addUserToWatchList(id: String): LiveData<ApiResponse<AcceptPendingWatchResponse>> {
        return dashboardRepository.addUserToWatchList(id)
    }

    override fun updateAndroidNotificationStatus(notificationStatus: String): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateAndroidNotificationStatus(notificationStatus)
    }

    override fun getQuestionGameWatchListAndSurveyInfo(screenId: String): LiveData<ApiResponse<GetQuestionGameWatchListAndSurveyInfoResponse>> {
        return dashboardRepository.getQuestionGameWatchListAndSurveyInfo(screenId)
    }

    override fun appUpgradeInfo(): LiveData<ApiResponse<GetAppUpgradeInfoResponse>> {
        return dashboardRepository.appUpgradeInfo()
    }

    override fun updateAppShortLink(
        linkUserId: String,
        shortUrl: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateAppShortLink(linkUserId, shortUrl)
    }

    override fun getFunCards(linkUserId: String): LiveData<ApiResponse<GetFunCardsResponse>> {
        return dashboardRepository.getFunCards(linkUserId)
    }

    override fun addNetworkErrorUserInfo(
        serviceName: String,
        networkErrorCode: String,
        userId: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.addNetworkErrorUserInfo(serviceName, networkErrorCode, userId)
    }

    override fun updateUserMoodReason(reason: String): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateUserMoodReason(reason)
    }

    override fun updateExternalActivityInfo(item: LoginDayActivityInfoList): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateExternalActivityInfo(item)
    }

    override fun updateAverageTimeInfo(
        startTime: String,
        endTime: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateAverageTimeInfo(startTime, endTime)
    }

    override fun userMoodResponseMsg(): LiveData<ApiResponse<GetUserMoodResponseMsgResponse>> {
        return dashboardRepository.userMoodResponseMsg()
    }

    override fun getOrderInfoByAdmin(id: String): LiveData<ApiResponse<CheckOrderInfoResponse>> {
        return dashboardRepository.getOrderInfoByAdmin(id)
    }

    override fun updateOrderInfoByAdmin(orderInfo: OrderInfo): LiveData<ApiResponse<StandardResponse>> {
        return dashboardRepository.updateOrderInfoByAdmin(orderInfo)
    }

    override fun userMoodHistory(): LiveData<ApiResponse<GetUserHistoryResponse>> {
        return dashboardRepository.userMoodHistory()
    }

    override fun getSubscriptionPackageInfo(): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>> {
        return dashboardRepository.getSubscriptionPackageInfo()
    }

    override fun getUserCurrentSubscriptions(): LiveData<ApiResponse<GetUserCurrentSubscriptionResponse>> {
        return dashboardRepository.getUserCurrentSubscriptions()
    }

    fun sendPushNotificationToLinkUser(
        linkUserId: String,
        screenId: String
    ): LiveData<ApiResponse<SendPushNotificationToLinkUserResponse>> {
        return onboardingRepository.sendPushNotificationToLinkUser(linkUserId, screenId)
    }

    fun getUserLinkInfo(
        linkFor: String?,
        playGroupId: String?,
        screenId: String?,
        activityText: String?
    ): LiveData<ApiResponse<GetUserLinkInfoResponse>> {
        return questionGamesRepository.getUserLinkInfo(linkFor, playGroupId, screenId, activityText)
    }

    val notificationCount: LiveData<Int>
        get() = notificationCounterRepository.notificationCount()
    val messageNotificationCount: LiveData<String>
        get() = notificationCounterRepository.messageNotificationCount()

    //    public LiveData<Integer> getSilentNotificationCountReset() {
    //        return counterRepository.getSilentNotificationCount();
    //    }
    val homeBadge: LiveData<Int>
        get() = notificationCounterRepository.notificationCount()
    val selectedExpression: LiveData<UserExpressionList>
        get() = expressionListLiveData

    val selectedExpressionNeedCounselling: MutableLiveData<UserExpressionList?>
        get() = counsellingLiveData

    fun setSelectedExpression(item: UserExpressionList) {
        expressionListLiveData.value = item
    }

    fun setSelectedExpressionNeedCounselling(item: UserExpressionList?) {
        counsellingLiveData.value = item
    }

    fun setShowHomeBadge(showBadge: Boolean) {
        showHomeBadge.value = showBadge
    }

    fun updateTrendingData(updateTrending: Boolean) {
        updateTrendingData.value = updateTrending
    }

    val getTrendingDataRefresh: LiveData<Boolean>
        get() = updateTrendingData

    fun setShowVocalsFragment(showBadge: Boolean) {
        showVocalsFragment.value = showBadge
    }

    fun setCallShowBadges(showBadge: Boolean) {
        callShowBadges.value = showBadge
    }

    fun setIsMoodsDialogShowing(showing: Boolean) {
        isMoodsDialogShowing.value = showing
    }

    fun setMoodsDialogShowingAfterOneDay(showing: Boolean) {
        moodsDialogShowingAfterOneDay.value = showing
    }

    fun setNotificationInfoListMutableLiveData(reload: String) {
        notificationInfoListMutableLiveData.value = reload
    }

    fun setUpdateUI(song: Song) {
        updateUI.value = song
    }

    fun setUpdatePaymentStatus(status: PaymentStatus) {
        updatePaymentStatus.value = status
    }

    fun setUpdateFreePaymentStatus(status: String) {
        updateFreePaymentStatus.value = status
    }

    fun setUpdateScrollPosition(song: Song) {
        updateScrollPosition.value = song
    }

    fun setShowInAppReview(showInAppReviewObj: Boolean) {
        showInAppReview.value = showInAppReviewObj
    }

    fun setShowFunCards(showFunCardsObj: Boolean) {
        showFunCards.value = showFunCardsObj
    }

    fun setShowAudioVocals(showFunCardsObj: Boolean) {
        showAudioVocals.value = showFunCardsObj
    }

    fun setLoginDayActivityInfoList(list: MutableList<LoginDayActivityInfoList>) {
        loginDayActivityInfoList.value = list
    }

    val getLoginDayActivityInfoList: MutableLiveData<MutableList<LoginDayActivityInfoList>>
        get() = loginDayActivityInfoList

    fun updateLoginDayActivityInfoList(list: MutableList<LoginDayActivityInfoList>) {
        loginDayActivityInfoList.value!!.addAll(list)
    }


    fun setShowCreateFab(show: Boolean) {
        showCreateFab.value = show
    }

    val getLoginDayActivityInfo: MutableLiveData<LoginDayActivityInfoList>
        get() = loginDayActivityInfo

    fun setLoginDayActivity(item: LoginDayActivityInfoList) {
        loginDayActivityInfo.value = item
    }

    fun setDismissMoodsDialog(showFunCardsObj: String) {
        dismissMoodsDialog.value = showFunCardsObj
    }

    fun setDismissMoodsDialogConsumed() {
        dismissMoodsDialog.value = null
    }

    fun setDismissMoodsReasonDialog(showFunCardsObj: String) {
        dismissMoodsReasonDialog.value = showFunCardsObj
    }

    fun setDismissMoodsDialogReasonConsumed() {
        dismissMoodsReasonDialog.value = null
    }

    fun setUpdateWatchList(updateWatchList: Boolean) {
        this.updateWatchList.value = updateWatchList
    }

    fun resetSilent() {
        notificationCounterRepository.resetSilent()
    }

    fun increaseSilentCount(userId: String) {
        notificationCounterRepository.increaseSilentCount(userId)
    }

    val notificationSilentCount: LiveData<String>
        get() = notificationCounterRepository.notificationSilentCount()

    fun setUpdateWatchlistResponse(updateWatchList: GetWatchListResponse) {
        updateWatchlistResponse.value = updateWatchList
    }

    fun removeSurveyItem(surveyId: String) {
        removeSurveyItem.value = surveyId
    }

    fun removeSurveyItemConsumed() {
        removeSurveyItem.value = null
    }

    init {
        this.dashboardRepository = dashboardRepository
        this.onboardingRepository = onboardingRepository
        this.notificationCounterRepository = notificationCounterRepository
        this.questionGamesRepository = questionGamesRepository
    }
}