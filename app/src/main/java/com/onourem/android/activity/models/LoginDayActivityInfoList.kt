package com.onourem.android.activity.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.onourem.android.activity.ui.audio.playback.Song
import kotlinx.parcelize.Parcelize

@Parcelize
open class LoginDayActivityInfoList : Cloneable, Parcelable {

    @SerializedName("activityGameResponseId")
    @Expose
    var activityGameResponseId: String? = null

    @SerializedName("activityHintOverrideText")
    @Expose
    var activityHintOverrideText: String? = null

    @SerializedName("activityId")
    @Expose
    var activityId: String? = null

    @SerializedName("activityImageLargeUrl")
    @Expose
    var activityImageLargeUrl: String? = null

    @SerializedName("activityImageUrl")
    @Expose
    var activityImageUrl: String? = null

    @SerializedName("activityReason")
    @Expose
    var activityReason: String? = null

    @SerializedName("activityTag")
    @Expose
    var activityTag: String? = null

    @SerializedName("activityText")
    @Expose
    var activityText: String? = null

    @SerializedName("activityType")
    @Expose
    var activityType: String? = null

    @SerializedName("activityTypeHint")
    @Expose
    var activityTypeHint: String? = null

    @SerializedName("activityTypeId")
    @Expose
    var activityTypeId: String? = null

    @SerializedName("activityTypeRule")
    @Expose
    var activityTypeRule: String? = null

    @SerializedName("anonymousSuggestion")
    @Expose
    var anonymousSuggestion: String? = null

    @SerializedName("askedById")
    @Expose
    var askedById: String? = null

    @SerializedName("askedByName")
    @Expose
    var askedByName: String? = null

    @SerializedName("commentEnabledForPlayGroup")
    @Expose
    internal var commentsEnabled: String? = null

    @SerializedName("defaultActivityTypeDesc")
    @Expose
    var defaultActivityTypeDesc: String? = null

    @SerializedName("feedsInfo")
    @Expose
    var feedsInfo: FeedsList? = null

    @SerializedName("friendCount")
    @Expose
    var friendCount: Long = 0

    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int? = null

    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int? = null

    @SerializedName("isImagePresent")
    @Expose
    var isImagePresent: String? = null

    @SerializedName("loginDay")
    @Expose
    var loginDay: String? = null

    @SerializedName("oclubActivityId")
    @Expose
    var oClubActivityId: String? = null

    @SerializedName("playgroupId")
    @Expose
    var playgroupId: String? = null

    @SerializedName("postCategory")
    @Expose
    var postCategory: String? = null

    @SerializedName("postCategoryId")
    @Expose
    var postCategoryId: String? = null

    @SerializedName("privacyId")
    @Expose
    var privacyId: String? = null

    @SerializedName("privacyName")
    @Expose
    var privacyName: String? = null

    @SerializedName("privacyScreenNeeded")
    @Expose
    var privacyScreenNeeded: String? = null

    @SerializedName("questionVideoUrl")
    @Expose
    var questionVideoUrl: String? = null

    @SerializedName("receiverRequired")
    @Expose
    var receiverRequired: String? = null

    @SerializedName("source")
    @Expose
    var source: String? = null

    @SerializedName("userLimit")
    @Expose
    var userLimit: String? = null

    @SerializedName("userParticipationStatus")
    @Expose
    var userParticipationStatus: String? = null

    @SerializedName("youTubeLink")
    @Expose
    var youTubeLink: String? = null

    @SerializedName("youTubeVideoId")
    @Expose
    var youTubeVideoId: String? = null

    @SerializedName("questionEditable")
    @Expose
    var isQuestionEditable = false

    @SerializedName("gameId")
    @Expose
    var gameId: String? = ""

    var surveySeeStats: String? = null

    var surveyCO: SurveyCOList? = null

    var songsFromServer: ArrayList<Song>? = null

    var watchListResponse: ArrayList<UserWatchList>? = null

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

}