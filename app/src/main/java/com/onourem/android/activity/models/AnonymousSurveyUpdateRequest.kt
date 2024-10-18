package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnonymousSurveyUpdateRequest {
    @SerializedName("surveyId")
    @Expose
    var surveyId: String? = null

    @SerializedName("optionId")
    @Expose
    var optionId: String? = null

    @SerializedName("otherText")
    @Expose
    var otherText: String? = null

    @SerializedName("screenId")
    @Expose
    var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null

    @SerializedName("timeSpentOnScreen")
    @Expose
    var timeSpentOnScreen: String? = null
}