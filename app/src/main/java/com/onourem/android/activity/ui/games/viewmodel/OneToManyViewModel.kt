package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import com.onourem.android.activity.repository.OneToManyRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.OneToManyRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetOneToManyGameActivityResResponse
import com.onourem.android.activity.models.GetUserActivityGroupResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.GetVisibilityResponse
import com.onourem.android.activity.models.UpdateVisibilityResponse

class OneToManyViewModel @Inject constructor(oneToManyRepository: OneToManyRepositoryImpl) :
    ViewModel(), OneToManyRepository {
    private val oneToManyRepository: OneToManyRepository
    val reloadRequired = MutableLiveData<String>()
    override fun getOneToManyGameActivityRes(
        activityId: String?,
        gameResIdFromOtherFlow: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        return oneToManyRepository.getOneToManyGameActivityRes(activityId, gameResIdFromOtherFlow)
    }

    override fun getQuestionResponseForGuestUser(activityId: String?): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        return oneToManyRepository.getQuestionResponseForGuestUser(activityId)
    }

    override fun getOneToManyResponseForFriendOfFriend(
        activityId: String?,
        gameIdToHighlight: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        return oneToManyRepository.getOneToManyResponseForFriendOfFriend(
            activityId,
            gameIdToHighlight
        )
    }

    override fun getRemainingOneToManyGameResponse(
        activityId: String?,
        gameIdToHighlight: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        return oneToManyRepository.getRemainingOneToManyGameResponse(activityId, gameIdToHighlight)
    }

    override fun getUserActivityGroupResponse(
        activityId: String?,
        playGroupId: String?,
        activityGameResponseId: String?,
        gameResIdFromOtherFlow: String?,
        oclubActivityId: String?
    ): LiveData<ApiResponse<GetUserActivityGroupResponse>> {
        return oneToManyRepository.getUserActivityGroupResponse(
            activityId,
            playGroupId,
            activityGameResponseId,
            gameResIdFromOtherFlow,
            oclubActivityId
        )
    }

    override fun getNextOneToManyGameActivityRes(
        activityPlayGroupId: String?,
        gameIds: String?,
        loginUserLastSeenTime: String?
    ): LiveData<ApiResponse<GetOneToManyGameActivityResResponse>> {
        return oneToManyRepository.getNextOneToManyGameActivityRes(
            activityPlayGroupId,
            gameIds,
            loginUserLastSeenTime
        )
    }

    override fun updateActivityTagStatus(
        activityId: String?,
        gameIds: String?,
        activityType: String?,
        playGroupId: String?,
        gameResponseId: String?,
        oclubActivityId: String?
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>> {
        return oneToManyRepository.updateActivityTagStatus(
            activityId,
            gameIds,
            activityType,
            playGroupId,
            gameResponseId,
            oclubActivityId
        )
    }

    override fun deleteOneToManyGameActivity(
        gameId: String?,
        playGroupId: String?,
        gameResponseId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return oneToManyRepository.deleteOneToManyGameActivity(gameId, playGroupId, gameResponseId)
    }

    override fun reportInAppropriateGame(
        gameId: String?,
        reportInAppropriateGame: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return oneToManyRepository.reportInAppropriateGame(gameId, reportInAppropriateGame)
    }

    override fun getVisibility(
        postId: String?,
        gameId: String?
    ): LiveData<ApiResponse<GetVisibilityResponse>> {
        return oneToManyRepository.getVisibility(postId, gameId)
    }

    override fun updateVisibility(
        postId: String?,
        gameId: String?,
        visibileTo: String?,
        pushToDiscover: String?
    ): LiveData<ApiResponse<UpdateVisibilityResponse>> {
        return oneToManyRepository.updateVisibility(postId, gameId, visibileTo, pushToDiscover)
    }

    override fun ignoreOneToManyGameActivity(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return oneToManyRepository.ignoreOneToManyGameActivity(
            gameId,
            gameResponseId,
            playGroupId,
            activityId
        )
    }

    override fun ignoreOClubActivityForPlaygroup(
        gameId: String?,
        gameResponseId: String?,
        playGroupId: String?,
        activityId: String?,
        oclubActivityId: String?
    ): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return oneToManyRepository.ignoreOClubActivityForPlaygroup(gameId,gameResponseId, playGroupId, activityId, oclubActivityId)
    }

    override fun updateActivityNotificationStatus(activityId: String?): LiveData<ApiResponse<StandardResponse>> {
        return oneToManyRepository.updateActivityNotificationStatus(activityId)
    }

    override fun updateActivityNotificationStatus(
        activityId: String?,
        playGroupId: String?,
        gameId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return oneToManyRepository.updateActivityNotificationStatus(activityId, playGroupId, gameId)
    }

    override fun updateResponseIrrelevant(
        gameResponseId: String?,
        playGroupId: String?,
        gameId: String?,
        participantId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return oneToManyRepository.updateResponseIrrelevant(
            gameResponseId,
            playGroupId,
            gameId,
            participantId
        )
    }

    fun setReloadRequired(str: String) {
        reloadRequired.value = str
    }

    init {
        this.oneToManyRepository = oneToManyRepository
    }
}