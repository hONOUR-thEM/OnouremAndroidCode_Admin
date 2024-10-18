package com.onourem.android.activity.ui.admin.create.surveys


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class SurveyActivityResponse(
    @SerializedName("adminSurveyResponseList")
    @Expose
    var adminSurveyResponseList: List<AdminSurveyResponse>,
    @SerializedName("errorCode")
    @Expose
    var errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,
    @SerializedName("surveyActivityDataList")
    @Expose
    var surveyActivityDataList: List<String>
)