package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap
import javax.inject.Named

class DashboardRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    DashboardRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    override fun moodExpressions(): LiveData<ApiResponse<ExpressionDataResponse>>{
            val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val body: MutableMap<String, String> = HashMap()
            body["screenId"] = "8"
            body["serviceName"] = "getExpressionAndAppData"
            body["deviceId"] = uniqueDeviceId!!
            body["timeZone"] = AppUtilities.getTimeZone()
            body["reqSource"] = "ANDROID"
            return apiService.getExpressionAndAppData(authToken.getHeaders(), body)
        }

    override fun updateUserMood(expressionId: String, timeSpentOnScreen: String): LiveData<ApiResponse<UpdateMoodResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, String> = HashMap()
        body["screenId"] = "8"
        body["serviceName"] = "updateUserMood"
        body["deviceId"] = uniqueDeviceId!!
        body["expressionId"] = expressionId
        body["timeSpentOnScreen"] = timeSpentOnScreen
        body["reqSource"] = "ANDROID"
        return apiService.updateUserMood(authToken.getHeaders(), body)
    }

    override fun logout(): LiveData<ApiResponse<LogoutResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "15"
        body["serviceName"] = "logout"
        body["deviceId"] = uniqueDeviceId!!
        return apiService.logout(basicAuth.getHeaders(), body)
    }

    override fun getUserWatchList(notificationById: String): LiveData<ApiResponse<GetWatchListResponse>> {
        //{"screenId" : "8", "serviceName" : "getUserWatchList"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserWatchList"
        params["screenId"] = "8"
        params["notificationById"] = notificationById
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getUserWatchList(basicAuth.getHeaders(), params)
    }

    //{"screenId" : "8", "serviceName" : "getUserWatchList"}
    override fun appUpgradeInfo(): LiveData<ApiResponse<GetAppUpgradeInfoResponse>>{
            //{"screenId" : "8", "serviceName" : "getUserWatchList"}
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "getAppUpgradeInfo"
            params["screenId"] = "8"
            params["timeZone"] = AppUtilities.getTimeZone()
            return apiService.getAppUpgradeInfo(basicAuth.getHeaders(), params)
        }

    override fun updateAppShortLink(
        linkUserId: String,
        shortUrl: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["linkUserId"] = linkUserId
        params["shortUrl"] = shortUrl
        return apiService.updateAppShortLink(basicAuth.getHeaders(), params)
    }

    override fun getFunCards(linkUserId: String): LiveData<ApiResponse<GetFunCardsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "50"
        params["serviceName"] = "getFunCards"
        params["linkUserId"] = linkUserId
        return apiService.getFunCards(basicAuth.getHeaders(), params)
    }

    override fun addNetworkErrorUserInfo(
        serviceName: String,
        networkErrorCode: String,
        userId: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = serviceName
        params["networkErrorCode"] = networkErrorCode
        params["userId"] = userId
        return apiService.addNetworkErrorUserInfo(basicAuth.getHeaders(), params)
    }

    override fun updateUserMoodReason(reason: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateUserMoodReason"
        params["reason"] = reason
        return apiService.updateUserMoodReason(basicAuth.getHeaders(), params)
    }

    override fun updateExternalActivityInfo(item: LoginDayActivityInfoList): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateUserMoodReason"
        params["activityId"] = item.activityId!!
        params["activityType"] = item.activityType!!
        return apiService.updateExternalActivityInfo(basicAuth.getHeaders(), params)
    }

    override fun updateAverageTimeInfo(
        startTime: String,
        endTime: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateAverageTimeInfo"
        params["startTime"] = startTime
        params["endTime"] = endTime
        return apiService.updateAverageTimeInfo(basicAuth.getHeaders(), params)
    }

    override fun userMoodResponseMsg(): LiveData<ApiResponse<GetUserMoodResponseMsgResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "getUserMoodResponseMsg"
            return apiService.getUserMoodResponseMsg(basicAuth.getHeaders(), params)
        }
    override fun userMoodHistory(): LiveData<ApiResponse<GetUserHistoryResponse>> {
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "getUserMoodHistory"
            return apiService.getUserMoodHistory(basicAuth.getHeaders(), params)
        }

    override fun getSubscriptionPackageInfo(): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getSubscriptionPackageInfo"
        return apiService.getSubscriptionPackageInfo(basicAuth.getHeaders(), params)
    }

    override fun applyDiscountCode(code: String): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "applyDiscountCode"
        params["discountCode"] = code
        return apiService.applyDiscountCode(basicAuth.getHeaders(), params)
    }

    override fun getFreeSubscriptions(packageCode: String, activationDate: String): LiveData<ApiResponse<GetFreeSubscriptionsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getFreeSubscriptions"
        params["packageCode"] = packageCode
        params["activationDate"] = activationDate
        return apiService.getFreeSubscriptions(basicAuth.getHeaders(), params)
    }

    override fun updateUserSubscriptionDetails(packageId: String, discountCode: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateUserSubscriptionDetails"
        params["packageId"] = packageId
        params["discountCode"] = discountCode
        return apiService.updateUserSubscriptionDetails(basicAuth.getHeaders(), params)
    }

    override fun getOrderInfoByAdmin(id: String): LiveData<ApiResponse<CheckOrderInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getOrderInfoByAdmin"
        params["orderId"] = id
        return apiService.getOrderInfoByAdmin(basicAuth.getHeaders(), params)
    }

    override fun updateOrderInfoByAdmin(orderInfo: OrderInfo): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.updateOrderInfoByAdmin(basicAuth.getHeaders(), orderInfo)
    }

    override fun getUserCurrentSubscriptions(): LiveData<ApiResponse<GetUserCurrentSubscriptionResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserCurrentSubscriptions"
        return apiService.getUserCurrentSubscriptions(basicAuth.getHeaders(), params)
    }

    override fun getOrderInfo(amount: String, currency: String, packageCode: String, discountCode: String): LiveData<ApiResponse<GetOrderInfoRes>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getOrderInfo"
        params["amount"] = amount
        params["currency"] = currency
        params["packageCode"] = packageCode
        params["discountCode"] = discountCode
        return apiService.getOrderInfo(basicAuth.getHeaders(), params)
    }

    override fun updateOrderStatus(id: String, orderResult: String, packageId: String, discountCode: String, orderStatus: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateOrderStatus"
        params["orderId"] = id
        params["packageId"] = packageId
        params["orderResult"] = orderResult
        params["discountCode"] = discountCode
        params["orderStatus"] = orderStatus
        return apiService.updateOrderStatus(basicAuth.getHeaders(), params)
    }

    override fun cancelWatchListPendingRequest(id: String): LiveData<ApiResponse<StandardResponse>> {
        //{"screenId" : "8", "serviceName" : "getUserWatchList"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "cancelWatchListPendingRequest"
        params["watchUserId"] = id
        params["screenId"] = "8"
        return apiService.cancelWatchListPendingRequest(basicAuth.getHeaders(), params)
    }

    override fun acceptPendingWatchRequest(id: String): LiveData<ApiResponse<AcceptPendingWatchResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "acceptPendingWatchRequest"
        params["watchUserId"] = id
        params["screenId"] = "8"
        return apiService.acceptPendingWatchRequest(basicAuth.getHeaders(), params)
    }

    override fun cancelWatchListRequest(id: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "cancelWatchListRequest"
        params["watchUserId"] = id!!
        params["screenId"] = "8"
        return apiService.cancelWatchListRequest(basicAuth.getHeaders(), params)
    }

    override fun watchFriendList(): LiveData<ApiResponse<GetWatchFriendListResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "cancelWatchListRequest"
            params["screenId"] = "11"
            return apiService.getWatchFriendList(basicAuth.getHeaders(), params)
        }

    override fun addUserToWatchList(id: String): LiveData<ApiResponse<AcceptPendingWatchResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "cancelWatchListRequest"
        params["watchUserId"] = id
        params["screenId"] = "11"
        return apiService.addUserToWatchList(basicAuth.getHeaders(), params)
    }

    override fun updateAndroidNotificationStatus(notificationStatus: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "UpdateAndroidNotificationStatus"
        params["notificationStatus"] = notificationStatus
        return apiService.updateAndroidNotificationStatus(basicAuth.getHeaders(), params)
    }

    override fun getQuestionGameWatchListAndSurveyInfo(screenId: String): LiveData<ApiResponse<GetQuestionGameWatchListAndSurveyInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getQuestionGameWatchListAndSurveyInfo"
        params["screenId"] = screenId
        params["timeZone"] = AppUtilities.getTimeZone()
        return apiService.getQuestionGameWatchListAndSurveyInfo(basicAuth.getHeaders(), params)
    }
}