package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Message {
    @SerializedName("conversationId")
    @Expose
    var conversationId: String? = null

    @SerializedName("createdDateTime")
    @Expose
    var createdDateTime: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("isLoginUserSame")
    @Expose
    var isLoginUserSame: String? = null

    @SerializedName("messageBy")
    @Expose
    var messageBy: String? = null

    @SerializedName("messageText")
    @Expose
    var messageText: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
    var blockedUser: String? = null
}