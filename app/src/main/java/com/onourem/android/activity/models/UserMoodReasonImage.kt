package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserMoodReasonImage(
    @SerializedName("imageHeight")
    @Expose
    val imageHeight: Int,
    @SerializedName("imageUrl")
    @Expose
    val imageUrl: String,
    @SerializedName("imageWidth")
    @Expose
    val imageWidth: Int
)