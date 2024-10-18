package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadPostRequest {
    @SerializedName("sketchCordY")
    @Expose
    var sketchCordY = ""

    @SerializedName("serviceName")
    @Expose
    var serviceName = ""

    @SerializedName("deviceId")
    @Expose
    var deviceId = ""

    @SerializedName("image")
    @Expose
    var image = ""

    @SerializedName("text")
    @Expose
    var text = ""

    @SerializedName("anonymousOnOff")
    @Expose
    var anonymousOnOff = "N"

    @SerializedName("activityId")
    @Expose
    var activityId = ""

    @SerializedName("activityText")
    @Expose
    var activityText = ""

    @SerializedName("campaignId")
    @Expose
    var campaignId = ""

    @SerializedName("screenId")
    @Expose
    var screenId = ""

    @SerializedName("participantId")
    @Expose
    var participantId = ""

    @SerializedName("userAction")
    @Expose
    var userAction = ""

    @SerializedName("sketchWidth")
    @Expose
    var sketchWidth = ""

    @SerializedName("postId")
    @Expose
    var postId = ""

    @SerializedName("emailAddressList")
    @Expose
    var emailAddressList = ""

    @SerializedName("playgroupId")
    @Expose
    var playgroupId = ""

    @SerializedName("gameId")
    @Expose
    var gameId = ""

    @SerializedName("loginDay")
    @Expose
    var loginDay = ""

    @SerializedName("sketchCordX")
    @Expose
    var sketchCordX = ""

    @SerializedName("smallPostImage")
    @Expose
    var smallPostImage = ""

    @SerializedName("templateId")
    @Expose
    var templateId = ""

    @SerializedName("activityTypeId")
    @Expose
    var activityTypeId = ""

    @SerializedName("sketchHeight")
    @Expose
    var sketchHeight = ""

    @SerializedName("receiverList")
    @Expose
    var receiverList = ""

    @SerializedName("activityGameResId")
    @Expose
    var activityGameResId = ""

    @SerializedName("oclubActivityId")
    @Expose
    var oClubActivityId: String? = null

    @SerializedName("friendIds")
    @Expose
    var friendIds = ""

    @SerializedName("visibleToList")
    @Expose
    var visibleToList = ""

    @SerializedName("categoryId")
    @Expose
    var categoryId = ""

    @SerializedName("anonymousUserList")
    @Expose
    var anonymousUserList = ""

    @SerializedName("autoDeleteOnOff")
    @Expose
    var autoDeleteOnOff = "N"

    @SerializedName("screenSize")
    @Expose
    var screenSize = ""

    @SerializedName("videoData")
    @Expose
    var videoData = ""

    @SerializedName("pushToDiscover")
    @Expose
    var pushToDiscover = ""

    @SerializedName("subCategoryId")
    @Expose
    var subCategoryId = ""
}