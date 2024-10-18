package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class FriendsCircleGameRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    FriendsCircleGameRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun verifyPhoneNumber(
        phoneNumber: String,
        countryCode: String
    ): LiveData<ApiResponse<SendOtpResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "verifyPhoneNumber"
        params["screenId"] = "56"
        params["phoneNumber"] = phoneNumber
        params["countryCode"] = countryCode
        return apiService.verifyPhoneNumber(basicAuth.headers, params)
    }

    override fun getVerifiedPhoneNumbers(): LiveData<ApiResponse<GetVerifiedPhoneNumbersResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getVerifiedPhoneNumbers"
        params["screenId"] = "56"
        return apiService.getVerifiedPhoneNumbers(basicAuth.headers, params)
    }

    override fun deleteVerifiedPhoneNumber(phoneNumber: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteVerifiedPhoneNumber"
        params["screenId"] = "56"
        params["phoneNumber"] = phoneNumber
        return apiService.deleteVerifiedPhoneNumber(basicAuth.headers, params)
    }

    override fun verifyOTP(
        phoneNumber: String,
        countryCode: String,
        otp: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "verifyOTP"
        params["screenId"] = "56"
        params["phoneNumber"] = phoneNumber
        params["countryCode"] = countryCode
        params["otp"] = otp
        return apiService.verifyOTP(basicAuth.headers, params)
    }

    override fun getQualityQuestions(): LiveData<ApiResponse<GetQualityQuestionsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getQualityQuestions"
        params["screenId"] = "56"
        return apiService.getQualityQuestions(basicAuth.headers, params)
    }

    override fun createQualityQuestionGame(questionMappingData: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createQualityQuestionGame"
        params["screenId"] = "56"
        params["questionMappingData"] = questionMappingData
        return apiService.createQualityQuestionGame(basicAuth.headers, params)
    }

    override fun getTaggedUserQualityInfo(): LiveData<ApiResponse<UserQualityResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTaggedUserQualityInfo"
        params["screenId"] = "56"
        return apiService.getTaggedUserQualityInfo(basicAuth.headers, params)
    }

    override fun updateQualitySeenStatus(): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateQualitySeenStatus"
        params["screenId"] = "56"
        return apiService.updateQualitySeenStatus(basicAuth.headers, params)
    }

    override fun getTaggedByUserList(questionId: String): LiveData<ApiResponse<GetTaggedByUserListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTaggedByUserList"
        params["screenId"] = "56"
        params["questionId"] = questionId
        return apiService.getTaggedByUserList(basicAuth.headers, params)
    }

    override fun addQualityQuestionVisibility(): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "addQualityQuestionVisibility"
        params["screenId"] = "56"
        return apiService.addQualityQuestionVisibility(basicAuth.headers, params)
    }
}