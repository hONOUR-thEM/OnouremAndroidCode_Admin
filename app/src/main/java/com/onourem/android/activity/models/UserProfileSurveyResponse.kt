package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserProfileSurveyResponse {
    @SerializedName("surveyLimit")
    @Expose
    internal var surveyLimit: Int? = null

    @SerializedName("surveyIdList")
    @Expose
    internal var surveyIdList: List<Int>? = null

    @SerializedName("surveyData")
    @Expose
    internal var surveyData: List<SurveyCOList>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
    fun getSurveyLimit(): Int? {
        return surveyLimit
    }

    fun setSurveyLimit(surveyLimit: Int?) {
        this.surveyLimit = surveyLimit
    }

    fun getSurveyIdList(): List<Int>? {
        return surveyIdList
    }

    fun setSurveyIdList(surveyIdList: List<Int>?) {
        this.surveyIdList = surveyIdList
    }

    fun getSurveyData(): List<SurveyCOList>? {
        return surveyData
    }

    fun setSurveyData(surveyData: List<SurveyCOList>?) {
        this.surveyData = surveyData
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
}