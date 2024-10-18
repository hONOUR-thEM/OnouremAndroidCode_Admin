package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class IntroRequest {
    @SerializedName("screenId")
    @Expose
    var screenId: String? = null

    @SerializedName("demoVersion")
    @Expose
    var demoVersion: String? = null

    @SerializedName("appVersion")
    @Expose
    var appVersion: String? = null

    @SerializedName("deviceName")
    @Expose
    var deviceName: String? = null

    @SerializedName("deviceModel")
    @Expose
    var deviceModel: String? = null

    @SerializedName("osVersion")
    @Expose
    var osVersion: String? = null

    @SerializedName("linkUserId")
    @Expose
    var linkUserId: String? = null

    @SerializedName("timeZone")
    @Expose
    var timeZone: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null

    @SerializedName("reqSource")
    @Expose
    var reqSource: String? = null

    @SerializedName("deviceId")
    @Expose
    var deviceId: String? = null
    var registrationId: String? = null
}