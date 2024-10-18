package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlayGroupUserInfoList {
    @SerializedName("userName")
    @Expose
    internal var userName: String? = null

    @SerializedName("userRelation")
    @Expose
    internal var userRelation: String? = null

    @SerializedName("userId")
    @Expose
    internal var userId: String? = null

    @SerializedName("userProfilePicUrl")
    @Expose
    internal var userProfilePicUrl: String? = null

    @SerializedName("isUserAdmin")
    @Expose
    var isUserAdmin: String? = null

    @SerializedName("userTypeId")
    @Expose
    internal var userType: String? = null
    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getUserRelation(): String? {
        return userRelation
    }

    fun setUserRelation(userRelation: String?) {
        this.userRelation = userRelation
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getUserProfilePicUrl(): String? {
        return userProfilePicUrl
    }

    fun setUserProfilePicUrl(userProfilePicUrl: String?) {
        this.userProfilePicUrl = userProfilePicUrl
    }

    fun getIsUserAdmin(): String? {
        return isUserAdmin
    }

    fun setIsUserAdmin(isUserAdmin: String?) {
        this.isUserAdmin = isUserAdmin
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is PlayGroupUserInfoList) return false
        val that = o
        if (getUserName() != that.getUserName()) return false
        if (getUserType() != that.getUserType()) return false
        if (if (getUserRelation() != null) getUserRelation() != that.getUserRelation() else that.getUserRelation() != null) return false
        if (getUserId() != that.getUserId()) return false
        if (if (getUserProfilePicUrl() != null) getUserProfilePicUrl() != that.getUserProfilePicUrl() else that.getUserProfilePicUrl() != null) return false
        return if (getIsUserAdmin() != null) getIsUserAdmin() == that.getIsUserAdmin() else that.getIsUserAdmin() == null
    }

    override fun hashCode(): Int {
        var result = getUserName().hashCode()
        result = 31 * result + if (getUserRelation() != null) getUserRelation().hashCode() else 0
        result = 31 * result + getUserId().hashCode()
        result = 31 * result + getUserType().hashCode()
        result = 31 * result + if (getUserProfilePicUrl() != null) getUserProfilePicUrl().hashCode() else 0
        result = 31 * result + if (getIsUserAdmin() != null) getIsUserAdmin().hashCode() else 0
        return result
    }

    fun getUserType(): String? {
        return userType
    }

    fun setUserType(userType: String?) {
        this.userType = userType
    }
}