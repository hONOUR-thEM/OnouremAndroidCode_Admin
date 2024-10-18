package com.onourem.android.activity.models


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubscriptionPackageRes(
    @SerializedName("category")
    @Expose
    var category: String?,
    @SerializedName("code")
    @Expose
    var code: String?,
    @SerializedName("cost")
    @Expose
    var cost: Int,
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
    var imageUrl: String?,
    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int,
    @SerializedName("name")
    @Expose
    var name: String?,
    @SerializedName("currency")
    @Expose
    var currency: String?,

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
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(code)
        parcel.writeInt(cost)
        parcel.writeString(description)
        parcel.writeInt(discountCost)
        parcel.writeInt(durationMonth)
        parcel.writeInt(freeInviteNumber)
        parcel.writeInt(imageHeight)
        parcel.writeString(imageUrl)
        parcel.writeInt(imageWidth)
        parcel.writeString(name)
        parcel.writeString(currency)
        parcel.writeString(selectionStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionPackageRes> {
        override fun createFromParcel(parcel: Parcel): SubscriptionPackageRes {
            return SubscriptionPackageRes(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionPackageRes?> {
            return arrayOfNulls(size)
        }
    }
}