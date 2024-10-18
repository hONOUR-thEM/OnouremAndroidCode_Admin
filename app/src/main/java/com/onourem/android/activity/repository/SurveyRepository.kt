package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.AnonymousSurveyResponse
import com.onourem.android.activity.models.AnonymousSurveyUpdateRequest
import com.onourem.android.activity.models.AnonymousSurveyUpdateResponse
import com.onourem.android.activity.models.StatisticSurveyRequest
import com.onourem.android.activity.models.StatisticSurveyResponse
import com.onourem.android.activity.models.UserProfileSurveyRequest
import com.onourem.android.activity.models.UserProfileSurveyResponse

interface SurveyRepository {
    fun getSurveyData(surveyId: String): LiveData<ApiResponse<AnonymousSurveyResponse>>
    fun getSurveyUpdate(anonymousSurveyUpdateRequest: AnonymousSurveyUpdateRequest): LiveData<ApiResponse<AnonymousSurveyUpdateResponse>>
    fun getSurveyGraphData(statisticSurveyRequest: StatisticSurveyRequest): LiveData<ApiResponse<StatisticSurveyResponse>>
    fun getSurveyDataForGuestUser(surveyId: String): LiveData<ApiResponse<AnonymousSurveyResponse>>
    fun getSurveyGraphDataForGuestUser(statisticSurveyRequest: StatisticSurveyRequest): LiveData<ApiResponse<StatisticSurveyResponse>>
    fun getUserProfileSurvey(userProfileSurveyRequest: UserProfileSurveyRequest): LiveData<ApiResponse<UserProfileSurveyResponse>>
    fun getNextUserProfileSurvey(userProfileSurveyRequest: UserProfileSurveyRequest): LiveData<ApiResponse<UserProfileSurveyResponse>>
}