package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDirectToOneGameActivityResResponse {
    @SerializedName("ActivityGameDirectToOneResList")
    @Expose
    var activityGameDirectToOneResList: List<ActivityGameDirectToOneResList>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
}