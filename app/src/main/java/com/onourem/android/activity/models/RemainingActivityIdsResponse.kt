package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RemainingActivityIdsResponse(
    @SerializedName("activityStatusList")
    @Expose
    val activityStatusList: List<String>,
    @SerializedName("errorCode")
    @Expose
    val errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String
)