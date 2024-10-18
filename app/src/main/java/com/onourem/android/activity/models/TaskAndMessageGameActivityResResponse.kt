package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TaskAndMessageGameActivityResResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: String? = null

    @SerializedName("feedsList")
    @Expose
    internal var feedsList: List<FeedsList>? = null

    @SerializedName("feedActionDataList")
    @Expose
    internal var feedActionDataList: List<String>? = null

    @SerializedName("postList")
    @Expose
    internal var postList: List<String>? = null

    @SerializedName("popularPostList")
    @Expose
    internal var popularPostList: List<String>? = null

    @SerializedName("filterCategoryList")
    @Expose
    internal var filterCategoryList: String? = null

    @SerializedName("filterCountryList")
    @Expose
    internal var filterCountryList: String? = null

    @SerializedName("filterSubCategoryList")
    @Expose
    internal var filterSubCategoryList: String? = null

    @SerializedName("displayNumberOfFeeds")
    @Expose
    internal var displayNumberOfFeeds: Int? = null

    @SerializedName("taskAndMessagePostList")
    @Expose
    internal var taskAndMessagePostList: List<TaskAndMessagePostList>? = null

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
    internal var thankYouList: List<String>? = null

    @SerializedName("missYouList")
    @Expose
    internal var missYouList: List<String>? = null

    @SerializedName("admireYouList")
    @Expose
    internal var admireYouList: List<String>? = null

    @SerializedName("feedCount")
    @Expose
    internal var feedCount: String? = null

    @SerializedName("userMsg")
    @Expose
    internal var userMsg: String? = null

    @SerializedName("userMessageLink")
    @Expose
    internal var userMessageLink: String? = null

    @SerializedName("pushPopupDuration")
    @Expose
    internal var pushPopupDuration: String? = null

    @SerializedName("pushEnableMessage")
    @Expose
    internal var pushEnableMessage: String? = null

    @SerializedName("invitationText")
    @Expose
    internal var invitationText: String? = null

    @SerializedName("forceAndAdviceUpgrade")
    @Expose
    internal var forceAndAdviceUpgrade: String? = null

    @SerializedName("customAlertDialog")
    @Expose
    internal var customAlertDialog: String? = null

    @SerializedName("totalMsgCount")
    @Expose
    internal var totalMsgCount: String? = null

    @SerializedName("categoryMsgCountList")
    @Expose
    internal var categoryMsgCountList: String? = null

    @SerializedName("userProfileData")
    @Expose
    internal var userProfileData: String? = null
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

    fun getFieldName(): String? {
        return fieldName
    }

    fun setFieldName(fieldName: String?) {
        this.fieldName = fieldName
    }

    fun getFeedsList(): List<FeedsList>? {
        return feedsList
    }

    fun setFeedsList(feedsList: List<FeedsList>?) {
        this.feedsList = feedsList
    }

    fun getFeedActionDataList(): List<String>? {
        return feedActionDataList
    }

    fun setFeedActionDataList(feedActionDataList: List<String>?) {
        this.feedActionDataList = feedActionDataList
    }

    fun getPostList(): List<String>? {
        return postList
    }

    fun setPostList(postList: List<String>?) {
        this.postList = postList
    }

    fun getPopularPostList(): List<String>? {
        return popularPostList
    }

    fun setPopularPostList(popularPostList: List<String>?) {
        this.popularPostList = popularPostList
    }

    fun getFilterCategoryList(): String? {
        return filterCategoryList
    }

    fun setFilterCategoryList(filterCategoryList: String?) {
        this.filterCategoryList = filterCategoryList
    }

    fun getFilterCountryList(): String? {
        return filterCountryList
    }

    fun setFilterCountryList(filterCountryList: String?) {
        this.filterCountryList = filterCountryList
    }

    fun getFilterSubCategoryList(): String? {
        return filterSubCategoryList
    }

    fun setFilterSubCategoryList(filterSubCategoryList: String?) {
        this.filterSubCategoryList = filterSubCategoryList
    }

    fun getDisplayNumberOfFeeds(): Int? {
        return displayNumberOfFeeds
    }

    fun setDisplayNumberOfFeeds(displayNumberOfFeeds: Int?) {
        this.displayNumberOfFeeds = displayNumberOfFeeds
    }

    fun getTaskAndMessagePostList(): List<TaskAndMessagePostList>? {
        return taskAndMessagePostList
    }

    fun setTaskAndMessagePostList(taskAndMessagePostList: List<TaskAndMessagePostList>?) {
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

    fun getThankYouList(): List<String>? {
        return thankYouList
    }

    fun setThankYouList(thankYouList: List<String>?) {
        this.thankYouList = thankYouList
    }

    fun getMissYouList(): List<String>? {
        return missYouList
    }

    fun setMissYouList(missYouList: List<String>?) {
        this.missYouList = missYouList
    }

    fun getAdmireYouList(): List<String>? {
        return admireYouList
    }

    fun setAdmireYouList(admireYouList: List<String>?) {
        this.admireYouList = admireYouList
    }

    fun getFeedCount(): String? {
        return feedCount
    }

    fun setFeedCount(feedCount: String?) {
        this.feedCount = feedCount
    }

    fun getUserMsg(): String? {
        return userMsg
    }

    fun setUserMsg(userMsg: String?) {
        this.userMsg = userMsg
    }

    fun getUserMessageLink(): String? {
        return userMessageLink
    }

    fun setUserMessageLink(userMessageLink: String?) {
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

    fun getInvitationText(): String? {
        return invitationText
    }

    fun setInvitationText(invitationText: String?) {
        this.invitationText = invitationText
    }

    fun getForceAndAdviceUpgrade(): String? {
        return forceAndAdviceUpgrade
    }

    fun setForceAndAdviceUpgrade(forceAndAdviceUpgrade: String?) {
        this.forceAndAdviceUpgrade = forceAndAdviceUpgrade
    }

    fun getCustomAlertDialog(): String? {
        return customAlertDialog
    }

    fun setCustomAlertDialog(customAlertDialog: String?) {
        this.customAlertDialog = customAlertDialog
    }

    fun getTotalMsgCount(): String? {
        return totalMsgCount
    }

    fun setTotalMsgCount(totalMsgCount: String?) {
        this.totalMsgCount = totalMsgCount
    }

    fun getCategoryMsgCountList(): String? {
        return categoryMsgCountList
    }

    fun setCategoryMsgCountList(categoryMsgCountList: String?) {
        this.categoryMsgCountList = categoryMsgCountList
    }

    fun getUserProfileData(): String? {
        return userProfileData
    }

    fun setUserProfileData(userProfileData: String?) {
        this.userProfileData = userProfileData
    }
}