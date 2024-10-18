package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetInstituteCounsellingInfoResponse
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class CounsellingRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    CounsellingRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun getInstitutionCounsellingInfo(): LiveData<ApiResponse<GetInstituteCounsellingInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper?.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getInstitutionCounsellingInfo"
        return apiService.getInstitutionCounsellingInfo(basicAuth.headers, params)
    }

}