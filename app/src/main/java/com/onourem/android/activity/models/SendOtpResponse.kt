package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SendOtpResponse(
    @SerializedName("errorCode")
    @Expose
    val errorCode: String,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String,

    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("otp")
    @Expose
    val otp: String
)