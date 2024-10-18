package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetMessagesResponse(
    @SerializedName("displayNumberOfMessages")
    @Expose
    val displayNumberOfMessages: Int,

    @SerializedName("errorCode")
    @Expose
    val errorCode: String,

    @SerializedName("conversationId")
    @Expose
    val conversationId: String,

    @SerializedName("conversationStatus")
    @Expose
    val conversationStatus: String,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String,

    @SerializedName("messageIdList")
    @Expose
    val messageIdList: List<Int>,

    @SerializedName("messagesList")
    @Expose
    val messageList: List<Message>

)