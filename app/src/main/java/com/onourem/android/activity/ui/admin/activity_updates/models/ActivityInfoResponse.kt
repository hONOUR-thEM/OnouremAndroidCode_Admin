package com.onourem.android.activity.ui.admin.activity_updates.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ActivityInfoResponse (
    @SerializedName("activityId")
    @Expose
    var activityId: String? = null,

    @SerializedName("activityTriggered")
    @Expose
    var activityTriggered: Boolean = false,

    @SerializedName("activityScheduled")
    @Expose
    var activityScheduled: Boolean = false,

    @SerializedName("createdTime")
    @Expose
    var createdTime: String? = null,

    @SerializedName("scheduledTime")
    @Expose
    var scheduledTime: String? = null,

    @SerializedName("triggeredTime")
    @Expose
    var triggeredTime: String? = null,

    @SerializedName("noOfAnswers")
    @Expose
    var noOfAnswers: String? = null,

    //category - funny emotional

    @SerializedName("activityCategory")
    @Expose
    var activityCategory: String? = null,

    @SerializedName("activityFor")
    @Expose
    var activityFor: String? = null,

    @SerializedName("isSentForAll")
    @Expose
    var isSentForAll: String? = null
)