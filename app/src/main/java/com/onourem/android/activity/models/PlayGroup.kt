package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlayGroup : Parcelable {


    //    @SerializedName("commentsEnabled")
//    @Expose
//    var commentsEnabled: String? = null

    //    @SerializedName("inviteLinkEnabled")
//    @Expose
//    var inviteLinkEnabled: String? = null

    @SerializedName("playGroupId")
    @Expose
    var playGroupId: String? = null

    @SerializedName("playGroupTypeId")
    @Expose
    var playGroupTypeId: String? = null

    @SerializedName("playGroupCategoryId")
    @Expose
    var playGroupCategoryId: String? = null

    @SerializedName("playGroupCategoryName")
    @Expose
    var playGroupCategoryName: String? = null

    @SerializedName("playGroupLanguageId")
    @Expose
    var playGroupLanguageId: String? = null

    @SerializedName("playGroupLanguageName")
    @Expose
    var playGroupLanguageName: String? = null

    @SerializedName("playGroupName")
    @Expose
    var playGroupName: String? = null

    @SerializedName("isUserAdmin")
    @Expose
    var isUserAdmin: String? = null

    @SerializedName("allCanAsk")
    @Expose
    var allCanAsk: String? = null

    @SerializedName("newquestionNumber")
    @Expose
    var newquestionNumber = 0

    @SerializedName("newAnswerNumber")
    @Expose
    var newAnswerNumber = 0

    @SerializedName("totalQuestionNumber")
    @Expose
    internal var totalQuestionNumber = 0

    @SerializedName("newMemeberNumber")
    @Expose
    var newMemeberNumber = 0

    @SerializedName("lastActivityTime")
    @Expose
    var lastActivityTime: String? = null

    @SerializedName("isActivityAskedBefore")
    @Expose
    var isActivityAskedBefore: String? = null

    @SerializedName("memberCount")
    @Expose
    var memberCount: String? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: String? = null
    var isDummyGroup = false
    var isNew = false
    var isSelected = false
    var isDisable = false

    constructor()
    protected constructor(`in`: Parcel) {
        playGroupId = `in`.readString()
        playGroupTypeId = `in`.readString()
        playGroupCategoryId = `in`.readString()
        playGroupCategoryName = `in`.readString()
        playGroupLanguageId = `in`.readString()
        playGroupLanguageName = `in`.readString()
        playGroupName = `in`.readString()
        isUserAdmin = `in`.readString()
        allCanAsk = `in`.readString()
        newquestionNumber = `in`.readInt()
        newAnswerNumber = `in`.readInt()
        totalQuestionNumber = `in`.readInt()
        newMemeberNumber = `in`.readInt()
        lastActivityTime = `in`.readString()
        isActivityAskedBefore = `in`.readString()
        memberCount = `in`.readString()
        createdBy = `in`.readString()
        isDummyGroup = `in`.readByte().toInt() != 0
        isNew = `in`.readByte().toInt() != 0
        isSelected = `in`.readByte().toInt() != 0
        isDisable = `in`.readByte().toInt() != 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(playGroupId)
        dest.writeString(playGroupTypeId)
        dest.writeString(playGroupCategoryId)
        dest.writeString(playGroupCategoryName)
        dest.writeString(playGroupLanguageId)
        dest.writeString(playGroupLanguageName)
        dest.writeString(playGroupName)
        dest.writeString(isUserAdmin)
        dest.writeString(allCanAsk)
        dest.writeInt(newquestionNumber)
        dest.writeInt(newAnswerNumber)
        dest.writeInt(totalQuestionNumber)
        dest.writeInt(newMemeberNumber)
        dest.writeString(lastActivityTime)
        dest.writeString(isActivityAskedBefore)
        dest.writeString(memberCount)
        dest.writeString(createdBy)
        dest.writeByte((if (isDummyGroup) 1 else 0).toByte())
        dest.writeByte((if (isNew) 1 else 0).toByte())
        dest.writeByte((if (isSelected) 1 else 0).toByte())
        dest.writeByte((if (isDisable) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    fun setTotalQuestionNumber(totalQuestionNumber: Int) {
        this.totalQuestionNumber = totalQuestionNumber
    }

    override fun toString(): String {
        return playGroupId!!
    }

    companion object CREATOR : Parcelable.Creator<PlayGroup> {
        override fun createFromParcel(parcel: Parcel): PlayGroup {
            return PlayGroup(parcel)
        }

        override fun newArray(size: Int): Array<PlayGroup?> {
            return arrayOfNulls(size)
        }
    }
}