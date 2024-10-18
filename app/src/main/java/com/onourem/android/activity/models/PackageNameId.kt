package com.onourem.android.activity.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PackageNameId {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("packageCode")
    @Expose
    var packageCode: String? = null

    override fun toString(): String {
        return name!!
    }
}