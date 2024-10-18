package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.onourem.android.activity.ui.utils.listners.CheckNullProcessable

class AcceptPendingWatchResponse : CheckNullProcessable {
    //    @SerializedName("watchListMsg")
    //    @Expose
    //    private String watchListMsg;
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    override fun checkNullValues() {
        if (errorCode == null) {
            errorCode = ""
        }

        if (errorMessage == null) {
            errorMessage = ""
        }
    }

}