package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class FeedsList : Parcelable {
    @SerializedName("postId")
    @Expose
    var postId: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: String? = null

    @SerializedName("postCreatedId")
    @Expose
    var postCreatedId: String? = null

    @SerializedName("reciever")
    @Expose
    internal var reciever: String? = null

    @SerializedName("receiverId")
    @Expose
    var receiverId: List<String>? = null

    @SerializedName("receiverTypeId")
    @Expose
    var receiverTypeId: List<String>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("postLargeImageURL")
    @Expose
    var postLargeImageURL: String? = null

    @SerializedName("postSmallImageURL")
    @Expose
    internal var postSmallImageURL: String? = null

    @SerializedName("postStatus")
    @Expose
    internal var postStatus: String? = null

    @SerializedName("postCreationDate")
    @Expose
    var postCreationDate: String? = null

    @SerializedName("commentCount")
    @Expose
    var commentCount: String? = "0"

    @SerializedName("actionId")
    @Expose
    var actionId: String? = null

    @SerializedName("visibility")
    @Expose
    var visibility: String? = null

    @SerializedName("videoURL")
    @Expose
    var videoURL: String? = null

    @SerializedName("categoryType")
    @Expose
    var categoryType: String? = null

    @SerializedName("isReceiverRequired")
    @Expose
    var isReceiverRequired: String? = null

    @SerializedName("anonymousOnOff")
    @Expose
    var anonymousOnOff: String? = null

    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userTypeId: String? = null

    @SerializedName("playgroupName")
    @Expose
    var playgroupName: String? = null

    @SerializedName("playgroupId")
    @Expose
    var playgroupId: String? = null

    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int? = null

    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int? = null

    @SerializedName("commentEnabledForPlayGroup")
    @Expose
    internal var commentsEnabled: String? = null

    constructor()
    protected constructor(`in`: Parcel) {
        postId = `in`.readString()
        firstName = `in`.readString()
        lastName = `in`.readString()
        profilePicture = `in`.readString()
        postCreatedId = `in`.readString()
        reciever = `in`.readString()
        receiverId = `in`.createStringArrayList()
        receiverTypeId = `in`.createStringArrayList()
        message = `in`.readString()
        postLargeImageURL = `in`.readString()
        postSmallImageURL = `in`.readString()
        postStatus = `in`.readString()
        postCreationDate = `in`.readString()
        commentCount = `in`.readString()
        actionId = `in`.readString()
        visibility = `in`.readString()
        videoURL = `in`.readString()
        categoryType = `in`.readString()
        isReceiverRequired = `in`.readString()
        anonymousOnOff = `in`.readString()
        activityId = `in`.readString()
        userTypeId = `in`.readString()
        playgroupId = `in`.readString()
        playgroupName = `in`.readString()
        commentsEnabled = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(postId)
        dest.writeString(firstName)
        dest.writeString(lastName)
        dest.writeString(profilePicture)
        dest.writeString(postCreatedId)
        dest.writeString(reciever)
        dest.writeStringList(receiverId)
        dest.writeStringList(receiverTypeId)
        dest.writeString(message)
        dest.writeString(postLargeImageURL)
        dest.writeString(postSmallImageURL)
        dest.writeString(postStatus)
        dest.writeString(postCreationDate)
        dest.writeString(commentCount)
        dest.writeString(actionId)
        dest.writeString(visibility)
        dest.writeString(videoURL)
        dest.writeString(categoryType)
        dest.writeString(isReceiverRequired)
        dest.writeString(anonymousOnOff)
        dest.writeString(activityId)
        dest.writeString(userTypeId)
        dest.writeString(playgroupName)
        dest.writeString(playgroupId)
        dest.writeString(commentsEnabled)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val feedsList = other as FeedsList
        if (postId != feedsList.postId) return false
        if (firstName != feedsList.firstName) return false
        if (lastName != feedsList.lastName) return false
        if (profilePicture != feedsList.profilePicture) return false
        if (postCreatedId != feedsList.postCreatedId) return false
        if (reciever != feedsList.reciever) return false
        if (receiverId != feedsList.receiverId) return false
        if (receiverTypeId != feedsList.receiverTypeId) return false
        if (message != feedsList.message) return false
        if (postLargeImageURL != feedsList.postLargeImageURL) return false
        if (postSmallImageURL != feedsList.postSmallImageURL) return false
        if (postStatus != feedsList.postStatus) return false
        if (postCreationDate != feedsList.postCreationDate) return false
        if (commentCount != feedsList.commentCount) return false
        if (actionId != feedsList.actionId) return false
        if (visibility != feedsList.visibility) return false
        if (videoURL != feedsList.videoURL) return false
        if (categoryType != feedsList.categoryType) return false
        if (isReceiverRequired != feedsList.isReceiverRequired) return false
        if (anonymousOnOff != feedsList.anonymousOnOff) return false
        return if (userTypeId != feedsList.userTypeId) false else activityId == feedsList.activityId
    }

    override fun hashCode(): Int {
        var result = if (postId != null) postId.hashCode() else 0
        result = 31 * result + if (firstName != null) firstName.hashCode() else 0
        result = 31 * result + if (lastName != null) lastName.hashCode() else 0
        result = 31 * result + if (profilePicture != null) profilePicture.hashCode() else 0
        result = 31 * result + if (postCreatedId != null) postCreatedId.hashCode() else 0
        result = 31 * result + if (reciever != null) reciever.hashCode() else 0
        result = 31 * result + if (receiverId != null) receiverId.hashCode() else 0
        result = 31 * result + if (receiverTypeId != null) receiverTypeId.hashCode() else 0
        result = 31 * result + if (message != null) message.hashCode() else 0
        result = 31 * result + if (postLargeImageURL != null) postLargeImageURL.hashCode() else 0
        result = 31 * result + if (postSmallImageURL != null) postSmallImageURL.hashCode() else 0
        result = 31 * result + if (postStatus != null) postStatus.hashCode() else 0
        result = 31 * result + if (postCreationDate != null) postCreationDate.hashCode() else 0
        result = 31 * result + if (commentCount != null) commentCount.hashCode() else 0
        result = 31 * result + if (actionId != null) actionId.hashCode() else 0
        result = 31 * result + if (visibility != null) visibility.hashCode() else 0
        result = 31 * result + if (videoURL != null) videoURL.hashCode() else 0
        result = 31 * result + if (categoryType != null) categoryType.hashCode() else 0
        result = 31 * result + if (isReceiverRequired != null) isReceiverRequired.hashCode() else 0
        result = 31 * result + if (anonymousOnOff != null) anonymousOnOff.hashCode() else 0
        result = 31 * result + if (activityId != null) activityId.hashCode() else 0
        result = 31 * result + if (userTypeId != null) userTypeId.hashCode() else 0
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FeedsList> = object : Parcelable.Creator<FeedsList> {
            override fun createFromParcel(`in`: Parcel): FeedsList {
                return FeedsList(`in`)
            }

            override fun newArray(size: Int): Array<FeedsList?> {
                return arrayOfNulls(size)
            }
        }
    }
}