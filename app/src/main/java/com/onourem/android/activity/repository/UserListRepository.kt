package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.BlockListResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.StandardResponse

interface UserListRepository {
    fun getNextFriendSuggestionList(userIds: String): LiveData<ApiResponse<UserListResponse>>
    fun getMyFriendList(inputUserID: String): LiveData<ApiResponse<UserListResponse>>
    fun getFriendSuggestionList(userId: String): LiveData<ApiResponse<UserListResponse>>
    fun getUserFriendList(inputUserID: String): LiveData<ApiResponse<UserListResponse>>
    fun getNextMyFriendList(userIds: String): LiveData<ApiResponse<UserListResponse>>
    fun getGlobalSearchResult(searchText: String): LiveData<ApiResponse<UserListResponse>>
    fun getNextGlobalSearchList(userIds: String): LiveData<ApiResponse<UserListResponse>>
    val blockedUserListFromServer: LiveData<ApiResponse<BlockListResponse>>

    //    LiveData<Resource<List<UserList>>> getAllLocalBlockedUsers();
    fun blockUser(user: UserList): LiveData<ApiResponse<StandardResponse>>
    fun blockAndReportUser(user: UserList): LiveData<ApiResponse<StandardResponse>>
    fun unBlockUser(user: UserList): LiveData<ApiResponse<StandardResponse>> //    LiveData<Resource<List<UserList>>> getAllUsersBlockedByMe();
    //    void removeLocalBlockedUser(UserList item);
}