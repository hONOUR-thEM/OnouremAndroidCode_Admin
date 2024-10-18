package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import com.onourem.android.activity.repository.QuestionGamesRepositoryImpl
import com.onourem.android.activity.repository.DashboardRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import com.onourem.android.activity.repository.OneToManyRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.QuestionGamesRepository
import com.onourem.android.activity.repository.OneToManyRepository
import androidx.lifecycle.MutableLiveData
import com.onourem.android.activity.repository.DashboardRepository
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import android.graphics.Bitmap
import androidx.core.util.Pair
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.bottomsheet.Action
import java.util.ArrayList

class QuestionGamesViewModel @Inject constructor(
    questionGamesRepository: QuestionGamesRepositoryImpl,
    dashboardRepository: DashboardRepositoryImpl,
    friendsRepository: FriendsRepositoryImpl,
    oneToManyRepository: OneToManyRepositoryImpl
) : ViewModel(), QuestionGamesRepository {
     val questionGamesRepository: QuestionGamesRepository
     val oneToManyRepository: OneToManyRepository
     val actionMutableLiveData = MutableLiveData<Action<*>?>()
     val selectedFriendForGroup = MutableLiveData<UserList?>(null)
     val selectedFriendsListForGroup = MutableLiveData<ArrayList<UserList?>?>(null)
     val dashboardRepository: DashboardRepository
     val friendsRepository: FriendsRepositoryImpl

    //private UserListRepositoryImpl userListRepository;
     val gameActivityUpdateStatus =
        MutableLiveData<Pair<String?, LoginDayActivityInfoList>?>(null)
    val reloadUILiveData = MutableLiveData(false)
    val refreshShowBadges = MutableLiveData<Boolean>()
    val inAppReviewPopup = MutableLiveData<Boolean>()
    val updateItem = MutableLiveData<LoginDayActivityInfoList>()
    val nextItemClick = MutableLiveData<Boolean>()
    val refreshEditedItem = MutableLiveData<LoginDayActivityInfoList>()
    val actionOpenPlayGroupListing = MutableLiveData<Boolean>()
    val actionCameFromDashboard = MutableLiveData<Boolean>()
    private var privacyGroup: PrivacyGroup? = null
    private var playGroup: PlayGroup? = null
    fun setNextItemClick(next: Boolean) {
        nextItemClick.value = next
    }

    fun setRefreshShowBadges(refresh: Boolean) {
        refreshShowBadges.value = refresh
    }

    override fun userPlayGroups(): LiveData<ApiResponse<PlayGroupsResponse>>  {
        return questionGamesRepository.userPlayGroups()
    }

    override fun getUserPlayGroupsById(playGroupId: String?): LiveData<ApiResponse<PlayGroupsResponse>>  {
        return questionGamesRepository.getUserPlayGroupsById(playGroupId)
    }

    val friendList: LiveData<ApiResponse<UserListResponse>> 
        get() = friendsRepository.getFriendList("24")

    override fun userActivityInfoNew(): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.userActivityInfoNew()
    }

    override fun getUserActivityGroupInfo(playGroupId: String?): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.getUserActivityGroupInfo(playGroupId)
    }

    override fun updateActivityMemberNumber(playGroupId: String?): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.updateActivityMemberNumber(playGroupId)
    }

    override fun createPlayGroup(
        groupName: String?,
        playGroupUserId: String?,
        allCanAsk: Boolean,
        playGroupCategoryId: String?,
        playGroupLanguageId: String?
    ): LiveData<ApiResponse<CreatePlayGroupResponse>>  {
        return questionGamesRepository.createPlayGroup(
            groupName,
            playGroupUserId,
            allCanAsk,
            playGroupCategoryId,
            playGroupLanguageId
        )
    }

    override fun getPlayGroupUsers(playGroupId: String?): LiveData<ApiResponse<GetPlayGroupUsersResponse>>  {
        return questionGamesRepository.getPlayGroupUsers(playGroupId)
    }

    override fun getNextPlayGroupUsers(
        playGroupId: String?,
        playGroupIdUserIds: String?
    ): LiveData<ApiResponse<GetPlayGroupUsersResponse>> {
        return questionGamesRepository.getNextPlayGroupUsers(playGroupId, playGroupIdUserIds)
    }

    override fun getUserLinkInfo(
        linkFor: String?,
        playGroupId: String?,
        screenId: String?,
        activityText: String?
    ): LiveData<ApiResponse<GetUserLinkInfoResponse>>  {
        return questionGamesRepository.getUserLinkInfo(linkFor, playGroupId, screenId, activityText)
    }

    override fun getAllGroups(screenId: String?): LiveData<ApiResponse<GetAllGroupsResponse>>  {
        return questionGamesRepository.getAllGroups(screenId)
    }

    override fun deleteGroup(groupId: String?): LiveData<ApiResponse<GetAllGroupsResponse>>  {
        return questionGamesRepository.deleteGroup(groupId)
    }

    override fun createCustomGroup(
        groupName: String?,
        addGroupUserList: String?
    ): LiveData<ApiResponse<GetAllGroupsResponse>>  {
        return questionGamesRepository.createCustomGroup(groupName, addGroupUserList)
    }

    override fun removePrivacyGroupUser(
        privacyGroupId: String?,
        removeUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.removePrivacyGroupUser(privacyGroupId, removeUserId)
    }

    override fun updatePrivacyGroupName(
        privacyGroupId: String?,
        privacyGroupName: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.updatePrivacyGroupName(privacyGroupId, privacyGroupName)
    }

    override fun addPrivacyGroupUser(
        groupId: String?,
        groupUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.addPrivacyGroupUser(groupId, groupUserId)
    }

    override fun getAllGroupUsers(
        groupId: String?,
        groupName: String?
    ): LiveData<ApiResponse<GetAllGroupsResponse>>  {
        return questionGamesRepository.getAllGroupUsers(groupId, groupName)
    }

    override fun addPlayGroupUser(
        playGroupId: String?,
        addUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.addPlayGroupUser(playGroupId, addUserId)
    }

    override fun removePlayGroupUser(
        playGroupId: String?,
        removeUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.removePlayGroupUser(playGroupId, removeUserId)
    }

    override fun updatePlayGroupName(
        playGroupId: String?,
        playGroupName: String?,
        playGroupCategoryId: String?,
        playGroupLanguageId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.updatePlayGroupName(
            playGroupId,
            playGroupName,
            playGroupCategoryId,
            playGroupLanguageId
        )
    }

    override fun updateAllCanSeeFlag(
        playGroupId: String?,
        allCanSeeFlag: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.updateAllCanSeeFlag(playGroupId, allCanSeeFlag)
    }

    override fun exitPlayGroupUser(
        playGroupId: String?,
        exitUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.exitPlayGroupUser(playGroupId, exitUserId)
    }

    override fun addPlayGroupAdmin(
        playGroupId: String?,
        adminStatus: String?,
        userId: String?
    ): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.addPlayGroupAdmin(playGroupId, adminStatus, userId)
    }

    fun sendFriendRequest(
        friendUserId: String?,
        screenId: String?,
    ): LiveData<ApiResponse<UserActionStandardResponse>>  {
        return friendsRepository.sendFriendRequest(friendUserId, screenId)
    }

    fun removeFriend(
        friendUserId: String?,
        screenId: String?,
    ): LiveData<ApiResponse<UserActionStandardResponse>>  {
        return friendsRepository.removeFriend(friendUserId, screenId)
    }

    fun cancelSentRequest(
        friendUserId: String?,
        screenId: String?,
    ): LiveData<ApiResponse<UserActionStandardResponse>>  {
        return friendsRepository.cancelSentRequest(friendUserId, screenId)
    }

    fun cancelPendingRequest(
        friendUserId: String?,
        screenId: String?,
    ): LiveData<ApiResponse<UserActionStandardResponse>>  {
        return friendsRepository.cancelPendingRequest(friendUserId, screenId)
    }

    fun acceptPendingRequest(
        friendUserId: String?,
        screenId: String?,
    ): LiveData<ApiResponse<UserActionStandardResponse>>  {
        return friendsRepository.acceptPendingRequest(friendUserId, screenId)
    }

    override fun userPreviousActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.userPreviousActivityInfo()
    }

    override fun userCreatedActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.userCreatedActivityInfo()
    }

    override fun getActivityGroups(activityId: String?): LiveData<ApiResponse<GetActivityGroupsResponse>>  {
        return questionGamesRepository.getActivityGroups(activityId)
    }

    override fun createGameActivity(gameActivityRequest: CreateGameActivityRequest): LiveData<ApiResponse<GameActivityUpdateResponse>>  {
        return questionGamesRepository.createGameActivity(gameActivityRequest)
    }

    override fun updateActivityMemberNumberP3(playGroupId: Int): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.updateActivityMemberNumberP3(playGroupId)
    }

    override fun getNextUserActivity(userActivityRequest: UserActivityRequest): LiveData<ApiResponse<UserActivityResponse>>  {
        return questionGamesRepository.getNextUserActivity(userActivityRequest)
    }

    override fun getNextUserActivityGroup(userActivityRequest: UserActivityRequest): LiveData<ApiResponse<UserActivityResponse>>  {
        return questionGamesRepository.getNextUserActivityGroup(userActivityRequest)
    }

    override fun createQuestion(
        text: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>  {
        return questionGamesRepository.createQuestion(
            text,
            uriImagePath,
            uriVideoPath,
            progressCallback
        )
    }

    override fun getUserActivityPlayGroupsNames(activityId: String?): LiveData<ApiResponse<PlayGroupsResponse>>  {
        return questionGamesRepository.getUserActivityPlayGroupsNames(activityId)
    }

    override fun questionFilterInfo(): LiveData<ApiResponse<GetQuestionFilterInfoResponse>>  {
        return questionGamesRepository.questionFilterInfo()
    }

    override fun getUserFriendAnsweredActivityInfo(linkUserId: String?): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.getUserFriendAnsweredActivityInfo(linkUserId)
    }

    override fun getTopPriorityActivityInfo(linkUserId: String?): LiveData<ApiResponse<NewActivityInfoResponse>>  {
        return questionGamesRepository.getTopPriorityActivityInfo(linkUserId)
    }

    override fun getTopPriorityActivityList(activityIds: String?): LiveData<ApiResponse<NewActivityInfoResponse>>  {
        return questionGamesRepository.getTopPriorityActivityList(activityIds)
    }

    override fun getNextTopPriorityActivityInfo(activityIds: String?): LiveData<ApiResponse<NewActivityInfoResponse>>  {
        return questionGamesRepository.getNextTopPriorityActivityInfo(activityIds)
    }

    override fun getRemainingTopPriorityActivityIdList(
        cardIds: String?,
        surveyIds: String?,
        remainingExternalActIds: String?,
        remainingPostIds: String?
    ): LiveData<ApiResponse<RemainingActivityIdsResponse>>  {
        return questionGamesRepository.getRemainingTopPriorityActivityIdList(
            cardIds,
            surveyIds,
            remainingExternalActIds,
            remainingPostIds
        )
    }

    override fun inviteFriendImageInfo(): LiveData<ApiResponse<GetInviteFriendImageInfoResponse>>  {
        return questionGamesRepository.inviteFriendImageInfo()
    }

    override fun userLoginDayActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.userLoginDayActivityInfo()
    }

    override fun userAnsweredActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.userAnsweredActivityInfo()
    }

    override fun loginUserCreatedActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.loginUserCreatedActivityInfo()
    }

    override fun getNextUserLoginDayActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.getNextUserLoginDayActivityInfo(activityIds, serviceName)
    }

    override fun getNextUserAnsweredActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.getNextUserAnsweredActivityInfo(activityIds, serviceName)
    }

    override fun getZZZNextUserAnsweredActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.getZZZNextUserAnsweredActivityInfo(activityIds, serviceName)
    }

    override fun getYYYNextUserLoginDayActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>  {
        return questionGamesRepository.getYYYNextUserLoginDayActivityInfo(activityIds, serviceName)
    }

    override fun editQuestion(
        activityId: String?,
        text: String?
    ): LiveData<ApiResponse<EditQuestionResponse>>  {
        return questionGamesRepository.editQuestion(activityId, text)
    }

    override fun deleteQuestion(activityId: String?): LiveData<ApiResponse<EditQuestionResponse>>  {
        return questionGamesRepository.deleteQuestion(activityId)
    }

    override fun ignoreQuestion(surveyId: String?): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.ignoreSurvey(surveyId)
    }

    override fun playGroupCategory(): LiveData<ApiResponse<GetPlayGroupCategories>>  {
        return questionGamesRepository.playGroupCategory()
    }

    override fun ignoreSurvey(surveyId: String?): LiveData<ApiResponse<StandardResponse>>  {
        return questionGamesRepository.ignoreSurvey(surveyId)
    }

    override fun questionsForGuestUser(): LiveData<ApiResponse<NewActivityInfoResponse>>  {
        return questionGamesRepository.questionsForGuestUser()
    }

    override fun getPlayGroupCategory(): LiveData<ApiResponse<GetPlayGroupCategories>> {
        return questionGamesRepository.playGroupCategory()
    }

    fun ignoreOneToManyGameActivity(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?,
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>  {
        return oneToManyRepository.ignoreOneToManyGameActivity(
            gameId,
            gameResponseId,
            playGroupId,
            activityId
        )
    }

    fun ignoreOClubActivityForPlaygroup(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?,
        oclubActivityId: String?,
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>  {
        return oneToManyRepository.ignoreOClubActivityForPlaygroup(
            gameId,
            gameResponseId,
            playGroupId,
            activityId,
            oclubActivityId
        )
    }

    fun actionPerformed(action: Action<*>) {
        actionMutableLiveData.postValue(action)
    }

    fun actionConsumed() {
        actionMutableLiveData.value = null
    }

    fun getActionMutableLiveData(): LiveData<Action<*>?> {
        return actionMutableLiveData
    }

    fun addNewFriendInGroup(selectedFriendForGroup: UserList) {
        this.selectedFriendForGroup.value = selectedFriendForGroup
    }

    fun resetAddNewFriendInGroup() {
        selectedFriendForGroup.value = null
    }

    fun getSelectedFriendForGroup(): LiveData<UserList?> {
        return selectedFriendForGroup
    }

    fun addNewSelectedFriendInGroup(selectedFriendsForGroup: ArrayList<UserList?>) {
        selectedFriendsListForGroup.value = selectedFriendsForGroup
    }

    fun resetSelectedNewFriendsInGroup() {
        selectedFriendsListForGroup.value = null
    }

    val selectedFriendListForGroup: LiveData<ArrayList<UserList?>?>
        get() = selectedFriendsListForGroup

    fun getPrivacyGroup(): PrivacyGroup {
        if (privacyGroup == null) {
            privacyGroup = PrivacyGroup()
            privacyGroup!!.isNew = true
        }
        return privacyGroup!!
    }

    fun setPrivacyGroup(privacyGroup: PrivacyGroup?) {
        this.privacyGroup = privacyGroup
    }

    fun getPlayGroup(): PlayGroup {
        if (playGroup == null) {
            playGroup = PlayGroup()
            playGroup!!.isNew = true
        }
        return playGroup!!
    }

    fun setPlayGroup(playGroup: PlayGroup?) {
        this.playGroup = playGroup
    }

    fun cancelWatchListRequest(id: String?): LiveData<ApiResponse<StandardResponse>>  {
        return dashboardRepository.cancelWatchListRequest(id!!)
    }

    @JvmName("getGameActivityUpdateStatus1")
    fun getGameActivityUpdateStatus(): MutableLiveData<Pair<String?, LoginDayActivityInfoList>?> {
        return gameActivityUpdateStatus
    }

    fun setGameActivityUpdateStatus(question: Pair<String?, LoginDayActivityInfoList>?) {
        gameActivityUpdateStatus.value = question
    }

    //    public LiveData<Resource<List<UserList>> > getLocalBlockUserList() {
    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    fun reloadUI(): LiveData<Boolean> {
        return reloadUILiveData
    }

    fun reloadUI(reload: Boolean) {
        reloadUILiveData.postValue(reload)
    }

    fun setRefreshEditedItem(item: LoginDayActivityInfoList) {
        refreshEditedItem.value = item
    }

    fun updateItem(): MutableLiveData<LoginDayActivityInfoList> {
        return updateItem
    }

    fun setUpdateItem(item: LoginDayActivityInfoList) {
        updateItem.value = item
    }

    fun actionOpenPlayGroupListingPerformed(action: Boolean) {
        actionOpenPlayGroupListing.postValue(action)
    }

    fun actionOpenPlayGroupListingConsumed() {
        actionOpenPlayGroupListing.value = false
    }

    fun getActionOpenPlayGroupListing(): LiveData<Boolean> {
        return actionOpenPlayGroupListing
    }

    fun actionCameFromDashboard(action: Boolean) {
        actionCameFromDashboard.postValue(action)
    }

    fun actionCameFromDashboardConsumed() {
        actionCameFromDashboard.value = false
    }

    fun getActionCameFromDashboard(): LiveData<Boolean> {
        return actionCameFromDashboard
    }

    fun setInAppReviewPopup(count: Boolean) {
        inAppReviewPopup.value = count
    }

    fun actionInAppReviewConsumed() {
        inAppReviewPopup.value = false
    }

    init {
        this.questionGamesRepository = questionGamesRepository
        this.dashboardRepository = dashboardRepository
        this.friendsRepository = friendsRepository
        this.oneToManyRepository = oneToManyRepository
        //        this.userListRepository = userListRepository;
    }
}