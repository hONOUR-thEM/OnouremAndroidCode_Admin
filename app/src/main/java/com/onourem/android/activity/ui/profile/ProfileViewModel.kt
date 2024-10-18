package com.onourem.android.activity.ui.profile

import javax.inject.Inject
import com.onourem.android.activity.repository.ProfileRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import com.onourem.android.activity.repository.TaskAndMessageRepositoryImpl
import com.onourem.android.activity.repository.UserListRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.ProfileRepository
import com.onourem.android.activity.repository.FriendsRepository
import com.onourem.android.activity.repository.TaskAndMessageRepository
import com.onourem.android.activity.repository.UserListRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetUserProfileDataResponse
import com.onourem.android.activity.models.GetUserProfilePostsResponse
import com.onourem.android.activity.models.GetFriendIdListResponse
import com.onourem.android.activity.models.TaskAndMessageGameActivityResResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UpdateUserCoverAndProfileImageResponse
import com.onourem.android.activity.models.GetGeoList
import com.onourem.android.activity.models.UpdateProfileRequest
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.GetUserReferenceList
import com.onourem.android.activity.models.SetUserReferenceResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.PostsActionResponse

class ProfileViewModel @Inject constructor(
    profileRepository: ProfileRepositoryImpl,
    friendsRepository: FriendsRepositoryImpl,
    taskAndMessageRepository: TaskAndMessageRepositoryImpl,
    userListRepository: UserListRepositoryImpl
) : ViewModel(), ProfileRepository {
    private val friendsRepository: FriendsRepository
    private val profileRepository: ProfileRepository
    private val taskAndMessageRepository: TaskAndMessageRepository
    private val userListRepository: UserListRepository

    //    public LiveData<Resource<List<UserList>>> getLocalBlockUserList() {
    private val profileImageStateLiveData = MutableLiveData<Boolean>()
    override fun getUserProfileData(userId: String?): LiveData<ApiResponse<GetUserProfileDataResponse>> {
        return profileRepository.getUserProfileData(userId)
    }

    override fun getUserProfilePosts(userId: String?): LiveData<ApiResponse<GetUserProfilePostsResponse>> {
        return profileRepository.getUserProfilePosts(userId)
    }

    override fun getFriendIdList(profileUserId: String?): LiveData<ApiResponse<GetFriendIdListResponse>> {
        return profileRepository.getFriendIdList(profileUserId)
    }

    override fun getNextUserProfilePosts(
        userId: String?,
        postIds: String?
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>> {
        return profileRepository.getNextUserProfilePosts(userId, postIds)
    }

    fun blockUser(userList: UserList): LiveData<ApiResponse<StandardResponse>> {
        return userListRepository.blockUser(userList)
    }

    fun blockAndReportUser(userList: UserList): LiveData<ApiResponse<StandardResponse>> {
        return userListRepository.blockAndReportUser(userList)
    }

    override fun updateUserCoverAndProfileImage(
        smallImage: String?,
        largeImage: String?,
        typeOfImage: String?
    ): LiveData<ApiResponse<UpdateUserCoverAndProfileImageResponse>> {
        return profileRepository.updateUserCoverAndProfileImage(smallImage, largeImage, typeOfImage)
    }

    override val countryList: LiveData<ApiResponse<GetGeoList>>
        get() = profileRepository.countryList

    override fun getStateList(countryId: String?): LiveData<ApiResponse<GetGeoList>> {
        return profileRepository.getStateList(countryId)
    }

    override fun getCityList(stateId: String?): LiveData<ApiResponse<GetGeoList>> {
        return profileRepository.getCityList(stateId)
    }

    override fun updateProfile(updateProfileRequest: UpdateProfileRequest): LiveData<ApiResponse<LoginResponse>> {
        return profileRepository.updateProfile(updateProfileRequest)
    }

    override fun validatePassword(updateProfileRequest: UpdateProfileRequest): LiveData<ApiResponse<LoginResponse>> {
        return profileRepository.validatePassword(updateProfileRequest)
    }

    override val userReferenceList: LiveData<ApiResponse<GetUserReferenceList>>
        get() = profileRepository.userReferenceList

    override fun setUserReference(refId: String?): LiveData<ApiResponse<SetUserReferenceResponse>> {
        return profileRepository.setUserReference(refId)
    }

    fun sendFriendRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.sendFriendRequest(friendUserId, screenId)
    }

    fun removeFriend(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.removeFriend(friendUserId, screenId)
    }

    fun cancelSentRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.cancelSentRequest(friendUserId, screenId)
    }

    fun cancelPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.cancelPendingRequest(friendUserId, screenId)
    }

    fun acceptPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.acceptPendingRequest(friendUserId, screenId)
    }

    fun reportAbuse(postId: String?): LiveData<ApiResponse<PostsActionResponse>> {
        return taskAndMessageRepository.reportAbuse(postId)
    }

    fun deletePosts(postId: String?): LiveData<ApiResponse<PostsActionResponse>> {
        return taskAndMessageRepository.deletePosts(postId)
    }

    fun setProfileImageUpdated(isUpdated: Boolean) {
        profileImageStateLiveData.value = isUpdated
    }

    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    init {
        this.friendsRepository = friendsRepository
        this.profileRepository = profileRepository
        this.taskAndMessageRepository = taskAndMessageRepository
        this.userListRepository = userListRepository
    }
}