package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogoutRequest {
    @SerializedName("screenId")
    @Expose
    var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null

    @SerializedName("deviceId")
    @Expose
    var deviceId: String? = null
}