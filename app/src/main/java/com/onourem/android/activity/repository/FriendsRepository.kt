package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.UserListResponse

interface FriendsRepository {
    fun sendFriendRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    fun removeFriend(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    fun cancelSentRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    fun cancelPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    fun acceptPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>>

    fun getFriendList(screenId: String?): LiveData<ApiResponse<UserListResponse>>
}