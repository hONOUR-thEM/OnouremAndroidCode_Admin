package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import com.onourem.android.activity.repository.DToOneRepositoryImpl
import com.onourem.android.activity.repository.UserListRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.DToOneRepository
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetDirectToOneGameActivityResResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.StandardResponse

class DToOneViewModel @Inject constructor(
    dToOneRepository: DToOneRepositoryImpl,
    userListRepository: UserListRepositoryImpl
) : ViewModel(), DToOneRepository {
    private val dToOneRepository: DToOneRepository
    private val userListRepository: UserListRepositoryImpl
    override fun getDirectToOneGameActivityRes(
        activityId: String,
        gameIdToHighlight: String
    ): LiveData<ApiResponse<GetDirectToOneGameActivityResResponse>> {
        return dToOneRepository.getDirectToOneGameActivityRes(activityId, gameIdToHighlight)
    }

    override fun updateActivityTagStatus(
        gameIds: String,
        activityId: String,
        activityType: String
    ): LiveData<ApiResponse<UpdateActivityTagStatusResponse>> {
        return dToOneRepository.updateActivityTagStatus(gameIds, activityId, activityType)
    }

    override fun deleteDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return dToOneRepository.deleteDirectToOneGameActivity(gameID)
    }

    override fun ignoreDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return dToOneRepository.ignoreDirectToOneGameActivity(gameID)
    }

    override fun cancelDirectToOneGameActivity(gameID: String): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return dToOneRepository.cancelDirectToOneGameActivity(gameID)
    }

    override fun updateActivityNotificationStatus(activityId: String): LiveData<ApiResponse<StandardResponse>> {
        return dToOneRepository.updateActivityNotificationStatus(activityId)
    } //    public LiveData<Resource<List<UserList>>> getLocalBlockUserList() {

    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    init {
        this.dToOneRepository = dToOneRepository
        this.userListRepository = userListRepository
    }
}