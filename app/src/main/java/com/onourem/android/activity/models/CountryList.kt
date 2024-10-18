package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("countryCode")
    @Expose
    private val countryCode: String? = null

    @SerializedName("cName")
    @Expose
    val cName: String? = null

    @SerializedName("showInFilter")
    @Expose
    private val showInFilter: String? = null
    override fun toString(): String {
        return cName!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CountryList
        return cName == that.cName
    }

    override fun hashCode(): Int {
        return cName?.hashCode() ?: 0
    }
}