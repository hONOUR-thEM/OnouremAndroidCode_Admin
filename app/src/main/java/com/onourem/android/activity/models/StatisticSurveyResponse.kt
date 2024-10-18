package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatisticSurveyResponse {
    @SerializedName("graphDataList")
    @Expose
    internal var graphDataList: List<String>? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("ageGroupList")
    @Expose
    internal var ageGroupList: List<String>? = null

    @SerializedName("totalUserAnswerCount")
    @Expose
    internal var totalUserAnswerCount: Int? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("genderList")
    @Expose
    internal var genderList: List<String>? = null
    fun getGraphDataList(): List<String>? {
        return graphDataList
    }

    fun setGraphDataList(graphDataList: List<String>?) {
        this.graphDataList = graphDataList
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getAgeGroupList(): List<String>? {
        return ageGroupList
    }

    fun setAgeGroupList(ageGroupList: List<String>?) {
        this.ageGroupList = ageGroupList
    }

    fun getTotalUserAnswerCount(): Int? {
        return totalUserAnswerCount
    }

    fun setTotalUserAnswerCount(totalUserAnswerCount: Int?) {
        this.totalUserAnswerCount = totalUserAnswerCount
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getGenderList(): List<String>? {
        return genderList
    }

    fun setGenderList(genderList: List<String>?) {
        this.genderList = genderList
    }
}