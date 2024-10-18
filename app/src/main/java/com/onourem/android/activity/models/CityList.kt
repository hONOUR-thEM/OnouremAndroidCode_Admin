package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CityList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("stateId")
    @Expose
    var stateId: Int? = null

    @SerializedName("cityName")
    @Expose
    var cityName: String? = null
    override fun toString(): String {
        return cityName!!
    }
}