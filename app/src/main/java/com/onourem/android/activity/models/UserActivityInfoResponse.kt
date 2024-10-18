package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserActivityInfoResponse {
    @SerializedName("gameResIdList")
    @Expose
    internal var gameResIdList: List<String> = emptyList()

    @SerializedName("displayNumberOfActivity")
    @Expose
    internal var displayNumberOfActivity: Long? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("activityStatusList")
    @Expose
    internal var activityStatusList: List<ActivityStatusList> = emptyList()

    @SerializedName("loginDayActivityInfoList")
    @Expose
    internal var loginDayActivityInfoList: List<LoginDayActivityInfoList> = emptyList()
    fun getDisplayNumberOfActivity(): Long? {
        return displayNumberOfActivity
    }

    fun setDisplayNumberOfActivity(displayNumberOfActivity: Long?) {
        this.displayNumberOfActivity = displayNumberOfActivity
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getActivityStatusList(): List<ActivityStatusList> {
        return activityStatusList
    }

    fun setActivityStatusList(activityStatusList: List<ActivityStatusList>) {
        this.activityStatusList = activityStatusList
    }

    fun getLoginDayActivityInfoList(): List<LoginDayActivityInfoList> {
        return loginDayActivityInfoList
    }

    fun setLoginDayActivityInfoList(loginDayActivityInfoList: List<LoginDayActivityInfoList>) {
        this.loginDayActivityInfoList = loginDayActivityInfoList
    }

    fun getGameResIdList(): List<String> {
        return gameResIdList
    }

    fun setGameResIdList(gameResIdList: List<String>) {
        this.gameResIdList = gameResIdList
    }
}