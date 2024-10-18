package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName

data class MoodInfoResponse(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("userMoodCardResponseList")
    val moodInfoDataList: List<MoodInfoData>,
)