package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserListResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("token")
    @Expose
    var token: Any? = null

    @SerializedName("userList")
    @Expose
    var userList: List<UserList> = emptyList()

    @SerializedName("message")
    @Expose
    var message: Any? = null

    @SerializedName("playgroupUserLimit")
    @Expose
    var playgroupUserLimit: Int? = null

    //    @SerializedName("displayNumberOfUsers")
    //    @Expose
    //    private Integer displayNumberOfUsers;
    @SerializedName("userIdList")
    @Expose
    var userIdList: List<Int>? = null
}