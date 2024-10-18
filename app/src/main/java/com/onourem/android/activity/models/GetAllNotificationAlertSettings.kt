package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllNotificationAlertSettings {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("friendRequestRecieved")
    @Expose
    var friendRequestRecieved: String? = null

    @SerializedName("recieveNewPost")
    @Expose
    var recieveNewPost: String? = null

    @SerializedName("likeYourPost")
    @Expose
    var likeYourPost: String? = null

    @SerializedName("commentYourPost")
    @Expose
    var commentYourPost: String? = null

    @SerializedName("shareYourPost")
    @Expose
    var shareYourPost: String? = null

    @SerializedName("dittoYourPost")
    @Expose
    var dittoYourPost: String? = null

    @SerializedName("mentionYourPost")
    @Expose
    var mentionYourPost: String? = null

    @SerializedName("tagYouPost")
    @Expose
    var tagYouPost: String? = null

    @SerializedName("watchRequestRecieved")
    @Expose
    var watchRequestRecieved: String? = null

    @SerializedName("newFriendRequest")
    @Expose
    var newFriendRequest: String? = null

    @SerializedName("questionAsked")
    @Expose
    var questionAsked: String? = null

    @SerializedName("questionAnswered")
    @Expose
    var questionAnswered: String? = null

    @SerializedName("nonFriendAnswerInOClub")
    @Expose
    var nonFriendAnswerInOClub: String? = null

    @SerializedName("toAllFriendsNotAnsweredInSolo")
    @Expose
    var toAllFriendsNotAnsweredInSolo: String? = null

    @SerializedName("friendAndStrangerCommOnMycontent")
    @Expose
    var friendAndStrangerCommOnMycontent: String? = null

    @SerializedName("friendCommentingOnFriendAnswer")
    @Expose
    var friendCommentingOnFriendAnswer: String? = null

    @SerializedName("friendCommentingOnAnswerIhaveCommented")
    @Expose
    var friendCommentingOnAnswerIhaveCommented: String? = null

    @SerializedName("friendCommentingOnStrangerAnswer")
    @Expose
    var friendCommentingOnStrangerAnswer: String? = null

    @SerializedName("nonfriendCommentingOnFriendAnswerInOClub")
    @Expose
    var nonfriendCommentingOnFriendanswerInOclub: String? = null
}