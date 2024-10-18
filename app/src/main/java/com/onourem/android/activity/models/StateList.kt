package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StateList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("countryId")
    @Expose
    var countryId: Int? = null

    @SerializedName("stateName")
    @Expose
    var stateName: String? = null
    override fun toString(): String {
        return stateName!!
    }
}