package com.onourem.android.activity.ui.admin.create.external_posts


import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class ExternalActivityData : Parcelable {

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

    @SerializedName("scheduledTime")
    val scheduledTime: String? = null

    @SerializedName("activityType")
    @Expose
    internal val activityType: String? = null

    @SerializedName("activityTriggered")
    @Expose
    internal val activityTriggered = false

    @SerializedName("activityScheduled")
    @Expose
    internal val activityScheduled = false

}
