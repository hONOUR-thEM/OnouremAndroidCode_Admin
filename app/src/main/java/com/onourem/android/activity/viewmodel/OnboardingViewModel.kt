package com.onourem.android.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.OnboardingRepository
import com.onourem.android.activity.repository.OnboardingRepositoryImpl
import javax.inject.Inject

class OnboardingViewModel @Inject internal constructor(onboardingRepository: OnboardingRepositoryImpl) :
    ViewModel(), OnboardingRepository {
    val linkLiveData = MutableLiveData<LinkData?>()
    val checkLink = MutableLiveData<Boolean>()
    val checkUserVerification = MutableLiveData<String>()
    var onboardingRepository: OnboardingRepository
    @JvmName("getLinkLiveData1")
    fun getLinkLiveData(): MutableLiveData<LinkData?> {
        return linkLiveData
    }

    fun setLinkLiveData(data: LinkData?) {
        linkLiveData.value = data
    }

    fun setCheckLink(check: Boolean) {
        checkLink.value = check
    }
    fun setCheckUserVerification(check: String) {
        checkUserVerification.value = check
    }

    override fun intro(): LiveData<ApiResponse<IntroResponse>> {
        return onboardingRepository.intro()
    }

    override fun socialLogin(
        linkUserId: String,
        languageId: String,
        username: String,
        password: String
    ): LiveData<ApiResponse<LoginResponse>> {
        return onboardingRepository.socialLogin(linkUserId, languageId, username, password)
    }

    override fun registerSocial(signUpRequest: SignUpRequest): LiveData<ApiResponse<LoginResponse>> {
        return onboardingRepository.registerSocial(signUpRequest)
    }

    override fun sendPushNotificationToLinkUser(
        linkUserId: String,
        screenId: String
    ): LiveData<ApiResponse<SendPushNotificationToLinkUserResponse>> {
        return onboardingRepository.sendPushNotificationToLinkUser(linkUserId, screenId)
    }

    override fun resendEmailVerification(): LiveData<ApiResponse<StandardResponse>> {
        return onboardingRepository.resendEmailVerification()
    }

    override fun registrationStatus(): LiveData<ApiResponse<LoginResponse>> {
        return onboardingRepository.registrationStatus()
    }

    override fun updateDateOfBirth(dateOfBirth: String): LiveData<ApiResponse<StandardResponse>> {
        return onboardingRepository.updateDateOfBirth(dateOfBirth)
    }

    override fun login(username: String, password: String): LiveData<ApiResponse<LoginResponse>> {
        return onboardingRepository.login(username, password)
    }

    override fun forgotPassword(email: String): LiveData<ApiResponse<ForgotPasswordResponse>> {
        return onboardingRepository.forgotPassword(email)
    }

    override fun signUp(signUpRequest: SignUpRequest): LiveData<ApiResponse<SignUpResponse>> {
        return onboardingRepository.signUp(signUpRequest)
    }

    override fun addNetworkErrorUserInfo(
        serviceName: String,
        networkErrorCode: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return onboardingRepository.addNetworkErrorUserInfo(serviceName, networkErrorCode)
    }

    init {
        this.onboardingRepository = onboardingRepository
    }
}