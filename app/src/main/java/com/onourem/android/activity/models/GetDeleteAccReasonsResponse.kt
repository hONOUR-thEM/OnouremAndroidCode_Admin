package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDeleteAccReasonsResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("subtitle")
    @Expose
    var subtitle: String? = null

    @SerializedName("reasonsList")
    @Expose
    var reasonsList: List<ReasonList>? = null
}