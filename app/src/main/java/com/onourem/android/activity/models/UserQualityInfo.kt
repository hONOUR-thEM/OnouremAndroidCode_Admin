package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserQualityInfo(
    @SerializedName("backgroundColor")
    var backgroundColor: String,
    @SerializedName("friendCount")
    val friendCount: Int,
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
    var waveColor: String
) : Serializable