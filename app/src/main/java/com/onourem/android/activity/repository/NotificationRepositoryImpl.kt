package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.NotificationRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetNewNotificationInfo
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.UpdateNotificationStaus
import com.onourem.android.activity.ui.utils.AppUtilities
import android.text.TextUtils
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.GetNextNoficationInfo
import com.onourem.android.activity.models.GetUserProfileFeeds
import com.onourem.android.activity.models.GetActivityInfoForNotificataion
import com.onourem.android.activity.models.PullNewNotificationInfo
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap
import javax.inject.Named

class NotificationRepositoryImpl //{"notificationId":notificationId!, "readStatus":"Y","screenId" : "21", "serviceName" : "updateNotificationStaus"}
//{"notificationId":notificationId,"screenId" : "21", "serviceName" : "deleteNotification"}
//{"notificationRequestData":requestData,"screenId" : "21", "serviceName" : "getNextNoficationInfo"}
//{"friendUserId":friendUserId,"screenId" : "21", "serviceName" : "acceptPendingRequest"}
//{"friendUserId":friendUserId,"screenId" : "21", "serviceName" : "cancelPendingRequest"}
//{"userId":loginUserId,"postIds":postId,"screenId" : "21", "serviceName" : "getUserProfileFeeds"}
//{"activityId" :activityId,"screenId" : "21", "serviceName" : "getActivityInfoForNotificataion", "activityGameRespId" : activityGameResId }
//{"topNotificationTime":finalTopFeedTime,"screenId" : "21", "serviceName" : "pullNewNotificationInfo"}
@Inject constructor(private val apiService: ApiService) : NotificationRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun newNotificationInfo(): LiveData<ApiResponse<GetNewNotificationInfo>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNewNotificationInfo"
        params["screenId"] = "21"
        return apiService.getNewNotificationInfo(basicAuth.getHeaders(), params)
    }

    override fun updateNotificationStaus(
        notificationId: String?,
        readStatus: String?,
        activityId: String?,
        actionType: String?
    ): LiveData<ApiResponse<UpdateNotificationStaus>> {
        AppUtilities.showLog(
            "updateNotification##",
            "actionType: $actionType activityId: $activityId"
        )
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateNotificationStaus"
        params["screenId"] = "21"
        params["notificationId"] = notificationId?: ""
        if (TextUtils.isEmpty(activityId)) {
            params["activityId"] = ""
        } else {
            params["activityId"] = activityId?: ""
        }
        if (TextUtils.isEmpty(actionType)) {
            params["actionType"] = ""
        } else {
            params["actionType"] = actionType?: ""
        }
        params["readStatus"] = readStatus?: ""
        return apiService.updateNotificationStaus(basicAuth.getHeaders(), params)
    }

    override fun deleteNotification(
        notificationId: String?,
        activityId: String?,
        actionType: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        AppUtilities.showLog(
            "deleteNotification##",
            "actionType: $actionType activityId: $activityId"
        )
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteNotification"
        params["screenId"] = "21"
        if (TextUtils.isEmpty(activityId)) {
            params["activityId"] = ""
        } else {
            params["activityId"] = activityId?: ""
        }
        if (TextUtils.isEmpty(actionType)) {
            params["actionType"] = ""
        } else {
            params["actionType"] = actionType?: ""
        }
        params["notificationId"] = notificationId?: ""
        return apiService.deleteNotification(basicAuth.getHeaders(), params)
    }

    override fun getNextNotificationInfo(notificationRequestData: String?): LiveData<ApiResponse<GetNextNoficationInfo>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextNoficationInfo"
        params["screenId"] = "21"
        params["notificationRequestData"] = notificationRequestData?: ""
        return apiService.getNextNoficationInfo(basicAuth.getHeaders(), params)
    }

    override fun getUserProfileFeeds(
        userId: String?,
        postIds: String?,
        notificationId: String?
    ): LiveData<ApiResponse<GetUserProfileFeeds>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPostDataFromNotification"
        params["screenId"] = "21"
        params["userId"] = userId?: ""
        params["postIds"] = postIds?: ""
        params["notificationId"] = notificationId?: ""
        return apiService.getUserProfileFeeds(basicAuth.getHeaders(), params)
    }

    override fun getActivityInfoForNotificataion(
        activityId: String?,
        activityGameRespId: String?,
        notificationId: String?
    ): LiveData<ApiResponse<GetActivityInfoForNotificataion>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getActivityInfoForNotificataion"
        params["screenId"] = "21"
        params["activityGameRespId"] = activityGameRespId?: ""
        params["activityId"] = activityId?: ""
        params["notificationId"] = notificationId?: ""
        return apiService.getActivityInfoForNotificataion(basicAuth.getHeaders(), params)
    }

    override fun getExternalActivityFromNotification(externalIds: String?): LiveData<ApiResponse<GetActivityInfoForNotificataion>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getExternalActivity"
        params["screenId"] = "21"
        params["externalIds"] = externalIds?: ""
        return apiService.getExternalActivityFromNotification(basicAuth.getHeaders(), params)
    }

    override fun pullNewNotificationInfo(topNotificationTime: String?): LiveData<ApiResponse<PullNewNotificationInfo>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "pullNewNotificationInfo"
        params["screenId"] = "21"
        params["topNotificationTime"] = topNotificationTime?: ""
        return apiService.pullNewNotificationInfo(basicAuth.getHeaders(), params)
    } //{"screenId" : "21", "serviceName" : "getNewNotificationInfo" }
}