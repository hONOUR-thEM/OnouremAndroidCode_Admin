package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivityGroupList {
    @SerializedName("groupId")
    @Expose
    var groupId: String? = null

    @SerializedName("groupName")
    @Expose
    var groupName: String? = null

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null
}