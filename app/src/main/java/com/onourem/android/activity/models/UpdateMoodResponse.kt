package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateMoodResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("totalCountMessage")
    @Expose
    var totalCountMessage: String? = null

    @SerializedName("loginDayActivityInfoList")
    @Expose
    var loginDayActivityInfoList: List<LoginDayActivityInfoList>? = null
}