package com.onourem.android.activity.ui.survey.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.SurveyRepository
import com.onourem.android.activity.repository.SurveyRepositoryImpl
import javax.inject.Inject

class SurveyViewModel @Inject constructor(surveyRepository: SurveyRepositoryImpl) : ViewModel() {
    private val surveyRepository: SurveyRepository
    private val actionMutableLiveData = MutableLiveData<SurveyCOList?>()
    val surveyAnswered = MutableLiveData<SurveyCOList?>()
    fun getSurveyData(surveyId: String): LiveData<ApiResponse<AnonymousSurveyResponse>> {
        return surveyRepository.getSurveyData(surveyId)
    }

    fun getSurveyDataForGuestUser(surveyId: String): LiveData<ApiResponse<AnonymousSurveyResponse>> {
        return surveyRepository.getSurveyDataForGuestUser(surveyId)
    }

    fun getSurveyUpdate(anonymousSurveyUpdateRequest: AnonymousSurveyUpdateRequest): LiveData<ApiResponse<AnonymousSurveyUpdateResponse>> {
        return surveyRepository.getSurveyUpdate(anonymousSurveyUpdateRequest)
    }

    fun getSurveyGraphData(statisticSurveyRequest: StatisticSurveyRequest): LiveData<ApiResponse<StatisticSurveyResponse>> {
        return surveyRepository.getSurveyGraphData(statisticSurveyRequest)
    }

    fun getSurveyGraphDataForGuestUser(statisticSurveyRequest: StatisticSurveyRequest): LiveData<ApiResponse<StatisticSurveyResponse>> {
        return surveyRepository.getSurveyGraphDataForGuestUser(statisticSurveyRequest)
    }

    fun getUserProfileSurvey(userProfileSurveyRequest: UserProfileSurveyRequest): LiveData<ApiResponse<UserProfileSurveyResponse>> {
        return surveyRepository.getUserProfileSurvey(userProfileSurveyRequest)
    }

    fun getNextUserProfileSurvey(userProfileSurveyRequest: UserProfileSurveyRequest): LiveData<ApiResponse<UserProfileSurveyResponse>> {
        return surveyRepository.getNextUserProfileSurvey(userProfileSurveyRequest)
    }

    fun actionPerformed(action: SurveyCOList) {
        actionMutableLiveData.postValue(action)
    }

    fun actionConsumed() {
        actionMutableLiveData.value = null
    }

    fun getActionMutableLiveData(): LiveData<SurveyCOList?> {
        return actionMutableLiveData
    }

    fun actionSurveyAnsweredPerformed(action: SurveyCOList) {
        surveyAnswered.postValue(action)
    }

    fun actionSurveyAnsweredConsumed() {
        surveyAnswered.value = null
    }

    fun getSurveyAnswered(): LiveData<SurveyCOList?> {
        return surveyAnswered
    }

    init {
        this.surveyRepository = surveyRepository
    }
}