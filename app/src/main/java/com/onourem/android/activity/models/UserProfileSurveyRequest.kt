package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserProfileSurveyRequest {
    @SerializedName("screenId")
    @Expose
    internal var screenId: String? = null

    @SerializedName("isFromAnswered")
    @Expose
    internal var isFromAnswered: String? = null

    @SerializedName("serviceName")
    @Expose
    internal var serviceName: String? = null

    @SerializedName("surveyIds")
    @Expose
    internal var surveyIds: String? = null
    fun getScreenId(): String? {
        return screenId
    }

    fun setScreenId(screenId: String?) {
        this.screenId = screenId
    }

    fun getServiceName(): String? {
        return serviceName
    }

    fun setServiceName(serviceName: String?) {
        this.serviceName = serviceName
    }

    fun getSurveyIds(): String? {
        return surveyIds
    }

    fun setSurveyIds(surveyIds: String?) {
        this.surveyIds = surveyIds
    }

    fun getIsFromAnswered(): String? {
        return isFromAnswered
    }

    fun setIsFromAnswered(isFromAnswered: String?) {
        this.isFromAnswered = isFromAnswered
    }
}