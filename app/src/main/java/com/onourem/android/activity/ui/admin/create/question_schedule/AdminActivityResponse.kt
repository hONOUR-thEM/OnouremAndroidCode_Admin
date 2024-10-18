package com.onourem.android.activity.ui.admin.create.question_schedule

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdminActivityResponse : Parcelable {
    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("activityType")
    @Expose
    var activityType: String? = null

    @SerializedName("activityText")
    @Expose
    var activityText: String? = null

    @SerializedName("activityImageUrl")
    @Expose
    var activityImageUrl: String? = null

    @SerializedName("activityVideoUrl")
    @Expose
    var activityVideoUrl: String? = null

    @SerializedName("activityTriggered")
    @Expose
    var activityTriggered = false

    @SerializedName("activityScheduled")
    @Expose
    var activityScheduled = false

    @SerializedName("createdTime")
    @Expose
    var createdTime: String? = null

    @SerializedName("scheduledTime")
    @Expose
    var scheduledTime: String? = null

    @SerializedName("youtubeLink")
    @Expose
    var youtubeLink: String? = null

    @SerializedName("activityCategory")
    @Expose
    var activityCategory: String? = null
    var isEditEnabled = false

    constructor() {}
    protected constructor(`in`: Parcel) {
        activityId = `in`.readString()
        activityType = `in`.readString()
        activityText = `in`.readString()
        activityImageUrl = `in`.readString()
        activityVideoUrl = `in`.readString()
        activityTriggered = `in`.readByte().toInt() != 0
        createdTime = `in`.readString()
        activityCategory = `in`.readString()
        scheduledTime = `in`.readString()
        youtubeLink = `in`.readString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isEditEnabled = `in`.readBoolean()
        }
    }

    fun setQuestionFor(questionFor: String?) {
        activityCategory = questionFor
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(activityId)
        parcel.writeString(activityType)
        parcel.writeString(activityText)
        parcel.writeString(activityImageUrl)
        parcel.writeString(activityVideoUrl)
        parcel.writeByte((if (activityTriggered) 1 else 0).toByte())
        parcel.writeByte((if (activityScheduled) 1 else 0).toByte())
        parcel.writeString(createdTime)
        parcel.writeString(activityCategory)
        parcel.writeString(youtubeLink)
        parcel.writeString(scheduledTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(isEditEnabled)
        }
    }

    companion object CREATOR : Parcelable.Creator<AdminActivityResponse> {
        override fun createFromParcel(parcel: Parcel): AdminActivityResponse {
            return AdminActivityResponse(parcel)
        }

        override fun newArray(size: Int): Array<AdminActivityResponse?> {
            return arrayOfNulls(size)
        }
    }
}