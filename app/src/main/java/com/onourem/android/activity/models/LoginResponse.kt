package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("errorCode")
    @Expose
    var errorCode = ""

    @SerializedName("errorMessage")
    @Expose
    var errorMessage = ""

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any = ""

    @SerializedName("profilePicture")
    @Expose
    var profilePicture = ""

    @SerializedName("coverPicture")
    @Expose
    var coverPicture = ""

    @SerializedName("emailAddress")
    @Expose
    var emailAddress = ""

    @SerializedName("firstName")
    @Expose
    var firstName = ""

    @SerializedName("lastName")
    @Expose
    var lastName = ""

    @SerializedName("password")
    @Expose
    var password: Any = ""

    @SerializedName("mobileNumber")
    @Expose
    var mobileNumber = ""

    @SerializedName("gender")
    @Expose
    var gender = ""

    @SerializedName("profession")
    @Expose
    var profession = ""

    @SerializedName("city")
    @Expose
    var city = ""

    @SerializedName("state")
    @Expose
    var state = ""

    @SerializedName("country")
    @Expose
    var country = ""

    @SerializedName("cityId")
    @Expose
    var cityId = ""

    @SerializedName("geoName")
    @Expose
    var geoName = ""

    @SerializedName("token")
    @Expose
    var token = ""

    @SerializedName("userId")
    @Expose
    var userId = ""

    @SerializedName("status")
    @Expose
    var status = ""

    @SerializedName("feeds")
    @Expose
    var feeds: Any = ""

    @SerializedName("largeProfilePicture")
    @Expose
    var largeProfilePicture = ""

    @SerializedName("largeCoverPicture")
    @Expose
    var largeCoverPicture = ""

    @SerializedName("registrationStaus")
    @Expose
    var isRegistrationStaus = false

    @SerializedName("notificationAlertSettings")
    @Expose
    var notificationAlertSettings: NotificationAlertSettings? = null

    @SerializedName("refName")
    @Expose
    var refName = ""

    @SerializedName("languageId")
    @Expose
    var languageId = ""

    @SerializedName("dateOfBirth")
    @Expose
    var dateOfBirth = ""

    @SerializedName("userTypeId")
    @Expose
    var userTypeId = ""
    var socialId = ""
    override fun toString(): String {
        return "LoginResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", fieldName=" + fieldName +
                ", profilePicture='" + profilePicture + '\'' +
                ", coverPicture='" + coverPicture + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password=" + password +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", gender='" + profession + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", cityId='" + cityId + '\'' +
                ", geoName='" + geoName + '\'' +
                ", token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", feeds=" + feeds +
                ", largeProfilePicture='" + largeProfilePicture + '\'' +
                ", largeCoverPicture='" + largeCoverPicture + '\'' +
                ", registrationStaus=" + isRegistrationStaus +
                ", notificationAlertSettings=" + notificationAlertSettings +
                ", refName='" + refName + '\'' +
                ", languageId='" + languageId + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", socialId='" + socialId + '\'' +
                '}'
    }
}