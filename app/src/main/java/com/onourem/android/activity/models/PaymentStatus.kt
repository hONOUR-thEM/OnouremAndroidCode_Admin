package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable

class PaymentStatus : Parcelable {
    var status: String? = null
    var orderId: String? = null
    var packageId: String? = null
    var html: String? = null
    var discountCode: String? = null

    constructor()
    protected constructor(`in`: Parcel) {
        status = `in`.readString()
        orderId = `in`.readString()
        packageId = `in`.readString()
        html = `in`.readString()
        discountCode = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(status)
        dest.writeString(orderId)
        dest.writeString(packageId)
        dest.writeString(html)
        dest.writeString(discountCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentStatus> {
        override fun createFromParcel(parcel: Parcel): PaymentStatus {
            return PaymentStatus(parcel)
        }

        override fun newArray(size: Int): Array<PaymentStatus?> {
            return arrayOfNulls(size)
        }
    }
}