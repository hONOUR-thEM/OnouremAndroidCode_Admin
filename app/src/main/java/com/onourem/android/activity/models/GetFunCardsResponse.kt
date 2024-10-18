package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetFunCardsResponse {
    @SerializedName("cardInfoList")
    @Expose
    var cardInfoList: List<CardInfo>? = null

    @SerializedName("subtitle")
    @Expose
    var subtitle: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null
}