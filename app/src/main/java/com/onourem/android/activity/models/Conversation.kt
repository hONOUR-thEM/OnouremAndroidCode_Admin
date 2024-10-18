package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Conversation() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("userOneId")
    @Expose
    var userOne: String? = null

    @SerializedName("userTwoId")
    @Expose
    var userTwo: String? = null

    @SerializedName("initiatedBy")
    @Expose
    var initiatedBy: String? = null

    @SerializedName("createdDateTime")
    @Expose
    var createdDateTime: String? = null

    @SerializedName("lastUpdatedDateTime")
    @Expose
    var updatedDateTime: String? = null

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: String? = null

    @SerializedName("userName")
    @Expose
    var userName: String? = null

    @SerializedName("status")
    @Expose
    var readStatus: String? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userTypeId: String? = null

    @SerializedName("lastMessage")
    @Expose
    var lastMessage: String? = null

    var userMessageFromWatchlist: String? = null

    var contentId: String? = null

    var contentType: String? = null

    var query: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        userOne = parcel.readString()
        userTwo = parcel.readString()
        initiatedBy = parcel.readString()
        createdDateTime = parcel.readString()
        updatedDateTime = parcel.readString()
        updatedBy = parcel.readString()
        userName = parcel.readString()
        readStatus = parcel.readString()
        profilePicture = parcel.readString()
        userTypeId = parcel.readString()
        contentId = parcel.readString()
        contentType = parcel.readString()
        query = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userOne)
        parcel.writeString(userTwo)
        parcel.writeString(initiatedBy)
        parcel.writeString(createdDateTime)
        parcel.writeString(updatedDateTime)
        parcel.writeString(updatedBy)
        parcel.writeString(userName)
        parcel.writeString(readStatus)
        parcel.writeString(profilePicture)
        parcel.writeString(userTypeId)
        parcel.writeString(contentId)
        parcel.writeString(contentType)
        parcel.writeString(query)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Conversation> {
        override fun createFromParcel(parcel: Parcel): Conversation {
            return Conversation(parcel)
        }

        override fun newArray(size: Int): Array<Conversation?> {
            return arrayOfNulls(size)
        }
    }
}