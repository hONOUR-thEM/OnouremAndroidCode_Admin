package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Expression {
    @SerializedName("infoText")
    @Expose
    var infoText: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null
}