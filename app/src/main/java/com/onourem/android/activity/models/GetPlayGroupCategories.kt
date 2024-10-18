package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetPlayGroupCategories {
    @SerializedName("appLanguageList")
    @Expose
    var appLanguageList: List<AppLanguageList>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("playGroupCategoryList")
    @Expose
    var playGroupCategoryList: List<PlayGroupCategoryList>? = null
}