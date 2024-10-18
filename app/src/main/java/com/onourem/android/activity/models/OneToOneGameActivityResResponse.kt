package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OneToOneGameActivityResResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("ActivityGameOneToOneResList")
    @Expose
    internal var activityGameOneToOneResList: List<ActivityGameOneToOneResList>? = null
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

    fun getActivityGameOneToOneResList(): List<ActivityGameOneToOneResList>? {
        return activityGameOneToOneResList
    }

    fun setActivityGameOneToOneResList(activityGameOneToOneResList: List<ActivityGameOneToOneResList>?) {
        this.activityGameOneToOneResList = activityGameOneToOneResList
    }
}