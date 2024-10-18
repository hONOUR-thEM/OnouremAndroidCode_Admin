package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ExpressionDataResponse : Serializable {
    @SerializedName("suggestAndInviteDescription")
    @Expose
    var suggestAndInviteDescription: List<String>? = null

    @SerializedName("appColorCO")
    @Expose
    var appColorCO: AppColorCO? = null

    @SerializedName("activityTag")
    @Expose
    var activityTag: ActivityTag? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null
    var userExpressionList: List<UserExpressionList>? = null

    @SerializedName("surveyCOList")
    @Expose
    var surveyCOList: List<SurveyCOList>? = null

    @SerializedName("forceAndAdviceUpgrade")
    @Expose
    var forceAndAdviceUpgrade: ForceAndAdviceUpgrade? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("userSettledGameCount")
    @Expose
    var userSettledGameCount: String? = null

    @SerializedName("localNotificataionTime")
    @Expose
    var localNotificataionTime: Long? = null

    @SerializedName("appMoudleList")
    @Expose
    var appMoudleList: List<AppMoudleList>? = null
}