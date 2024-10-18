package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AppMoudleList {
    @SerializedName("moduleId")
    @Expose
    var moduleId: String? = null

    @SerializedName("moduleName")
    @Expose
    var moduleName: String? = null

    @SerializedName("imageName")
    @Expose
    var imageName: String? = null

    @SerializedName("tileBackgroundColor")
    @Expose
    var tileBackgroundColor: Any? = null

    @SerializedName("tileBorderColor")
    @Expose
    var tileBorderColor: Any? = null
}