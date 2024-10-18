package com.onourem.android.activity.models


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserCurrentSubscriptionInfo(
    @SerializedName("daysRemainingInSubscription")
    @Expose
    var daysRemainingInSubscription: Int,
    @SerializedName("freeInviteNumber")
    @Expose
    var freeInviteNumber: Int,
    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int,
    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int,
    @SerializedName("packageCode")
    @Expose
    var packageCode: String?,
    @SerializedName("packageImageUrl")
    @Expose
    var packageImageUrl: String?,
    @SerializedName("startDate")
    @Expose
    var startDate: String?,
    @SerializedName("unusedInviteNumber")
    @Expose
    var unusedInviteNumber: Int,
    @SerializedName("validTill")
    @Expose
    var validTill: String?,
    @SerializedName("freeInvitePackageId")
    @Expose
    var freeInvitePackageId: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(daysRemainingInSubscription)
        parcel.writeInt(freeInviteNumber)
        parcel.writeInt(imageHeight)
        parcel.writeInt(imageWidth)
        parcel.writeString(packageCode)
        parcel.writeString(packageImageUrl)
        parcel.writeString(startDate)
        parcel.writeInt(unusedInviteNumber)
        parcel.writeString(validTill)
        parcel.writeString(freeInvitePackageId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserCurrentSubscriptionInfo> {
        override fun createFromParcel(parcel: Parcel): UserCurrentSubscriptionInfo {
            return UserCurrentSubscriptionInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserCurrentSubscriptionInfo?> {
            return arrayOfNulls(size)
        }
    }

}