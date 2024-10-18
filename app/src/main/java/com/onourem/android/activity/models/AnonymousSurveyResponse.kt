package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnonymousSurveyResponse : Serializable {

    @SerializedName("message")
    @Expose
    internal var message: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("surveyCOList")
    @Expose
    var surveyCOList: List<SurveyCOList> = emptyList()

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
}