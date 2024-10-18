package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonDataMap {
    @SerializedName("SignUp")
    @Expose
    var signUp: SignUp? = null

    @SerializedName("Survey")
    @Expose
    var survey: Survey? = null

    @SerializedName("Relation")
    @Expose
    var relation: Relation? = null

    @SerializedName("Expression")
    @Expose
    var expression: Expression? = null

    @SerializedName("WatchList")
    @Expose
    var watchList: WatchList? = null

    @SerializedName("Activity")
    @Expose
    internal var activity: Activity? = null

    @SerializedName("Module")
    @Expose
    var module: Module? = null

    @SerializedName("Statistics")
    @Expose
    var statistics: Statistics? = null
    fun requireActivity(): Activity? {
        return activity
    }

    fun setActivity(activity: Activity?) {
        this.activity = activity
    }
}