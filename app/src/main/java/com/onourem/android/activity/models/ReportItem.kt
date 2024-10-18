package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReportItem(
    @SerializedName("reportedUserId")
    @Expose
    val userId: String,

    @SerializedName("reportedUserName")
    @Expose
    val userName: String,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("createdOn")
    @Expose
    val createdOn: String,

    @SerializedName("createdUserId")
    @Expose
    val contentUserId: String,

    @SerializedName("createdUserName")
    @Expose
    val contentUserName: String,

    @SerializedName("activityId")
    @Expose
    val activityId: String,

    @SerializedName("activityType")
    @Expose
    val activityType: String,
//
//    @SerializedName("reportedOn")
//    @Expose
//    val reportedOn: String,
//
//    @SerializedName("commentId")
//    @Expose
//    val commentId: String,
)