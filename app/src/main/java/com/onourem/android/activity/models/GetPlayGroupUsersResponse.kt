package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetPlayGroupUsersResponse {
    @SerializedName("playGroupUserInfoList")
    @Expose
    var playGroupUserInfoList: List<PlayGroupUserInfoList>? = null

    @SerializedName("playgroupUserIdList")
    @Expose
    var playgroupUserIdList: List<Int>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("allCanAsk")
    @Expose
    var allCanAsk: String? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: String? = null

    @SerializedName("displayNumberOfUsers")
    @Expose
    var displayNumberOfUsers: Long? = null
}