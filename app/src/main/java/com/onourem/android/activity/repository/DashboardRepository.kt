package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*

interface DashboardRepository {
    fun moodExpressions(): LiveData<ApiResponse<ExpressionDataResponse>>
    fun updateUserMood(expressionId: String, timeSpentOnScreen: String): LiveData<ApiResponse<UpdateMoodResponse>>
    fun logout(): LiveData<ApiResponse<LogoutResponse>>?
    fun getUserWatchList(notificationById: String): LiveData<ApiResponse<GetWatchListResponse>>
    fun cancelWatchListPendingRequest(id: String): LiveData<ApiResponse<StandardResponse>>
    fun acceptPendingWatchRequest(id: String): LiveData<ApiResponse<AcceptPendingWatchResponse>>
    fun cancelWatchListRequest(id: String): LiveData<ApiResponse<StandardResponse>>
    fun watchFriendList(): LiveData<ApiResponse<GetWatchFriendListResponse>>
    fun addUserToWatchList(id: String): LiveData<ApiResponse<AcceptPendingWatchResponse>>
    fun updateAndroidNotificationStatus(notificationStatus: String): LiveData<ApiResponse<StandardResponse>>
    fun getQuestionGameWatchListAndSurveyInfo(screenId: String): LiveData<ApiResponse<GetQuestionGameWatchListAndSurveyInfoResponse>>
    fun appUpgradeInfo(): LiveData<ApiResponse<GetAppUpgradeInfoResponse>>
    fun updateAppShortLink(
        linkUserId: String,
        shortUrl: String
    ): LiveData<ApiResponse<StandardResponse>>

    fun getFunCards(linkUserId: String): LiveData<ApiResponse<GetFunCardsResponse>>
    fun addNetworkErrorUserInfo(
        serviceName: String,
        networkErrorCode: String,
        userId: String
    ): LiveData<ApiResponse<StandardResponse>>

    fun updateUserMoodReason(reason: String): LiveData<ApiResponse<StandardResponse>>
    fun updateExternalActivityInfo(item: LoginDayActivityInfoList): LiveData<ApiResponse<StandardResponse>>
    fun updateAverageTimeInfo(
        startTime: String,
        endTime: String
    ): LiveData<ApiResponse<StandardResponse>>

    fun userMoodResponseMsg(): LiveData<ApiResponse<GetUserMoodResponseMsgResponse>>
    fun userMoodHistory(): LiveData<ApiResponse<GetUserHistoryResponse>>

    fun getSubscriptionPackageInfo(): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>>

    fun getUserCurrentSubscriptions(): LiveData<ApiResponse<GetUserCurrentSubscriptionResponse>>

    fun getFreeSubscriptions(packageCode: String, activationDate: String): LiveData<ApiResponse<GetFreeSubscriptionsResponse>>

    fun applyDiscountCode(code: String): LiveData<ApiResponse<GetSubscriptionPackageResListRessponse>>

    fun getOrderInfo(amount: String, currency: String, packageCode: String, discountCode: String ): LiveData<ApiResponse<GetOrderInfoRes>>

    fun updateUserSubscriptionDetails(packageId: String, discountCode: String): LiveData<ApiResponse<StandardResponse>>

    fun updateOrderStatus(id: String, orderResult: String, packageId: String, discountCode: String, orderStatus: String): LiveData<ApiResponse<StandardResponse>>

    fun getOrderInfoByAdmin(id: String): LiveData<ApiResponse<CheckOrderInfoResponse>>

    fun updateOrderInfoByAdmin(orderInfo: OrderInfo): LiveData<ApiResponse<StandardResponse>>

}