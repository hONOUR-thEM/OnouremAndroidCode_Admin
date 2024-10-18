package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName

data class PublicPostListResponse(
    @SerializedName("displayNumberOfPosts")
    val displayNumberOfPosts: Int,
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("feedsList")
    val feedsList: List<FeedsList>,
    @SerializedName("postIdList")
    val postIdList: List<String>
)