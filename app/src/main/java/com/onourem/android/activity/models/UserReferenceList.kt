package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserReferenceList {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("refCode")
    @Expose
    var refCode: String? = null

    @SerializedName("refName")
    @Expose
    var refName: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
    override fun toString(): String {
        return refName!!
    }
}