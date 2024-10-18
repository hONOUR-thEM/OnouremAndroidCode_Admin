package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class FriendsRepositoryImpl @Inject constructor(
    private val preferenceHelper: SharedPreferenceHelper, @param:Named(
        "uniqueDeviceId"
    ) private val uniqueDeviceId: String?, private val apiService: ApiService
) : FriendsRepository {
    override fun sendFriendRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        //{"friendUserId":111,"screenId" : "24", "serviceName" : "sendFriendRequest"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "sendFriendRequest"
        params["screenId"] = screenId?: ""
        params["deviceId"] = uniqueDeviceId?: ""
        params["reqSource"] = "ANDROID"
        params["friendUserId"] = friendUserId?: ""
        return apiService.sendFriendRequest(basicAuth.getHeaders(), params)
    }

    override fun removeFriend(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        //{"friendUserId":121,"screenId" : "24", "serviceName" : "removeFriend"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "removeFriend"
        params["screenId"] = screenId?: ""
        params["deviceId"] = uniqueDeviceId?: ""
        params["reqSource"] = "ANDROID"
        params["friendUserId"] = friendUserId?: ""
        return apiService.removeFriend(basicAuth.getHeaders(), params)
    }

    override fun cancelSentRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        //{"friendUserId":friendUserId,"screenId" : "24", "serviceName" : "cancelSentRequest"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "cancelSentRequest"
        params["screenId"] = screenId ?: ""// profile 22 or 24 friends list
        params["deviceId"] = uniqueDeviceId?: ""
        params["reqSource"] = "ANDROID"
        params["friendUserId"] = friendUserId?: ""
        return apiService.cancelSentRequest(basicAuth.getHeaders(), params)
    }

    override fun cancelPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        //{"friendUserId":friendUserId,"screenId" : "24", "serviceName" : "cancelPendingRequest"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "cancelPendingRequest"
        params["screenId"] = screenId?: ""
        params["deviceId"] = uniqueDeviceId?: ""
        params["reqSource"] = "ANDROID"
        params["friendUserId"] = friendUserId?: ""
        return apiService.cancelPendingRequest(basicAuth.getHeaders(), params)
    }

    override fun acceptPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        //{"friendUserId":friendUserId,"screenId" : "24", "serviceName" : "acceptPendingRequest"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "acceptPendingRequest"
        params["screenId"] = screenId?: ""
        params["deviceId"] = uniqueDeviceId?: ""
        params["reqSource"] = "ANDROID"
        params["friendUserId"] = friendUserId?: ""
        return apiService.acceptPendingRequest(basicAuth.getHeaders(), params)
    }

    override fun getFriendList(screenId: String?): LiveData<ApiResponse<UserListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getFriendList"
        params["screenId"] = screenId?: ""
        params["deviceId"] = uniqueDeviceId?: ""
        params["reqSource"] = "ANDROID"
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getFriendList(basicAuth.getHeaders(), params)
    }
}