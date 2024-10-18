package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserReferenceList {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("userReferenceList")
    @Expose
    var userReferenceList: List<UserReferenceList>? = null
}