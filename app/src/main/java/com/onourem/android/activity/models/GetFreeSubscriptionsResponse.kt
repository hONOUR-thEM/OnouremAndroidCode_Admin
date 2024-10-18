package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class GetFreeSubscriptionsResponse(
    @SerializedName("errorCode")
    @Expose
    var errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,
    @SerializedName("freeSubscriptionResList")
    @Expose
    var freeSubscriptionResList: List<FreeSubscriptionRes>
)