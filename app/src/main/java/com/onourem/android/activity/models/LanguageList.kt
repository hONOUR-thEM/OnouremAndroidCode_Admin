package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LanguageList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("languageName")
    @Expose
    var languageName: String? = null

    @SerializedName("languageCode")
    @Expose
    var languageCode: String? = null
}