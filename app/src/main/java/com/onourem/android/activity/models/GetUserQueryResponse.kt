package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetUserQueryResponse(
    @SerializedName("errorCode")
    @Expose
    val errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String,

    @SerializedName("UserQueryResList")
    @Expose
    val userQueryList: List<UserQueryItem>,
)