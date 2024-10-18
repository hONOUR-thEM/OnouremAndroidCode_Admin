package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetPostCategoryListNewResponse {
    @SerializedName("videoDuration")
    @Expose
    var videoDuration: String? = null

    @SerializedName("categoryList")
    @Expose
    var categoryList: List<CategoryList>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null
}