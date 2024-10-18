package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
class GetOclubAutoTriggerDailyActivityByAdminResponse(
    @SerializedName("autoTrigggerDailyActivityResList")
    @Expose
    var autoTrigggerDailyActivityResList: List<AutoTrigggerDailyActivityRes>? = null,
    @SerializedName("errorCode")
    @Expose
    var errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String
) : Parcelable