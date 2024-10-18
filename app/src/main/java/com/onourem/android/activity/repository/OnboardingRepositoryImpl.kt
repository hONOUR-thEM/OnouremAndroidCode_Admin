package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.OnboardingRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.BasicAuth
import com.onourem.android.activity.models.ForgotPasswordResponse
import com.onourem.android.activity.network.DefaultHeaders
import com.onourem.android.activity.models.ForgotPasswordRequest
import com.onourem.android.activity.models.SignUpRequest
import com.onourem.android.activity.models.SignUpResponse
import com.onourem.android.activity.models.IntroResponse
import com.onourem.android.activity.models.IntroRequest
import android.text.TextUtils
import com.onourem.android.activity.models.SendPushNotificationToLinkUserResponse
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap
import javax.inject.Named

class OnboardingRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    OnboardingRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    //https://onourem.net/bnn/onouremweb/bnn/getAppDemoForUser
    //https://onourem.net/bnn/onouremweb/bnn/registerSocial
    //https://onourem.net/bnn/onouremweb/bnn/socialLogin
    //https://onourem.net/bnn/onouremweb/bnn/login
    //https://onourem.net/bnn/onouremweb/bnn/register
    override fun login(
        username: String,
        password: String
    ): LiveData<ApiResponse<LoginResponse>> {
        val basicAuth: Auth = BasicAuth(username, password)
        val body: MutableMap<String, Any> = HashMap()
        body["languageId"] = "1"
        body["screenId"] = "2"
        body["serviceName"] = "login"
        body["deviceId"] = uniqueDeviceId!!
        body["appVersion"] =
            preferenceHelper!!.getString(Constants.KEY_APP_VERSION)
        body["deviceName"] =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_NAME)
        body["deviceModel"] =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_MODEL)
        body["osVersion"] =
            preferenceHelper!!.getString(Constants.KEY_OS_VERSION)
        body["registrationId"] =
            preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
        return apiService.login(basicAuth.getHeaders(), body)
    }

    override fun forgotPassword(email: String): LiveData<ApiResponse<ForgotPasswordResponse>> {
        val basicAuth: Auth = DefaultHeaders()
        return apiService.forgetPassword(
            basicAuth.getHeaders(),
            ForgotPasswordRequest(email, uniqueDeviceId?: "")
        )
    }

    override fun signUp(signUpRequest: SignUpRequest): LiveData<ApiResponse<SignUpResponse>> {
        val basicAuth: Auth = DefaultHeaders()
        signUpRequest.registrationId =
            preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
        signUpRequest.appVersion =
            preferenceHelper!!.getString(Constants.KEY_APP_VERSION)
        signUpRequest.deviceModel =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_MODEL)
        signUpRequest.deviceName =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_NAME)
        signUpRequest.osVersion =
            preferenceHelper!!.getString(Constants.KEY_OS_VERSION)
        return apiService.register(basicAuth.getHeaders(), signUpRequest)
    }

    override fun intro(): LiveData<ApiResponse<IntroResponse>> {
        val basicAuth: Auth = DefaultHeaders()
        val introRequest = IntroRequest()
        introRequest.demoVersion = "1"
        introRequest.screenId = "1"
        introRequest.deviceId = uniqueDeviceId
        introRequest.appVersion =
            preferenceHelper!!.getString(Constants.KEY_APP_VERSION)
        introRequest.deviceModel =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_MODEL)
        introRequest.deviceName =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_NAME)
        introRequest.osVersion =
            preferenceHelper!!.getString(Constants.KEY_OS_VERSION)
        introRequest.linkUserId =
            preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
        introRequest.registrationId =
            preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
        return apiService.intro(basicAuth.getHeaders(), introRequest)
    }

    override fun socialLogin(
        linkUserId: String,
        languageId: String,
        username: String,
        password: String
    ): LiveData<ApiResponse<LoginResponse>> {
        val basicAuth: Auth = BasicAuth(username, password)
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "2"
        body["serviceName"] = "socialLogin"
        body["socialSource"] = "Facebook"
        body["languageId"] = languageId
        if (!TextUtils.isEmpty(linkUserId)) {
            body["linkUserId"] = linkUserId
        }
        body["deviceId"] = uniqueDeviceId!!
        body["appVersion"] =
            preferenceHelper!!.getString(Constants.KEY_APP_VERSION)
        body["deviceName"] =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_NAME)
        body["deviceModel"] =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_MODEL)
        body["osVersion"] =
            preferenceHelper!!.getString(Constants.KEY_OS_VERSION)
        body["registrationId"] =
            preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
        return apiService.socialLogin(basicAuth.getHeaders(), body)
    }

    override fun registerSocial(signUpRequest: SignUpRequest): LiveData<ApiResponse<LoginResponse>> {
        val basicAuth: Auth = DefaultHeaders()
        signUpRequest!!.registrationId =
            preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
        signUpRequest.appVersion =
            preferenceHelper!!.getString(Constants.KEY_APP_VERSION)
        signUpRequest.deviceModel =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_MODEL)
        signUpRequest.deviceName =
            preferenceHelper!!.getString(Constants.KEY_DEVICE_NAME)
        signUpRequest.osVersion =
            preferenceHelper!!.getString(Constants.KEY_OS_VERSION)
        return apiService.registerSocial(basicAuth.getHeaders(), signUpRequest)
    }

    override fun sendPushNotificationToLinkUser(
        linkUserId: String,
        screenId: String
    ): LiveData<ApiResponse<SendPushNotificationToLinkUserResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = screenId
        body["linkUserId"] = linkUserId
        body["serviceName"] = "sendPushNotificationToLinkUser"
        return apiService.sendPushNotificationToLinkUser(basicAuth.getHeaders(), body)
    }

    //    {"linkUserId": appDelegate.linkUserId,"screenId" : "2",  "serviceName" : "sendPushNotificationToLinkUser"}
    override fun resendEmailVerification(): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "5"
        body["serviceName"] = "resendEmailVerification"
        body["registrationId"] =
            preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
        return apiService.resendEmailVerification(basicAuth.getHeaders(), body)
    }

    override fun registrationStatus(): LiveData<ApiResponse<LoginResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val body: MutableMap<String, Any> = HashMap()
            body["screenId"] = "5"
            body["serviceName"] = "getRegistrationStatus"
            body["registrationId"] =
                preferenceHelper!!.getString(Constants.KEY_FCM_TOKEN)
            return apiService.getRegistrationStatus(basicAuth.getHeaders(), body)
        }

    override fun updateDateOfBirth(dateOfBirth: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "6"
        body["dateOfBirth"] = dateOfBirth
        body["serviceName"] = "updateDateOfBirth"
        //"dateOfBirth" : dateOfBirth,"screenId" : "6", "serviceName" : "updateDateOfBirth"
        return apiService.updateDateOfBirth(basicAuth.getHeaders(), body)
    }

    override fun addNetworkErrorUserInfo(
        serviceName: String,
        networkErrorCode: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = DefaultHeaders()
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = serviceName
        params["networkErrorCode"] = networkErrorCode
        if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN)) {
            params["userId"] =
                preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        }
        return apiService.addNetworkErrorUserInfo(basicAuth.getHeaders(), params)
    }
}