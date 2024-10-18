package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnonymousSurveyUpdateResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("message")
    @Expose
    internal var message: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
}