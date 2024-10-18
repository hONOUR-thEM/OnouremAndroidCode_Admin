package com.onourem.android.activity.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class NewActivityInfoResponse : Parcelable {

    @SerializedName("filterActivities")
    @Expose
    val filterActivities: String? = null

    @SerializedName("displayNumberOfActivity")
    @Expose
    var displayNumberOfActivity: Long? = null

    @SerializedName("isUserPlayedGame")
    @Expose
    var isUserPlayedGame: Int = 0

    @SerializedName("totalActivities")
    @Expose
    var totalActivities: Int = 0

    @SerializedName("isBond003AvailableForUsers")
    @Expose
    var isBond003AvailableForUsers: Int = 0

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("loginDayActivityInfoList")
    @Expose
    var loginDayActivityInfoList: List<LoginDayActivityInfoList>? = null

    @SerializedName("loginDayActivityMoodInfoList")
    @Expose
    var loginDayActivityMoodInfoList: List<LoginDayActivityInfoList>? = null
}