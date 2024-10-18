package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetPostCategoryListNewResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse
import com.onourem.android.activity.models.PlayGroupsResponse
import com.onourem.android.activity.models.UploadPostRequest
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.models.UploadPostResponse

interface AppreciationRepository {
    fun getPostCategoryListNew(cityId: String): LiveData<ApiResponse<GetPostCategoryListNewResponse>>

    fun searchUsersForWriteNew(searchText: String): LiveData<ApiResponse<UserListResponse>>

    fun createQuestionForOneToMany(
        text: String,
        templateId: String,
        image: String,
        smallPostImage: String
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>

    fun getUserActivityPlayGroups(activityId: String): LiveData<ApiResponse<PlayGroupsResponse>>

    fun uploadPost(
        uploadPostRequest: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>>
}