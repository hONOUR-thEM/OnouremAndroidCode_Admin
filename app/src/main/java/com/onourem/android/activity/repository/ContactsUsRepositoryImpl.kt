package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.ContactsUsRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap

class ContactsUsRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    ContactsUsRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun contactUs(
        user_text: String,
        actionToPerform: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "contactUs"
        params["actionToPerform"] = actionToPerform
        params["screenId"] = "35"
        params["user_text"] = user_text
        return apiService.contactUs(basicAuth.getHeaders(), params)
    }
}