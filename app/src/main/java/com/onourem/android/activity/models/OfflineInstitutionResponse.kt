package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
class OfflineInstitutionResponse(
    @SerializedName("contactNumber1")
    @Expose
    var contactNumber1: String,
    @SerializedName("contactNumber2")
    @Expose
    var contactNumber2: String,
    @SerializedName("designation")
    @Expose
    var designation: String,
    @SerializedName("emailAddress")
    @Expose
    var emailAddress: String,
    @SerializedName("emergencytime")
    @Expose
    var emergencytime: String,
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("imageUrl")
    @Expose
    var imageUrl: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("timings")
    @Expose
    var timings: String
) : Parcelable
//