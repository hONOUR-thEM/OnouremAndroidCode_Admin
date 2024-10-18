package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Module {
    @SerializedName("myMoodInfoText")
    @Expose
    var myMoodInfoText: String? = null

    @SerializedName("nourishRelationInfoText")
    @Expose
    var nourishRelationInfoText: String? = null

    @SerializedName("inviteInfoText")
    @Expose
    var inviteInfoText: String? = null

    @SerializedName("feedInfoText")
    @Expose
    var feedInfoText: String? = null

    @SerializedName("socialMindsetInfoText")
    @Expose
    var socialMindsetInfoText: String? = null

    @SerializedName("otherTextForinvitedyoutobeonWatchList")
    @Expose
    var otherTextForinvitedyoutobeonWatchList: String? = null

    @SerializedName("honourThemInfoText")
    @Expose
    var honourThemInfoText: String? = null

    @SerializedName("otherTextForyouInvited")
    @Expose
    var otherTextForyouInvited: String? = null

    @SerializedName("watchListInfoText")
    @Expose
    var watchListInfoText: String? = null
}