package com.onourem.android.activity.repository

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants.KEY_AUTH_TOKEN
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Constants.KEY_AUTH_TOKEN
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part.Companion.createFormData
import java.io.File
import javax.inject.Inject
import javax.inject.Named


class QuestionGamesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val adminApiService: AdminApiService
) :
    QuestionGamesRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun userPlayGroups(): LiveData<ApiResponse<PlayGroupsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPlayGroupNames"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserPlayGroups(basicAuth.getHeaders(), params)
    }

    override fun getUserPlayGroupsById(playGroupId: String?): LiveData<ApiResponse<PlayGroupsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPlayGroupNames"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playgroupId"] = playGroupId ?: ""
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserPlayGroups(basicAuth.getHeaders(), params)
    }

    override fun userActivityInfoNew(): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserActivityInfoNew"
        params["screenId"] = "12"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserActivityInfoNew(basicAuth.getHeaders(), params)
    }

    override fun getUserActivityGroupInfo(playGroupId: String?): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getLoginUserActivityGroupInfo"
        params["screenId"] = "12"
        params["deviceId"] = uniqueDeviceId ?: ""
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: "" ?: ""
        return apiService.getUserActivityGroupInfo(basicAuth.getHeaders(), params)
    }

    override fun updateActivityMemberNumber(playGroupId: String?): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateActivityMemberNumber"
        params["screenId"] = "12"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        return apiService.updateActivityMemberNumber(basicAuth.getHeaders(), params)
    }

    override fun createPlayGroup(
        groupName: String?,
        playGroupUserId: String?,
        allCanAsk: Boolean,
        playGroupCategoryId: String?,
        playGroupLanguageId: String?
    ): LiveData<ApiResponse<CreatePlayGroupResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createPlayGroup"
        params["screenId"] = "24"
        params["playGroupName"] = groupName ?: ""
        params["allCanAskFlag"] = if (allCanAsk) "Y" else "N"
        params["playGroupUserId"] = playGroupUserId ?: ""
        params["playGroupLanguageId"] = playGroupLanguageId ?: ""
        params["playGroupCategoryId"] = playGroupCategoryId ?: ""
        return apiService.createPlayGroup(basicAuth.getHeaders(), params)
    }

    override fun getUserLinkInfo(
        linkFor: String?,
        acitivityId: String?,
        screenId: String?,
        activityText: String?
    ): LiveData<ApiResponse<GetUserLinkInfoResponse>> { //
        //{ "linkFor":"PlayGroup","playGroupId" : 121,"screenId" : "23", "serviceName" : "getUserLinkInfo"}

        //Game Reciever :  //{"linkFor" : activityType, "reasonId" : activityId,"screenId" : "48", "serviceName" : "getUserLinkInfo" ,"activityId" : activityId, "activityText" : activity.activityText
        // }
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserLinkInfo"
        params["linkFor"] = linkFor ?: ""
        params["screenId"] = screenId ?: ""
        if (screenId.equals("23", ignoreCase = true)) {
            params["playGroupId"] = acitivityId ?: ""
        } else if (screenId.equals("42", ignoreCase = true)) {
            params["reasonId"] = ""
        } else if (screenId.equals("53", ignoreCase = true)) {
            params["audioId"] = acitivityId ?: ""
        } else {
            params["screenId"] = screenId ?: ""
            params["linkFor"] = linkFor ?: ""
            params["reasonId"] = acitivityId ?: ""
            params["activityId"] = acitivityId ?: ""
            params["activityText"] = activityText ?: ""
        }
        return apiService.getUserLinkInfo(basicAuth.getHeaders(), params)
    }

    override fun getPlayGroupUsers(playGroupId: String?): LiveData<ApiResponse<GetPlayGroupUsersResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPlayGroupUsers"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        return apiService.getPlayGroupUsers(basicAuth.getHeaders(), params)
    }

    override fun getNextPlayGroupUsers(
        playGroupId: String?,
        playGroupIdUserIds: String?
    ): LiveData<ApiResponse<GetPlayGroupUsersResponse>> {

        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextPlayGroupUsers"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        params["playGroupIdUserIds"] = playGroupIdUserIds ?: ""
        return apiService.getNextPlayGroupUsers(basicAuth.getHeaders(), params)
    }

    override fun addPlayGroupUser(
        playGroupId: String?,
        addUserId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{"addUserId":123,"playGroupId" : 11,"screenId" : "24", "serviceName" : "addPlayGroupUser"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "addPlayGroupUser"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        params["addUserId"] = addUserId ?: ""
        return apiService.addPlayGroupUser(basicAuth.getHeaders(), params)
    }

    override fun removePlayGroupUser(
        playGroupId: String?,
        removeUserId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{"removeUserId":111,"playGroupId" : 11,"screenId" : "24", "serviceName" : "removePlayGroupUser" }
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "removePlayGroupUser"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        params["removeUserId"] = removeUserId ?: ""
        return apiService.removePlayGroupUser(basicAuth.getHeaders(), params)
    }

    override fun exitPlayGroupUser(
        playGroupId: String?,
        exitUserId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{"exitUserId":121,"playGroupId" : 11,"screenId" : "24", "serviceName" : "exitPlayGroupUser" }
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "exitPlayGroupUser"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        params["exitUserId"] = exitUserId ?: ""
        return apiService.exitPlayGroupUser(basicAuth.getHeaders(), params)
    }

    override fun updatePlayGroupName(
        playGroupId: String?,
        playGroupName: String?,
        playGroupCategoryId: String?,
        playGroupLangId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updatePlayGroupName"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        params["playGroupCategoryId"] = playGroupCategoryId ?: ""
        params["playGroupLanguageId"] = playGroupLangId ?: ""
        params["playGroupName"] = playGroupName ?: ""
        return apiService.updatePlayGroupName(basicAuth.getHeaders(), params)
    }

    override fun updateAllCanSeeFlag(
        playGroupId: String?,
        allCanSeeFlag: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{"playGroupId":11,"screenId" : "24", "serviceName" : "updateAllCanSeeFlag" ,"allCanSeeFlag":allCanAsk}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateAllCanSeeFlag"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["playGroupId"] = playGroupId ?: ""
        params["allCanSeeFlag"] = allCanSeeFlag ?: ""
        return apiService.updateAllCanSeeFlag(basicAuth.getHeaders(), params)
    }

    override fun addPlayGroupAdmin(
        playGroupId: String?,
        adminStatus: String?,
        userId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //"adminStatus":"Y","playGroupId" : 123, "userId" : userId,"screenId" : "24", "serviceName" : "addPlayGroupAdmin"
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "addPlayGroupAdmin"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["reqSource"] = "ANDROID"
        params["userId"] = userId ?: ""
        params["playGroupId"] = playGroupId ?: ""
        params["adminStatus"] = adminStatus ?: ""
        return apiService.addPlayGroupAdmin(basicAuth.getHeaders(), params)
    }

    override fun getAllGroups(screenId: String?): LiveData<ApiResponse<GetAllGroupsResponse>> {
        //{"screenId" : "23", "serviceName" : "getAllGroups"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getAllGroups"
        params["screenId"] = screenId ?: ""
        params["deviceId"] = uniqueDeviceId!!
        return apiService.getAllGroups(basicAuth.getHeaders(), params)
    }

    override fun deleteGroup(groupId: String?): LiveData<ApiResponse<GetAllGroupsResponse>> {
        //{"groupId":121,"screenId" : "23", "serviceName" : "deleteGroup"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteGroup"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["groupId"] = groupId ?: ""
        return apiService.deleteGroup(basicAuth.getHeaders(), params)
    }

    override fun createCustomGroup(
        groupName: String?,
        addGroupUserList: String?
    ): LiveData<ApiResponse<GetAllGroupsResponse>> {
        //{
        // "groupName": 111,
        // "addGroupUserList": 111",
        // "deleteGroupUserList": "",
        // "screenId": "24",
        // "serviceName": "createCustomGroup"
        //}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createCustomGroup"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["groupName"] = groupName ?: ""
        params["addGroupUserList"] = addGroupUserList ?: ""
        return apiService.createCustomGroup(basicAuth.getHeaders(), params)
    }

    override fun removePrivacyGroupUser(
        privacyGroupId: String?,
        removeUserId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{"removeUserId":userId,"privacyGroupId" : self.selectedGroupId,"screenId" : "24", "serviceName" : "removePrivacyGroupUser"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "removePrivacyGroupUser"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["privacyGroupId"] = privacyGroupId ?: ""
        params["removeUserId"] = removeUserId ?: ""
        return apiService.removePrivacyGroupUser(basicAuth.getHeaders(), params)
    }

    override fun updatePrivacyGroupName(
        privacyGroupId: String?,
        privacyGroupName: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{
        // "privacyGroupName": newgroupName,
        // "privacyGroupId": 11,
        // "screenId": "24",
        // "serviceName": "updatePrivacyGroupName"
        //}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updatePrivacyGroupName"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["privacyGroupId"] = privacyGroupId ?: ""
        params["privacyGroupName"] = privacyGroupName ?: ""
        return apiService.updatePrivacyGroupName(basicAuth.getHeaders(), params)
    }

    override fun addPrivacyGroupUser(
        groupId: String?,
        groupUserId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{
        // "groupId": 111,
        // "groupUserId": 111,
        // "screenId": "24",
        // "serviceName": "addPrivacyGroupUser"
        //}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "addPrivacyGroupUser"
        params["screenId"] = "24"
        params["deviceId"] = uniqueDeviceId!!
        params["groupId"] = groupId!!.toInt()
        params["groupUserId"] = groupUserId!!.toInt()
        return apiService.addPrivacyGroupUser(basicAuth.getHeaders(), params)
    }

    override fun getAllGroupUsers(
        groupId: String?,
        groupName: String?
    ): LiveData<ApiResponse<GetAllGroupsResponse>> {
        //"groupId":groupIdData,"groupName" : groupNameData,"screenId" : "24", "serviceName" : "getAllGroupUsers"
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getAllGroupUsers"
        params["screenId"] = "24"
        params["groupId"] = groupId ?: ""
        params["groupName"] = groupName ?: ""
        return apiService.getAllGroupUsers(basicAuth.getHeaders(), params)
    }

    //{
    // "screenId" : "12", "serviceName" : "getUserPreviousActivityInfo"
    //}
    override fun userPreviousActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>> {
        //{
        // "screenId" : "12", "serviceName" : "getUserPreviousActivityInfo"
        //}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserPreviousActivityInfo"
        params["screenId"] = "12"
        return apiService.getUserPreviousActivityInfo(basicAuth.getHeaders(), params)
    }

    //        {
// "screenId" : "12", "serviceName" : "getUserCreatedActivityInfo"
//}
    override fun userCreatedActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>> {
//        {
// "screenId" : "12", "serviceName" : "getUserCreatedActivityInfo"
//}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserCreatedActivityInfo"
        params["screenId"] = "12"
        return apiService.getUserCreatedActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getActivityGroups(activityId: String?): LiveData<ApiResponse<GetActivityGroupsResponse>> {
//        {
//            "activityId":activities[currentActivityIndex].activityID,"screenId" : "8", "serviceName" : "getActivityGroups"
//        }
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getActivityGroups"
        params["screenId"] = "8"
        params["activityId"] = activityId ?: ""
        return apiService.getActivityGroups(basicAuth.getHeaders(), params)
    }

    override fun createGameActivity(gameActivityRequest: CreateGameActivityRequest): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        //{"activityId":activityId,"activityTypeId":activityTypeId,"loginDay":loginDay,"screenId" : "12", "serviceName" : "createGameActivity"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        return apiService.createGameActivity(basicAuth.getHeaders(), gameActivityRequest)
    }

    override fun updateActivityMemberNumberP3(playGroupId: Int): LiveData<ApiResponse<StandardResponse>> {
        //{"playGroupId" : selectedGroupIdForScreen, "screenId" : "12", "serviceName" : "updateActivityMemberNumber"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateActivityMemberNumber"
        params["screenId"] = "12"
        params["playGroupId"] = playGroupId ?: ""
        return apiService.updateActivityMemberNumberP3(basicAuth.getHeaders(), params)
    }

    override fun getNextUserActivity(userActivityRequest: UserActivityRequest): LiveData<ApiResponse<UserActivityResponse>> {
        //{"activityIds":requestData, "screenId" : "12", "serviceName" : "getNextUserActivity", "activityTags" : activityTag,
        // "userParticipationStatus":userParticipationStatus, "activityReason":activityReason, "friendCount" : friendCount,
        // "activityGameResponseId" : activityGameResponseId}
        userActivityRequest.screenId = "12"
        userActivityRequest.serviceName = "getNextUserActivity"
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        return apiService.getNextUserActivity(basicAuth.getHeaders(), userActivityRequest)
    }

    override fun getNextUserActivityGroup(userActivityRequest: UserActivityRequest): LiveData<ApiResponse<UserActivityResponse>> {
        //{"activityIds":requestData, "screenId" : "12", "serviceName" : "getNextUserActivityGroup", "activityTags" : activityTag, "userParticipationStatus":userParticipationStatus, "activityReason":activityReason, "friendCount" : friendCount, "activityGameResponseId" : activityGameResponseId}
        userActivityRequest.screenId = "12"
        userActivityRequest.serviceName = "getNextLoginUserActivityGroup"
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        return apiService.getNextUserActivityGroup(basicAuth.getHeaders(), userActivityRequest)
    }

    override fun createQuestion(
        text: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> {
        //{"text": messageBase64, "templateId": "111", "image": largeBase64String?, "smallPostImage": smallBase64String?, "screenId" : "18", "serviceName" : "createNewQuestion"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        if (!TextUtils.isEmpty(uriVideoPath)) {
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "uploadQuestionData"
            params["text"] = text ?: ""
            if (uriImagePath != null) {
                params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
            } else {
                params["image"] = ""
                params["smallPostImage"] = ""
            }
            params["templateId"] = ""
            params["screenId"] = "18"
            val file = File(uriVideoPath)
            val fileUploadProgressRequestBody =
                FileUploadProgressRequestBody(file, "video/mp4".toMediaType(), progressCallback)
            val boundary = "===" + System.currentTimeMillis() + "==="
            basicAuth.headers["boundary"] = boundary
            val json = Gson().toJson(params)
            basicAuth.headers.remove("Content-Type")
            return apiService.uploadQuestionData(
                basicAuth.getHeaders(), createFormData("questionData", json),
                createFormData("filename", file.name),
                createFormData(
                    "questionVideoUrl",
                    file.name,
                    fileUploadProgressRequestBody
                )
            )
        }
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createNewQuestion"
        params["text"] = text ?: ""

//        try {
//            Objects.requireNonNull(params.put("text", Base64Utility.encodeToString?(text.getBytes(ISysConfig.APP_CHARACTER_ENCODING), Base64.NO_WRAP)))?: "";
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        if (uriImagePath != null) {
            params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
            params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
        } else {
            params["image"] = ""
            params["smallPostImage"] = ""
        }
        params["templateId"] = ""
        params["screenId"] = "18"
        return apiService.createQuestionForOneToMany(basicAuth.getHeaders(), params)
    }

    override fun getUserActivityPlayGroupsNames(activityId: String?): LiveData<ApiResponse<PlayGroupsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserActivityPlayGroupsNames"
        params["screenId"] = "48"
        params["activityId"] = activityId ?: ""
        return apiService.getUserActivityPlayGroupsNames(basicAuth.getHeaders(), params)
    }

    override fun questionFilterInfo(): LiveData<ApiResponse<GetQuestionFilterInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getQuestionFilterInfo"
        params["screenId"] = "21"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getQuestionFilterInfo(basicAuth.getHeaders(), params)
    }

    //New Filters Integration
    override fun getUserFriendAnsweredActivityInfo(linkUserId: String?): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserFriendAnsweredActivityInfo"
        params["screenId"] = "12"
        params["linkUserId"] = linkUserId ?: ""
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserFriendAnsweredActivityInfo(basicAuth.getHeaders(), params)
    }

    //New Filters Integration
    override fun getTopPriorityActivityInfo(linkUserId: String?): LiveData<ApiResponse<NewActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTopPriorityActivityInfo"
        params["screenId"] = "12"
        params["linkUserId"] = linkUserId ?: ""
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getTopPriorityActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getTopPriorityActivityList(activityIds: String?): LiveData<ApiResponse<NewActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTopPriorityActivityList"
        params["screenId"] = "12"
        params["activityIds"] = activityIds ?: ""
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getTopPriorityActivityList(basicAuth.getHeaders(), params)
    }

    override fun getNextTopPriorityActivityInfo(activityIds: String?): LiveData<ApiResponse<NewActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextTopPriorityActivityInfo"
        params["screenId"] = "12"
        params["activityIds"] = activityIds ?: ""
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getNextTopPriorityActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getRemainingTopPriorityActivityIdList(
        cardIds: String?,
        surveyIds: String?,
        remainingExternalActIds: String?,
        remainingPostIds: String?
    ): LiveData<ApiResponse<RemainingActivityIdsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getRemainingTopPriorityActivityIdList"
        params["screenId"] = "12"
        params["remainingCardIds"] = cardIds ?: ""
        params["remainingSurveyIds"] = surveyIds ?: ""
        params["remainingPostIds"] = remainingPostIds ?: ""
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getRemainingTopPriorityActivityIdList(basicAuth.getHeaders(), params)
    }

    override fun inviteFriendImageInfo(): LiveData<ApiResponse<GetInviteFriendImageInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getInviteFriendImageInfo"
        params["screenId"] = "12"
        return apiService.getInviteFriendImageInfo(basicAuth.getHeaders(), params)
    }

    override fun userLoginDayActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserLoginDayActivityInfo"
        params["screenId"] = "12"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserLoginDayActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun userAnsweredActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserAnsweredActivityInfo"
        params["screenId"] = "12"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserAnsweredActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun loginUserCreatedActivityInfo(): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getLoginUserCreatedActivityInfo"
        params["screenId"] = "12"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getLoginUserCreatedActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getNextUserLoginDayActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = serviceName ?: ""
        params["screenId"] = "12"
        params["activityIds"] = activityIds ?: ""
        return apiService.getNextUserLoginDayActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getNextUserAnsweredActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = serviceName ?: "" // getUserFriendAnsweredActivityInfo //
        params["screenId"] = "12"
        params["activityGameResponseId"] = activityIds ?: ""
        return apiService.getNextUserAnsweredActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getZZZNextUserAnsweredActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = serviceName ?: "" // getUserFriendAnsweredActivityInfo //
        params["screenId"] = "12"
        params["activityGameResponseId"] = activityIds ?: ""
        return apiService.getZZZNextUserAnsweredActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun getYYYNextUserLoginDayActivityInfo(
        activityIds: String?,
        serviceName: String?
    ): LiveData<ApiResponse<UserActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = serviceName ?: ""
        params["screenId"] = "12"
        params["activityIds"] = activityIds ?: ""
        return apiService.getYYYNextUserLoginDayActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun editQuestion(
        activityId: String?,
        text: String?
    ): LiveData<ApiResponse<EditQuestionResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "editQuestion"
        params["screenId"] = "12"
        params["activityId"] = activityId ?: ""
        params["activityText"] = text ?: ""
        return apiService.editQuestion(basicAuth.getHeaders(), params)
    }

    override fun deleteQuestion(activityId: String?): LiveData<ApiResponse<EditQuestionResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteQuestion"
        params["screenId"] = "12"
        params["activityId"] = activityId ?: ""
        return apiService.deleteQuestion(basicAuth.getHeaders(), params)
    }

    override fun ignoreQuestion(activityId: String?): LiveData<ApiResponse<StandardResponse>>? {
        return null
    }

    override fun playGroupCategory(): LiveData<ApiResponse<GetPlayGroupCategories>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPlayGroupCategory"
        return apiService.getPlayGroupCategory(basicAuth.getHeaders(), params)
    }

    override fun ignoreSurvey(surveyId: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "ignoreOneToManyGameActivity"
        body["surveyId"] = surveyId ?: ""
        return apiService.ignoreSurvey(basicAuth.getHeaders(), body)
    }

    override fun questionsForGuestUser(): LiveData<ApiResponse<NewActivityInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getQuestionsForGuestUser"
        return apiService.getQuestionsForGuestUser(basicAuth.getHeaders(), body)
    }

    override fun getPlayGroupCategory(): LiveData<ApiResponse<GetPlayGroupCategories>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getPlayGroupCategory"
        return apiService.getPlayGroupCategory(basicAuth.getHeaders(), body)
    }

  }