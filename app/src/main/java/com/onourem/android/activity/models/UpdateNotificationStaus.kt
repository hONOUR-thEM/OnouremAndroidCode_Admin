package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateNotificationStaus {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: Any? = null

    @SerializedName("notificationCount")
    @Expose
    internal var notificationCount: Any? = null

    @SerializedName("notificationInfoList")
    @Expose
    internal var notificationInfoList: Any? = null

    @SerializedName("notificationIds")
    @Expose
    internal var notificationIds: Any? = null

    @SerializedName("displayNumberOfNotification")
    @Expose
    internal var displayNumberOfNotification: String? = null

    @SerializedName("notificationEnglishMsg")
    @Expose
    internal var notificationEnglishMsg: NotificationEnglishMsg? = null

    @SerializedName("notificationHindiMsg")
    @Expose
    internal var notificationHindiMsg: NotificationHindiMsg? = null
    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getFieldName(): Any? {
        return fieldName
    }

    fun setFieldName(fieldName: Any?) {
        this.fieldName = fieldName
    }

    fun getNotificationCount(): Any? {
        return notificationCount
    }

    fun setNotificationCount(notificationCount: Any?) {
        this.notificationCount = notificationCount
    }

    fun getNotificationInfoList(): Any? {
        return notificationInfoList
    }

    fun setNotificationInfoList(notificationInfoList: Any?) {
        this.notificationInfoList = notificationInfoList
    }

    fun getNotificationIds(): Any? {
        return notificationIds
    }

    fun setNotificationIds(notificationIds: Any?) {
        this.notificationIds = notificationIds
    }

    fun getDisplayNumberOfNotification(): String? {
        return displayNumberOfNotification
    }

    fun setDisplayNumberOfNotification(displayNumberOfNotification: String?) {
        this.displayNumberOfNotification = displayNumberOfNotification
    }

    fun getNotificationEnglishMsg(): NotificationEnglishMsg? {
        return notificationEnglishMsg
    }

    fun setNotificationEnglishMsg(notificationEnglishMsg: NotificationEnglishMsg?) {
        this.notificationEnglishMsg = notificationEnglishMsg
    }

    fun getNotificationHindiMsg(): NotificationHindiMsg? {
        return notificationHindiMsg
    }

    fun setNotificationHindiMsg(notificationHindiMsg: NotificationHindiMsg?) {
        this.notificationHindiMsg = notificationHindiMsg
    }
}