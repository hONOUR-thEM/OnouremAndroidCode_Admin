package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
class InstitutionInfo(
    @SerializedName("address")
    @Expose
    var address: String,
    @SerializedName("appointmentLink")
    @Expose
    var appointmentLink: String,
    @SerializedName("logoLink")
    @Expose
    var logoLink: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("officialHelpLink")
    @Expose
    var officialHelpLink: String,
    @SerializedName("teamPage")
    @Expose
    var teamPage: String,
    @SerializedName("counsellingContactNumber")
    @Expose
    var counsellingContactNumber: String,
    @SerializedName("timings")
    @Expose
    var timings: String
) : Parcelable