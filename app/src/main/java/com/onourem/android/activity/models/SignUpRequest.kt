package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignUpRequest {
    @SerializedName("firstName")
    @Expose
    var firstName = ""

    @SerializedName("lastName")
    @Expose
    var lastName = ""

    @SerializedName("emailAddress")
    @Expose
    var emailAddress = ""

    @SerializedName("password")
    @Expose
    var password = ""

    @SerializedName("gender")
    @Expose
    var gender = ""

    @SerializedName("profession")
    @Expose
    var profession = ""

    @SerializedName("deviceId")
    @Expose
    var deviceId = ""

    @SerializedName("linkUserId")
    @Expose
    var linkUserId = ""

    @SerializedName("languageId")
    @Expose
    var languageId = ""

    @SerializedName("screenId")
    @Expose
    var screenId = ""

    @SerializedName("serviceName")
    @Expose
    var serviceName = ""

    @SerializedName("countryCode")
    @Expose
    var countryCode = ""

    @SerializedName("socialId")
    @Expose
    var socialId = ""

    @SerializedName("socialSource")
    @Expose
    var socialSource = ""

    @SerializedName("profilePicture")
    @Expose
    var profilePicture = ""

    @SerializedName("largeProfilePicture")
    @Expose
    var largeProfilePicture = ""

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

    @SerializedName("registrationId")
    @Expose
    var registrationId: String? = null

    @SerializedName("timeZone")
    @Expose
    var timeZone: String? = null
}