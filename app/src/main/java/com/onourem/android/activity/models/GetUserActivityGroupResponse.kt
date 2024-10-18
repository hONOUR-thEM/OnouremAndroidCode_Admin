package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetUserActivityGroupResponse : PopUpCommonData(), Serializable {
    @SerializedName("activityGameResponseIdList")
    @Expose
    internal var activityGameResponseIdList: List<Int> = emptyList()

    @SerializedName("activityTagStatus")
    @Expose
    internal var activityTagStatus: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    //    @SerializedName("displayNumberOfGames")
    //    @Expose
    //    internal Integer displayNumberOfGames;
    //    @SerializedName("participantResponseStatus")
    //    @Expose
    //    internal String participantResponseStatus;
    @SerializedName("ActivityGameOneToManyResList")
    @Expose
    internal var activityGameOneToManyResList: List<ActivityGameOneToManyResList> = emptyList()
    fun getActivityGameResponseIdList(): List<Int> {
        return activityGameResponseIdList
    }

    fun setActivityGameResponseIdList(activityGameResponseIdList: List<Int>) {
        this.activityGameResponseIdList = activityGameResponseIdList
    }

    fun getActivityTagStatus(): String? {
        return activityTagStatus
    }

    fun setActivityTagStatus(activityTagStatus: String?) {
        this.activityTagStatus = activityTagStatus
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    //    public Integer getDisplayNumberOfGames() {
    //        return displayNumberOfGames;
    //    }
    //
    //    public void setDisplayNumberOfGames(Integer displayNumberOfGames) {
    //        this.displayNumberOfGames = displayNumberOfGames;
    //    }
    //    public String getParticipantResponseStatus() {
    //        return participantResponseStatus;
    //    }
    //
    //    public void setParticipantResponseStatus(String participantResponseStatus) {
    //        this.participantResponseStatus = participantResponseStatus;
    //    }
    fun getActivityGameOneToManyResList(): List<ActivityGameOneToManyResList> {
        return activityGameOneToManyResList
    }

    fun setActivityGameOneToManyResList(activityGameOneToManyResList: List<ActivityGameOneToManyResList>) {
        this.activityGameOneToManyResList = activityGameOneToManyResList
    }
}