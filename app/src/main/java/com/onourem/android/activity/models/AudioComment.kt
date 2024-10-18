package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AudioComment {
    @SerializedName("commentId")
    @Expose
    var commentId: String? = null

    @SerializedName("commentText")
    @Expose
    var commentText: String? = null

    @SerializedName("commentedBy")
    @Expose
    var commentedBy: String? = null

    @SerializedName("userName")
    @Expose
    var userName: String? = null

    @SerializedName("userProfilePic")
    @Expose
    var userProfilePic: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null
}