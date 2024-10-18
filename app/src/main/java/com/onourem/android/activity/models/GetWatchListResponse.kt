package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.onourem.android.activity.models.ForceAndAdviceUpgrade
import com.onourem.android.activity.models.UserWatchList

class GetWatchListResponse : Parcelable {
    @SerializedName("userWatchList")
    @Expose
    internal var userWatchList: MutableList<UserWatchList>? = null

    @SerializedName("onetoManyGamePoint")
    @Expose
    var onetoManyGamePoint: String? = null

    @SerializedName("descriptionTextForWatchList")
    @Expose
    var descriptionTextForWatchList: String? = null

    @SerializedName("finalFriendList")
    @Expose
    var finalFriendList: List<String>? = null

    @SerializedName("isInviteFriendPending")
    @Expose
    var isInviteFriendPending: String? = null

    @SerializedName("gamePoint")
    @Expose
    var gamePoint: String? = null

    @SerializedName("videoDuration")
    @Expose
    var videoDuration: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("forceAndAdviceUpgrade")
    @Expose
    var forceAndAdviceUpgrade: ForceAndAdviceUpgrade? = null

    @SerializedName("descriptionTextForWatchListWhenZeroWatching")
    @Expose
    var descriptionTextForWatchListWhenZeroWatching: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("numberOfInstallPoint")
    @Expose
    var numberOfInstallPoint: String? = null
    var isFromEditAdd = false

    constructor()
    protected constructor(`in`: Parcel) {
        userWatchList = ArrayList()
        `in`.readList(userWatchList as ArrayList<UserWatchList>, UserWatchList::class.java.classLoader)
        onetoManyGamePoint = `in`.readString()
        descriptionTextForWatchList = `in`.readString()
        finalFriendList = `in`.createStringArrayList()
        isInviteFriendPending = `in`.readString()
        gamePoint = `in`.readString()
        videoDuration = `in`.readString()
        errorMessage = `in`.readString()
        forceAndAdviceUpgrade = `in`.readParcelable(ForceAndAdviceUpgrade::class.java.classLoader)
        descriptionTextForWatchListWhenZeroWatching = `in`.readString()
        errorCode = `in`.readString()
        numberOfInstallPoint = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(userWatchList)
        dest.writeString(onetoManyGamePoint)
        dest.writeString(descriptionTextForWatchList)
        dest.writeStringList(finalFriendList)
        dest.writeString(isInviteFriendPending)
        dest.writeString(gamePoint)
        dest.writeString(videoDuration)
        dest.writeString(errorMessage)
        dest.writeParcelable(forceAndAdviceUpgrade, flags)
        dest.writeString(descriptionTextForWatchListWhenZeroWatching)
        dest.writeString(errorCode)
        dest.writeString(numberOfInstallPoint)
    }

    companion object CREATOR : Parcelable.Creator<GetWatchListResponse> {
        override fun createFromParcel(parcel: Parcel): GetWatchListResponse {
            return GetWatchListResponse(parcel)
        }

        override fun newArray(size: Int): Array<GetWatchListResponse?> {
            return arrayOfNulls(size)
        }
    }
}