package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatisticSurveyRequest {
    @SerializedName("surveyId")
    @Expose
    internal var surveyId: Int? = null

    @SerializedName("optionId")
    @Expose
    internal var optionId: String? = null

    @SerializedName("screenId")
    @Expose
    internal var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    internal var serviceName: String? = null
    fun getSurveyId(): Int? {
        return surveyId
    }

    fun setSurveyId(surveyId: Int?) {
        this.surveyId = surveyId
    }

    fun getOptionId(): String? {
        return optionId
    }

    fun setOptionId(optionId: String?) {
        this.optionId = optionId
    }

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
}