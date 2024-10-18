package com.onourem.android.activity.models

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserWatchList : Parcelable {
    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("profilePictureUrl")
    @Expose
    var profilePictureUrl: String? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("expressionId")
    @Expose
    var expressionId: String? = null

    @SerializedName("expressionName")
    @Expose
    var expressionName: String? = null

    @SerializedName("expressionImageUrl")
    @Expose
    var expressionImageUrl: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userTypeId: String? = null
    var centerText: String? = null
    var subText: String? = null
    var drawable: Drawable? = null
    var viewType = 0

    constructor()
    protected constructor(`in`: Parcel) {
        userId = `in`.readString()
        firstName = `in`.readString()
        lastName = `in`.readString()
        profilePictureUrl = `in`.readString()
        createdOn = `in`.readString()
        expressionId = `in`.readString()
        expressionName = `in`.readString()
        expressionImageUrl = `in`.readString()
        status = `in`.readString()
        centerText = `in`.readString()
        userTypeId = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(userId)
        dest.writeString(firstName)
        dest.writeString(lastName)
        dest.writeString(profilePictureUrl)
        dest.writeString(createdOn)
        dest.writeString(expressionId)
        dest.writeString(expressionName)
        dest.writeString(expressionImageUrl)
        dest.writeString(status)
        dest.writeString(centerText)
        dest.writeString(userTypeId)
    }

    companion object CREATOR : Parcelable.Creator<UserWatchList> {
        override fun createFromParcel(parcel: Parcel): UserWatchList {
            return UserWatchList(parcel)
        }

        override fun newArray(size: Int): Array<UserWatchList?> {
            return arrayOfNulls(size)
        }
    }
}