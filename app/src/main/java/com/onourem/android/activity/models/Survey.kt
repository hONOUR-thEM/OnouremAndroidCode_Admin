package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Survey {
    @SerializedName("textForNoSelectionAlert")
    @Expose
    var textForNoSelectionAlert: String? = null

    @SerializedName("titleForNoSelectionAlert")
    @Expose
    var titleForNoSelectionAlert: String? = null

    @SerializedName("titleForExpiredSurveyAlert")
    @Expose
    var titleForExpiredSurveyAlert: String? = null

    @SerializedName("respondedText")
    @Expose
    var respondedText: String? = null

    @SerializedName("screenInfoPopupText")
    @Expose
    var screenInfoPopupText: String? = null

    @SerializedName("textForExpiredSurveyAlert")
    @Expose
    var textForExpiredSurveyAlert: String? = null

    @SerializedName("expiredText")
    @Expose
    var expiredText: String? = null

    @SerializedName("anonymousLabelText")
    @Expose
    var anonymousLabelText: String? = null
}