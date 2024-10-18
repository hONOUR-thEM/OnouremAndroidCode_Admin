package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class FreeSubscriptionRes(
    @SerializedName("activationDate")
    @Expose
    var activationDate: String,
    @SerializedName("codeValidTill")
    @Expose
    var codeValidTill: String,
    @SerializedName("shareText")
    @Expose
    var shareText: String,
    @SerializedName("discountCoupon")
    @Expose
    var discountCoupon: String,
    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int,
    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int,
    @SerializedName("packageCode")
    @Expose
    var packageCode: String,
    @SerializedName("packageImageUrl")
    @Expose
    var packageImageUrl: String,
    @SerializedName("status")
    @Expose
    var status: String
)