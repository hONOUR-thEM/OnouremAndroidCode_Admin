package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentList {
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

    //    @SerializedName("commentedDateAndTime")
    //    @Expose
    //    private String commentedDateAndTime;
    @SerializedName("userProfilePic")
    @Expose
    var userProfilePic: String? = null

    //    @SerializedName("postSmallImageUrl")
    //    @Expose
    //    private String postSmallImageUrl;
    @SerializedName("postLargeImageUrl")
    @Expose
    var postLargeImageUrl: String? = null

    @SerializedName("videoImageUrl")
    @Expose
    var videoImageUrl: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null
}