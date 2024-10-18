package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OClubSettingsItem(
    @SerializedName("playGroupName")
    @Expose
    val playGroupName: String,
    @SerializedName("playGroupId")
    @Expose
    val playGroupId: String,
    @SerializedName("commentsEnabled")
    @Expose
    val commentsEnabled: String,
    @SerializedName("inviteLinkEnabled")
    @Expose
    val inviteLinkEnabled: String,
)