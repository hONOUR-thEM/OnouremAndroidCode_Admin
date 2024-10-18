package com.onourem.android.activity.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse


interface QuestionGamesRepository {
    fun userPlayGroups(): LiveData<ApiResponse<PlayGroupsResponse>>
    fun getUserPlayGroupsById(playGroupId: String?): LiveData<ApiResponse<PlayGroupsResponse>>
    fun userActivityInfoNew(): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun getUserActivityGroupInfo(playGroupId: String?): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun updateActivityMemberNumber(playGroupId: String?): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun createPlayGroup(
        groupName: String?,
        playGroupUserId: String?,
        allCanAsk: Boolean,
        playGroupCategoryId: String?,
        playGroupLanguageId: String?
    ): LiveData<ApiResponse<CreatePlayGroupResponse>>

    fun getUserLinkInfo(
        linkFor: String?,
        playGroupId: String?,
        screenId: String?,
        activityText: String?
    ): LiveData<ApiResponse<GetUserLinkInfoResponse>>

    fun getPlayGroupUsers(playGroupId: String?): LiveData<ApiResponse<GetPlayGroupUsersResponse>>

    fun getNextPlayGroupUsers(playGroupId: String?, playGroupIdUserIds : String?): LiveData<ApiResponse<GetPlayGroupUsersResponse>>

    fun addPlayGroupUser(
        playGroupId: String?,
        addUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun removePlayGroupUser(
        playGroupId: String?,
        removeUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun exitPlayGroupUser(
        playGroupId: String?,
        exitUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun updatePlayGroupName(
        playGroupId: String?,
        playGroupName: String?,
        playGroupCategoryId: String?,
        playGroupLanguageId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun updateAllCanSeeFlag(
        playGroupId: String?,
        allCanSeeFlag: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun addPlayGroupAdmin(
        playGroupId: String?,
        adminStatus: String?,
        userId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    //Privacy Group
    fun getAllGroups(screenId: String?): LiveData<ApiResponse<GetAllGroupsResponse>>
    fun deleteGroup(groupId: String?): LiveData<ApiResponse<GetAllGroupsResponse>>
    fun createCustomGroup(
        groupName: String?,
        addGroupUserList: String?
    ): LiveData<ApiResponse<GetAllGroupsResponse>>

    fun removePrivacyGroupUser(
        privacyGroupId: String?,
        removeUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun updatePrivacyGroupName(
        privacyGroupId: String?,
        privacyGroupName: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun addPrivacyGroupUser(
        groupId: String?,
        groupUserId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    //Phase 3
    fun getAllGroupUsers(
        groupId: String?,
        groupName: String?
    ): LiveData<ApiResponse<GetAllGroupsResponse>>

    fun userPreviousActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun userCreatedActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun getActivityGroups(activityId: String?): LiveData<ApiResponse<GetActivityGroupsResponse>>
    fun createGameActivity(gameActivityRequest: CreateGameActivityRequest): LiveData<ApiResponse<GameActivityUpdateResponse>>
    fun updateActivityMemberNumberP3(playGroupId: Int): LiveData<ApiResponse<StandardResponse>>
    fun getNextUserActivity(userActivityRequest: UserActivityRequest): LiveData<ApiResponse<UserActivityResponse>>
    fun getNextUserActivityGroup(userActivityRequest: UserActivityRequest): LiveData<ApiResponse<UserActivityResponse>>
    fun createQuestion(
        text: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>

    fun getUserActivityPlayGroupsNames(activityId: String?): LiveData<ApiResponse<PlayGroupsResponse>>
    fun questionFilterInfo(): LiveData<ApiResponse<GetQuestionFilterInfoResponse>>
    fun getUserFriendAnsweredActivityInfo(linkUserId: String?): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun userLoginDayActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun userAnsweredActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun loginUserCreatedActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>>
    fun getNextUserLoginDayActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    fun getNextUserAnsweredActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    fun getZZZNextUserAnsweredActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    fun getYYYNextUserLoginDayActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    fun editQuestion(
        activityId: String?,
        text: String?
    ): LiveData<ApiResponse<EditQuestionResponse>>

    fun deleteQuestion(activityId: String?): LiveData<ApiResponse<EditQuestionResponse>>
    fun ignoreQuestion(activityId: String?): LiveData<ApiResponse<StandardResponse>>?
    fun playGroupCategory(): LiveData<ApiResponse<GetPlayGroupCategories>>
    fun getTopPriorityActivityInfo(linkUserId: String?): LiveData<ApiResponse<NewActivityInfoResponse>>
    fun getTopPriorityActivityList(activityIds: String?): LiveData<ApiResponse<NewActivityInfoResponse>>
    fun getNextTopPriorityActivityInfo(activityIds: String?): LiveData<ApiResponse<NewActivityInfoResponse>>
    fun getRemainingTopPriorityActivityIdList(
        cardIds: String?,
        surveyIds: String?,
        remainingExternalActIds: String?,
        remainingPostIds: String?
    ): LiveData<ApiResponse<RemainingActivityIdsResponse>>

    fun inviteFriendImageInfo(): LiveData<ApiResponse<GetInviteFriendImageInfoResponse>>
    fun ignoreSurvey(surveyId: String?): LiveData<ApiResponse<StandardResponse>>
    fun questionsForGuestUser(): LiveData<ApiResponse<NewActivityInfoResponse>>

    //Admin

    fun getPlayGroupCategory(): LiveData<ApiResponse<GetPlayGroupCategories>>


}