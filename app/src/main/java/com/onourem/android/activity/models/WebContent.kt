package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable

class WebContent : Parcelable {
    var tnc: String? = null
    var property: String? = null
    var screenId: String? = null
    var title: String? = null

    constructor()
    protected constructor(`in`: Parcel) {
        tnc = `in`.readString()
        property = `in`.readString()
        screenId = `in`.readString()
        title = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(tnc)
        dest.writeString(property)
        dest.writeString(screenId)
        dest.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WebContent> {
        override fun createFromParcel(parcel: Parcel): WebContent {
            return WebContent(parcel)
        }

        override fun newArray(size: Int): Array<WebContent?> {
            return arrayOfNulls(size)
        }
    }
}