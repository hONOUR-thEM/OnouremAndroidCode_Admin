package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForgotPasswordResponse {
    //    @SerializedName("dateOfBirth")
    //    @Expose
    //    private Object dateOfBirth;
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

    //    @SerializedName("coverPicture")
    //    @Expose
    //    private Object coverPicture;
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

    //    @SerializedName("mobileNumber")
    //    @Expose
    //    private Object mobileNumber;
    @SerializedName("gender")
    @Expose
    var gender: Any? = null

    //    @SerializedName("city")
    //    @Expose
    //    private String city;
    @SerializedName("state")
    @Expose
    var state: String? = null

    //    @SerializedName("country")
    //    @Expose
    //    private String country;
    @SerializedName("cityId")
    @Expose
    var cityId: Any? = null

    //    @SerializedName("geoName")
    //    @Expose
    //    private Object geoName;
    @SerializedName("token")
    @Expose
    var token: Any? = null

    @SerializedName("userId")
    @Expose
    var userId: Any? = null

    @SerializedName("status")
    @Expose
    var status: Any? = null

    //    @SerializedName("feeds")
    //    @Expose
    //    private Object feeds;
    @SerializedName("largeProfilePicture")
    @Expose
    var largeProfilePicture: Any? = null

    //    @SerializedName("largeCoverPicture")
    //    @Expose
    //    private Object largeCoverPicture;
    //    @SerializedName("registrationStaus")
    //    @Expose
    //    private Boolean registrationStaus;
    //    @SerializedName("notificationAlertSettings")
    //    @Expose
    //    private Object notificationAlertSettings;
    //    @SerializedName("refName")
    //    @Expose
    //    private Object refName;
    @SerializedName("languageId")
    @Expose
    var languageId: Any? = null
}