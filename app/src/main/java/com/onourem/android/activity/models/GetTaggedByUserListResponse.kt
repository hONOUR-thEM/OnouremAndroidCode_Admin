package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName

data class GetTaggedByUserListResponse(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("userList")
    val userList: List<User>
)