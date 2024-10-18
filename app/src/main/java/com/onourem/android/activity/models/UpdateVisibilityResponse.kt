package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateVisibilityResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: Any? = null

    @SerializedName("token")
    @Expose
    internal var token: Any? = null

    @SerializedName("groupList")
    @Expose
    internal var groupList: Any? = null

    @SerializedName("userList")
    @Expose
    internal var userList: Any? = null

    @SerializedName("commentList")
    @Expose
    internal var commentList: Any? = null

    @SerializedName("commentId")
    @Expose
    internal var commentId: Any? = null

    @SerializedName("visibilityGroupList")
    @Expose
    internal var visibilityGroupList: Any? = null

    @SerializedName("campaignViewCO")
    @Expose
    internal var campaignViewCO: Any? = null

    @SerializedName("participationStatus")
    @Expose
    internal var participationStatus: Any? = null

    @SerializedName("message")
    @Expose
    internal var message: Any? = null

    @SerializedName("activityTagStatus")
    @Expose
    internal var activityTagStatus: Any? = null

    @SerializedName("outsideOnouremEmailMessage")
    @Expose
    internal var outsideOnouremEmailMessage: Any? = null

    @SerializedName("outsideOnouremEmailSubject")
    @Expose
    internal var outsideOnouremEmailSubject: Any? = null
    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getFieldName(): Any? {
        return fieldName
    }

    fun setFieldName(fieldName: Any?) {
        this.fieldName = fieldName
    }

    fun getToken(): Any? {
        return token
    }

    fun setToken(token: Any?) {
        this.token = token
    }

    fun getGroupList(): Any? {
        return groupList
    }

    fun setGroupList(groupList: Any?) {
        this.groupList = groupList
    }

    fun getUserList(): Any? {
        return userList
    }

    fun setUserList(userList: Any?) {
        this.userList = userList
    }

    fun getCommentList(): Any? {
        return commentList
    }

    fun setCommentList(commentList: Any?) {
        this.commentList = commentList
    }

    fun getCommentId(): Any? {
        return commentId
    }

    fun setCommentId(commentId: Any?) {
        this.commentId = commentId
    }

    fun getVisibilityGroupList(): Any? {
        return visibilityGroupList
    }

    fun setVisibilityGroupList(visibilityGroupList: Any?) {
        this.visibilityGroupList = visibilityGroupList
    }

    fun getCampaignViewCO(): Any? {
        return campaignViewCO
    }

    fun setCampaignViewCO(campaignViewCO: Any?) {
        this.campaignViewCO = campaignViewCO
    }

    fun getParticipationStatus(): Any? {
        return participationStatus
    }

    fun setParticipationStatus(participationStatus: Any?) {
        this.participationStatus = participationStatus
    }

    fun getMessage(): Any? {
        return message
    }

    fun setMessage(message: Any?) {
        this.message = message
    }

    fun getActivityTagStatus(): Any? {
        return activityTagStatus
    }

    fun setActivityTagStatus(activityTagStatus: Any?) {
        this.activityTagStatus = activityTagStatus
    }

    fun getOutsideOnouremEmailMessage(): Any? {
        return outsideOnouremEmailMessage
    }

    fun setOutsideOnouremEmailMessage(outsideOnouremEmailMessage: Any?) {
        this.outsideOnouremEmailMessage = outsideOnouremEmailMessage
    }

    fun getOutsideOnouremEmailSubject(): Any? {
        return outsideOnouremEmailSubject
    }

    fun setOutsideOnouremEmailSubject(outsideOnouremEmailSubject: Any?) {
        this.outsideOnouremEmailSubject = outsideOnouremEmailSubject
    }
}