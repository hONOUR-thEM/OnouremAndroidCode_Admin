package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GroupPointsMap {
    @SerializedName("oo")
    @Expose
    var oo: String? = null

    @SerializedName("1")
    @Expose
    internal var _1: String? = null

    @SerializedName("2")
    @Expose
    internal var _2: String? = null

    @SerializedName("4")
    @Expose
    internal var _4: String? = null
    fun get1(): String? {
        return _1
    }

    fun set1(_1: String?) {
        this._1 = _1
    }

    fun get2(): String? {
        return _2
    }

    fun set2(_2: String?) {
        this._2 = _2
    }

    fun get4(): String? {
        return _4
    }

    fun set4(_4: String?) {
        this._4 = _4
    }
}