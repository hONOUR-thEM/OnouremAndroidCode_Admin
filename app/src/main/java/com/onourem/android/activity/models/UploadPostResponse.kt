package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadPostResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("token")
    @Expose
    var token: Any? = null

    @SerializedName("groupList")
    @Expose
    var groupList: Any? = null

    @SerializedName("userList")
    @Expose
    var userList: Any? = null

    @SerializedName("commentList")
    @Expose
    var commentList: Any? = null

    @SerializedName("commentId")
    @Expose
    var commentId: Any? = null

    @SerializedName("visibilityGroupList")
    @Expose
    var visibilityGroupList: Any? = null

    @SerializedName("campaignViewCO")
    @Expose
    var campaignViewCO: Any? = null

    @SerializedName("participationStatus")
    @Expose
    var participationStatus: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("activityTagStatus")
    @Expose
    var activityTagStatus: String? = null

    @SerializedName("outsideOnouremEmailMessage")
    @Expose
    var outsideOnouremEmailMessage: String? = null

    @SerializedName("outsideOnouremEmailSubject")
    @Expose
    var outsideOnouremEmailSubject: String? = null

    @SerializedName("activityText")
    @Expose
    var activityText: String? = null

    @SerializedName("postshareStartMsg")
    @Expose
    var postShareStartMsg: String? = null

    @SerializedName("postshareEndMsg")
    @Expose
    var postShareEndMsg: String? = null

    @SerializedName("linkUserId")
    @Expose
    var linkUserId: String? = null

    @SerializedName("linkType")
    @Expose
    var linkType: String? = null
}