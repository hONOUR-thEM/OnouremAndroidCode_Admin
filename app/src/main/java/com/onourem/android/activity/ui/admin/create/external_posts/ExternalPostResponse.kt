package com.onourem.android.activity.ui.admin.create.external_posts


import com.google.gson.annotations.SerializedName

data class ExternalPostResponse(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("externalActivityDataList")
    val externalActivityDataList: List<ExternalActivityData>,
    @SerializedName("externalActivityIdList")
    val externalActivityIdList: List<String>
)