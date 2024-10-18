package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import com.onourem.android.activity.repository.UserListRepositoryImpl
import com.onourem.android.activity.repository.QuestionGamesRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.UserListRepository
import com.onourem.android.activity.repository.QuestionGamesRepository
import com.onourem.android.activity.repository.FriendsRepository
import androidx.lifecycle.MutableLiveData
import com.onourem.android.activity.models.GameActivityUpdateResponse
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.CreateGameActivityRequest
import com.onourem.android.activity.models.GetUserLinkInfoResponse
import com.onourem.android.activity.models.PlayGroupsResponse

class GamesReceiverViewModel @Inject constructor(
    userListRepository: UserListRepositoryImpl,
    questionGamesRepository: QuestionGamesRepositoryImpl,
    friendsRepository: FriendsRepositoryImpl
) : ViewModel() {
    val userListRepository: UserListRepository
    val questionGamesRepository: QuestionGamesRepository
    val friendsRepository: FriendsRepository
    val gameActivityUpdateResponseLiveData =
        MutableLiveData<GameActivityUpdateResponse?>(null)
    val friendList: LiveData<ApiResponse<UserListResponse>>
        get() = friendsRepository.getFriendList("48")

    fun createGameActivity(gameActivity: CreateGameActivityRequest): LiveData<ApiResponse<GameActivityUpdateResponse>> {
        return questionGamesRepository.createGameActivity(gameActivity)
    }

    fun getUserLinkInfo(
        linkFor: String?,
        playGroupId: String?,
        screenId: String?,
        activityText: String?
    ): LiveData<ApiResponse<GetUserLinkInfoResponse>> {
        return questionGamesRepository.getUserLinkInfo(linkFor, playGroupId, screenId, activityText)
    }

    fun getUserActivityPlayGroupsNames(activityId: String): LiveData<ApiResponse<PlayGroupsResponse>> {
        return questionGamesRepository.getUserActivityPlayGroupsNames(activityId)
    }

    fun getGameActivityUpdateResponseLiveData(): LiveData<GameActivityUpdateResponse?> {
        return gameActivityUpdateResponseLiveData
    }

    fun setGameActivityUpdateResponseLiveData(status: GameActivityUpdateResponse?) {
        gameActivityUpdateResponseLiveData.value = status
    } //    public LiveData<Resource<List<UserList>>> getLocalBlockUserList() {

    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    init {
        this.userListRepository = userListRepository
        this.questionGamesRepository = questionGamesRepository
        this.friendsRepository = friendsRepository
    }
}