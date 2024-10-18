package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
class OnlineInstitutionResponse(
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("partnerEmail")
    @Expose
    var partnerEmail: String,
    @SerializedName("partnerImage")
    @Expose
    var partnerImage: String,
    @SerializedName("partnerLink")
    @Expose
    var partnerLink: String,
    @SerializedName("partnerName")
    @Expose
    var partnerName: String,
    @SerializedName("partnerPhoneNumber")
    @Expose
    var partnerPhoneNumber: String
) : Parcelable