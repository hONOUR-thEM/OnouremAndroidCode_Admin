package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetFriendIdListResponse {
    @SerializedName("friendIdList")
    @Expose
    var friendIdList: List<String>? = null

    @SerializedName("numberOfFriendsRequired")
    @Expose
    var numberOfFriendsRequired: Int? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
}