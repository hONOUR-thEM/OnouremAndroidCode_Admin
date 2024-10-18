package com.onourem.android.activity.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PackageCategory {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    override fun toString(): String {
        return category!!
    }
}