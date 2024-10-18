package com.onourem.android.activity.ui.appreciation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.repository.AppreciationRepository
import com.onourem.android.activity.repository.AppreciationRepositoryImpl
import com.onourem.android.activity.repository.UserListRepository
import com.onourem.android.activity.repository.UserListRepositoryImpl
import javax.inject.Inject

class AppreciationViewModel @Inject constructor(
    appreciationRepository: AppreciationRepositoryImpl,
    friendsRepository: UserListRepositoryImpl
) : ViewModel(), AppreciationRepository {
    private val appreciationRepository: AppreciationRepository
    private val userListRepository: UserListRepository
    private val categoryMutableLiveData = MutableLiveData<CategoryList?>(null)
    override fun getPostCategoryListNew(cityId: String): LiveData<ApiResponse<GetPostCategoryListNewResponse>> {
        return appreciationRepository.getPostCategoryListNew(cityId)
    }

    override fun searchUsersForWriteNew(searchText: String): LiveData<ApiResponse<UserListResponse>> {
        return appreciationRepository.searchUsersForWriteNew(searchText)
    }

    override fun createQuestionForOneToMany(
        text: String,
        templateId: String,
        image: String,
        smallPostImage: String
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> {
        return appreciationRepository.createQuestionForOneToMany(
            text,
            templateId,
            image,
            smallPostImage
        )
    }

    override fun getUserActivityPlayGroups(activityId: String): LiveData<ApiResponse<PlayGroupsResponse>> {
        return appreciationRepository.getUserActivityPlayGroups(activityId)
    }

    override fun uploadPost(
        uploadPostRequest: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>> {
        return appreciationRepository.uploadPost(
            uploadPostRequest,
            uriImagePath,
            uriVideoPath,
            progressCallback
        )
    }

    val selectedCategory: LiveData<CategoryList?>
        get() = categoryMutableLiveData

    fun setSelectedCategory(category: CategoryList?) {
        categoryMutableLiveData.postValue(category)
    }

    fun getGlobalSearchResult(searchText: String): LiveData<ApiResponse<UserListResponse>> {
        return userListRepository.getGlobalSearchResult(searchText)
    } //    public LiveData<Resource<List<UserList>>> getLocalBlockUserList() {

    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    init {
        this.appreciationRepository = appreciationRepository
        userListRepository = friendsRepository
    }
}