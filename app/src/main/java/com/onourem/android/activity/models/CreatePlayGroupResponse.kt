package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreatePlayGroupResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("playGroupId")
    @Expose
    var playGroupId: Int? = null

    @SerializedName("playGroupName")
    @Expose
    var playGroupName: String? = null

    @SerializedName("lastActivityTime")
    @Expose
    var lastActivityTime: String? = null
}