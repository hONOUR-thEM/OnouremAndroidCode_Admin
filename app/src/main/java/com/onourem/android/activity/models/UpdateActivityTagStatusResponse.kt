package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateActivityTagStatusResponse {
    @SerializedName("activityTagStatus")
    @Expose
    internal var activityTagStatus: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("message")
    @Expose
    private val message: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("participantResponseStatus")
    @Expose
    internal var participantResponseStatus: String? = null
    fun getActivityTagStatus(): String? {
        return activityTagStatus
    }

    fun setActivityTagStatus(activityTagStatus: String?) {
        this.activityTagStatus = activityTagStatus
    }

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

    fun getParticipantResponseStatus(): String? {
        return participantResponseStatus
    }

    fun setParticipantResponseStatus(participantResponseStatus: String?) {
        this.participantResponseStatus = participantResponseStatus
    }
}