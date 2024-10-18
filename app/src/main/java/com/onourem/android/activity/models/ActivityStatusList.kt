package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivityStatusList {
    @SerializedName("activityId")
    @Expose
    var activityId = 0

    @SerializedName("activityTag")
    @Expose
    var activityTag: String = ""

    @SerializedName("userParticipationStatus")
    @Expose
    var userParticipationStatus: String = ""

    @SerializedName("activityReason")
    @Expose
    var activityReason: String = ""

    @SerializedName("friendCount")
    @Expose
    var friendCount = 0

    @SerializedName("activityResponseId")
    @Expose
    var activityResponseId = 0
}