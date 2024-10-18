package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WatchList {
    @SerializedName("otherTextForgotinvitedbyyou")
    @Expose
    var otherTextForgotinvitedbyyou: String? = null

    @SerializedName("otherTextForandyouare")
    @Expose
    var otherTextForandyouare: String? = null

    @SerializedName("otherTextForisnotonyourWatchList")
    @Expose
    var otherTextForisnotonyourWatchList: String? = null

    @SerializedName("otherTextForAddRemovefriendsfromWatchList")
    @Expose
    var otherTextForAddRemovefriendsfromWatchList: String? = null

    @SerializedName("maximumWatchingTitle")
    @Expose
    var maximumWatchingTitle: String? = null

    @SerializedName("otherTextForhasinvitedyou")
    @Expose
    var otherTextForhasinvitedyou: String? = null

    @SerializedName("maximumWatchPopUpTextTranslation")
    @Expose
    var maximumWatchPopUpTextTranslation: String? = null
}