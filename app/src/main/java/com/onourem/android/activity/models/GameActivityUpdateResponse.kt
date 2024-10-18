package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GameActivityUpdateResponse {
    @SerializedName("activityTagStatus")
    @Expose
    var activityTagStatus: String? = null

    @SerializedName("participationStatus")
    @Expose
    var participationStatus: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}