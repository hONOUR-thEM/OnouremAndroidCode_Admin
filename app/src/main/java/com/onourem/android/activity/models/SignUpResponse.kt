package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignUpResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: String? = null

    @SerializedName("coverPicture")
    @Expose
    var coverPicture: String? = null

    @SerializedName("emailAddress")
    @Expose
    var emailAddress: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("password")
    @Expose
    var password: Any? = null

    @SerializedName("mobileNumber")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("cityId")
    @Expose
    var cityId: String? = null

    @SerializedName("geoName")
    @Expose
    var geoName: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("status")
    @Expose
    var status: Any? = null

    @SerializedName("feeds")
    @Expose
    var feeds: Any? = null

    @SerializedName("largeProfilePicture")
    @Expose
    var largeProfilePicture: String? = null

    @SerializedName("largeCoverPicture")
    @Expose
    var largeCoverPicture: String? = null

    @SerializedName("registrationStaus")
    @Expose
    var registrationStaus: Boolean? = null

    @SerializedName("notificationAlertSettings")
    @Expose
    var notificationAlertSettings: NotificationAlertSettings? = null

    @SerializedName("refName")
    @Expose
    var refName: Any? = null

    @SerializedName("languageId")
    @Expose
    var languageId: String? = null

    @SerializedName("dateOfBirth")
    @Expose
    var dateOfBirth: String? = null

    @SerializedName("timeZone")
    @Expose
    var timeZone: String? = null
}