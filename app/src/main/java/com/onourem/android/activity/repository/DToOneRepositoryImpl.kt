package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.DToOneRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetDirectToOneGameActivityResResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap

class DToOneRepositoryImpl //{"activityId" : activity.activityID,"screenId" : "33", "serviceName" : "updateActivityTagStatus", "gameIds": gameIds, "activityType" : activity.activityType}
//"gameId" : self.games[loginUserActingOnIndex].gameID,"screenId" : "33", "serviceName" : "deleteDirectToOneGameActivity"
//"gameId" : self.games[loginUserActingOnIndex].gameID,"screenId" : "33", "serviceName" : "ignoreDirectToOneGameActivity"
//{"gameId" : self.games[loginUserActingOnIndex].gameID,"screenId" : "33", "serviceName" : "cancelDirectToOneGameActivity"}
@Inject constructor(var apiService: ApiService) : DToOneRepository {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getDirectToOneGameActivityRes(
        activityId: String,
        gameIdToHighlight: String
    ): LiveData<ApiResponse<GetDirectToOneGameActivityResResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "getDirectToOneGameActivityRes"
        body["activityId"] = activityId
        body["gameIdFromOtherFlow"] = gameIdToHighlight
        return apiService.getDirectToOneGameActivityRes(basicAuth.getHeaders(), body)
    }

    override fun updateActivityTagStatus(
        gameIds: String,
        activityId: String,
        activityType: String
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "updateActivityTagStatus"
        body["activityId"] = activityId
        body["gameIds"] = gameIds
        body["activityType"] = activityType
        return apiService.updateActivityTagStatus(basicAuth.getHeaders(), body)
    }

    override fun deleteDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "deleteDirectToOneGameActivity"
        body["gameId"] = gameID
        return apiService.deleteDirectToOneGameActivity(basicAuth.getHeaders(), body)
    }

    override fun ignoreDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "ignoreDirectToOneGameActivity"
        body["gameId"] = gameID
        return apiService.ignoreDirectToOneGameActivity(basicAuth.getHeaders(), body)
    }

    //    @Override
    //    public LiveData<ApiResponse<IgnoreOneToManyGameActivityResponse>> ignoreDirectToOneGameActivity(String gameId, String gameResponseId, String playGroupId) {
    //        //{"gameId" : self.games[loginUserActingOnIndex].gameID,"gameResponseId":self.games[loginUserActingOnIndex].activityGameResId,
    //        // "playGroupId":self.games[loginUserActingOnIndex].playGroupId}
    //        Auth basicAuth = new AuthToken(preferenceHelper.getString(KEY_AUTH_TOKEN));
    //        Map<String, Object> body = new HashMap<>();
    //        body.put("screenId", "29");
    ////        body.put("serviceName", "updateVisibility");
    //        body.put("gameResponseId", gameResponseId);
    //        body.put("gameId", gameId);
    //        body.put("playGroupId", playGroupId);
    //        body.put("deviceId", uniqueDeviceId);
    //        return apiService.ignoreOneToManyGameActivity(basicAuth.getHeaders(), body);
    //    }
    override fun cancelDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "cancelDirectToOneGameActivity"
        body["gameId"] = gameID
        return apiService.cancelDirectToOneGameActivity(basicAuth.getHeaders(), body)
    }

    override fun updateActivityNotificationStatus(activityId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "updateActivityNotificationStatus"
        body["activityId"] = activityId
        body["action"] = "activity"
        return apiService.updateActivityNotificationStatus(basicAuth.getHeaders(), body)
    } //{"activityId":activity.activityID,"screenId" : "33", "serviceName" : "getDirectToOneGameActivityRes"}
}