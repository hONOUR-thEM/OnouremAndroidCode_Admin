package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class OneToManyRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    OneToManyRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    override fun getOneToManyGameActivityRes(
        activityId: String?,
        gameResIdFromOtherFlow: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        //{"activityId":122,"screenId" : "36", "serviceName" : "getOneToManyGameActivityRes"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getOneToManyGameResponse"
        body["activityId"] = activityId ?: ""
        body["gameIdFromOtherFlow"] = gameResIdFromOtherFlow ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.getOneToManyGameActivityRes(basicAuth.getHeaders(), body)
    }

    override fun getQuestionResponseForGuestUser(activityId: String?): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getQuestionResponseForGuestUser"
        body["activityId"] = activityId ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.getQuestionResponseForGuestUser(basicAuth.getHeaders(), body)
    }

    override fun getOneToManyResponseForFriendOfFriend(
        activityId: String?,
        gameResIdFromOtherFlow: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getOneToManyResponseForFriendOfFriend"
        body["gameIdFromOtherFlow"] = gameResIdFromOtherFlow ?: ""
        body["activityId"] = activityId ?: ""
        return apiService.getOneToManyResponseForFriendOfFriend(basicAuth.getHeaders(), body)
    }

    override fun getRemainingOneToManyGameResponse(
        activityId: String?,
        gameResIdFromOtherFlow: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getRemainingOneToManyGameResponse"
        body["gameIdFromOtherFlow"] = gameResIdFromOtherFlow ?: ""
        body["activityId"] = activityId ?: ""
        return apiService.getRemainingOneToManyGameResponse(basicAuth.getHeaders(), body)
    }

    override fun getUserActivityGroupResponse(
        activityId: String?,
        playGroupId: String?,
        activityGameResponseId: String?,
        gameResIdFromOtherFlow: String?,
        oclubActivityId: String?
    ): LiveData<ApiResponse<GetUserActivityGroupResponse>> {
        //{"activityId":activity.activityID,"playGroupId":activityPlayGroupId, "screenId" : "36","serviceName" : "getUserActivityGroupResponse", "activityGameResponseId" : activity.activityGameResponseId}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getUserActivityGroupResponse"
        body["activityId"] = activityId ?: ""
        body["playGroupId"] = playGroupId ?: ""
        body["activityGameResponseId"] = activityGameResponseId ?: ""
        body["gameResIdFromOtherFlow"] = gameResIdFromOtherFlow ?: ""
        body["deviceId"] = uniqueDeviceId!!
        body["oclubActivityId"] = oclubActivityId ?: ""

        return apiService.getUserActivityGroupResponse(basicAuth.getHeaders(), body)
    }

    override fun getNextOneToManyGameActivityRes(
        activityPlayGroupId: String?,
        gameIds: String?,
        loginUserLastSeenTime: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        //{"gameIds":requestData,"screenId" : "17", "serviceName" : "getNextGamesForOneToMany", "activityPlayGroupId" : activityPlayGroupId}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "17"
        body["serviceName"] = "getNextGamesForOneToMany"
        body["gameIds"] = gameIds ?: ""
        body["activityPlayGroupId"] = activityPlayGroupId ?: ""
        body["loginUserLastSeenTime"] = loginUserLastSeenTime ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.getNextOneToManyGameActivityRes(basicAuth.getHeaders(), body)
    }

    override fun updateActivityTagStatus(
        activityId: String?,
        gameIds: String?,
        activityType: String?,
        playGroupId: String?,
        gameResponseId: String?,
        oclubActivityId: String?
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>> {
        //{"activityId" : activity.activityID,"screenId" : "36", "serviceName" : "updateActivityTagStatus", "gameIds": gameIds,
        // "activityType" : activity.activityType,"playGroupId":activityPlayGroupId,"gameResponseId":activity.activityGameResponseId}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "updateActivityTagStatus"
        body["deviceId"] = uniqueDeviceId!!
        body["activityId"] = activityId ?: ""
        body["gameIds"] = gameIds ?: ""
        body["activityType"] = activityType ?: ""
        body["playGroupId"] = playGroupId ?: ""
        body["gameResponseId"] = gameResponseId ?: ""
        body["oclubActivityId"] = oclubActivityId ?: ""
        return apiService.updateActivityTagStatus(basicAuth.getHeaders(), body)
    }

    override fun deleteOneToManyGameActivity(
        gameId: String?,
        playGroupId: String?,
        gameResponseId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        //{"gameId" : self.games[loginUserActingOnIndex].gameID,"screenId" : "36", "serviceName" : "deleteOneToManyGameActivity",
        // "gameResponseId": self.games[loginUserActingOnIndex].activityGameResId,"playGroupId": self.games[loginUserActingOnIndex].playGroupId}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "deleteOneToManyGameActivity"
        body["gameId"] = gameId ?: ""
        body["playGroupId"] = playGroupId ?: ""
        body["gameResponseId"] = gameResponseId ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.deleteOneToManyGameActivity(basicAuth.getHeaders(), body)
    }

    override fun reportInAppropriateGame(
        gameId: String?,
        gameResponseId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        //{"gameId":gameId,"screenId" : "36", "serviceName" : "ReportInAppropriateGame" }
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "ReportInAppropriateGame"
        body["gameId"] = gameId ?: ""
        body["gameResponseId"] = gameId ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.reportInappropriateGame(basicAuth.getHeaders(), body)
    }

    override fun getVisibility(
        postId: String?,
        gameId: String?
    ): LiveData<ApiResponse<GetVisibilityResponse>> {
        //{"postId":postId,"gameId": gameId, "screenId" : "29", "serviceName" : "getVisibility"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "getVisibility"
        body["postId"] = postId ?: ""
        body["gameId"] = gameId ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.getVisibility(basicAuth.getHeaders(), body)
    }

    override fun updateVisibility(
        postId: String?,
        gameId: String?,
        visibileTo: String?,
        pushToDiscover: String?
    ): LiveData<ApiResponse<UpdateVisibilityResponse>> {
        //{"postId":postId, "gameId": gameId, "visibileTo":groupIdList, "pushToDiscover": pushToDiscover,"screenId" : "29",
        // "serviceName" : "updateVisibility"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "29"
        body["serviceName"] = "updateVisibility"
        body["postId"] = postId ?: ""
        body["gameId"] = gameId ?: ""
        body["visibileTo"] = visibileTo ?: ""
        body["pushToDiscover"] = pushToDiscover ?: ""
        body["deviceId"] = uniqueDeviceId!!
        return apiService.updateVisibility(basicAuth.getHeaders(), body)
    }

    override fun ignoreOneToManyGameActivity(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        //{"gameId" : self.games[loginUserActingOnIndex].gameID,"gameResponseId":self.games[loginUserActingOnIndex].activityGameResId,
        // "playGroupId":self.games[loginUserActingOnIndex].playGroupId}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "ignoreOneToManyGameActivity"
        body["deviceId"] = uniqueDeviceId!!
        body["gameId"] = gameId ?: ""
        body["gameResponseId"] = gameResponseId ?: ""
        body["playGroupId"] = playGroupId ?: ""
        body["activityId"] = activityId ?: ""
        return apiService.ignoreOneToManyGameActivity(basicAuth.getHeaders(), body)
    }

    override fun ignoreOClubActivityForPlaygroup(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?,
        oclubActivityId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "ignoreOClubActivityForPlaygroup"
        body["gameId"] = gameId ?: ""
        body["gameResponseId"] = gameResponseId ?: ""
        body["playGroupId"] = playGroupId ?: ""
        body["activityId"] = activityId ?: ""
        body["oclubActivityId"] = oclubActivityId ?: ""
        return apiService.ignoreOClubActivityForPlaygroup(basicAuth.getHeaders(), body)
    }

    override fun updateActivityNotificationStatus(activityId: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "updateActivityNotificationStatus"
        body["activityId"] = activityId ?: ""
        body["action"] = "activity"
        return apiService.updateActivityNotificationStatus(basicAuth.getHeaders(), body)
        //"activityId" : activity.activityID,"screenId" : "36", "serviceName" : "updateActivityNotificationStatus", "action": "activity"
    }

    override fun updateActivityNotificationStatus(
        activityId: String?,
        playGroupId: String?,
        gameId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "updateActivityNotificationStatus"
        body["activityId"] = activityId ?: ""
        body["action"] = "activity"
        body["playGroupId"] = playGroupId ?: ""
        body["gameId"] = gameId ?: ""
        return apiService.updateActivityNotificationStatus(basicAuth.getHeaders(), body)
        //"activityId" : activity.activityID,"screenId" : "36", "serviceName" : "updateActivityNotificationStatus", "action": "activity","playGroupId":activityPlayGroupId,"gameId": gameIds,
    }

    override fun updateResponseIrrelevant(
        gameResponseId: String?,
        playGroupId: String?,
        gameId: String?,
        participantId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["serviceName"] = "updateResponseIrrelevant"
        body["participantId"] = participantId ?: ""
        body["gameResponseId"] = gameResponseId ?: ""
        body["playgroupId"] = playGroupId ?: ""
        body["gameId"] = gameId ?: ""
        return apiService.updateResponseIrrelevant(basicAuth.getHeaders(), body)
    }
}