package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName

data class UserQualityResponse(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("userQualityInfoList")
    val userQualityInfoList: List<UserQualityInfo>
)