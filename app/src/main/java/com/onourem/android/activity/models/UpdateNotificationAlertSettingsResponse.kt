package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateNotificationAlertSettingsResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: Any? = null

    @SerializedName("updateEmailAlertSettings")
    @Expose
    internal var updateEmailAlertSettings: Any? = null

    @SerializedName("updateNotificationAlertSettings")
    @Expose
    internal var updateNotificationAlertSettings: String? = null

    @SerializedName("reActivateAccount")
    @Expose
    internal var reActivateAccount: Any? = null

    @SerializedName("deActivateAccount")
    @Expose
    internal var deActivateAccount: Any? = null

    @SerializedName("getAllEmailAlertSettings")
    @Expose
    internal var getAllEmailAlertSettings: Any? = null

    @SerializedName("getAllNotificationAlertSettings")
    @Expose
    internal var getAllNotificationAlertSettings: Any? = null
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

    fun getUpdateEmailAlertSettings(): Any? {
        return updateEmailAlertSettings
    }

    fun setUpdateEmailAlertSettings(updateEmailAlertSettings: Any?) {
        this.updateEmailAlertSettings = updateEmailAlertSettings
    }

    fun getUpdateNotificationAlertSettings(): String? {
        return updateNotificationAlertSettings
    }

    fun setUpdateNotificationAlertSettings(updateNotificationAlertSettings: String?) {
        this.updateNotificationAlertSettings = updateNotificationAlertSettings
    }

    fun getReActivateAccount(): Any? {
        return reActivateAccount
    }

    fun setReActivateAccount(reActivateAccount: Any?) {
        this.reActivateAccount = reActivateAccount
    }

    fun getDeActivateAccount(): Any? {
        return deActivateAccount
    }

    fun setDeActivateAccount(deActivateAccount: Any?) {
        this.deActivateAccount = deActivateAccount
    }

    fun getGetAllEmailAlertSettings(): Any? {
        return getAllEmailAlertSettings
    }

    fun setGetAllEmailAlertSettings(getAllEmailAlertSettings: Any?) {
        this.getAllEmailAlertSettings = getAllEmailAlertSettings
    }

    fun getGetAllNotificationAlertSettings(): Any? {
        return getAllNotificationAlertSettings
    }

    fun setGetAllNotificationAlertSettings(getAllNotificationAlertSettings: Any?) {
        this.getAllNotificationAlertSettings = getAllNotificationAlertSettings
    }
}