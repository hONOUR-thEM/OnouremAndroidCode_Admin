package com.onourem.android.activity.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class MoodInfoData : Parcelable {

    @SerializedName("expressionId")
    val expressionId: String? = null

    @SerializedName("id")
    val id: String? = null

    @SerializedName("imageUrl")
    val imageUrl: String? = null

    @SerializedName("sourceName")
    val sourceName: String? = null

    @SerializedName("status")
    val status: String? = null

    @SerializedName("summary")
    val summary: String? = null

    @SerializedName("videoId")
    val videoId: String? = null

    @SerializedName("videoUrl")
    val videoUrl: String? = null

    @SerializedName("youtubeLink")
    val youtubeLink: String? = null

    @SerializedName("createdDate")
    val createdDate: String? = null

}
