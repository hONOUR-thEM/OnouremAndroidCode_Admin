package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SendPushNotificationToLinkUserResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: Any? = null

    @SerializedName("token")
    @Expose
    internal var token: Any? = null

    @SerializedName("userList")
    @Expose
    internal var userList: Any? = null

    @SerializedName("message")
    @Expose
    internal var message: String? = null
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

    fun getToken(): Any? {
        return token
    }

    fun setToken(token: Any?) {
        this.token = token
    }

    fun getUserList(): Any? {
        return userList
    }

    fun setUserList(userList: Any?) {
        this.userList = userList
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }
}