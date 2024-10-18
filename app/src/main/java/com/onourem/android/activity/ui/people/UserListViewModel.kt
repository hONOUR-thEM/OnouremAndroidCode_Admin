package com.onourem.android.activity.ui.people

import com.onourem.android.activity.ui.games.fragments.UserRelation.Companion.getStatus
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import android.view.ViewGroup
import com.onourem.android.activity.ui.survey.BaseViewHolder
import android.view.LayoutInflater
import com.onourem.android.activity.ui.games.fragments.UserRelation
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.people.UserListAdapter
import android.widget.Filter.FilterResults
import android.annotation.SuppressLint
import com.onourem.android.activity.ui.people.UserListFOFAdapter
import javax.inject.Inject
import com.onourem.android.activity.repository.UserListRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.UserListRepository
import com.onourem.android.activity.repository.FriendsRepository
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.BlockListResponse

class UserListViewModel @Inject constructor(
    userListRepository: UserListRepositoryImpl,
    friendsRepository: FriendsRepositoryImpl
) : ViewModel() {
    private val userListRepository: UserListRepository
    private val friendsRepository: FriendsRepository
    fun getFriendSuggestionList(userId: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getFriendSuggestionList(userId)
    }

    fun getNextFriendSuggestionList(userIds: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getNextFriendSuggestionList(userIds)
    }

    fun getMyFriendList(inputUserID: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getMyFriendList(inputUserID)
    }

    fun getNextMyFriendList(userIds: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getNextMyFriendList(userIds)
    }

    fun getGlobalSearchResult(searchText: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getGlobalSearchResult(searchText)
    }

    fun getNextGlobalSearchList(userIds: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getNextGlobalSearchList(userIds)
    }

    //    public LiveData<Resource<List<UserList>>> getLocalBlockUserList() {
    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    //    public LiveData<Resource<List<UserList>>> getAllUsersBlockedByMe() {
    //        return userListRepository.getAllUsersBlockedByMe();
    //    }
    fun unBlockUser(user: UserList): LiveData<ApiResponse<StandardResponse>> {
        return userListRepository.unBlockUser(user)
    }

    fun sendFriendRequest(
        friendUserId: String,
        screenId: String,
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.sendFriendRequest(friendUserId, screenId)
    }

    fun removeFriend(
        friendUserId: String,
        screenId: String,
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.removeFriend(friendUserId, screenId)
    }

    fun cancelSentRequest(
        friendUserId: String,
        screenId: String,
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.cancelSentRequest(friendUserId, screenId)
    }

    fun cancelPendingRequest(
        friendUserId: String,
        screenId: String,
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.cancelPendingRequest(friendUserId, screenId)
    }

    fun acceptPendingRequest(
        friendUserId: String,
        screenId: String,
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.acceptPendingRequest(friendUserId, screenId)
    }

    //    public void notifyUserBlocked(UserList item) {
    //        blockedUserLiveData.setValue(item);
    //    }
    //    public void removeLocalBlockedUser(UserList item) {
    //        userListRepository.removeLocalBlockedUser(item);
    //    }
    fun getUserFriendList(userId: String,): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getUserFriendList(userId)
    }

    val blockedUserListFromServer: LiveData<ApiResponse<BlockListResponse>>
        get() = userListRepository.blockedUserListFromServer

    //    private MutableLiveData<UserList> blockedUserLiveData = new MutableLiveData<>();
    init {
        this.userListRepository = userListRepository
        this.friendsRepository = friendsRepository
    }
}