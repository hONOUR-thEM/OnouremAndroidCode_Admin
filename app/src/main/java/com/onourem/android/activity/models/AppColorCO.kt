package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AppColorCO {
    @SerializedName("id")
    @Expose
    var id: Any? = null

    @SerializedName("themeName")
    @Expose
    var themeName: String? = null

    @SerializedName("themeColor")
    @Expose
    var themeColor: String? = null

    @SerializedName("textColorOnThemeColor")
    @Expose
    var textColorOnThemeColor: String? = null

    @SerializedName("textColorOnWhiteBackground")
    @Expose
    var textColorOnWhiteBackground: Any? = null

    @SerializedName("subtleTextColor")
    @Expose
    var subtleTextColor: String? = null

    @SerializedName("subtleTextColorOnWhiteBackground")
    @Expose
    var subtleTextColorOnWhiteBackground: String? = null

    @SerializedName("imageColor")
    @Expose
    var imageColor: String? = null

    @SerializedName("traingleColor")
    @Expose
    var traingleColor: String? = null

    @SerializedName("themeColorTop")
    @Expose
    var themeColorTop: String? = null

    @SerializedName("themeColorBottom")
    @Expose
    var themeColorBottom: String? = null

    @SerializedName("quoteTextColor")
    @Expose
    var quoteTextColor: String? = null

    @SerializedName("quoteAuthorColor")
    @Expose
    var quoteAuthorColor: String? = null

    @SerializedName("tileBackgroundColor")
    @Expose
    var tileBackgroundColor: String? = null

    @SerializedName("tileBorderColor")
    @Expose
    var tileBorderColor: String? = null

    @SerializedName("quotationAuthor")
    @Expose
    var quotationAuthor: String? = null

    @SerializedName("quotation")
    @Expose
    var quotation: String? = null

    @SerializedName("userMoodUrl")
    @Expose
    var userMoodUrl: String? = null

    @SerializedName("expressionTitle")
    @Expose
    var expressionTitle: String? = null

    @SerializedName("commonDataMap")
    @Expose
    var commonDataMap: CommonDataMap? = null

    @SerializedName("surveyResponded")
    @Expose
    var surveyResponded: String? = null

    @SerializedName("groupPointsMap")
    @Expose
    var groupPointsMap: LinkedHashMap<String, String>? = null
}