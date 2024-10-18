package com.onourem.android.activity.ui.games.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.ActivityStatusList

class GetUserLoginDayActivityInfoResponse {
    @SerializedName("gameResIdList")
    @Expose
    var gameResIdList: List<String>? = null

    @SerializedName("displayNumberOfActivity")
    @Expose
    var displayNumberOfActivity: Int? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("loginDayActivityInfoList")
    @Expose
    var loginDayActivityInfoList: List<LoginDayActivityInfoList>? = null
}