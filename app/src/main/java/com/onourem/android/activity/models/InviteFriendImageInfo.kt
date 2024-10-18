package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InviteFriendImageInfo(
    @SerializedName("imageHeight")
    @Expose
    val imageHeight: Int,
    @SerializedName("imageName")
    @Expose
    val imageName: String,
    @SerializedName("imageRow")
    @Expose
    val imageRow: Int,
    @SerializedName("imageWidth")
    @Expose
    val imageWidth: Int,
    @SerializedName("orderNumber")
    @Expose
    val orderNumber: Int
)