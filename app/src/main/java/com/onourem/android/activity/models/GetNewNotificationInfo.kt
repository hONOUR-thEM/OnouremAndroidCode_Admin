package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetNewNotificationInfo : PopUpCommonData(), Serializable {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("notificationCount")
    @Expose
    var notificationCount: Any? = null

    @SerializedName("notificationInfoList")
    @Expose
    var notificationInfoList: List<NotificationInfoList>? = null

    @SerializedName("notificationIds")
    @Expose
    var notificationIds: List<Int>? = null

    @SerializedName("displayNumberOfNotification")
    @Expose
    var displayNumberOfNotification: String? = null

    @SerializedName("notificationEnglishMsg")
    @Expose
    var notificationEnglishMsg: NotificationEnglishMsg? = null

    @SerializedName("notificationHindiMsg")
    @Expose
    var notificationHindiMsg: NotificationHindiMsg? = null
}