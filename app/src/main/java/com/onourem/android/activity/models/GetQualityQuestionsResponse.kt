package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class GetQualityQuestionsResponse : Serializable {
    @SerializedName("errorCode")
    val errorCode: String? = null

    @SerializedName("errorMessage")
    val errorMessage: String? = null

    @SerializedName("qualityQuestionList")
    val qualityQuestionList: List<QualityQuestion>? = null


}
