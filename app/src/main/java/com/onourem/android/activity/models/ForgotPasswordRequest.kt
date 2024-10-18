package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForgotPasswordRequest(
    @field:Expose @field:SerializedName("emailAddress") var emailAddress: String,
    @field:Expose @field:SerializedName("deviceId") var deviceId: String
)