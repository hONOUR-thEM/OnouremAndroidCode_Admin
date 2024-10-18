package com.onourem.android.activity.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.SettingsRepository
import com.onourem.android.activity.repository.SettingsRepositoryImpl
import javax.inject.Inject

class SettingsViewModel @Inject constructor(settingsRepository: SettingsRepositoryImpl) :
    ViewModel(), SettingsRepository {
    private val settingsRepository: SettingsRepository
    private val actionMutableLiveData = MutableLiveData<String?>()
    override fun getTermsConditions(
        tnc: String,
        screenId: String,
        property: String
    ): LiveData<ApiResponse<GetTermsConditionsResponse>> {
        return settingsRepository.getTermsConditions(tnc, screenId, property)
    }

    override fun getTermsAndConditionAndPolicy(
        tnc: String,
        screenId: String,
        property: String
    ): LiveData<ApiResponse<GetTermsConditionsResponse>> {
        return settingsRepository.getTermsAndConditionAndPolicy(tnc, screenId, property)
    }

    override fun deleteAccount(
        userId: String,
        reason: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return settingsRepository.deleteAccount(userId, reason)
    }

    override fun userAcctDeletedReasonList(): LiveData<ApiResponse<GetDeleteAccReasonsResponse>> {
        return settingsRepository.userAcctDeletedReasonList()
    }

    override fun allNotificationAlertSettings(): LiveData<ApiResponse<GetAllNotificationAlertSettingsResponse>> {
        return settingsRepository.allNotificationAlertSettings()
    }

    override fun updateNotificationAlertSettings(
        typeOfAlert: String,
        valueOfAlert: String
    ): LiveData<ApiResponse<UpdateNotificationAlertSettingsResponse>> {
        return settingsRepository.updateNotificationAlertSettings(typeOfAlert, valueOfAlert)
    }

    override fun updatePreferredNotificationTime(preferredTime: String): LiveData<ApiResponse<StandardResponse>> {
        return settingsRepository.updatePreferredNotificationTime(preferredTime)
    }

    fun actionPerformed(action: String) {
        actionMutableLiveData.postValue(action)
    }

    fun actionConsumed() {
        actionMutableLiveData.value = null
    }

    fun getActionMutableLiveData(): LiveData<String?> {
        return actionMutableLiveData
    }

    init {
        this.settingsRepository = settingsRepository
    }
}