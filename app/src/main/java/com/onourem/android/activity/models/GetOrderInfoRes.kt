package com.onourem.android.activity.models


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class GetOrderInfoRes(
    @SerializedName("message")
    @Expose
    var message: String?= null,

    @SerializedName("errorCode")
    @Expose
    var errorCode: String?= null,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String?= null,

    @Expose
    var rsaKeyUrl: String?= null,
    @SerializedName("transactionUrl")
    @Expose
    var transactionUrl: String?= null,

    @SerializedName("createdOrderId")
    @Expose
    var createdOrderId: String?= null,

    @SerializedName("redirectUrl")
    @Expose
    var redirectUrl: String?= null,
    var currency: String?= null,
    var amount: String?= null,
    var packageCode: String?= null,
    var discountCode: String?= null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(message)
        dest.writeString(errorCode)
        dest.writeString(errorMessage)
        dest.writeString(rsaKeyUrl)
        dest.writeString(transactionUrl)
        dest.writeString(createdOrderId)
        dest.writeString(redirectUrl)
        dest.writeString(currency)
        dest.writeString(amount)
        dest.writeString(packageCode)
        dest.writeString(discountCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetOrderInfoRes> {
        override fun createFromParcel(parcel: Parcel): GetOrderInfoRes {
            return GetOrderInfoRes(parcel)
        }

        override fun newArray(size: Int): Array<GetOrderInfoRes?> {
            return arrayOfNulls(size)
        }
    }
}