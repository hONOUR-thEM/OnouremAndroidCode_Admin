package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PortalSignUpRequest {
    @SerializedName("contactPerson")
    @Expose
    var contactPerson = ""

    @SerializedName("contactNumber")
    @Expose
    var contactNumber = ""

    @SerializedName("email")
    @Expose
    var emailAddress = ""

    @SerializedName("password")
    @Expose
    var password = ""

    @SerializedName("institutionId")
    @Expose
    var institutionId = ""
}