package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetOneToManyGameActivityResResponse
import com.onourem.android.activity.models.GetUserActivityGroupResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.GetVisibilityResponse
import com.onourem.android.activity.models.UpdateVisibilityResponse

interface OneToManyRepository {
    fun getOneToManyGameActivityRes(
        activityId: String?,
        gameResIdFromOtherFlow: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    fun getQuestionResponseForGuestUser(activityId: String?): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>
    fun getOneToManyResponseForFriendOfFriend(
        activityId: String?,
        gameIdToHighlight: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    fun getRemainingOneToManyGameResponse(
        activityId: String?,
        gameIdToHighlight: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    fun getUserActivityGroupResponse(
        activityId: String?,
        playGroupId: String?,
        activityGameResponseId: String?,
        gameResIdFromOtherFlow: String?,
        oclubActivityId: String?,
    ): LiveData<ApiResponse<GetUserActivityGroupResponse>>

    fun getNextOneToManyGameActivityRes(
        activityPlayGroupId: String?,
        gameIds: String?,
        loginUserLastSeenTime: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>>

    fun updateActivityTagStatus(
        activityId: String?,
        gameIds: String?,
        activityType: String?,
        playGroupId: String?,
        gameResponseId: String?,
        oclubActivityId: String?,
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>>

    fun deleteOneToManyGameActivity(
        gameId: String?,
        playGroupId: String?,
        gameResponseId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    fun reportInAppropriateGame(
        gameId: String?,
        reportInAppropriateGame: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun getVisibility(
        postId: String?,
        gameId: String?
    ): LiveData<ApiResponse<GetVisibilityResponse>>

    fun updateVisibility(
        postId: String?,
        gameId: String?,
        visibileTo: String?,
        pushToDiscover: String?
    ): LiveData<ApiResponse<UpdateVisibilityResponse>>

    fun ignoreOneToManyGameActivity(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    fun ignoreOClubActivityForPlaygroup(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?,
        oclubActivityId: String?,
    ): LiveData<ApiResponse<GameActivityUpdateResponse>>

    fun updateActivityNotificationStatus(activityId: String?): LiveData<ApiResponse<StandardResponse>>
    fun updateActivityNotificationStatus(
        activityId: String?,
        playGroupId: String?,
        gameId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun updateResponseIrrelevant(
        gameResponseId: String?,
        playGroupId: String?,
        gameId: String?,
        participantId: String?
    ): LiveData<ApiResponse<StandardResponse>>
}