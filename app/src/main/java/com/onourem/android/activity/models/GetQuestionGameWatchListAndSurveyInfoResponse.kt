package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetQuestionGameWatchListAndSurveyInfoResponse : Serializable {
    @SerializedName("isSurveyNew")
    @Expose
    internal var isSurveyNew: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("isQuestionNew")
    @Expose
    internal var isQuestionNew: String? = null

    @SerializedName("isWatchListNew")
    @Expose
    internal var isWatchListNew: String? = null
    fun getIsSurveyNew(): String? {
        return isSurveyNew
    }

    fun setIsSurveyNew(isSurveyNew: String?) {
        this.isSurveyNew = isSurveyNew
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

    fun getIsQuestionNew(): String? {
        return isQuestionNew
    }

    fun setIsQuestionNew(isQuestionNew: String?) {
        this.isQuestionNew = isQuestionNew
    }

    fun getIsWatchListNew(): String? {
        return isWatchListNew
    }

    fun setIsWatchListNew(isWatchListNew: String?) {
        this.isWatchListNew = isWatchListNew
    }
}