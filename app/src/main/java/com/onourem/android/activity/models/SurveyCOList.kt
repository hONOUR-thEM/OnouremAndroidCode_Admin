package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SurveyCOList : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("surveytype")
    @Expose
    var surveytype: String? = null

    @SerializedName("surveyText")
    @Expose
    var surveyText: String? = null

    @SerializedName("surveyHintText")
    @Expose
    var surveyHintText: String? = null

    @SerializedName("surveyImageUrl")
    @Expose
    var surveyImageUrl: String? = null

    @SerializedName("surveyOption")
    @Expose
    var surveyOption: List<String> = emptyList()

    @SerializedName("surveyOptionIds")
    @Expose
    var surveyOptionIds: List<String> = emptyList()

    @SerializedName("userAnserForSurvey")
    @Expose
    var userAnserForSurvey: String? = null

    @SerializedName("showStats")
    @Expose
    internal var showStats: String? = null

    var surveyIdFromQuestionListing: String? = null
}