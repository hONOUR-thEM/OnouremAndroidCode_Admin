package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationAlertSettings {
    @SerializedName("id")
    @Expose
    internal var id: Long = 0

    @SerializedName("userId")
    @Expose
    internal var userId: Long = 0

    @SerializedName("friendRequestRecieved")
    @Expose
    internal var friendRequestRecieved: String? = null

    @SerializedName("recieveNewPost")
    @Expose
    internal var recieveNewPost: String? = null

    @SerializedName("likeYourPost")
    @Expose
    internal var likeYourPost: String? = null

    @SerializedName("commentYourPost")
    @Expose
    internal var commentYourPost: String? = null

    @SerializedName("shareYourPost")
    @Expose
    internal var shareYourPost: String? = null

    @SerializedName("dittoYourPost")
    @Expose
    internal var dittoYourPost: String? = null

    @SerializedName("mentionYourPost")
    @Expose
    internal var mentionYourPost: String? = null

    @SerializedName("tagYouPost")
    @Expose
    internal var tagYouPost: String? = null

    @SerializedName("watchRequestRecieved")
    @Expose
    internal var watchRequestRecieved: String? = null
    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getUserId(): Long {
        return userId
    }

    fun setUserId(userId: Long) {
        this.userId = userId
    }

    fun getFriendRequestRecieved(): String? {
        return friendRequestRecieved
    }

    fun setFriendRequestRecieved(friendRequestRecieved: String?) {
        this.friendRequestRecieved = friendRequestRecieved
    }

    fun getRecieveNewPost(): String? {
        return recieveNewPost
    }

    fun setRecieveNewPost(recieveNewPost: String?) {
        this.recieveNewPost = recieveNewPost
    }

    fun getLikeYourPost(): String? {
        return likeYourPost
    }

    fun setLikeYourPost(likeYourPost: String?) {
        this.likeYourPost = likeYourPost
    }

    fun getCommentYourPost(): String? {
        return commentYourPost
    }

    fun setCommentYourPost(commentYourPost: String?) {
        this.commentYourPost = commentYourPost
    }

    fun getShareYourPost(): String? {
        return shareYourPost
    }

    fun setShareYourPost(shareYourPost: String?) {
        this.shareYourPost = shareYourPost
    }

    fun getDittoYourPost(): String? {
        return dittoYourPost
    }

    fun setDittoYourPost(dittoYourPost: String?) {
        this.dittoYourPost = dittoYourPost
    }

    fun getMentionYourPost(): String? {
        return mentionYourPost
    }

    fun setMentionYourPost(mentionYourPost: String?) {
        this.mentionYourPost = mentionYourPost
    }

    fun getTagYouPost(): String? {
        return tagYouPost
    }

    fun setTagYouPost(tagYouPost: String?) {
        this.tagYouPost = tagYouPost
    }

    fun getWatchRequestRecieved(): String? {
        return watchRequestRecieved
    }

    fun setWatchRequestRecieved(watchRequestRecieved: String?) {
        this.watchRequestRecieved = watchRequestRecieved
    }
}