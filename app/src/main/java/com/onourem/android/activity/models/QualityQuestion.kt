package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import java.io.Serializable

data class QualityQuestion(
    @SerializedName("backgroundColor")
    val backgroundColor: String,
    @SerializedName("imageHeight")
    val imageHeight: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("imageWidth")
    val imageWidth: Int,
    @SerializedName("qualityName")
    val qualityName: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("waveColor")
    val waveColor: String,

    var contactList: ArrayList<QuestionForContacts> = ArrayList<QuestionForContacts>()

) : Serializable