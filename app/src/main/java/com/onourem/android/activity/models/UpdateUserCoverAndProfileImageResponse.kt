package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateUserCoverAndProfileImageResponse {
    @SerializedName("profilePicture")
    @Expose
    internal var profilePicture: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("largeProfilePicture")
    @Expose
    internal var largeProfilePicture: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("largeCoverPicture")
    @Expose
    internal var largeCoverPicture: String? = null

    @SerializedName("coverPicture")
    @Expose
    internal var coverPicture: String? = null
    fun getLargeCoverPicture(): String? {
        return largeCoverPicture
    }

    fun setLargeCoverPicture(largeCoverPicture: String?) {
        this.largeCoverPicture = largeCoverPicture
    }

    fun getCoverPicture(): String? {
        return coverPicture
    }

    fun setCoverPicture(coverPicture: String?) {
        this.coverPicture = coverPicture
    }

    fun getProfilePicture(): String? {
        return profilePicture
    }

    fun setProfilePicture(profilePicture: String?) {
        this.profilePicture = profilePicture
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getLargeProfilePicture(): String? {
        return largeProfilePicture
    }

    fun setLargeProfilePicture(largeProfilePicture: String?) {
        this.largeProfilePicture = largeProfilePicture
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }
}