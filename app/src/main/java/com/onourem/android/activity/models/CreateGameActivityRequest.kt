package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateGameActivityRequest {
    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("activityTypeId")
    @Expose
    var activityTypeId: String? = null

    @SerializedName("userIds")
    @Expose
    var userIds: String? = null

    @SerializedName("loginDay")
    @Expose
    var loginDay: String? = null

    @SerializedName("screenId")
    @Expose
    var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null

    @SerializedName("playGroupIds")
    @Expose
    var playGroupIds: String? = null

    @SerializedName("isActivityForNewQuestion")
    @Expose
    var isActivityForNewQuestion: String? = null

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null


    @SerializedName("genderType")
    @Expose
    var genderType: String? = null

}