package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationInfoList : Serializable {

    @SerializedName("showStats")
    @Expose
    var showStats: String? = null

    @SerializedName("actionByUserID")
    @Expose
    var actionByUserID: String? = null

    @SerializedName("actionByName")
    @Expose
    var actionByName: String? = null

    @SerializedName("actionType")
    @Expose
    var actionType: String? = null

    @SerializedName("actionByPic")
    @Expose
    var actionByPic: String? = null

    @SerializedName("actionPostID")
    @Expose
    var actionPostID: String? = null

    @SerializedName("actionDateTime")
    @Expose
    var actionDateTime: String? = null

    @SerializedName("checkedStatus")
    @Expose
    var checkedStatus: String? = null

    @SerializedName("notificationId")
    @Expose
    var notificationId: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("actionPostCategory")
    @Expose
    var actionPostCategory: String? = null

    @SerializedName("postCategoryColorCode")
    @Expose
    var postCategoryColorCode: String? = null

    @SerializedName("postCategoryImageCode")
    @Expose
    var postCategoryImageCode: String? = null

    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("gameId")
    @Expose
    var gameId: String? = null

    @SerializedName("anonymousOnOff")
    @Expose
    var anonymousOnOff: String? = null

    @SerializedName("playgroupId")
    @Expose
    var playgroupId: String? = null

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null

    @SerializedName("playGroupName")
    @Expose
    var playGroupName: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null

    @SerializedName("audioInfoId")
    @Expose
    var audioIdFromNotification: String? = null
    var isProfilePicClickable = true
}