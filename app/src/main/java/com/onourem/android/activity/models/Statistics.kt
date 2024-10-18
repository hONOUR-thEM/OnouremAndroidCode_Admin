package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Statistics {
    @SerializedName("ageGroupsolderthan60yrs")
    @Expose
    var ageGroupsolderthan60yrs: String? = null

    @SerializedName("ageGroups17to25yrs")
    @Expose
    var ageGroups17to25yrs: String? = null

    @SerializedName("statWithGenderText")
    @Expose
    var statWithGenderText: String? = null

    @SerializedName("statWithAgeText")
    @Expose
    var statWithAgeText: String? = null

    @SerializedName("genderTrans")
    @Expose
    var genderTrans: String? = null

    @SerializedName("ageGroupslessthan17yrs")
    @Expose
    var ageGroupslessthan17yrs: String? = null

    @SerializedName("titleForGenderNeededAlert")
    @Expose
    var titleForGenderNeededAlert: String? = null

    @SerializedName("ageGroups40To60yrs")
    @Expose
    var ageGroups40To60yrs: String? = null

    @SerializedName("genderUnspecified")
    @Expose
    var genderUnspecified: String? = null

    @SerializedName("genderMale")
    @Expose
    var genderMale: String? = null

    @SerializedName("textForGenderNeededAlert")
    @Expose
    var textForGenderNeededAlert: String? = null

    @SerializedName("ageGroups25To40yrs")
    @Expose
    var ageGroups25To40yrs: String? = null

    @SerializedName("screenInfoPopupText")
    @Expose
    var screenInfoPopupText: String? = null

    @SerializedName("genderFemale")
    @Expose
    var genderFemale: String? = null

    @SerializedName("functionOfText")
    @Expose
    var functionOfText: String? = null
}