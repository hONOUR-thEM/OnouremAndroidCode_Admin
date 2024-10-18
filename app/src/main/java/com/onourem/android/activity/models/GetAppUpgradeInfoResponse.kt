package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAppUpgradeInfoResponse {
    @SerializedName("onetoManyGamePoint")
    @Expose
    var onetoManyGamePoint: String? = null

    @SerializedName("descriptionTextForWatchList")
    @Expose
    var descriptionTextForWatchList: String? = null

    @SerializedName("videoDuration")
    @Expose
    var videoDuration: String? = null

    @SerializedName("isInviteFriendPending")
    @Expose
    var isInviteFriendPending: String? = null

    @SerializedName("gamePoint")
    @Expose
    var gamePoint: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("forceAndAdviceUpgrade")
    @Expose
    var forceAndAdviceUpgrade: ForceAndAdviceUpgrade? = null

    @SerializedName("descriptionTextForWatchListWhenZeroWatching")
    @Expose
    var descriptionTextForWatchListWhenZeroWatching: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("numberOfInstallPoint")
    @Expose
    var numberOfInstallPoint: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userTypeId: String? = null

    @SerializedName("productTourVideo1")
    @Expose
    var productTourVideo1: String? = null

    @SerializedName("productTourVideo2")
    @Expose
    var productTourVideo2: String? = null
}