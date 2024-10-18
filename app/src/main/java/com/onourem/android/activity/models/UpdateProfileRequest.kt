package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateProfileRequest {
    @SerializedName("phone")
    @Expose
    var phone = ""

    @SerializedName("firstName")
    @Expose
    var firstName = ""

    @SerializedName("lastName")
    @Expose
    var lastName = ""

    @SerializedName("gender")
    @Expose
    var gender = ""

    @SerializedName("profession")
    @Expose
    var profession = ""

    @SerializedName("cityId")
    @Expose
    var cityId = ""

    @SerializedName("stateId")
    @Expose
    var stateId = ""

    @SerializedName("countryId")
    @Expose
    var countryId = ""

    @SerializedName("profilePic")
    @Expose
    var profilePic = ""

    @SerializedName("largeProfilePic")
    @Expose
    var largeProfilePic = ""

    @SerializedName("coverPicture")
    @Expose
    var coverPicture = ""

    @SerializedName("emailAddress")
    @Expose
    var emailAddress = ""

    @SerializedName("smallCoverPicture")
    @Expose
    var smallCoverPicture = ""

    @SerializedName("refCode")
    @Expose
    var refCode = ""

    @SerializedName("screenId")
    @Expose
    var screenId = ""

    @SerializedName("serviceName")
    @Expose
    var serviceName = ""
    var password = ""
    var oldPassword = ""
}