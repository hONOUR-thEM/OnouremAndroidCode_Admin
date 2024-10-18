package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnonymousSurveyRequest {
    @SerializedName("surveyId")
    @Expose
    var surveyId: String? = null

    @SerializedName("screenId")
    @Expose
    var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null
}