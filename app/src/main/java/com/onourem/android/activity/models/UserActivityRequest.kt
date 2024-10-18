package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserActivityRequest {
    @SerializedName("activityIds")
    @Expose
    var activityIds: String? = null

    @SerializedName("playGroupId")
    @Expose
    var playGroupId: String? = null

    @SerializedName("screenId")
    @Expose
    var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null

    @SerializedName("activityTags")
    @Expose
    var activityTags: String? = null

    @SerializedName("userParticipationStatus")
    @Expose
    var userParticipationStatus: String? = null

    @SerializedName("activityReason")
    @Expose
    var activityReason: String? = null

    @SerializedName("friendCount")
    @Expose
    var friendCount: String? = null

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null
}