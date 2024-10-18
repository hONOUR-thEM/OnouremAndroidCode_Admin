package com.onourem.android.activity.ui.admin.create.surveys


import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class AdminSurveyResponse() : Parcelable {

    @SerializedName("activityCategory")
    @Expose
    var activityCategory: String? = null

    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("activityImageUrl")
    @Expose
    var activityImageUrl: String? = null

    @SerializedName("activityText")
    @Expose
    var activityText: String? = null

    @SerializedName("activityTriggered")
    @Expose
    internal var activityTriggered: Boolean = false

    @SerializedName("activityScheduled")
    @Expose
    internal var activityScheduled: Boolean = false

    @SerializedName("activityType")
    @Expose
    internal var activityType: String? = null

    @SerializedName("activityVideoUrl")
    @Expose
    var activityVideoUrl: String? = null

    @SerializedName("createdTime")
    @Expose
    var createdTime: String? = null

    @SerializedName("scheduledTime")
    @Expose
    var scheduledTime: String? = null

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activityCategory)
        parcel.writeString(activityId)
        parcel.writeString(activityImageUrl)
        parcel.writeString(activityText)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(activityTriggered)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(activityScheduled)
        }
        parcel.writeString(activityType)
        parcel.writeString(activityVideoUrl)
        parcel.writeString(createdTime)
        parcel.writeString(scheduledTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdminSurveyResponse> {
        override fun createFromParcel(parcel: Parcel): AdminSurveyResponse {
            return AdminSurveyResponse(parcel)
        }

        override fun newArray(size: Int): Array<AdminSurveyResponse?> {
            return arrayOfNulls(size)
        }
    }
}
