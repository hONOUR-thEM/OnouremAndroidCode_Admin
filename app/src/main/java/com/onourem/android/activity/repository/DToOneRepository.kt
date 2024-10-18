package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetDirectToOneGameActivityResResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse

interface DToOneRepository {
    fun getDirectToOneGameActivityRes(
        activityId: String,
        gameIdToHighlight: String
    ): LiveData<ApiResponse<GetDirectToOneGameActivityResResponse>>

    fun updateActivityTagStatus(
        gameIds: String,
        activityId: String,
        activityType: String
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>> 

    fun deleteDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> 
    fun ignoreDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> 
    fun cancelDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> 
    fun updateActivityNotificationStatus(activityId: String): LiveData<ApiResponse<StandardResponse>> 
}