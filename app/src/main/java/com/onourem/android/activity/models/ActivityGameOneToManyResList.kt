package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivityGameOneToManyResList : Parcelable {
    @SerializedName("gameId")
    @Expose
    var gameId: String? = null

    @SerializedName("participantId")
    @Expose
    var participantId: String? = null

    @SerializedName("participantImageUrl")
    @Expose
    var participantImageUrl: String? = null

    @SerializedName("participantName")
    @Expose
    var participantName: String? = null

    @SerializedName("participantResponseStatus")
    @Expose
    var participantResponseStatus: String? = null

    @SerializedName("participantResponse")
    @Expose
    var participantResponse: String? = null

    @SerializedName("participantResponseImageLargeLink")
    @Expose
    var participantResponseImageLargeLink: String? = null

    @SerializedName("participantResponseVideoLink")
    @Expose
    var participantResponseVideoLink: String? = null

    @SerializedName("isGameExpired")
    @Expose
    var isGameExpired: String? = null

    @SerializedName("commentCount")
    @Expose
    var commentCount: String? = null

    @SerializedName("showEditOrDelete")
    @Expose
    var showEditOrDelete: String? = null

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null

    @SerializedName("playGroupId")
    @Expose
    var playGroupId: String? = null

    @SerializedName("isNewResponse")
    @Expose
    var isNewResponse: String? = null

    @SerializedName("isNewComment")
    @Expose
    var isNewComment: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null

    @SerializedName("userAnsweredCount")
    @Expose
    var userAnsweredCount: String? = null

    @SerializedName("lastSeenTimeStamp")
    @Expose
    var lastSeenTimeStamp: String? = null

    var commentsEnabled: String? = null

    constructor()
    protected constructor(`in`: Parcel) {
        gameId = `in`.readString()
        participantId = `in`.readString()
        participantImageUrl = `in`.readString()
        participantName = `in`.readString()
        participantResponseStatus = `in`.readString()
        participantResponse = `in`.readString()
        participantResponseImageLargeLink = `in`.readString()
        participantResponseVideoLink = `in`.readString()
        isGameExpired = `in`.readString()
        commentCount = `in`.readString()
        showEditOrDelete = `in`.readString()
        activityGameResponseId = `in`.readString()
        playGroupId = `in`.readString()
        isNewResponse = `in`.readString()
        isNewComment = `in`.readString()
        userType = `in`.readString()
        userAnsweredCount = `in`.readString()
        lastSeenTimeStamp = `in`.readString()
        commentsEnabled = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(gameId)
        dest.writeString(participantId)
        dest.writeString(participantImageUrl)
        dest.writeString(participantName)
        dest.writeString(participantResponseStatus)
        dest.writeString(participantResponse)
        dest.writeString(participantResponseImageLargeLink)
        dest.writeString(participantResponseVideoLink)
        dest.writeString(isGameExpired)
        dest.writeString(commentCount)
        dest.writeString(showEditOrDelete)
        dest.writeString(activityGameResponseId)
        dest.writeString(playGroupId)
        dest.writeString(isNewResponse)
        dest.writeString(isNewComment)
        dest.writeString(userType)
        dest.writeString(userAnsweredCount)
        dest.writeString(lastSeenTimeStamp)
        dest.writeString(commentsEnabled)
    }

    override fun describeContents(): Int {
        return 0
    }

//    companion object {
//        val CREATOR: Parcelable.Creator<ActivityGameOneToManyResList> =
//            object : Parcelable.Creator<ActivityGameOneToManyResList> {
//                override fun createFromParcel(`in`: Parcel): ActivityGameOneToManyResList {
//                    return ActivityGameOneToManyResList(`in`)
//                }
//
//                override fun newArray(size: Int): Array<ActivityGameOneToManyResList?> {
//                    return arrayOfNulls(size)
//                }
//            }
//    }

    companion object CREATOR : Parcelable.Creator<ActivityGameOneToManyResList> {
        override fun createFromParcel(parcel: Parcel): ActivityGameOneToManyResList {
            return ActivityGameOneToManyResList(parcel)
        }

        override fun newArray(size: Int): Array<ActivityGameOneToManyResList?> {
            return arrayOfNulls(size)
        }
    }
}