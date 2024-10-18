package com.onourem.android.activity.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class OClubCategory {
    var id: String? = null

    var category: String? = null

    var status: String? = null

    override fun toString(): String {
        return category!!
    }
}