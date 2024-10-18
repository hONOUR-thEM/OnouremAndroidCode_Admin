package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TaskAndMessagePostList {
    @SerializedName("gameId")
    @Expose
    internal var gameId: String? = null

    @SerializedName("participantId")
    @Expose
    internal var participantId: String? = null

    @SerializedName("participantImageUrl")
    @Expose
    internal var participantImageUrl: String? = null

    @SerializedName("participantName")
    @Expose
    internal var participantName: String? = null

    @SerializedName("participantResponseStatus")
    @Expose
    internal var participantResponseStatus: String? = null

    @SerializedName("participantResponse")
    @Expose
    internal var participantResponse: String? = null

    @SerializedName("participantResponseImageSmallLink")
    @Expose
    internal var participantResponseImageSmallLink: String? = null

    @SerializedName("participantResponseImageLargeLink")
    @Expose
    internal var participantResponseImageLargeLink: String? = null

    @SerializedName("participantResponseVideoLink")
    @Expose
    internal var participantResponseVideoLink: String? = null

    @SerializedName("participantResponseDate")
    @Expose
    internal var participantResponseDate: String? = null

    @SerializedName("gameStartedByUserID")
    @Expose
    internal var gameStartedByUserID: String? = null

    @SerializedName("gameStartedByUserName")
    @Expose
    internal var gameStartedByUserName: Any? = null

    @SerializedName("gameStartDate")
    @Expose
    internal var gameStartDate: String? = null

    @SerializedName("gameExpiryDate")
    @Expose
    internal var gameExpiryDate: String? = null

    @SerializedName("isGameExpired")
    @Expose
    internal var isGameExpired: String? = null

    @SerializedName("autodeleteStatus")
    @Expose
    internal var autodeleteStatus: String? = null

    @SerializedName("isResponseDeleted")
    @Expose
    internal var isResponseDeleted: String? = null

    @SerializedName("commentCount")
    @Expose
    internal var commentCount: String? = null

    @SerializedName("participantStatus")
    @Expose
    internal var participantStatus: String? = null

    @SerializedName("askedByFirstName")
    @Expose
    internal var askedByFirstName: String? = null
    fun getGameId(): String? {
        return gameId
    }

    fun setGameId(gameId: String?) {
        this.gameId = gameId
    }

    fun getParticipantId(): String? {
        return participantId
    }

    fun setParticipantId(participantId: String?) {
        this.participantId = participantId
    }

    fun getParticipantImageUrl(): String? {
        return participantImageUrl
    }

    fun setParticipantImageUrl(participantImageUrl: String?) {
        this.participantImageUrl = participantImageUrl
    }

    fun getParticipantName(): String? {
        return participantName
    }

    fun setParticipantName(participantName: String?) {
        this.participantName = participantName
    }

    fun getParticipantResponseStatus(): String? {
        return participantResponseStatus
    }

    fun setParticipantResponseStatus(participantResponseStatus: String?) {
        this.participantResponseStatus = participantResponseStatus
    }

    fun getParticipantResponse(): String? {
        return participantResponse
    }

    fun setParticipantResponse(participantResponse: String?) {
        this.participantResponse = participantResponse
    }

    fun getParticipantResponseImageSmallLink(): String? {
        return participantResponseImageSmallLink
    }

    fun setParticipantResponseImageSmallLink(participantResponseImageSmallLink: String?) {
        this.participantResponseImageSmallLink = participantResponseImageSmallLink
    }

    fun getParticipantResponseImageLargeLink(): String? {
        return participantResponseImageLargeLink
    }

    fun setParticipantResponseImageLargeLink(participantResponseImageLargeLink: String?) {
        this.participantResponseImageLargeLink = participantResponseImageLargeLink
    }

    fun getParticipantResponseVideoLink(): String? {
        return participantResponseVideoLink
    }

    fun setParticipantResponseVideoLink(participantResponseVideoLink: String?) {
        this.participantResponseVideoLink = participantResponseVideoLink
    }

    fun getParticipantResponseDate(): String? {
        return participantResponseDate
    }

    fun setParticipantResponseDate(participantResponseDate: String?) {
        this.participantResponseDate = participantResponseDate
    }

    fun getGameStartedByUserID(): String? {
        return gameStartedByUserID
    }

    fun setGameStartedByUserID(gameStartedByUserID: String?) {
        this.gameStartedByUserID = gameStartedByUserID
    }

    fun getGameStartedByUserName(): Any? {
        return gameStartedByUserName
    }

    fun setGameStartedByUserName(gameStartedByUserName: Any?) {
        this.gameStartedByUserName = gameStartedByUserName
    }

    fun getGameStartDate(): String? {
        return gameStartDate
    }

    fun setGameStartDate(gameStartDate: String?) {
        this.gameStartDate = gameStartDate
    }

    fun getGameExpiryDate(): String? {
        return gameExpiryDate
    }

    fun setGameExpiryDate(gameExpiryDate: String?) {
        this.gameExpiryDate = gameExpiryDate
    }

    fun getIsGameExpired(): String? {
        return isGameExpired
    }

    fun setIsGameExpired(isGameExpired: String?) {
        this.isGameExpired = isGameExpired
    }

    fun getAutodeleteStatus(): String? {
        return autodeleteStatus
    }

    fun setAutodeleteStatus(autodeleteStatus: String?) {
        this.autodeleteStatus = autodeleteStatus
    }

    fun getIsResponseDeleted(): String? {
        return isResponseDeleted
    }

    fun setIsResponseDeleted(isResponseDeleted: String?) {
        this.isResponseDeleted = isResponseDeleted
    }

    fun getCommentCount(): String? {
        return commentCount
    }

    fun setCommentCount(commentCount: String?) {
        this.commentCount = commentCount
    }

    fun getParticipantStatus(): String? {
        return participantStatus
    }

    fun setParticipantStatus(participantStatus: String?) {
        this.participantStatus = participantStatus
    }

    fun getAskedByFirstName(): String? {
        return askedByFirstName
    }

    fun setAskedByFirstName(askedByFirstName: String?) {
        this.askedByFirstName = askedByFirstName
    }
}