package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlayGroupsResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("answerNumberCount")
    @Expose
    var answerNumberCount = 0

    @SerializedName("questionNumberCount")
    @Expose
    var questionNumberCount = 0

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("playGroupNameList")
    @Expose
    internal var playGroup: List<PlayGroup> = emptyList()
}