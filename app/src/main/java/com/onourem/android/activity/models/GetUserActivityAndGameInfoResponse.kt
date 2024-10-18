package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserActivityAndGameInfoResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("loginDayActivityInfoList")
    @Expose
    internal var loginDayActivityInfoList: List<LoginDayActivityInfoList>? = null

    //    @SerializedName("taskAndMessagePostList")
    //    @Expose
    //    private List<Object> taskAndMessagePostList = null;
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

    fun getLoginDayActivityInfoList(): List<LoginDayActivityInfoList>? {
        return loginDayActivityInfoList
    }

    fun setLoginDayActivityInfoList(loginDayActivityInfoList: List<LoginDayActivityInfoList>?) {
        this.loginDayActivityInfoList = loginDayActivityInfoList
    } //    public List<Object> getTaskAndMessagePostList() {
    //        return taskAndMessagePostList;
    //    }
    //
    //    public void setTaskAndMessagePostList(List<Object> taskAndMessagePostList) {
    //        this.taskAndMessagePostList = taskAndMessagePostList;
    //    }
}