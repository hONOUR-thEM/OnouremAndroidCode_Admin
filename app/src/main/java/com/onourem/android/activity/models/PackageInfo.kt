package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PackageInfo(

    @SerializedName("category")
    @Expose
    var category: String?,

    @SerializedName("code")
    @Expose
    var packageCode: String?,

    @SerializedName("cost")
    @Expose
    var realCost: Int,

    @SerializedName("description")
    @Expose
    var description: String?,

    @SerializedName("discountCost")
    @Expose
    var discountCost: Int,

    @SerializedName("durationMonth")
    @Expose
    var durationMonth: Int,

    @SerializedName("freeInviteNumber")
    @Expose
    var freeInviteNumber: Int,

    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int,

    @SerializedName("imageUrl")
    @Expose
    var packageImageUrl: Int,

    @SerializedName("imageWidth")
    @Expose
    var imageWidth: String?,

    @SerializedName("name")
    @Expose
    var name: String?,

    @SerializedName("totalSubscriptions")
    @Expose
    var totalSubscriptions: String?,

    @SerializedName("unusedInviteNumber")
    @Expose
    var unusedInviteNumber: String?,

    @SerializedName("daysRemainingInSubscription")
    @Expose
    var daysRemainingInSubscription: String?,

    var currentActiveSubscription: String?,

    var selectionStatus: String? = null,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(packageCode)
        parcel.writeInt(realCost)
        parcel.writeString(description)
        parcel.writeInt(discountCost)
        parcel.writeInt(durationMonth)
        parcel.writeInt(freeInviteNumber)
        parcel.writeInt(imageHeight)
        parcel.writeInt(packageImageUrl)
        parcel.writeString(imageWidth)
        parcel.writeString(name)
        parcel.writeString(totalSubscriptions)
        parcel.writeString(unusedInviteNumber)
        parcel.writeString(daysRemainingInSubscription)
        parcel.writeString(currentActiveSubscription)
        parcel.writeString(selectionStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PackageInfo> {
        override fun createFromParcel(parcel: Parcel): PackageInfo {
            return PackageInfo(parcel)
        }

        override fun newArray(size: Int): Array<PackageInfo?> {
            return arrayOfNulls(size)
        }
    }
}