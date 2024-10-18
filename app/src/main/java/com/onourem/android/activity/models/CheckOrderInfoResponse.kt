package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class CheckOrderInfoResponse(
    @SerializedName("errorCode")
    @Expose
    var errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,
    @SerializedName("orderInfo")
    @Expose
    var orderInfo: OrderInfo
)