package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AudioCommentResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("displayNumberOfComments")
    @Expose
    var displayNumberOfComments: String? = null

    @SerializedName("audioCommentList")
    @Expose
    var audioCommentList: List<AudioComment>? = null

    @SerializedName("audioCommentIdList")
    @Expose
    var audioCommentIdList: List<Int>? = null
}