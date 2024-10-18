package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserProfilePostsResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: Any? = null

    @SerializedName("feedsList")
    @Expose
    internal var feedsList: List<FeedsList>? = null

    @SerializedName("feedActionDataList")
    @Expose
    internal var feedActionDataList: List<Int>? = null

    @SerializedName("postList")
    @Expose
    internal var postList: List<Any>? = null

    @SerializedName("popularPostList")
    @Expose
    internal var popularPostList: List<Any>? = null

    @SerializedName("filterCategoryList")
    @Expose
    internal var filterCategoryList: Any? = null

    @SerializedName("filterCountryList")
    @Expose
    internal var filterCountryList: Any? = null

    @SerializedName("filterSubCategoryList")
    @Expose
    internal var filterSubCategoryList: Any? = null

    @SerializedName("displayNumberOfFeeds")
    @Expose
    internal var displayNumberOfFeeds: Int? = null

    @SerializedName("taskAndMessagePostList")
    @Expose
    internal var taskAndMessagePostList: Any? = null

    @SerializedName("thankYouCount")
    @Expose
    internal var thankYouCount: Int? = null

    @SerializedName("missYouCount")
    @Expose
    internal var missYouCount: Int? = null

    @SerializedName("admireYouCount")
    @Expose
    internal var admireYouCount: Int? = null

    @SerializedName("thankYouList")
    @Expose
    internal var thankYouList: List<Any>? = null

    @SerializedName("missYouList")
    @Expose
    internal var missYouList: List<Any>? = null

    @SerializedName("admireYouList")
    @Expose
    internal var admireYouList: List<Any>? = null

    @SerializedName("feedCount")
    @Expose
    internal var feedCount: Any? = null

    @SerializedName("userMsg")
    @Expose
    internal var userMsg: Any? = null

    @SerializedName("userMessageLink")
    @Expose
    internal var userMessageLink: Any? = null

    @SerializedName("pushPopupDuration")
    @Expose
    internal var pushPopupDuration: String? = null

    @SerializedName("pushEnableMessage")
    @Expose
    internal var pushEnableMessage: String? = null

    @SerializedName("invitationText")
    @Expose
    internal var invitationText: Any? = null

    @SerializedName("forceAndAdviceUpgrade")
    @Expose
    internal var forceAndAdviceUpgrade: Any? = null

    @SerializedName("customAlertDialog")
    @Expose
    internal var customAlertDialog: Any? = null

    @SerializedName("totalMsgCount")
    @Expose
    internal var totalMsgCount: Any? = null

    @SerializedName("categoryMsgCountList")
    @Expose
    internal var categoryMsgCountList: Any? = null

    @SerializedName("userProfileData")
    @Expose
    internal var userProfileData: Any? = null
    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getFieldName(): Any? {
        return fieldName
    }

    fun setFieldName(fieldName: Any?) {
        this.fieldName = fieldName
    }

    fun getFeedsList(): List<FeedsList>? {
        return feedsList
    }

    fun setFeedsList(feedsList: List<FeedsList>?) {
        this.feedsList = feedsList
    }

    fun getFeedActionDataList(): List<Int>? {
        return feedActionDataList
    }

    fun setFeedActionDataList(feedActionDataList: List<Int>?) {
        this.feedActionDataList = feedActionDataList
    }

    fun getPostList(): List<Any>? {
        return postList
    }

    fun setPostList(postList: List<Any>?) {
        this.postList = postList
    }

    fun getPopularPostList(): List<Any>? {
        return popularPostList
    }

    fun setPopularPostList(popularPostList: List<Any>?) {
        this.popularPostList = popularPostList
    }

    fun getFilterCategoryList(): Any? {
        return filterCategoryList
    }

    fun setFilterCategoryList(filterCategoryList: Any?) {
        this.filterCategoryList = filterCategoryList
    }

    fun getFilterCountryList(): Any? {
        return filterCountryList
    }

    fun setFilterCountryList(filterCountryList: Any?) {
        this.filterCountryList = filterCountryList
    }

    fun getFilterSubCategoryList(): Any? {
        return filterSubCategoryList
    }

    fun setFilterSubCategoryList(filterSubCategoryList: Any?) {
        this.filterSubCategoryList = filterSubCategoryList
    }

    fun getDisplayNumberOfFeeds(): Int? {
        return displayNumberOfFeeds
    }

    fun setDisplayNumberOfFeeds(displayNumberOfFeeds: Int?) {
        this.displayNumberOfFeeds = displayNumberOfFeeds
    }

    fun getTaskAndMessagePostList(): Any? {
        return taskAndMessagePostList
    }

    fun setTaskAndMessagePostList(taskAndMessagePostList: Any?) {
        this.taskAndMessagePostList = taskAndMessagePostList
    }

    fun getThankYouCount(): Int? {
        return thankYouCount
    }

    fun setThankYouCount(thankYouCount: Int?) {
        this.thankYouCount = thankYouCount
    }

    fun getMissYouCount(): Int? {
        return missYouCount
    }

    fun setMissYouCount(missYouCount: Int?) {
        this.missYouCount = missYouCount
    }

    fun getAdmireYouCount(): Int? {
        return admireYouCount
    }

    fun setAdmireYouCount(admireYouCount: Int?) {
        this.admireYouCount = admireYouCount
    }

    fun getThankYouList(): List<Any>? {
        return thankYouList
    }

    fun setThankYouList(thankYouList: List<Any>?) {
        this.thankYouList = thankYouList
    }

    fun getMissYouList(): List<Any>? {
        return missYouList
    }

    fun setMissYouList(missYouList: List<Any>?) {
        this.missYouList = missYouList
    }

    fun getAdmireYouList(): List<Any>? {
        return admireYouList
    }

    fun setAdmireYouList(admireYouList: List<Any>?) {
        this.admireYouList = admireYouList
    }

    fun getFeedCount(): Any? {
        return feedCount
    }

    fun setFeedCount(feedCount: Any?) {
        this.feedCount = feedCount
    }

    fun getUserMsg(): Any? {
        return userMsg
    }

    fun setUserMsg(userMsg: Any?) {
        this.userMsg = userMsg
    }

    fun getUserMessageLink(): Any? {
        return userMessageLink
    }

    fun setUserMessageLink(userMessageLink: Any?) {
        this.userMessageLink = userMessageLink
    }

    fun getPushPopupDuration(): String? {
        return pushPopupDuration
    }

    fun setPushPopupDuration(pushPopupDuration: String?) {
        this.pushPopupDuration = pushPopupDuration
    }

    fun getPushEnableMessage(): String? {
        return pushEnableMessage
    }

    fun setPushEnableMessage(pushEnableMessage: String?) {
        this.pushEnableMessage = pushEnableMessage
    }

    fun getInvitationText(): Any? {
        return invitationText
    }

    fun setInvitationText(invitationText: Any?) {
        this.invitationText = invitationText
    }

    fun getForceAndAdviceUpgrade(): Any? {
        return forceAndAdviceUpgrade
    }

    fun setForceAndAdviceUpgrade(forceAndAdviceUpgrade: Any?) {
        this.forceAndAdviceUpgrade = forceAndAdviceUpgrade
    }

    fun getCustomAlertDialog(): Any? {
        return customAlertDialog
    }

    fun setCustomAlertDialog(customAlertDialog: Any?) {
        this.customAlertDialog = customAlertDialog
    }

    fun getTotalMsgCount(): Any? {
        return totalMsgCount
    }

    fun setTotalMsgCount(totalMsgCount: Any?) {
        this.totalMsgCount = totalMsgCount
    }

    fun getCategoryMsgCountList(): Any? {
        return categoryMsgCountList
    }

    fun setCategoryMsgCountList(categoryMsgCountList: Any?) {
        this.categoryMsgCountList = categoryMsgCountList
    }

    fun getUserProfileData(): Any? {
        return userProfileData
    }

    fun setUserProfileData(userProfileData: Any?) {
        this.userProfileData = userProfileData
    }
}