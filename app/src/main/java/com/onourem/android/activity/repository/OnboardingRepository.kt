package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.ForgotPasswordResponse
import com.onourem.android.activity.models.SignUpRequest
import com.onourem.android.activity.models.SignUpResponse
import com.onourem.android.activity.models.IntroResponse
import com.onourem.android.activity.models.SendPushNotificationToLinkUserResponse
import com.onourem.android.activity.models.StandardResponse

interface OnboardingRepository {
    fun login(username: String, password: String): LiveData<ApiResponse<LoginResponse>>
    fun forgotPassword(email: String): LiveData<ApiResponse<ForgotPasswordResponse>>
    fun signUp(signUpRequest: SignUpRequest): LiveData<ApiResponse<SignUpResponse>>
    fun intro(): LiveData<ApiResponse<IntroResponse>>
    fun socialLogin(
        linkUserId: String,
        languageId: String,
        username: String,
        password: String
    ): LiveData<ApiResponse<LoginResponse>>

    fun registerSocial(signUpRequest: SignUpRequest): LiveData<ApiResponse<LoginResponse>>
    fun sendPushNotificationToLinkUser(
        linkUserId: String,
        screenId: String
    ): LiveData<ApiResponse<SendPushNotificationToLinkUserResponse>>

    fun resendEmailVerification(): LiveData<ApiResponse<StandardResponse>>
    fun registrationStatus(): LiveData<ApiResponse<LoginResponse>>
    fun updateDateOfBirth(dateOfBirth: String): LiveData<ApiResponse<StandardResponse>>
    fun addNetworkErrorUserInfo(
        serviceName: String,
        networkErrorCode: String
    ): LiveData<ApiResponse<StandardResponse>>
}