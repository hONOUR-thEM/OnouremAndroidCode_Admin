package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName

data class GetVerifiedPhoneNumbersResponse(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("verifiedNumberList")
    val verifiedNumberList: List<String>
)