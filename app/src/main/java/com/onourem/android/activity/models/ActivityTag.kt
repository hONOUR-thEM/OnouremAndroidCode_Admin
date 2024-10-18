package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivityTag {
    @SerializedName("NewSuggestion")
    @Expose
    var newSuggestion: String? = null

    @SerializedName("FriendAnswered")
    @Expose
    var friendAnswered: String? = null

    @SerializedName("Tryanonymousmatchup")
    @Expose
    var tryanonymousmatchup: String? = null

    @SerializedName("FriendAsked")
    @Expose
    var friendAsked: String? = null

    @SerializedName("NewAnswerHidden")
    @Expose
    var newAnswerHidden: String? = null

    @SerializedName("NewAnswer")
    @Expose
    var newAnswer: String? = null

    @SerializedName("Pending")
    @Expose
    var pending: String? = null

    @SerializedName("Settled")
    @Expose
    var settled: String? = null

    @SerializedName("Waiting")
    @Expose
    var waiting: String? = null
}