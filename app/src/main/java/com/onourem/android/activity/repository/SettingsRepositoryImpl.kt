package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.SettingsRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetTermsConditionsResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import android.text.TextUtils
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.GetDeleteAccReasonsResponse
import com.onourem.android.activity.models.GetAllNotificationAlertSettingsResponse
import com.onourem.android.activity.models.UpdateNotificationAlertSettingsResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap
import javax.inject.Named

class SettingsRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    SettingsRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getTermsConditions(
        tnc: String,
        screenId: String,
        property: String
    ): LiveData<ApiResponse<GetTermsConditionsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTermsConditions"
        params["screenId"] = screenId
        if (!TextUtils.isEmpty(property)) {
            params["property"] = property
        } else {
            params["tnc"] = tnc
        }
        return apiService.getTermsConditions(basicAuth.getHeaders(), params)

        //{"tnc":"Y","screenId" : "38", "serviceName" : "getTermsConditions"}
        //{"tnc":"N","screenId" : "40", "serviceName" : "getTermsConditions" }
        //{"property":"communityGuidelines","screenId" : "40", "serviceName" : "getTermsConditions" }
    }

    override fun getTermsAndConditionAndPolicy(
        tnc: String,
        screenId: String,
        property: String
    ): LiveData<ApiResponse<GetTermsConditionsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTermsAndConditionAndPolicy"
        params["screenId"] = screenId
        if (!TextUtils.isEmpty(property)) {
            params["property"] = property
        } else {
            params["tnc"] = tnc
        }
        return apiService.getTermsAndConditionAndPolicy(basicAuth.getHeaders(), params)
    }

    override fun deleteAccount(
        userId: String,
        reason: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteAccount"
        params["screenId"] = "32"
        params["deletedReason"] = reason
        return apiService.deleteAccount(basicAuth.getHeaders(), params)
    }

    override fun userAcctDeletedReasonList(): LiveData<ApiResponse<GetDeleteAccReasonsResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "getUserAcctDeletedReasonList"
            params["screenId"] = "31"
            return apiService.getUserAcctDeletedReasonList(basicAuth.getHeaders(), params)
        }
    override fun allNotificationAlertSettings(): LiveData<ApiResponse<GetAllNotificationAlertSettingsResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "getAllNotificationAlertSettings"
            params["screenId"] = "31"
            return apiService.getAllNotificationAlertSettings(basicAuth.getHeaders(), params)
        }

    override fun updateNotificationAlertSettings(
        typeOfAlert: String,
        valueOfAlert: String
    ): LiveData<ApiResponse<UpdateNotificationAlertSettingsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateNotificationAlertSettings"
        params["screenId"] = "31"
        params["typeOfAlert"] = typeOfAlert
        params["valueOfAlert"] = valueOfAlert
        return apiService.updateNotificationAlertSettings(basicAuth.getHeaders(), params)
    }

    override fun updatePreferredNotificationTime(preferredTime: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updatePreferredNotificationTime"
        params["screenId"] = "31"
        params["notificationPreferredTime"] = preferredTime
        return apiService.updatePreferredNotificationTime(basicAuth.getHeaders(), params)
    }
}