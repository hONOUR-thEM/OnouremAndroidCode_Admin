package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.TaskAndMessageGameActivityResResponse
import com.onourem.android.activity.models.GetUserActivityAndGameInfoResponse
import com.onourem.android.activity.models.PostsActionResponse

interface TaskAndMessageRepository {
    fun getTaskAndMessageGameActivityRes(activityId: String?): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>>

    fun getTaskMessageForPlaygroup(activityId: String?, oclubActivityId: String?, playgroupId: String?): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>>

    fun getUserActivityAndGameInfo(
        activityId: String?,
        profileUserId: String?
    ): LiveData<ApiResponse<GetUserActivityAndGameInfoResponse>>

    fun reportAbuse(postId: String?): LiveData<ApiResponse<PostsActionResponse>>
    fun deletePosts(postId: String?): LiveData<ApiResponse<PostsActionResponse>>
}