package com.onourem.android.activity.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class InstituteNameId {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    override fun toString(): String {
        return extractSubstring(name!!)
    }

    private fun extractSubstring(inputString: String): String {
        val startIndex = inputString.indexOf("=") + 1
        return inputString.substring(startIndex)
    }
}