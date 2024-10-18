package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetQuestionFilterInfoResponse {
    @SerializedName("newQuestionsBubbleTag")
    @Expose
    internal var newQuestionsBubbleTag: String? = null

    @SerializedName("friendsPlayingBubbleTag")
    @Expose
    internal var friendsPlayingBubbleTag: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("answeredBubbleTag")
    @Expose
    internal var answeredBubbleTag: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("oClubBubbleTag")
    @Expose
    internal var oClubBubbleTag: String? = null

    @SerializedName("messagingBubbleTag")
    @Expose
    internal var messagingBubbleTag: String? = null

    fun getNewQuestionsBubbleTag(): String? {
        return newQuestionsBubbleTag
    }

    fun setNewQuestionsBubbleTag(newQuestionsBubbleTag: String?) {
        this.newQuestionsBubbleTag = newQuestionsBubbleTag
    }

    fun getFriendsPlayingBubbleTag(): String? {
        return friendsPlayingBubbleTag
    }

    fun setFriendsPlayingBubbleTag(friendsPlayingBubbleTag: String?) {
        this.friendsPlayingBubbleTag = friendsPlayingBubbleTag
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getAnsweredBubbleTag(): String? {
        return answeredBubbleTag
    }

    fun setAnsweredBubbleTag(answeredBubbleTag: String?) {
        this.answeredBubbleTag = answeredBubbleTag
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getOClubBubbleTag(): String? {
        return oClubBubbleTag
    }

    fun setOClubBubbleTag(oClubBubbleTag: String?) {
        this.oClubBubbleTag = oClubBubbleTag
    }

    fun getMessagingBubbleTag(): String? {
        return messagingBubbleTag
    }

    fun setMessagingBubbleTag(messagingBubbleTag: String?) {
        this.messagingBubbleTag = messagingBubbleTag
    }
}