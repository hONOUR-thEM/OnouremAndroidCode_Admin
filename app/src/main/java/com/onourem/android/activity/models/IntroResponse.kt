package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class IntroResponse {
    @SerializedName("linkVerified")
    @Expose
    var linkVerified: Boolean? = null

    @SerializedName("demoImgList")
    @Expose
    var demoImgList: List<String>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("languageList")
    @Expose
    var languageList: List<LanguageList>? = null

    @SerializedName("canAppInstalledDirectly")
    @Expose
    var canAppInstalledDirectly: String? = null

    @SerializedName("productTourVideo1")
    @Expose
    var productTourVideo1: String? = null

    @SerializedName("productTourVideo2")
    @Expose
    var productTourVideo2: String? = null
}