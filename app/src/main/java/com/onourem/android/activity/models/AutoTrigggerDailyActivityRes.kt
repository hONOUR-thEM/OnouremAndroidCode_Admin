package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
class AutoTrigggerDailyActivityRes(
    @SerializedName("activityId")
    @Expose
    var activityId: String,
    @SerializedName("activityText")
    @Expose
    var activityText: String,
    @SerializedName("activityType")
    @Expose
    var activityType: String,
    @SerializedName("dayNumber")
    @Expose
    var dayNumber: String,
    @SerializedName("dayPriority")
    @Expose
    var dayPriority: String,
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("oclubCategoryId")
    @Expose
    var oclubCategoryId: String,
    @SerializedName("categoryName")
    @Expose
    var categoryName: String,
    @SerializedName("status")
    @Expose
    var status: String
) : Parcelable