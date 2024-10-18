package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetPostCategoryListNewResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse
import com.onourem.android.activity.models.PlayGroupsResponse
import com.onourem.android.activity.models.UploadPostRequest
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.models.UploadPostResponse
import android.text.TextUtils
import com.onourem.android.activity.network.FileUploadProgressRequestBody
import com.google.gson.Gson
import com.onourem.android.activity.ui.utils.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part.Companion.createFormData
import java.io.File
import java.util.HashMap
import javax.inject.Named

class AppreciationRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    AppreciationRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getPostCategoryListNew(cityId: String): LiveData<ApiResponse<GetPostCategoryListNewResponse>> {

        //{
        // "cityId" : "", "deviceId": deviceId,"screenId" : "18", "serviceName" : "getPostCategoryList"
        //}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPostCategoryList"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["cityId"] = cityId
        return apiService.getPostCategoryListNew(basicAuth.headers, params)
    }

    override fun searchUsersForWriteNew(searchText: String): LiveData<ApiResponse<UserListResponse>> {
        TODO("Not yet implemented")
    }

    override fun createQuestionForOneToMany(
        text: String,
        templateId: String,
        image: String,
        smallPostImage: String
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> {
        TODO("Not yet implemented")
    }

    override fun getUserActivityPlayGroups(activityId: String): LiveData<ApiResponse<PlayGroupsResponse>> {
        TODO("Not yet implemented")
    }


    override fun uploadPost(
        uploadPostRequest: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        uploadPostRequest.serviceName = "uploadJsonPost"
        /*        if (!TextUtils.isEmpty(uriImagePath)) {
        } else*/if (!TextUtils.isEmpty(uriVideoPath)) {
            val file = File(uriVideoPath)
            val fileUploadProgressRequestBody =
                FileUploadProgressRequestBody(file, "video/mp4".toMediaType(), progressCallback)
            uploadPostRequest.serviceName = "uploadVideoPost"
            val boundary = "===" + System.currentTimeMillis() + "==="
            basicAuth.headers["boundary"] = boundary
            val json = Gson().toJson(uploadPostRequest)
            basicAuth.headers.remove("Content-Type")
            return apiService.uploadVideoPost(
                basicAuth.headers, createFormData("postData", json),
                createFormData("filename", file.name),
                createFormData(
                    "postVideoUrl",
                    file.name,
                    fileUploadProgressRequestBody
                )
            )
        }
        basicAuth.headers["Content-Type"] = "application/json"
        val objectMap: MutableMap<String, Any> = HashMap()
        val json = Gson().toJson(uploadPostRequest)
        objectMap["postData"] = json
        return apiService.uploadJsonPost(basicAuth.headers, objectMap)
    }
}