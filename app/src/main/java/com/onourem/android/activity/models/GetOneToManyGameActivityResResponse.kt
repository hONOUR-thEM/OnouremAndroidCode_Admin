package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetOneToManyGameActivityResResponse : PopUpCommonData(), Serializable {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    //    public Integer getDisplayNumberOfGames() {
    //        return displayNumberOfGames;
    //    }
    //
    //    public void setDisplayNumberOfGames(Integer displayNumberOfGames) {
    //        this.displayNumberOfGames = displayNumberOfGames;
    //    }
    //    @SerializedName("displayNumberOfGames")
    //    @Expose
    //    private Integer displayNumberOfGames;
    @SerializedName("gameIdList")
    @Expose
    var gameIdList: List<Int>? = null

    @SerializedName("ActivityGameOneToManyResList")
    @Expose
    var activityGameOneToManyResList: List<ActivityGameOneToManyResList>? = null
}