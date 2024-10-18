package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PrivacyGroup : Parcelable {
    @SerializedName("groupId")
    @Expose
    var groupId = 0

    @SerializedName("groupName")
    @Expose
    var groupName: String? = null

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null
    var isNew = false

    constructor()
    protected constructor(`in`: Parcel) {
        groupId = `in`.readInt()
        groupName = `in`.readString()
        activityGameResponseId = `in`.readString()
        isNew = `in`.readInt() == 1
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(groupId)
        dest.writeString(groupName)
        dest.writeString(activityGameResponseId)
        dest.writeInt(if (isNew) 1 else 0)
    }

    override fun toString(): String {
        return groupId.toString()
    }

    companion object CREATOR : Parcelable.Creator<PrivacyGroup> {
        override fun createFromParcel(parcel: Parcel): PrivacyGroup {
            return PrivacyGroup(parcel)
        }

        override fun newArray(size: Int): Array<PrivacyGroup?> {
            return arrayOfNulls(size)
        }
    }
}