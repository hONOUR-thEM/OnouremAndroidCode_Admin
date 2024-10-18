package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.SurveyRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.AnonymousSurveyResponse
import com.onourem.android.activity.models.AnonymousSurveyRequest
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.AnonymousSurveyUpdateRequest
import com.onourem.android.activity.models.AnonymousSurveyUpdateResponse
import com.onourem.android.activity.models.StatisticSurveyRequest
import com.onourem.android.activity.models.StatisticSurveyResponse
import com.onourem.android.activity.models.UserProfileSurveyRequest
import com.onourem.android.activity.models.UserProfileSurveyResponse
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Named

class SurveyRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    SurveyRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    override fun getSurveyData(surveyId: String): LiveData<ApiResponse<AnonymousSurveyResponse>> {
        val anonymousSurveyRequest = AnonymousSurveyRequest()
        anonymousSurveyRequest.surveyId = surveyId
        anonymousSurveyRequest.screenId = "9"
        anonymousSurveyRequest.serviceName = "getSurveyData"
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getSurveyData(basicAuth.getHeaders(), anonymousSurveyRequest)
    }

    override fun getSurveyUpdate(anonymousSurveyUpdateRequest: AnonymousSurveyUpdateRequest): LiveData<ApiResponse<AnonymousSurveyUpdateResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getSurveyUpdate(basicAuth.getHeaders(), anonymousSurveyUpdateRequest)
    }

    override fun getSurveyGraphData(statisticSurveyRequest: StatisticSurveyRequest): LiveData<ApiResponse<StatisticSurveyResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getSurveyGraphData(basicAuth.getHeaders(), statisticSurveyRequest)
    }

    override fun getSurveyDataForGuestUser(surveyId: String): LiveData<ApiResponse<AnonymousSurveyResponse>> {
        val anonymousSurveyRequest = AnonymousSurveyRequest()
        anonymousSurveyRequest.surveyId = surveyId
        anonymousSurveyRequest.screenId = "9"
        anonymousSurveyRequest.serviceName = "getSurveyDataForGuestUser"
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getSurveyDataForGuestUser(basicAuth.getHeaders(), anonymousSurveyRequest)
    }

    override fun getSurveyGraphDataForGuestUser(statisticSurveyRequest: StatisticSurveyRequest): LiveData<ApiResponse<StatisticSurveyResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getSurveyGraphDataForGuestUser(
            basicAuth.getHeaders(),
            statisticSurveyRequest
        )
    }

    override fun getUserProfileSurvey(userProfileSurveyRequest: UserProfileSurveyRequest): LiveData<ApiResponse<UserProfileSurveyResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getUserProfileSurey(basicAuth.getHeaders(), userProfileSurveyRequest)
    }

    override fun getNextUserProfileSurvey(userProfileSurveyRequest: UserProfileSurveyRequest): LiveData<ApiResponse<UserProfileSurveyResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        return apiService.getNextUserProfileSurey(basicAuth.getHeaders(), userProfileSurveyRequest)
    }
}