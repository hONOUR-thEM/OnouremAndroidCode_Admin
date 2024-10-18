package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StandardResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
}