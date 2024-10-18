package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*

interface FriendsCircleGameRepository {
    fun verifyPhoneNumber(
        phoneNumber: String,
        countryCode: String
    ): LiveData<ApiResponse<SendOtpResponse>>

    fun getVerifiedPhoneNumbers(): LiveData<ApiResponse<GetVerifiedPhoneNumbersResponse>>

    fun deleteVerifiedPhoneNumber(phoneNumber: String): LiveData<ApiResponse<StandardResponse>>

    fun verifyOTP(
        phoneNumber: String,
        countryCode: String,
        otp: String
    ): LiveData<ApiResponse<StandardResponse>>

    fun getQualityQuestions(): LiveData<ApiResponse<GetQualityQuestionsResponse>>

    fun createQualityQuestionGame(questionMappingData: String): LiveData<ApiResponse<StandardResponse>>

    fun getTaggedUserQualityInfo(): LiveData<ApiResponse<UserQualityResponse>>

    fun updateQualitySeenStatus(): LiveData<ApiResponse<StandardResponse>>

    fun getTaggedByUserList(questionId: String): LiveData<ApiResponse<GetTaggedByUserListResponse>>

    fun addQualityQuestionVisibility(): LiveData<ApiResponse<StandardResponse>>


}