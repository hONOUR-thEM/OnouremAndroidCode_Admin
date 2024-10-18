package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetGeoList {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("countryList")
    @Expose
    var countryList: List<CountryList>? = null

    @SerializedName("stateList")
    @Expose
    var stateList: List<StateList>? = null

    @SerializedName("cityList")
    @Expose
    var cityList: List<CityList>? = null

    @SerializedName("campaignAskWhyCityDescription")
    @Expose
    var campaignAskWhyCityDescription: String? = null
}