package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable

class LinkData : Parcelable {
    var linkUserId: String? = ""
    var linkType: String? = ""
    var isVerified: String? = "false"
    var pendingDynamicLinkData: String? = ""
    var notificationCallback: String? = ""
    var canInstallDirectly: String? = "N"

    constructor()
    protected constructor(`in`: Parcel) {
        linkUserId = `in`.readString()
        linkType = `in`.readString()
        isVerified = `in`.readString()
        pendingDynamicLinkData = `in`.readString()
        notificationCallback = `in`.readString()
        canInstallDirectly = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(linkUserId)
        dest.writeString(linkType)
        dest.writeString(isVerified)
        dest.writeString(pendingDynamicLinkData)
        dest.writeString(notificationCallback)
        dest.writeString(canInstallDirectly)
    }

    companion object CREATOR : Parcelable.Creator<LinkData> {
        override fun createFromParcel(parcel: Parcel): LinkData {
            return LinkData(parcel)
        }

        override fun newArray(size: Int): Array<LinkData?> {
            return arrayOfNulls(size)
        }
    }
}