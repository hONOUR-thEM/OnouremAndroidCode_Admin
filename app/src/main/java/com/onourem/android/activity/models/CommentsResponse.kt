package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentsResponse {
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
    var commentList: List<CommentList>? = null

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
    var participationStatus: Any? = null

    @SerializedName("message")
    @Expose
    var message: Any? = null

    @SerializedName("activityTagStatus")
    @Expose
    var activityTagStatus: Any? = null

    @SerializedName("outsideOnouremEmailMessage")
    @Expose
    var outsideOnouremEmailMessage: Any? = null

    @SerializedName("outsideOnouremEmailSubject")
    @Expose
    var outsideOnouremEmailSubject: Any? = null
}