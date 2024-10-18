package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserProfileData : Parcelable {
    @SerializedName("loginUserId")
    @Expose
    var loginUserId: String? = null

    @SerializedName("followingCount")
    @Expose
    var followingCount: String? = null

    @SerializedName("followerCount")
    @Expose
    var followerCount: String? = null

    @SerializedName("friendCount")
    @Expose
    var friendCount: String? = null

    @SerializedName("loginUserProfileRelation")
    @Expose
    var loginUserProfileRelation: String? = null

    @SerializedName("followStatus")
    @Expose
    var followStatus: String? = null

    @SerializedName("userProfilePicture")
    @Expose
    var userProfilePicture: String? = null

    @SerializedName("coverPicture")
    @Expose
    var coverPicture: String? = null

    @SerializedName("coverLargePicture")
    @Expose
    var coverLargePicture: String? = null

    @SerializedName("userName")
    @Expose
    var userName: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("thankYouCount")
    @Expose
    var thankYouCount: String? = null

    @SerializedName("missYouCount")
    @Expose
    var missYouCount: String? = null

    @SerializedName("admireYouCount")
    @Expose
    var admireYouCount: String? = null

    @SerializedName("userStatus")
    @Expose
    var userStatus: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null

    constructor()
    protected constructor(`in`: Parcel) {
        loginUserId = `in`.readString()
        followingCount = `in`.readString()
        followerCount = `in`.readString()
        friendCount = `in`.readString()
        loginUserProfileRelation = `in`.readString()
        followStatus = `in`.readString()
        userProfilePicture = `in`.readString()
        coverPicture = `in`.readString()
        coverLargePicture = `in`.readString()
        userName = `in`.readString()
        location = `in`.readString()
        thankYouCount = `in`.readString()
        missYouCount = `in`.readString()
        admireYouCount = `in`.readString()
        userStatus = `in`.readString()
        userType = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(loginUserId)
        dest.writeString(followingCount)
        dest.writeString(followerCount)
        dest.writeString(friendCount)
        dest.writeString(loginUserProfileRelation)
        dest.writeString(followStatus)
        dest.writeString(userProfilePicture)
        dest.writeString(coverPicture)
        dest.writeString(coverLargePicture)
        dest.writeString(userName)
        dest.writeString(location)
        dest.writeString(thankYouCount)
        dest.writeString(missYouCount)
        dest.writeString(admireYouCount)
        dest.writeString(userStatus)
        dest.writeString(userType)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as UserProfileData
        if (loginUserId != that.loginUserId) return false
        if (followingCount != that.followingCount) return false
        if (followerCount != that.followerCount) return false
        if (friendCount != that.friendCount) return false
        if (loginUserProfileRelation != that.loginUserProfileRelation) return false
        if (followStatus != that.followStatus) return false
        if (userProfilePicture != that.userProfilePicture) return false
        if (coverPicture != that.coverPicture) return false
        if (coverLargePicture != that.coverLargePicture) return false
        if (userName != that.userName) return false
        if (location != that.location) return false
        if (thankYouCount != that.thankYouCount) return false
        if (missYouCount != that.missYouCount) return false
        if (admireYouCount != that.admireYouCount) return false
        return if (userType != that.userType) false else userStatus == that.userStatus
    }

    override fun hashCode(): Int {
        var result = if (loginUserId != null) loginUserId.hashCode() else 0
        result = 31 * result + if (followingCount != null) followingCount.hashCode() else 0
        result = 31 * result + if (followerCount != null) followerCount.hashCode() else 0
        result = 31 * result + if (friendCount != null) friendCount.hashCode() else 0
        result = 31 * result + if (loginUserProfileRelation != null) loginUserProfileRelation.hashCode() else 0
        result = 31 * result + if (followStatus != null) followStatus.hashCode() else 0
        result = 31 * result + if (userProfilePicture != null) userProfilePicture.hashCode() else 0
        result = 31 * result + if (coverPicture != null) coverPicture.hashCode() else 0
        result = 31 * result + if (coverLargePicture != null) coverLargePicture.hashCode() else 0
        result = 31 * result + if (userName != null) userName.hashCode() else 0
        result = 31 * result + if (location != null) location.hashCode() else 0
        result = 31 * result + if (thankYouCount != null) thankYouCount.hashCode() else 0
        result = 31 * result + if (missYouCount != null) missYouCount.hashCode() else 0
        result = 31 * result + if (admireYouCount != null) admireYouCount.hashCode() else 0
        result = 31 * result + if (userStatus != null) userStatus.hashCode() else 0
        result = 31 * result + if (userType != null) userType.hashCode() else 0
        return result
    }

    companion object CREATOR : Parcelable.Creator<UserProfileData> {
        override fun createFromParcel(parcel: Parcel): UserProfileData {
            return UserProfileData(parcel)
        }

        override fun newArray(size: Int): Array<UserProfileData?> {
            return arrayOfNulls(size)
        }
    }
}