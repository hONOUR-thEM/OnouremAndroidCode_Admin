package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetNewNotificationInfo
import com.onourem.android.activity.models.UpdateNotificationStaus
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.GetNextNoficationInfo
import com.onourem.android.activity.models.GetUserProfileFeeds
import com.onourem.android.activity.models.GetActivityInfoForNotificataion
import com.onourem.android.activity.models.PullNewNotificationInfo

interface NotificationRepository {
    fun newNotificationInfo(): LiveData<ApiResponse<GetNewNotificationInfo> >
    fun updateNotificationStaus(
        notificationId: String?,
        readStatus: String?,
        activityId: String?,
        actionType: String?
    ): LiveData<ApiResponse<UpdateNotificationStaus> >

    fun deleteNotification(
        notificationId: String?,
        activityId: String?,
        actionType: String?
    ): LiveData<ApiResponse<StandardResponse> >

    fun getNextNotificationInfo(notificationRequestData: String?): LiveData<ApiResponse<GetNextNoficationInfo> >
    fun getUserProfileFeeds(
        userId: String?,
        postIds: String?,
        notificationId: String?
    ): LiveData<ApiResponse<GetUserProfileFeeds> >

    fun getActivityInfoForNotificataion(
        activityId: String?,
        activityGameRespId: String?,
        notificationId: String?
    ): LiveData<ApiResponse<GetActivityInfoForNotificataion> >

    fun getExternalActivityFromNotification(
        externalIds: String?,
    ): LiveData<ApiResponse<GetActivityInfoForNotificataion> >

    fun pullNewNotificationInfo(topNotificationTime: String?): LiveData<ApiResponse<PullNewNotificationInfo> >
}