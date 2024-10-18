package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetUserProfileDataResponse
import com.onourem.android.activity.models.GetUserProfilePostsResponse
import com.onourem.android.activity.models.GetFriendIdListResponse
import com.onourem.android.activity.models.TaskAndMessageGameActivityResResponse
import com.onourem.android.activity.models.UpdateUserCoverAndProfileImageResponse
import com.onourem.android.activity.models.GetGeoList
import com.onourem.android.activity.models.UpdateProfileRequest
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.GetUserReferenceList
import com.onourem.android.activity.models.SetUserReferenceResponse

interface ProfileRepository {
    fun getUserProfileData(userId: String?): LiveData<ApiResponse<GetUserProfileDataResponse>>
    fun getUserProfilePosts(userId: String?): LiveData<ApiResponse<GetUserProfilePostsResponse>>
    fun getFriendIdList(profileUserId: String?): LiveData<ApiResponse<GetFriendIdListResponse>>
    fun getNextUserProfilePosts(
        userId: String?,
        postIds: String?
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>>

    fun updateUserCoverAndProfileImage(
        smallImage: String?,
        largeImage: String?,
        typeOfImage: String?
    ): LiveData<ApiResponse<UpdateUserCoverAndProfileImageResponse>>

    val countryList: LiveData<ApiResponse<GetGeoList>>
    fun getStateList(countryId: String?): LiveData<ApiResponse<GetGeoList>>
    fun getCityList(stateId: String?): LiveData<ApiResponse<GetGeoList>>
    fun updateProfile(updateProfileRequest: UpdateProfileRequest): LiveData<ApiResponse<LoginResponse>>
    fun validatePassword(updateProfileRequest: UpdateProfileRequest): LiveData<ApiResponse<LoginResponse>>
    val userReferenceList: LiveData<ApiResponse<GetUserReferenceList>>
    fun setUserReference(refId: String?): LiveData<ApiResponse<SetUserReferenceResponse>>
}