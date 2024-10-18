package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PullNewNotificationInfo {
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
    internal var notificationInfoList: List<NotificationInfoList>? = null
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

    fun getNotificationInfoList(): List<NotificationInfoList>? {
        return notificationInfoList
    }

    fun setNotificationInfoList(notificationInfoList: List<NotificationInfoList>?) {
        this.notificationInfoList = notificationInfoList
    }
}