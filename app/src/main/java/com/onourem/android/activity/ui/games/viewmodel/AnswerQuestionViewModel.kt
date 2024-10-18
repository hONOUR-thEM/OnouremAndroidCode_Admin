package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.repository.AppreciationRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.models.UploadPostRequest
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UploadPostResponse
import android.text.TextUtils
import com.onourem.android.activity.models.ExpressionDataResponse
import com.google.gson.Gson
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap

class AnswerQuestionViewModel @Inject constructor(
    sharedPreferenceHelper: SharedPreferenceHelper,
    private val appreciationRepository: AppreciationRepositoryImpl
) : ViewModel() {
    var groupPointsMap = HashMap<String, String>()
    fun getPointsForGroup(idKey: String): String? {
        return groupPointsMap[idKey]
    }

    fun uploadPost(
        post: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>> {
        return appreciationRepository.uploadPost(post, uriImagePath, uriVideoPath, progressCallback)
    }

    init {
        val response = sharedPreferenceHelper.getString(Constants.KEY_EXPRESSIONS_RESPONSE)
        if (!TextUtils.isEmpty(response)) {
            val dataResponse = Gson().fromJson(response, ExpressionDataResponse::class.java)
            groupPointsMap = dataResponse.appColorCO?.groupPointsMap!!
        }
    }
}