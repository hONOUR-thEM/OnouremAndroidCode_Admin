package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SocialLoginResponse {
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
    var profilePicture: Any? = null

    @SerializedName("coverPicture")
    @Expose
    var coverPicture: Any? = null

    @SerializedName("emailAddress")
    @Expose
    var emailAddress: Any? = null

    @SerializedName("firstName")
    @Expose
    var firstName: Any? = null

    @SerializedName("lastName")
    @Expose
    var lastName: Any? = null

    @SerializedName("password")
    @Expose
    var password: Any? = null

    @SerializedName("mobileNumber")
    @Expose
    var mobileNumber: Any? = null

    @SerializedName("gender")
    @Expose
    var gender: Any? = null

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
    var cityId: Any? = null

    @SerializedName("geoName")
    @Expose
    var geoName: Any? = null

    @SerializedName("token")
    @Expose
    var token: Any? = null

    @SerializedName("userId")
    @Expose
    var userId: Any? = null

    @SerializedName("status")
    @Expose
    var status: Any? = null

    @SerializedName("feeds")
    @Expose
    var feeds: Any? = null

    @SerializedName("largeProfilePicture")
    @Expose
    var largeProfilePicture: Any? = null

    @SerializedName("largeCoverPicture")
    @Expose
    var largeCoverPicture: Any? = null

    @SerializedName("registrationStaus")
    @Expose
    var registrationStaus: Boolean? = null

    @SerializedName("notificationAlertSettings")
    @Expose
    var notificationAlertSettings: Any? = null

    @SerializedName("refName")
    @Expose
    var refName: Any? = null

    @SerializedName("languageId")
    @Expose
    var languageId: Any? = null

    @SerializedName("dateOfBirth")
    @Expose
    var dateOfBirth: Any? = null
}