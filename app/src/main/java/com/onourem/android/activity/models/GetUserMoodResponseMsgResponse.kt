package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

open class GetUserMoodResponseMsgResponse() : Parcelable {
    @SerializedName("changedMessageList")
    @Expose
    val changedMessageList: List<ChangedMessage> = emptyList()
    @SerializedName("errorCode")
    @Expose
    val errorCode: String? = null
    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String? = null
    @SerializedName("negativeResponseMsg")
    @Expose
    val negativeResponseMsg: String? = null

    @SerializedName("positiveResponseMsg")
    @Expose
    val positiveResponseMsg: String? = null

    @SerializedName("displayPhoneButton")
    @Expose
    val displayPhoneButton: String? = null

    @SerializedName("phoneInfoImageUrl")
    @Expose
    val phoneInfoImageUrl: String? = null

    @SerializedName("freeTrialRemainingDays")
    @Expose
    val freeTrialRemainingDays: Int? = null

    @SerializedName("canUserAccessApp")
    @Expose
    val canUserAccessApp: Boolean? = null

    @SerializedName("userMoodReasonImageList")
    @Expose
    val userMoodReasonImageList: List<UserMoodReasonImage> = emptyList()

    @SerializedName("exampleTexts")
    @Expose
    val exampleTexts: ArrayList<String>? = null

    @SerializedName("displayCounsellingPopup")
    @Expose
    val displayCounsellingPopup: String? = null

    @SerializedName("onlineCounselling")
    @Expose
    val onlineCounselling: String? = null


    @SerializedName("offlineCounselling")
    @Expose
    val offlineCounselling: String? = null


    @SerializedName("onouremOnlineCounselling")
    @Expose
    val onouremOnlineCounselling: String? = null

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetUserMoodResponseMsgResponse> {
        override fun createFromParcel(parcel: Parcel): GetUserMoodResponseMsgResponse {
            return GetUserMoodResponseMsgResponse(parcel)
        }

        override fun newArray(size: Int): Array<GetUserMoodResponseMsgResponse?> {
            return arrayOfNulls(size)
        }
    }
}
