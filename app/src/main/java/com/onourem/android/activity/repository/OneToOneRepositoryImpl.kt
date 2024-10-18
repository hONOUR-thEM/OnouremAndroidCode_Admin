package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.OneToOneRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.OneToOneGameActivityResResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap

class OneToOneRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    OneToOneRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getOneToOneGameActivityRes(
        activityId: String,
        gameIdToHighlight: String
    ): LiveData<ApiResponse<OneToOneGameActivityResResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "getOneToOneGameActivityRes"
        body["activityId"] = activityId
        body["gameIdFromOtherFlow"] = gameIdToHighlight
        return apiService.getOneToOneGameActivityRes(basicAuth.getHeaders(), body)
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

    override fun deleteOneToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "deleteOneToOneGameActivity"
        body["gameId"] = gameID
        return apiService.deleteOneToOneGameActivity(basicAuth.getHeaders(), body)
    }

    override fun ignoreOneToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "ignoreOneToOneGameActivity"
        body["gameId"] = gameID
        return apiService.ignoreOneToOneGameActivity(basicAuth.getHeaders(), body)
    }

    override fun cancelOneToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "33"
        body["serviceName"] = "cancelOneToOneGameActivity"
        body["gameId"] = gameID
        return apiService.cancelOneToOneGameActivity(basicAuth.getHeaders(), body)
    }

    override fun updateActivityNotificationStatus(activityId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "34"
        body["serviceName"] = "updateActivityNotificationStatus"
        body["activityId"] = activityId
        body["action"] = "activity"
        return apiService.updateActivityNotificationStatus(basicAuth.getHeaders(), body)
    }
}