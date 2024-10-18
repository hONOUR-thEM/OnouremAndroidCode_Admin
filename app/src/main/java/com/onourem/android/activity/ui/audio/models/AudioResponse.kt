package com.onourem.android.activity.ui.audio.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AudioResponse(
    @SerializedName("title")
    @Expose
    var audioTitle: String? = null,

    @SerializedName("audioId")
    @Expose
    var audioId: String? = null,

    @SerializedName("creatorId")
    @Expose
    var creatorId: String? = null,

    @SerializedName("createdDate")
    @Expose
    var createdDate: String? = null,

    @SerializedName("numberOfLike")
    @Expose
    var numberOfLike: String? = null,

    @SerializedName("numberOfViews")
    @Expose
    var numberOfViews: String? = null,

    @SerializedName("audioDuration")
    @Expose
    var audioDuration: String? = null,

    @SerializedName("audioUrl")
    @Expose
    var audioUrl: String? = null,

    @SerializedName("privacyId")
    @Expose
    var privacyId: String? = null,

    @SerializedName("profilePictureUrl")
    @Expose
    var profilePictureUrl: String? = null,

    @SerializedName("categoryName")
    @Expose
    var categoryName: String? = null,

    @SerializedName("isAudioLiked")
    @Expose
    var isAudioLiked: String? = null,

    @SerializedName("userName")
    @Expose
    var userName: String? = null,

    @SerializedName("audioStatus")
    @Expose
    var audioStatus: String? = null,

    @SerializedName("userFollowingCreator")
    @Expose
    var userFollowingCreator: String? = null,

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null,

    @SerializedName("audioCommentCount")
    @Expose
    var commentCount: String? = null,

    @SerializedName("audioRating")
    @Expose
    var audioRating: String? = null,
)