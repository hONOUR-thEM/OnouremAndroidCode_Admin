package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import com.onourem.android.activity.repository.QuestionGamesRepositoryImpl
import com.onourem.android.activity.repository.DashboardRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import com.onourem.android.activity.repository.OneToManyRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.QuestionGamesRepository
import com.onourem.android.activity.repository.OneToManyRepository
import androidx.lifecycle.MutableLiveData
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.repository.DashboardRepository
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.models.PlayGroup
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.PlayGroupsResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.UserActivityInfoResponse
import com.onourem.android.activity.models.CreatePlayGroupResponse
import com.onourem.android.activity.models.GetPlayGroupUsersResponse
import com.onourem.android.activity.models.GetUserLinkInfoResponse
import com.onourem.android.activity.models.GetAllGroupsResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.GetActivityGroupsResponse
import com.onourem.android.activity.models.CreateGameActivityRequest
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.models.UserActivityRequest
import com.onourem.android.activity.models.UserActivityResponse
import android.graphics.Bitmap
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse
import com.onourem.android.activity.models.GetQuestionFilterInfoResponse
import com.onourem.android.activity.models.NewActivityInfoResponse
import com.onourem.android.activity.models.RemainingActivityIdsResponse
import com.onourem.android.activity.models.GetInviteFriendImageInfoResponse
import com.onourem.android.activity.models.EditQuestionResponse
import com.onourem.android.activity.models.GetPlayGroupCategories
import com.onourem.android.activity.repository.OneToOneRepositoryImpl
import com.onourem.android.activity.repository.UserListRepositoryImpl
import com.onourem.android.activity.repository.OneToOneRepository
import com.onourem.android.activity.models.OneToOneGameActivityResResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.repository.TaskAndMessageRepositoryImpl
import com.onourem.android.activity.repository.TaskAndMessageRepository
import com.onourem.android.activity.models.TaskAndMessageGameActivityResResponse
import com.onourem.android.activity.models.GetUserActivityAndGameInfoResponse
import com.onourem.android.activity.models.PostsActionResponse
import com.onourem.android.activity.ui.bottomsheet.Action

class UserActionViewModel @Inject constructor() : ViewModel() {
    val actionMutableLiveData = MutableLiveData<Action<Any?>?>()
    val actionMutableLiveDataMyVocals = MutableLiveData<Action<Any?>?>()
    val actionMutableLiveDataTrending = MutableLiveData<Action<Any?>?>()
    val actionMutableLiveDataFavorite = MutableLiveData<Action<Any?>?>()
    val actionMutableLiveDataFriends = MutableLiveData<Action<Any?>?>()
    val searchActionMutableLiveData = MutableLiveData<Action<Any?>?>()
    val counsellingMutableLiveData = MutableLiveData<Action<Any?>?>()
    fun actionPerformed(action: Action<Any?>) {
        actionMutableLiveData.postValue(action)
    }

    fun actionPerformedMyVocals(action: Action<Any?>) {
        actionMutableLiveDataMyVocals.postValue(action)
    }

    fun actionPerformedTrending(action: Action<Any?>) {
        actionMutableLiveDataTrending.postValue(action)
    }

    fun actionPerformedFavorite(action: Action<Any?>) {
        actionMutableLiveDataFavorite.postValue(action)
    }

    fun actionPerformedFriends(action: Action<Any?>) {
        actionMutableLiveDataFriends.postValue(action)
    }

    fun actionConsumed() {
        actionMutableLiveData.value = null
    }

    fun getActionMutableLiveData(): LiveData<Action<Any?>?> {
        return actionMutableLiveData
    }

    fun searchActionPerformed(action: Action<Any?>) {
        searchActionMutableLiveData.postValue(action)
    }

    fun searchActionConsumed() {
        searchActionMutableLiveData.value = null
    }

    fun getSearchActionMutableLiveData(): LiveData<Action<Any?>?> {
        return searchActionMutableLiveData
    }

    fun actionConsumedMyVocals() {
        actionMutableLiveDataMyVocals.value = null
    }

    fun getActionMutableLiveDataMyVocals(): LiveData<Action<Any?>?> {
        return actionMutableLiveDataMyVocals
    }

    fun actionConsumedTrending() {
        actionMutableLiveDataTrending.value = null
    }

    fun getActionMutableLiveDataTrending(): LiveData<Action<Any?>?> {
        return actionMutableLiveDataTrending
    }

    fun actionConsumedFavorite() {
        actionMutableLiveDataFavorite.value = null
    }

    fun getActionMutableLiveDataFavorite(): LiveData<Action<Any?>?> {
        return actionMutableLiveDataFavorite
    }

    fun actionConsumedFriends() {
        actionMutableLiveDataFriends.value = null
    }

    fun getActionMutableLiveDataFriends(): LiveData<Action<Any?>?> {
        return actionMutableLiveDataFriends
    }

    fun actionPerformedCounselling(action: Action<Any?>) {
        counsellingMutableLiveData.postValue(action)
    }

    fun actionConsumedCounselling() {
        counsellingMutableLiveData.value = null
    }

    fun getCounsellingMutableLiveData(): LiveData<Action<Any?>?> {
        return counsellingMutableLiveData
    }
}