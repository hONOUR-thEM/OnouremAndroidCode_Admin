package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChangedMessage(
    @SerializedName("cost")
    @Expose
    val cost: String,
    @SerializedName("creatorId")
    @Expose
    val creatorId: String,
    @SerializedName("expressionText")
    @Expose
    val expressionText: String,
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("imageName")
    @Expose
    val imageName: String,
    @SerializedName("isImagePaid")
    @Expose
    val isImagePaid: String
)