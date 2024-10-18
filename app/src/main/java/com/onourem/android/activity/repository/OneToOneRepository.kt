package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.OneToOneGameActivityResResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse

interface OneToOneRepository {
    fun getOneToOneGameActivityRes(
        activityId: String,
        gameIdToHighlight: String
    ): LiveData<ApiResponse<OneToOneGameActivityResResponse>>

    fun updateActivityTagStatus(
        gameIds: String,
        activityId: String,
        activityType: String
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>>

    fun deleteOneToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>>
    fun ignoreOneToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>>
    fun cancelOneToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>>
    fun updateActivityNotificationStatus(activityId: String): LiveData<ApiResponse<StandardResponse>>
}