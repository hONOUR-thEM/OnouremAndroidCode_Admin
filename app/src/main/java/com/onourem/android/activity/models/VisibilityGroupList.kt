package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VisibilityGroupList {
    @SerializedName("groupId")
    @Expose
    var groupId = 0

    @SerializedName("groupName")
    @Expose
    var groupName: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    override fun toString(): String {
        return groupId.toString()
    }
}