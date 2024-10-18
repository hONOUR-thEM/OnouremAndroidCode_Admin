package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("profilePicture")
    val profilePicture: String,
    @SerializedName("status")
    val status: Any,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userStatus")
    val userStatus: Any,
    @SerializedName("userTypeId")
    val userTypeId: String
)