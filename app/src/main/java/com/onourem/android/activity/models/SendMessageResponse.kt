package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SendMessageResponse(

    @SerializedName("errorCode")
    @Expose
    var errorCode: String,

    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,

    @SerializedName("userMessageObj")
    @Expose
    var message: Message

)