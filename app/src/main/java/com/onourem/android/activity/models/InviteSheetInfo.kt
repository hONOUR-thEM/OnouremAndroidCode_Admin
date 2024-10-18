package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InviteSheetInfo {
    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("largeImageUrl")
    @Expose
    var largeImageUrl: String? = null

    @SerializedName("videoUrl")
    @Expose
    var videoUrl: String? = null

    @SerializedName("smallImageUrl")
    @Expose
    var smallImageUrl: String? = null

    @SerializedName("imageWidth")
    @Expose
    var imageWidth: String? = null

    @SerializedName("imageHeight")
    @Expose
    var imageHeight: String? = null

    @SerializedName("activityText")
    @Expose
    var activityText: String? = null
}