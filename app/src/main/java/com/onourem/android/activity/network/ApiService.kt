package com.onourem.android.activity.network

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("getAppDemoForUser")
    @JvmSuppressWildcards
    fun intro(
        @HeaderMap headers: Map<String, String>,
        @Body introRequest: IntroRequest
    ): LiveData<ApiResponse<IntroResponse>>

    @POST("login")
    @JvmSuppressWildcards
    fun login(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<LoginResponse>>

    @POST("forgetPassword")
    @JvmSuppressWildcards
    fun forgetPassword(
        @HeaderMap headers: Map<String, String>,
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): LiveData<ApiResponse<ForgotPasswordResponse>>

    @POST("register")
    @JvmSuppressWildcards
    fun register(
        @HeaderMap headers: Map<String, String>,
        @Body signUpRequest: SignUpRequest
    ): LiveData<ApiResponse<SignUpResponse>>

    @POST("resendEmailVerification")
    @JvmSuppressWildcards
    fun resendEmailVerification(
        @HeaderMap headers: Map<String, String>,
        @Body stringMap: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getRegistrationStatus")
    @JvmSuppressWildcards
    fun getRegistrationStatus(
        @HeaderMap headers: Map<String, String>,
        @Body stringMap: Map<String, Any>
    ): LiveData<ApiResponse<LoginResponse>>

    @POST("updateDateOfBirth")
    @JvmSuppressWildcards
    fun  //"dateOfBirth" : dateOfBirth,"screenId" : "6", "serviceName" : "updateDateOfBirth"
            updateDateOfBirth(
        @HeaderMap headers: Map<String, String>,
        @Body stringMap: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    //Facebook API
    @POST("socialLogin")
    @JvmSuppressWildcards
    fun socialLogin(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<LoginResponse>>

    @POST("registerSocial")
    @JvmSuppressWildcards
    fun registerSocial(
        @HeaderMap headers: Map<String, String>,
        @Body signUpRequest: SignUpRequest
    ): LiveData<ApiResponse<LoginResponse>>

    @POST("sendPushNotificationToLinkUser")
    @JvmSuppressWildcards
    fun sendPushNotificationToLinkUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<SendPushNotificationToLinkUserResponse>>

    //https://onourem.net/onouremweb/bnn/ (If user comes through link)
    //https://onourem.net/onouremweb/bnn/socialLogin (If user does not user link)
    //https://onourem.net/onouremweb/bnn/registerSocial
    //https://onourem.net/onouremweb/bnn/getBlockUserList
    //https://onourem.net/onouremweb/bnn/sendPushNotificationToLinkUser
    //----------------x---------------------x---------------------question games - Phase 1----------------------x---------------------x-----------------------x
    //Survey Webservices
    @POST("getSurveyDataForGuestUser")
    @JvmSuppressWildcards
    fun getSurveyDataForGuestUser(
        @HeaderMap headers: Map<String, String>,
        @Body anonymousSurveyRequest: AnonymousSurveyRequest
    ): LiveData<ApiResponse<AnonymousSurveyResponse>>

    @POST("getSurveyData")
    @JvmSuppressWildcards
    fun getSurveyData(
        @HeaderMap headers: Map<String, String>,
        @Body anonymousSurveyRequest: AnonymousSurveyRequest
    ): LiveData<ApiResponse<AnonymousSurveyResponse>>

    @POST("updateSurveyResult")
    @JvmSuppressWildcards
    fun getSurveyUpdate(
        @HeaderMap headers: Map<String, String>,
        @Body anonymousSurveyUpdateRequest: AnonymousSurveyUpdateRequest
    ): LiveData<ApiResponse<AnonymousSurveyUpdateResponse>>

    @POST("getSurveyGraphData")
    @JvmSuppressWildcards
    fun getSurveyGraphData(
        @HeaderMap headers: Map<String, String>,
        @Body statisticSurveyRequest: StatisticSurveyRequest
    ): LiveData<ApiResponse<StatisticSurveyResponse>>

    @POST("getSurveyGraphDataForGuestUser")
    @JvmSuppressWildcards
    fun getSurveyGraphDataForGuestUser(
        @HeaderMap headers: Map<String, String>,
        @Body statisticSurveyRequest: StatisticSurveyRequest
    ): LiveData<ApiResponse<StatisticSurveyResponse>>

    @POST("getUserProfileSurveyAnsweredUnAnswered")
    @JvmSuppressWildcards
    fun  //    @POST("getUserProfileSurey")
            getUserProfileSurey(
        @HeaderMap headers: Map<String, String>,
        @Body userProfileSurveyRequest: UserProfileSurveyRequest
    ): LiveData<ApiResponse<UserProfileSurveyResponse>>

    @POST("getNextUserProfileSurey")
    @JvmSuppressWildcards
    fun getNextUserProfileSurey(
        @HeaderMap headers: Map<String, String>,
        @Body userProfileSurveyRequest: UserProfileSurveyRequest
    ): LiveData<ApiResponse<UserProfileSurveyResponse>>

    //Dashboard Webservices
    @POST("getExpressionAndAppData")
    @JvmSuppressWildcards
    fun getExpressionAndAppData(
        @HeaderMap headers: Map<String, String>,
        @Body expressionDataRequest: Map<String, String>
    ): LiveData<ApiResponse<ExpressionDataResponse>>

    @POST("updateUserMood")
    @JvmSuppressWildcards
    fun updateUserMood(
        @HeaderMap headers: Map<String, String>,
        @Body userMoodRequest: Map<String, String>
    ): LiveData<ApiResponse<UpdateMoodResponse>>

    //logout
    @POST("logout")
    @JvmSuppressWildcards
    fun logout(
        @HeaderMap headers: Map<String, String>,
        @Body logoutRequest: Map<String, Any>
    ): LiveData<ApiResponse<LogoutResponse>>

    //----------------x---------------------x---------------------question games - Phase 2----------------------x---------------------x-----------------------x
    @POST("getUserPlayGroups")
    @JvmSuppressWildcards
    fun getUserPlayGroups(
        @HeaderMap headers: Map<String, String>,
        @Body request: Map<String, Any>
    ): LiveData<ApiResponse<PlayGroupsResponse>>

    @POST("getUserActivityPlayGroupsNames")
    @JvmSuppressWildcards
    fun getUserActivityPlayGroupsNames(
        @HeaderMap headers: Map<String, String>,
        @Body request: Map<String, Any>
    ): LiveData<ApiResponse<PlayGroupsResponse>>

    @POST("getFriendList")
    @JvmSuppressWildcards
    fun getFriendList(
        @HeaderMap headers: Map<String, String>,
        @Body request: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("getUserActivityInfoNew")
    @JvmSuppressWildcards
    fun getUserActivityInfoNew(
        @HeaderMap headers: Map<String, String>,
        @Body request: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    @POST("getLoginUserActivityGroupInfo")
    @JvmSuppressWildcards
    fun getUserActivityGroupInfo(
        @HeaderMap headers: Map<String, String>,
        @Body request: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    @POST("updateActivityMemberNumber")
    @JvmSuppressWildcards
    fun updateActivityMemberNumber(
        @HeaderMap headers: Map<String, String>,
        @Body request: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    @POST("createPlayGroup")
    @JvmSuppressWildcards
    fun createPlayGroup(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<CreatePlayGroupResponse>>

    @POST("getUserLinkInfo")
    @JvmSuppressWildcards
    fun getUserLinkInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserLinkInfoResponse>>

    @POST("getPlayGroupUsersForAndroid")
    @JvmSuppressWildcards
    fun getPlayGroupUsers(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetPlayGroupUsersResponse>>

    @POST("getNextPlayGroupUsers")
    @JvmSuppressWildcards
    fun getNextPlayGroupUsers(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetPlayGroupUsersResponse>>

    @POST("addPlayGroupUser")
    @JvmSuppressWildcards
    fun addPlayGroupUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("removePlayGroupUser")
    @JvmSuppressWildcards
    fun removePlayGroupUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("exitPlayGroupUser")
    @JvmSuppressWildcards
    fun exitPlayGroupUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updatePlayGroupName")
    @JvmSuppressWildcards
    fun updatePlayGroupName(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateAllCanSeeFlag")
    @JvmSuppressWildcards
    fun updateAllCanSeeFlag(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("addPlayGroupAdmin")
    @JvmSuppressWildcards
    fun addPlayGroupAdmin(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("sendFriendRequest")
    @JvmSuppressWildcards
    fun sendFriendRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    @POST("removeFriend")
    @JvmSuppressWildcards
    fun removeFriend(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    @POST("cancelSentRequest")
    @JvmSuppressWildcards
    fun cancelSentRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    @POST("cancelPendingRequest")
    @JvmSuppressWildcards
    fun cancelPendingRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    @POST("acceptPendingRequest")
    @JvmSuppressWildcards
    fun acceptPendingRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    //Privacy Group
    @POST("getAllGroups")
    @JvmSuppressWildcards
    fun getAllGroups(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAllGroupsResponse>>

    @POST("getAllGroupUsers")
    @JvmSuppressWildcards
    fun getAllGroupUsers(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAllGroupsResponse>>

    @POST("deleteGroup")
    @JvmSuppressWildcards
    fun deleteGroup(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAllGroupsResponse>>

    @POST("createCustomGroup")
    @JvmSuppressWildcards
    fun createCustomGroup(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAllGroupsResponse>>

    @POST("removePrivacyGroupUser")
    @JvmSuppressWildcards
    fun removePrivacyGroupUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updatePrivacyGroupName")
    @JvmSuppressWildcards
    fun updatePrivacyGroupName(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("addPrivacyGroupUser")
    @JvmSuppressWildcards
    fun addPrivacyGroupUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    //----------------x---------------------x---------------------Question Games - Phase 3----------------------x---------------------x-----------------------x
    //ques I created
    @POST("getUserPreviousActivityInfo")
    @JvmSuppressWildcards
    fun getUserPreviousActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    @POST("getUserCreatedActivityInfo")
    @JvmSuppressWildcards
    fun getUserCreatedActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    @POST("getActivityGroups")
    @JvmSuppressWildcards
    fun getActivityGroups(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetActivityGroupsResponse>>

    @POST("createGameActivity")
    @JvmSuppressWildcards
    fun createGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body createGameActivityRequest: CreateGameActivityRequest
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("updateActivityMemberNumber")
    @JvmSuppressWildcards
    fun updateActivityMemberNumberP3(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getNextUserActivity")
    @JvmSuppressWildcards
    fun getNextUserActivity(
        @HeaderMap headers: Map<String, String>,
        @Body userActivityRequest: UserActivityRequest
    ): LiveData<ApiResponse<UserActivityResponse>>

    @POST("getNextLoginUserActivityGroup")
    @JvmSuppressWildcards
    fun getNextUserActivityGroup(
        @HeaderMap headers: Map<String, String>,
        @Body userActivityRequest: UserActivityRequest
    ): LiveData<ApiResponse<UserActivityResponse>>

    //WatchList
    @POST("getUserWatchList")
    @JvmSuppressWildcards
    fun getUserWatchList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetWatchListResponse>>

    @POST("getAppUpgradeInfo")
    @JvmSuppressWildcards
    fun getAppUpgradeInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAppUpgradeInfoResponse>>

    @POST("cancelWatchListPendingRequest")
    @JvmSuppressWildcards
    fun cancelWatchListPendingRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("acceptPendingWatchRequest")
    @JvmSuppressWildcards
    fun acceptPendingWatchRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<AcceptPendingWatchResponse>>

    @POST("cancelWatchListRequest")
    @JvmSuppressWildcards
    fun cancelWatchListRequest(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getWatchFriendList")
    @JvmSuppressWildcards
    fun getWatchFriendList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetWatchFriendListResponse>>

    @POST("addUserToWatchList")
    @JvmSuppressWildcards
    fun addUserToWatchList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<AcceptPendingWatchResponse>>

    //----------------x---------------------x--------------------- Phase 4----------------------x---------------------x-----------------------x
    @POST("getPostCategoryListNew")
    @JvmSuppressWildcards
    fun getPostCategoryListNew(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetPostCategoryListNewResponse>>

    @Multipart
    @POST("uploadVideoPost")
    @JvmSuppressWildcards
    fun uploadVideoPost(
        @HeaderMap headers: Map<String, String>,
        @Part data: MultipartBody.Part,
        @Part body: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): LiveData<ApiResponse<UploadPostResponse>>

    @Multipart
    @POST("uploadPost")
    @JvmSuppressWildcards
    fun uploadPost(
        @HeaderMap headers: Map<String, String>,
        @Part postRequest: Part
    ): LiveData<ApiResponse<UploadPostResponse>>

    @POST("uploadJsonPost")
    @JvmSuppressWildcards
    fun uploadJsonPost(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UploadPostResponse>>

    //@POST("getFriendList")
    //@POST("createGameActivity")
    @POST("searchUsersForWriteNew")
    @JvmSuppressWildcards
    fun searchUsersForWriteNew(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("createQuestionForOneToMany")
    @JvmSuppressWildcards
    fun createQuestionForOneToMany(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>

    @POST("getUserActivityPlayGroups")
    @JvmSuppressWildcards
    fun getUserActivityPlayGroups(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<PlayGroupsResponse>>

    @POST("getFriendSuggetionList")
    @JvmSuppressWildcards
    fun getFriendSuggetionList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("getNextFriendSuggetionList")
    @JvmSuppressWildcards
    fun getNextFriendSuggestionList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("getMyFriendList")
    @JvmSuppressWildcards
    fun getMyFriendList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("getUserFriendList")
    @JvmSuppressWildcards
    fun getUserFriendList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("getNextMyFriendList")
    @JvmSuppressWildcards
    fun getNextMyFriendList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("searchUsersForWriteNew")
    @JvmSuppressWildcards
    fun getGlobalSearchResult(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    @POST("getNextGlobalSearchList")
    @JvmSuppressWildcards
    fun getNextGlobalSearchList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserListResponse>>

    //----------------x---------------------x--------------------- Phase 5----------------------x---------------------x-----------------------x
    //    @POST("getOneToManyGameActivityRes") //old
    @POST("getOneToManyGameResponse")
    @JvmSuppressWildcards
    fun  //new
            getOneToManyGameActivityRes(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    @POST("getQuestionResponseForGuestUser")
    @JvmSuppressWildcards
    fun  //new
            getQuestionResponseForGuestUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    @POST("getOneToManyResponseForFriendOfFriend")
    @JvmSuppressWildcards
    fun getOneToManyResponseForFriendOfFriend(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    @POST("getRemainingOneToManyGameResponse")
    @JvmSuppressWildcards
    fun getRemainingOneToManyGameResponse(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    @POST("getUserActivityGroupResponse")
    @JvmSuppressWildcards
    fun getUserActivityGroupResponse(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserActivityGroupResponse>>

    @POST("getNextOneToManyGameActivityRes")
    @JvmSuppressWildcards
    fun getNextOneToManyGameActivityRes(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    @POST("updateActivityTagStatus")
    @JvmSuppressWildcards
    fun updateActivityTagStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>>

    @POST("deleteOneToManyGameActivity")
    @JvmSuppressWildcards
    fun deleteOneToManyGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("reportInappropriateGame")
    @JvmSuppressWildcards
    fun reportInappropriateGame(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getVisibility")
    @JvmSuppressWildcards
    fun getVisibility(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetVisibilityResponse>>

    @POST("updateVisibility")
    @JvmSuppressWildcards
    fun updateVisibility(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UpdateVisibilityResponse>>

    @POST("ignoreOneToManyGameActivity")
    @JvmSuppressWildcards
    fun ignoreOneToManyGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("ignoreOclubActivityForPlaygroup")
    @JvmSuppressWildcards
    fun ignoreOClubActivityForPlaygroup(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("ignoreSurvey")
    @JvmSuppressWildcards
    fun ignoreSurvey(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getDirectToOneGameActivityRes")
    @JvmSuppressWildcards
    fun getDirectToOneGameActivityRes(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetDirectToOneGameActivityResResponse>>

    //    @POST("updateActivityTagStatus")
    //    LiveData<ApiResponse<UpdateActivityTagStatusResponse>> updateActivityTagStatus(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);
    @POST("deleteDirectToOneGameActivity")
    @JvmSuppressWildcards
    fun deleteDirectToOneGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("ignoreDirectToOneGameActivity")
    @JvmSuppressWildcards
    fun ignoreDirectToOneGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("cancelDirectToOneGameActivity")
    @JvmSuppressWildcards
    fun cancelDirectToOneGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    // Comments
    @POST("getPostComments")
    @JvmSuppressWildcards
    fun getPostComments(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<CommentsResponse>>

    @POST("deleteComment")
    @JvmSuppressWildcards
    fun deleteComment(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    // One to one
    @POST("getOneToOneGameActivityRes")
    @JvmSuppressWildcards
    fun getOneToOneGameActivityRes(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<OneToOneGameActivityResResponse>>

    @POST("deleteOneToOneGameActivity")
    @JvmSuppressWildcards
    fun deleteOneToOneGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("ignoreOneToOneGameActivity")
    @JvmSuppressWildcards
    fun ignoreOneToOneGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    @POST("cancelOneToOneGameActivity")
    @JvmSuppressWildcards
    fun cancelOneToOneGameActivity(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    //TaskAndMessage
    @POST("getTaskAndMessageGameActivityRes")
    @JvmSuppressWildcards
    fun getTaskAndMessageGameActivityRes(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>>

    //TaskAndMessage
    @POST("getTaskMessageForPlaygroup")
    @JvmSuppressWildcards
    fun getTaskMessageForPlaygroup(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>>

    @POST("getUserActivityAndGameInfo")
    @JvmSuppressWildcards
    fun getUserActivityAndGameInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserActivityAndGameInfoResponse>>

    @POST("reportAbuse")
    @JvmSuppressWildcards
    fun reportAbuse(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<PostsActionResponse>>

    @POST("deletePosts")
    @JvmSuppressWildcards
    fun deletePosts(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<PostsActionResponse>>

    //----------------x---------------------x--------------------- Phase 6----------------------x---------------------x-----------------------x
    @POST("getUserProfileaData")
    @JvmSuppressWildcards
    fun getUserProfileData(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserProfileDataResponse>>

    @POST("getUserProfilePosts")
    @JvmSuppressWildcards
    fun getUserProfilePosts(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserProfilePostsResponse>>

    @POST("getFriendIdList")
    @JvmSuppressWildcards
    fun getFriendIdList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetFriendIdListResponse>>

    @POST("getNextUserProfilePosts")
    @JvmSuppressWildcards
    fun getNextUserProfilePosts(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>>

    @POST("updateUserCoverAndProfileImage")
    @JvmSuppressWildcards
    fun updateUserCoverAndProfileImage(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UpdateUserCoverAndProfileImageResponse>>

    //Notification
    @POST("getNewNotificationInfo")
    @JvmSuppressWildcards
    fun getNewNotificationInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetNewNotificationInfo>>

    @POST("updateNotificationStaus")
    @JvmSuppressWildcards
    fun updateNotificationStaus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UpdateNotificationStaus>>

    @POST("deleteNotification")
    @JvmSuppressWildcards
    fun deleteNotification(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getNextNoficationInfo")
    @JvmSuppressWildcards
    fun getNextNoficationInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetNextNoficationInfo>>

    // old service - getUserProfileFeeds
    @POST("getPostDataFromNotification")
    @JvmSuppressWildcards
    fun getUserProfileFeeds(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserProfileFeeds>>

    @POST("getActivityInfoForNotificataion")
    @JvmSuppressWildcards
    fun getActivityInfoForNotificataion(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetActivityInfoForNotificataion>>

    @POST("getExternalActivityFromNotification")
    @JvmSuppressWildcards
    fun getExternalActivityFromNotification(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetActivityInfoForNotificataion>>

    @POST("pullNewNotificationInfo")
    @JvmSuppressWildcards
    fun pullNewNotificationInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<PullNewNotificationInfo>>

    @POST("getAllNotificationAlertSettings")
    @JvmSuppressWildcards
    fun getAllNotificationAlertSettings(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAllNotificationAlertSettingsResponse>>

    @POST("getUserAcctDeletedReasonList")
    @JvmSuppressWildcards
    fun getUserAcctDeletedReasonList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetDeleteAccReasonsResponse>>

    @POST("updateNotificationAlertSettings")
    @JvmSuppressWildcards
    fun updateNotificationAlertSettings(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UpdateNotificationAlertSettingsResponse>>

    @POST("updatePreferredNotificationTime")
    @JvmSuppressWildcards
    fun updatePreferredNotificationTime(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    //Phase 7
    //Terms
    @POST("getTermsConditions")
    @JvmSuppressWildcards
    fun getTermsConditions(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetTermsConditionsResponse>>

    //Terms
    @POST("getTermsAndConditionAndPolicy")
    @JvmSuppressWildcards
    fun getTermsAndConditionAndPolicy(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetTermsConditionsResponse>>

    //https://onourem.net/onouremweb/bnn/getTermsConditions
    @POST("getBlockUserList")
    @JvmSuppressWildcards
    fun getBlockUserList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<BlockListResponse>>

    @POST("unBlockUser")
    @JvmSuppressWildcards
    fun unBlockUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("blockUser")
    @JvmSuppressWildcards
    fun blockUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("blockAndReportUser")
    @JvmSuppressWildcards
    fun blockAndReportUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("deleteAccount")
    @JvmSuppressWildcards
    fun deleteAccount(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getGeoList")
    @JvmSuppressWildcards
    fun getCountryList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetGeoList>>

    @POST("getGeoList")
    @JvmSuppressWildcards
    fun getStateList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetGeoList>>

    @POST("getGeoList")
    @JvmSuppressWildcards
    fun getCityList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetGeoList>>

    @POST("updateProfile")
    @JvmSuppressWildcards
    fun updateProfile(
        @HeaderMap headers: Map<String, String>,
        @Body updateProfileRequest: UpdateProfileRequest
    ): LiveData<ApiResponse<LoginResponse>>

    @POST("getUserReferenceList")
    @JvmSuppressWildcards
    fun getUserReferenceList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserReferenceList>>

    @POST("setUserReference")
    @JvmSuppressWildcards
    fun setUserReference(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<SetUserReferenceResponse>>

    @POST("validatePassword")
    @JvmSuppressWildcards
    fun validatePassword(
        @HeaderMap headers: Map<String, String>,
        @Body updateProfileRequest: UpdateProfileRequest
    ): LiveData<ApiResponse<LoginResponse>>

    @POST("contactUs")
    @JvmSuppressWildcards
    fun contactUs(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateAndroidDeviceInfo")
    @JvmSuppressWildcards
    fun updateFirebaseToken(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): Call<String>

    @POST("updateAverageTimeInfo")
    @JvmSuppressWildcards
    fun updateAverageTimeInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    //Filter Services Change start -->
    @POST("updateActivityNotificationStatus")
    @JvmSuppressWildcards
    fun updateActivityNotificationStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getQuestionGameWatchListAndSurveyInfo")
    @JvmSuppressWildcards
    fun getQuestionGameWatchListAndSurveyInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetQuestionGameWatchListAndSurveyInfoResponse>>

    @POST("getQuestionFilterInfo")
    @JvmSuppressWildcards
    fun getQuestionFilterInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetQuestionFilterInfoResponse>>

    //FFF
    @POST("getUserFriendAnsweredActivityInfo")
    @JvmSuppressWildcards
    fun getUserFriendAnsweredActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //    //New FFF
    //    @POST("getTopPriorityActivityInfoNew")
    //    LiveData<ApiResponse<NewActivityInfoResponse>> getTopPriorityActivityInfo(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);
    //New FFF
    //    @POST("getTopPriorityActivityWithFriendGame")
//    @POST("getTopPriorityActivityWithPostGame")
    @POST("getTopTranding")
    @JvmSuppressWildcards
    fun getTopPriorityActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<NewActivityInfoResponse>>

    //New Second FFF getTopPriorityActivityListNew
    //    @POST("getTopPriorityActivityListNew")
//    @POST("getTopPriorityActivityAndPostListNew")
    @POST("createTrandingDataNew")
    @JvmSuppressWildcards
    fun getTopPriorityActivityList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<NewActivityInfoResponse>>

    //Next New getNextTopPriorityActivityInfo
//    @POST("getNextTopPriorityActivityInfo")
    @POST("getNextTrandingData")
    @JvmSuppressWildcards
    fun getNextTopPriorityActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<NewActivityInfoResponse>>

    @POST("getRemainingTopPriorityActivityIdList")
    @JvmSuppressWildcards
    fun getRemainingTopPriorityActivityIdList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<RemainingActivityIdsResponse>>

    //FFF Next
    @POST("getNextUserActivity")
    @JvmSuppressWildcards
    fun getNextUserAnsweredActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //AAA
    @POST("getUserLoginDayActivityInfo")
    @JvmSuppressWildcards
    fun getUserLoginDayActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //AAA Next
    @POST("getNextUserLoginDayActivityInfo")
    @JvmSuppressWildcards
    fun getNextUserLoginDayActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //ZZZ
    @POST("getUserAnsweredActivityInfo")
    @JvmSuppressWildcards
    fun getUserAnsweredActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //ZZZ Next
    @POST("getNextUserAnsweredActivityInfo")
    @JvmSuppressWildcards
    fun getZZZNextUserAnsweredActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //YYY
    @POST("getLoginUserCreatedActivityInfo")
    @JvmSuppressWildcards
    fun getLoginUserCreatedActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    //YYY Next
    @POST("getNextUserLoginDayActivityInfo")
    @JvmSuppressWildcards
    fun getYYYNextUserLoginDayActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserActivityInfoResponse>>

    @POST("UpdateAndroidNotificationStatus")
    @JvmSuppressWildcards
    fun updateAndroidNotificationStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("editQuestion")
    @JvmSuppressWildcards
    fun editQuestion(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<EditQuestionResponse>>

    @POST("deleteQuestion")
    @JvmSuppressWildcards
    fun deleteQuestion(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<EditQuestionResponse>>

    @POST("updateResponseIrrelevant")
    @JvmSuppressWildcards
    fun updateResponseIrrelevant(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateAppShortLink")
    @JvmSuppressWildcards
    fun updateAppShortLink(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getPlayGroupCategory")
    @JvmSuppressWildcards
    fun getPlayGroupCategory(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetPlayGroupCategories>>

    @POST("addNetworkErrorUserInfo")
    @JvmSuppressWildcards
    fun addNetworkErrorUserInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getFunCards")
    @JvmSuppressWildcards
    fun getFunCards(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetFunCardsResponse>>

    @POST("getAudioCategory")
    @JvmSuppressWildcards
    fun getAudioCategory(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioCategoryResponse>>

    @POST("getAudioInfo")
    @JvmSuppressWildcards
    fun getAudioInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("getNextAudioInfo")
    @JvmSuppressWildcards
    fun getNextAudioInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("getFriendAudioInfo")
    @JvmSuppressWildcards
    fun getFriendAudioInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("getLoginUserAudioInfo")
    @JvmSuppressWildcards
    fun getLoginUserAudioInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("getAudioInfoLikedByLoginUser")
    @JvmSuppressWildcards
    fun getAudioInfoLikedByLoginUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("getAudioInfoForGuestUser")
    @JvmSuppressWildcards
    fun getAudioInfoForGuestUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("getAudioForOtherUser")
    @JvmSuppressWildcards
    fun getAudioForOtherUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @POST("searchAudioByTitle")
    @JvmSuppressWildcards
    fun searchAudioByTitle(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    @Multipart
    @POST("uploadQuestionData")
    @JvmSuppressWildcards
    fun uploadQuestionData(
        @HeaderMap headers: Map<String, String>,
        @Part data: MultipartBody.Part,
        @Part body: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>

    @Multipart
    @POST("uploadAudioInfo")
    @JvmSuppressWildcards
    fun uploadAudioInfo(
        @HeaderMap headers: Map<String, String>,
        @Part data: MultipartBody.Part,
        @Part body: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("likeAudio")
    @JvmSuppressWildcards
    fun likeAudio(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("unlikeAudio")
    @JvmSuppressWildcards
    fun unLikeAudio(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateAudioVisibility")
    @JvmSuppressWildcards
    fun updateAudioVisibility(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("deleteAudio")
    @JvmSuppressWildcards
    fun deleteAudio(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("reportInappropriateAudio")
    @JvmSuppressWildcards
    fun reportInappropriateAudio(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateAudioTitle")
    @JvmSuppressWildcards
    fun updateAudioTitle(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("addAudioInfoHistory")
    @JvmSuppressWildcards
    fun addAudioInfoHistory(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): Call<String>

    @POST("unfollowAudioCreator")
    @JvmSuppressWildcards
    fun unfollowAudioCreator(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("followAudioCreator")
    @JvmSuppressWildcards
    fun followAudioCreator(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("writeAudioComments")
    @JvmSuppressWildcards
    fun writeAudioComments(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getAudioComments")
    @JvmSuppressWildcards
    fun getAudioComments(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<AudioCommentResponse>>

    @POST("getNextAudioComments")
    @JvmSuppressWildcards
    fun getNextAudioComments(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<AudioCommentResponse>>

    @POST("deleteAudioComments")
    @JvmSuppressWildcards
    fun deleteAudioComments(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("reportInappropriateAudioComment")
    @JvmSuppressWildcards
    fun reportInappropriateAudioComment(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("reportInappropriatePostComment")
    @JvmSuppressWildcards
    fun reportInappropriatePostComment(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("reportInappropriateOneToManyResponseComment")
    @JvmSuppressWildcards
    fun reportInappropriateOneToManyResponseComment(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getConversationsList")
    @JvmSuppressWildcards
    fun getConversations(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetConversationsResponse>>

    @POST("sendMessage")
    @JvmSuppressWildcards
    fun postMessage(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<SendMessageResponse>>

    @POST("getUserMessages")
    @JvmSuppressWildcards
    fun getUserMessages(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetMessagesResponse>>

    @POST("getNextMessages")
    @JvmSuppressWildcards
    fun getNextMessages(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetMessagesResponse>>

    @POST("updateUserMessageReadStatus")
    @JvmSuppressWildcards
    fun updateUserMessageReadStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("deleteConversation")
    @JvmSuppressWildcards
    fun deleteConversation(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateUserChatNotificationReadStatus")
    @JvmSuppressWildcards
    fun updateUserChatNotificationReadStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("deleteUserMessage")
    @JvmSuppressWildcards
    fun deleteUserMessage(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<DeletedMessageResponse>>

    @POST("updateUserMoodReason")
    @JvmSuppressWildcards
    fun updateUserMoodReason(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("updateExternalActivityInfo")
    @JvmSuppressWildcards
    fun updateExternalActivityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getUserMoodResponseMsg")
    @JvmSuppressWildcards
    fun getUserMoodResponseMsg(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserMoodResponseMsgResponse>>

    @POST("getUserMoodHistory")
    @JvmSuppressWildcards
    fun getUserMoodHistory(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserHistoryResponse>>

    @POST("getInviteFriendImageInfo")
    @JvmSuppressWildcards
    fun getInviteFriendImageInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetInviteFriendImageInfoResponse>>

    @POST("getQuestionsForGuestUser")
    @JvmSuppressWildcards
    fun getQuestionsForGuestUser(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<NewActivityInfoResponse>>

    //Friends Circle Api
    @POST("verifyPhoneNumber")
    @JvmSuppressWildcards
    fun verifyPhoneNumber(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<SendOtpResponse>>

    @POST("verifyOTP")
    @JvmSuppressWildcards
    fun verifyOTP(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getQualityQuestions")
    @JvmSuppressWildcards
    fun getQualityQuestions(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetQualityQuestionsResponse>>

    @POST("createQualityQuestionGame")
    @JvmSuppressWildcards
    fun createQualityQuestionGame(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getTaggedUserQualityInfo")
    @JvmSuppressWildcards
    fun getTaggedUserQualityInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<UserQualityResponse>>

    @POST("getVerifiedPhoneNumbers")
    @JvmSuppressWildcards
    fun getVerifiedPhoneNumbers(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetVerifiedPhoneNumbersResponse>>

    @POST("deleteVerifiedPhoneNumber")
    @JvmSuppressWildcards
    fun deleteVerifiedPhoneNumber(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getTaggedByUserList")
    @JvmSuppressWildcards
    fun getTaggedByUserList(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetTaggedByUserListResponse>>

    @POST("updateQualitySeenStatus")
    @JvmSuppressWildcards
    fun updateQualitySeenStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("addQualityQuestionVisibility")
    @JvmSuppressWildcards
    fun addQualityQuestionVisibility(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getSubscriptionPackageInfo")
    @JvmSuppressWildcards
    fun getSubscriptionPackageInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>>

    @POST("getUserCurrentSubscriptions")
    @JvmSuppressWildcards
    fun getUserCurrentSubscriptions(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetUserCurrentSubscriptionResponse>>

    @POST("getOrderInfo")
    @JvmSuppressWildcards
    fun getOrderInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetOrderInfoRes>>

    @POST("updateOrderStatus")
    @JvmSuppressWildcards
    fun updateOrderStatus(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("applyDiscountCode")
    @JvmSuppressWildcards
    fun applyDiscountCode(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>>

    @POST("updateUserSubscriptionDetails")
    @JvmSuppressWildcards
    fun updateUserSubscriptionDetails(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<StandardResponse>>

    @POST("getFreeSubscriptions")
    @JvmSuppressWildcards
    fun getFreeSubscriptions(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetFreeSubscriptionsResponse>>

    @POST("getOrderInfoByAdmin")
    @JvmSuppressWildcards
    fun getOrderInfoByAdmin(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<CheckOrderInfoResponse>>


    @POST("updateOrderInfoByAdmin")
    @JvmSuppressWildcards
    fun updateOrderInfoByAdmin(
        @HeaderMap headers: Map<String, String>,
        @Body orderInfo: OrderInfo
    ): LiveData<ApiResponse<StandardResponse>>


    @POST("getInstitutionCounsellingInfo")
    @JvmSuppressWildcards
    fun getInstitutionCounsellingInfo(
        @HeaderMap headers: Map<String, String>,
        @Body params: Map<String, Any>
    ): LiveData<ApiResponse<GetInstituteCounsellingInfoResponse>>

}