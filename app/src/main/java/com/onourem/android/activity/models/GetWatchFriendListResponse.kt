package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetWatchFriendListResponse protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("userWatchFriendList")
    @Expose
    internal var userWatchFriendList: List<UserWatchList>? = null
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

    fun getUserWatchFriendList(): List<UserWatchList>? {
        return userWatchFriendList
    }

    fun setUserWatchFriendList(userWatchFriendList: List<UserWatchList>) {
        this.userWatchFriendList = userWatchFriendList
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(errorMessage)
        dest.writeString(errorCode)
        dest.writeTypedList(userWatchFriendList)
    }

    companion object CREATOR : Parcelable.Creator<GetWatchFriendListResponse> {
        override fun createFromParcel(parcel: Parcel): GetWatchFriendListResponse {
            return GetWatchFriendListResponse(parcel)
        }

        override fun newArray(size: Int): Array<GetWatchFriendListResponse?> {
            return arrayOfNulls(size)
        }
    }
}