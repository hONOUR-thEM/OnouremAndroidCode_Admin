package com.onourem.android.activity.models


import androidx.annotation.DrawableRes
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserMoodHistory(
    @SerializedName("createdOn")
    @Expose
    val createdOn: String,
    @SerializedName("energy")
    @Expose
    val energy: String,
    @SerializedName("expressionId")
    @Expose
    val expressionId: String,
    @SerializedName("expressionName")
    @Expose
    var expressionName: String,
    @SerializedName("positivity")
    @Expose
    val positivity: String,
    @SerializedName("reason")
    @Expose
    val reason: String,

    var quadrant: String,

    @DrawableRes
    var image: Int
) {
    override fun toString(): String {
        return expressionName
    }
}