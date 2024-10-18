package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserLinkInfoResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("title")
    @Expose
    internal var title: String? = null

    @SerializedName("message")
    @Expose
    internal var message: String? = null

    @SerializedName("userLink")
    @Expose
    internal var userLink: String? = null

    @SerializedName("linkUserId")
    @Expose
    internal var linkUserId: String? = null

    @SerializedName("linkType")
    @Expose
    internal var linkType: String? = null

    @SerializedName("activityText")
    @Expose
    internal var activityText: String? = null

    @SerializedName("activityImageUrl")
    @Expose
    internal var activityImageUrl: String? = null

    @SerializedName("shortLink")
    @Expose
    internal var shortLink: String? = null

    @SerializedName("linkSharingDisabledMessage")
    @Expose
    internal var linkSharingDisabledMessage: String? = null
    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getUserLink(): String? {
        return userLink
    }

    fun setUserLink(userLink: String?) {
        this.userLink = userLink
    }

    fun getLinkUserId(): String? {
        return linkUserId
    }

    fun setLinkUserId(linkUserId: String?) {
        this.linkUserId = linkUserId
    }

    fun getLinkType(): String? {
        return linkType
    }

    fun setLinkType(linkType: String?) {
        this.linkType = linkType
    }

    fun getActivityText(): String? {
        return activityText
    }

    fun setActivityText(activityText: String?) {
        this.activityText = activityText
    }

    fun getActivityImageUrl(): String? {
        return activityImageUrl
    }

    fun setActivityImageUrl(activityImageUrl: String?) {
        this.activityImageUrl = activityImageUrl
    }

    fun getShortLink(): String? {
        return shortLink
    }

    fun setShortLink(shortLink: String?) {
        this.shortLink = shortLink
    }
}