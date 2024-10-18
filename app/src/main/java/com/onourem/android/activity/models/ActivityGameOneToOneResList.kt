package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivityGameOneToOneResList {
    @SerializedName("gameId")
    @Expose
    var gameId: String? = null

    @SerializedName("participantId")
    @Expose
    var participantId: List<String> = emptyList()

    @SerializedName("userTypeId")
    @Expose
    var userType: List<String> = emptyList()

    @SerializedName("participantImageUrl")
    @Expose
    var participantImageUrl: List<String> = emptyList()

    @SerializedName("participantName")
    @Expose
    var participantName: List<String> = emptyList()

    @SerializedName("participantResponseStatus")
    @Expose
    var participantResponseStatus: List<String> = emptyList()

    @SerializedName("participantResponse")
    @Expose
    var participantResponse: List<String> = emptyList()

    //    @SerializedName("participantResponseImageSmallLink")
    //    @Expose
    //    private List<String> participantResponseImageSmallLink = null;
    @SerializedName("participantResponseImageLargeLink")
    @Expose
    var participantResponseImageLargeLink: List<String> = emptyList()

    @SerializedName("participantResponseVideoLink")
    @Expose
    var participantResponseVideoLink: List<String> = emptyList()

    //    @SerializedName("participantResponseDate")
    //    @Expose
    //    private List<String> participantResponseDate = null;
    @SerializedName("gameStartedByUserID")
    @Expose
    var gameStartedByUserID: String? = null

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
    //    private List<String> autodeleteStatus = null;
    @SerializedName("isResponseDeleted")
    @Expose
    var isResponseDeleted: List<String> = emptyList()

    @SerializedName("commentCount")
    @Expose
    var commentCount: String? = null

    //    @SerializedName("participantStatus")
    //    @Expose
    //    private List<String> participantStatus = null;
    @SerializedName("showEditOrDelete")
    @Expose
    var showEditOrDelete: List<String> = emptyList()

    //    @SerializedName("sortDateObj")
    //    @Expose
    //    private long sortDateObj;
    @SerializedName("isNewResponse")
    @Expose
    var isNewResponse: String? = null

    @SerializedName("isNewComment")
    @Expose
    var isNewComment: String? = null
}