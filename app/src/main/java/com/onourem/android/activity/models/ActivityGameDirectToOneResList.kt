package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivityGameDirectToOneResList {
    @SerializedName("gameId")
    @Expose
    var gameId: String? = null

    @SerializedName("participantId")
    @Expose
    var participantId: String? = null

    @SerializedName("participantImageUrl")
    @Expose
    var participantImageUrl: String? = null

    @SerializedName("participantName")
    @Expose
    var participantName: String? = null

    @SerializedName("participantResponseStatus")
    @Expose
    var participantResponseStatus: String? = null

    @SerializedName("participantResponse")
    @Expose
    var participantResponse: String? = null

    @SerializedName("participantResponseImageSmallLink")
    @Expose
    private val participantResponseImageSmallLink: String? = null

    @SerializedName("participantResponseImageLargeLink")
    @Expose
    var participantResponseImageLargeLink: String? = null

    @SerializedName("participantResponseVideoLink")
    @Expose
    var participantResponseVideoLink: String? = null

    //    @SerializedName("participantResponseDate")
    //    @Expose
    //    private String participantResponseDate;
    @SerializedName("gameStartedByUserID")
    @Expose
    var gameStartedByUserID: String? = null

    //    @SerializedName("gameStartedByUserName")
    //    @Expose
    //    private Object gameStartedByUserName;
    //    @SerializedName("gameStartDate")
    //    @Expose
    //    private String gameStartDate;
    //    @SerializedName("gameExpiryDate")
    //    @Expose
    //    private String gameExpiryDate;
    @SerializedName("isGameExpired")
    @Expose
    var isGameExpired: String? = null

    //    @SerializedName("autodeleteStatus")
    //    @Expose
    //    private String autodeleteStatus;
    @SerializedName("isResponseDeleted")
    @Expose
    var isResponseDeleted: String? = null

    @SerializedName("commentCount")
    @Expose
    var commentCount: String? = null

    //    @SerializedName("participantStatus")
    //    @Expose
    //    private String participantStatus;
    @SerializedName("askedByFirstName")
    @Expose
    var askedByFirstName: String? = null

    @SerializedName("showEditOrDelete")
    @Expose
    var showEditOrDelete: String? = null

    @SerializedName("isNewResponse")
    @Expose
    var isNewResponse: String? = null

    @SerializedName("isNewComment")
    @Expose
    var isNewComment: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null

    @SerializedName("askedByUserTypeId")
    @Expose
    var askedByUserType: String? = null
}