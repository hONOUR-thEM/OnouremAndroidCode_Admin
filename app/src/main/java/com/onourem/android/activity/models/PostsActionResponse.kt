package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostsActionResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("groupList")
    @Expose
    var groupList: String? = null

    @SerializedName("userList")
    @Expose
    var userList: String? = null

    @SerializedName("commentList")
    @Expose
    var commentList: String? = null

    @SerializedName("commentId")
    @Expose
    var commentId: String? = null

    @SerializedName("visibilityGroupList")
    @Expose
    var visibilityGroupList: String? = null

    @SerializedName("campaignViewCO")
    @Expose
    var campaignViewCO: String? = null

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
}