package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExpressionDataRequest(
    @field:Expose @field:SerializedName("timeZone") val timeZone: String,
    @field:Expose @field:SerializedName("screenId") val screenId: String,
    @field:Expose @field:SerializedName(
        "serviceName"
    ) val serviceName: String
)