package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetTermsConditionsResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.GetDeleteAccReasonsResponse
import com.onourem.android.activity.models.GetAllNotificationAlertSettingsResponse
import com.onourem.android.activity.models.UpdateNotificationAlertSettingsResponse

interface SettingsRepository {
    fun getTermsConditions(
        tnc: String,
        screenId: String,
        property: String
    ): LiveData<ApiResponse<GetTermsConditionsResponse>>

    fun getTermsAndConditionAndPolicy(
        tnc: String,
        screenId: String,
        property: String
    ): LiveData<ApiResponse<GetTermsConditionsResponse>>

    fun deleteAccount(userId: String, reason: String): LiveData<ApiResponse<StandardResponse>>
    fun userAcctDeletedReasonList(): LiveData<ApiResponse<GetDeleteAccReasonsResponse>>
    fun allNotificationAlertSettings(): LiveData<ApiResponse<GetAllNotificationAlertSettingsResponse>>
    fun updateNotificationAlertSettings(
        typeOfAlert: String,
        valueOfAlert: String
    ): LiveData<ApiResponse<UpdateNotificationAlertSettingsResponse>>

    fun updatePreferredNotificationTime(preferredTime: String): LiveData<ApiResponse<StandardResponse>>
}