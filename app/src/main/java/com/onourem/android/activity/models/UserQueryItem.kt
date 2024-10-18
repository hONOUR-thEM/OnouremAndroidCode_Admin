package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserQueryItem(
    @SerializedName("userId")
    @Expose
    val userId: String,
    @SerializedName("userName")
    @Expose
    val userName: String,
    @SerializedName("userText")
    @Expose
    val description: String,
    @SerializedName("createdOn")
    @Expose
    val createdOn: String,
    @SerializedName("queryType")
    @Expose
    val queryType: String,

    )