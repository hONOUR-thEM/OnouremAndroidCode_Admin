package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GroupMember {
    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("followerStatus")
    @Expose
    var followerStatus: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userTypeId: String? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is GroupMember) return false
        val that = o
        if (userId != that.userId) return false
        if (if (firstName != null) firstName != that.firstName else that.firstName != null) return false
        if (if (lastName != null) lastName != that.lastName else that.lastName != null) return false
        if (if (profilePicture != null) profilePicture != that.profilePicture else that.profilePicture != null) return false
        if (if (status != null) status != that.status else that.status != null) return false
        if (if (userTypeId != null) userTypeId != that.status else that.userTypeId != null) return false
        return if (followerStatus != null) followerStatus == that.followerStatus else that.followerStatus == null
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + if (firstName != null) firstName.hashCode() else 0
        result = 31 * result + if (lastName != null) lastName.hashCode() else 0
        result = 31 * result + if (profilePicture != null) profilePicture.hashCode() else 0
        result = 31 * result + if (status != null) status.hashCode() else 0
        result = 31 * result + if (followerStatus != null) followerStatus.hashCode() else 0
        result = 31 * result + if (userTypeId != null) userTypeId.hashCode() else 0
        return result
    }
}