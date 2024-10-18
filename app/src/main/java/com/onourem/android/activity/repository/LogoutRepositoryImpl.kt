package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.LogoutResponse
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class LogoutRepositoryImpl @Inject constructor(var apiService: ApiService) : LogoutRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    override fun logout(): LiveData<ApiResponse<LogoutResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "15"
        body["serviceName"] = "logout"
        body["deviceId"] = uniqueDeviceId!!
        return apiService.logout(basicAuth.headers, body)
    }
}