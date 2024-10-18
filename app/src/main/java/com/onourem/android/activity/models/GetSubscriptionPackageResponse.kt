package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetSubscriptionPackageResponse(
    @SerializedName("message")
    @Expose
    var message: String,
    @SerializedName("errorCode")
    @Expose
    var errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,
    @SerializedName("subscriptionPackageResList")
    @Expose
    var subscriptionPackageResList: List<PackageInfo>? = null
)