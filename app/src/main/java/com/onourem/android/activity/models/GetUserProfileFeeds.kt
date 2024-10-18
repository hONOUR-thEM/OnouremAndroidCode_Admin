package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserProfileFeeds : Parcelable {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("feedsList")
    @Expose
    var feedsList: List<FeedsList>? = null

    @SerializedName("feedActionDataList")
    @Expose
    var feedActionDataList: List<Any?>? = null

    @SerializedName("postList")
    @Expose
    var postList: List<Any?>? = null

    @SerializedName("popularPostList")
    @Expose
    var popularPostList: List<Any?>? = null

    @SerializedName("filterCategoryList")
    @Expose
    var filterCategoryList: Any? = null

    @SerializedName("filterCountryList")
    @Expose
    var filterCountryList: Any? = null

    @SerializedName("filterSubCategoryList")
    @Expose
    var filterSubCategoryList: Any? = null

    @SerializedName("displayNumberOfFeeds")
    @Expose
    var displayNumberOfFeeds: Int? = null

    @SerializedName("taskAndMessagePostList")
    @Expose
    var taskAndMessagePostList: Any? = null

    @SerializedName("thankYouCount")
    @Expose
    var thankYouCount: Int? = null

    @SerializedName("missYouCount")
    @Expose
    var missYouCount: Int? = null

    @SerializedName("admireYouCount")
    @Expose
    var admireYouCount: Int? = null

    @SerializedName("thankYouList")
    @Expose
    var thankYouList: List<Any?>? = null

    @SerializedName("missYouList")
    @Expose
    var missYouList: List<Any?>? = null

    @SerializedName("admireYouList")
    @Expose
    var admireYouList: List<Any?>? = null

    @SerializedName("feedCount")
    @Expose
    var feedCount: Any? = null

    @SerializedName("userMsg")
    @Expose
    var userMsg: Any? = null

    @SerializedName("userMessageLink")
    @Expose
    var userMessageLink: Any? = null

    @SerializedName("pushPopupDuration")
    @Expose
    var pushPopupDuration: String? = null

    @SerializedName("pushEnableMessage")
    @Expose
    var pushEnableMessage: String? = null

    @SerializedName("invitationText")
    @Expose
    var invitationText: Any? = null

    @SerializedName("forceAndAdviceUpgrade")
    @Expose
    var forceAndAdviceUpgrade: Any? = null

    @SerializedName("customAlertDialog")
    @Expose
    var customAlertDialog: Any? = null

    @SerializedName("totalMsgCount")
    @Expose
    var totalMsgCount: Any? = null

    @SerializedName("categoryMsgCountList")
    @Expose
    var categoryMsgCountList: Any? = null

    @SerializedName("userProfileData")
    @Expose
    var userProfileData: Any? = null

    constructor()
    protected constructor(`in`: Parcel) {
        errorCode = `in`.readString()
        errorMessage = `in`.readString()
        fieldName = `in`.readParcelable(Any::class.java.classLoader)
        feedsList = `in`.createTypedArrayList(FeedsList.CREATOR)
        feedActionDataList = ArrayList()
        `in`.readList(feedActionDataList as ArrayList<Any?>, Any::class.java.classLoader)
        postList = ArrayList()
        `in`.readList(postList as ArrayList<Any?>, Any::class.java.classLoader)
        popularPostList = ArrayList()
        `in`.readList(popularPostList as ArrayList<Any?>, Any::class.java.classLoader)
        filterCategoryList = `in`.readParcelable(Any::class.java.classLoader)
        filterCountryList = `in`.readParcelable(Any::class.java.classLoader)
        filterSubCategoryList = `in`.readParcelable(Any::class.java.classLoader)
        displayNumberOfFeeds = `in`.readValue(Int::class.java.classLoader) as Int?
        taskAndMessagePostList = `in`.readParcelable(Any::class.java.classLoader)
        thankYouCount = `in`.readValue(Int::class.java.classLoader) as Int?
        missYouCount = `in`.readValue(Int::class.java.classLoader) as Int?
        admireYouCount = `in`.readValue(Int::class.java.classLoader) as Int?
        thankYouList = ArrayList()
        `in`.readList(thankYouList as ArrayList<Any?>, Any::class.java.classLoader)
        missYouList = ArrayList()
        `in`.readList(missYouList as ArrayList<Any?>, Any::class.java.classLoader)
        admireYouList = ArrayList()
        `in`.readList(admireYouList as ArrayList<Any?>, Any::class.java.classLoader)
        feedCount = `in`.readParcelable(Any::class.java.classLoader)
        userMsg = `in`.readParcelable(Any::class.java.classLoader)
        userMessageLink = `in`.readParcelable(Any::class.java.classLoader)
        pushPopupDuration = `in`.readString()
        pushEnableMessage = `in`.readString()
        invitationText = `in`.readParcelable(Any::class.java.classLoader)
        forceAndAdviceUpgrade = `in`.readParcelable(Any::class.java.classLoader)
        customAlertDialog = `in`.readParcelable(Any::class.java.classLoader)
        totalMsgCount = `in`.readParcelable(Any::class.java.classLoader)
        categoryMsgCountList = `in`.readParcelable(Any::class.java.classLoader)
        userProfileData = `in`.readParcelable(Any::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(errorCode)
        dest.writeString(errorMessage)
        dest.writeParcelable(fieldName as Parcelable?, flags)
        dest.writeTypedList(feedsList)
        dest.writeList(feedActionDataList)
        dest.writeList(postList)
        dest.writeList(popularPostList)
        dest.writeParcelable(filterCategoryList as Parcelable?, flags)
        dest.writeParcelable(filterCountryList as Parcelable?, flags)
        dest.writeParcelable(filterSubCategoryList as Parcelable?, flags)
        dest.writeValue(displayNumberOfFeeds)
        dest.writeParcelable(taskAndMessagePostList as Parcelable?, flags)
        dest.writeValue(thankYouCount)
        dest.writeValue(missYouCount)
        dest.writeValue(admireYouCount)
        dest.writeList(thankYouList)
        dest.writeList(missYouList)
        dest.writeList(admireYouList)
        dest.writeParcelable(feedCount as Parcelable?, flags)
        dest.writeParcelable(userMsg as Parcelable?, flags)
        dest.writeParcelable(userMessageLink as Parcelable?, flags)
        dest.writeString(pushPopupDuration)
        dest.writeString(pushEnableMessage)
        dest.writeParcelable(invitationText as Parcelable?, flags)
        dest.writeParcelable(forceAndAdviceUpgrade as Parcelable?, flags)
        dest.writeParcelable(customAlertDialog as Parcelable?, flags)
        dest.writeParcelable(totalMsgCount as Parcelable?, flags)
        dest.writeParcelable(categoryMsgCountList as Parcelable?, flags)
        dest.writeParcelable(userProfileData as Parcelable?, flags)
    }

    companion object CREATOR : Parcelable.Creator<GetUserProfileFeeds> {
        override fun createFromParcel(parcel: Parcel): GetUserProfileFeeds {
            return GetUserProfileFeeds(parcel)
        }

        override fun newArray(size: Int): Array<GetUserProfileFeeds?> {
            return arrayOfNulls(size)
        }
    }
}