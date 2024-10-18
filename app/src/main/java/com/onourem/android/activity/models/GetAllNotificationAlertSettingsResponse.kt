package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllNotificationAlertSettingsResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("updateEmailAlertSettings")
    @Expose
    var updateEmailAlertSettings: Any? = null

    @SerializedName("updateNotificationAlertSettings")
    @Expose
    var updateNotificationAlertSettings: Any? = null

    @SerializedName("reActivateAccount")
    @Expose
    var reActivateAccount: Any? = null

    @SerializedName("deActivateAccount")
    @Expose
    var deActivateAccount: Any? = null

    @SerializedName("getAllEmailAlertSettings")
    @Expose
    var getAllEmailAlertSettings: Any? = null

    @SerializedName("notficationPreferredTime")
    @Expose
    var notficationPreferredTime: String? = null

    @SerializedName("getAllNotificationAlertSettings")
    @Expose
    var getAllNotificationAlertSettings: GetAllNotificationAlertSettings? = null
}