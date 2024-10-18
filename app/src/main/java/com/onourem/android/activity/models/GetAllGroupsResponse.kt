package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllGroupsResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("groupNames")
    @Expose
    var privacyGroups: List<PrivacyGroup>? = null

    @SerializedName("groupMember")
    @Expose
    var groupMember: List<GroupMember>? = null

    @SerializedName("groupId")
    @Expose
    var groupId: String? = null
}