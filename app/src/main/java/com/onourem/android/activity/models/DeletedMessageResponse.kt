package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DeletedMessageResponse(

    @SerializedName("errorCode")
    @Expose
    var errorCode: String,

    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,

    @SerializedName("deletedMessage")
    @Expose
    var deletedMessage: String

)