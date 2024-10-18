package com.onourem.android.activity.ui.admin.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.CategoryList
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse
import com.onourem.android.activity.models.FutureQuestionDetails
import com.onourem.android.activity.models.GetOClubSettingsResponse
import com.onourem.android.activity.models.GetOclubAutoTriggerDailyActivityByAdminResponse
import com.onourem.android.activity.models.GetPackageAndInstitutionCodeResponse
import com.onourem.android.activity.models.GetPostCategoryListNewResponse
import com.onourem.android.activity.models.GetReportResponse
import com.onourem.android.activity.models.GetUserQueryResponse
import com.onourem.android.activity.models.Institute
import com.onourem.android.activity.models.MoodInfoResponse
import com.onourem.android.activity.models.PortalSignUpRequest
import com.onourem.android.activity.models.PortalUsersResponse
import com.onourem.android.activity.models.PublicPostListResponse
import com.onourem.android.activity.models.RequestAddSurvey
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.Subscription
import com.onourem.android.activity.models.SubscriptionDiscount
import com.onourem.android.activity.models.UpdateExternalContent
import com.onourem.android.activity.models.UploadPostRequest
import com.onourem.android.activity.models.UploadPostResponse
import com.onourem.android.activity.models.UploadSurveyResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.repository.AdminRepository
import com.onourem.android.activity.repository.AdminRepositoryImpl
import com.onourem.android.activity.repository.QuestionGamesRepositoryImpl
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalPostResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.create.surveys.SurveyActivityResponse
import javax.inject.Inject

class AdminViewModel @Inject constructor(
    val repository: AdminRepositoryImpl,
) : ViewModel(), AdminRepository {

    private val categoryMutableLiveData = MutableLiveData<CategoryList?>(null)


    override fun createSystemQuestion(
        text: String,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        futureQuestionDetails: FutureQuestionDetails?,
        progressCallback: ProgressCallback?
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> {
        return repository.createSystemQuestion(text, uriImagePath, uriVideoPath, futureQuestionDetails, progressCallback)
    }

    override fun getActivityCreatedByAdmin(searchDate: String?, activityType: String?): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        return repository.getActivityCreatedByAdmin(searchDate, activityType)
    }

    override fun getNextActivityCreatedByAdmin(ids: String?, activityType: String?): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        return repository.getNextActivityCreatedByAdmin(ids,activityType)
    }

    override fun deleteActivityCreatedByAdmin(activityId: String?): LiveData<ApiResponse<StandardResponse>> {
        return repository.deleteActivityCreatedByAdmin(activityId)
    }


    override fun addSurveyInfo(
        requestAddSurvey: RequestAddSurvey?,
        uri: String?,
        progressCallback: ProgressCallback?
    ): LiveData<ApiResponse<UploadSurveyResponse>> {
        return repository.addSurveyInfo(requestAddSurvey, uri, progressCallback)
    }

    override fun notifyAllUserByAdmin(
        type: String?,
        htmlSubject: String?,
        htmlBodyContent: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.notifyAllUserByAdmin(type, htmlSubject, htmlBodyContent)
    }

    override fun addExternalContentByAdmin(
        summary: String?,
        videoLink: String?,
        externalLink: String?,
        sourceName: String?,
        isYouTubeLink: String?,
        smallPostImage: String?,
        image: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.addExternalContentByAdmin(
            summary, videoLink, externalLink, sourceName, isYouTubeLink, smallPostImage, image
        )
    }

    override fun addMoodResponseCounsellingCardByAdmin(
        moodId: String?,
        summary: String?,
        videoLink: String?,
        externalLink: String?,
        sourceName: String?,
        isYouTubeLink: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.addMoodResponseCounsellingCardByAdmin(
            moodId, summary, videoLink, externalLink, sourceName, isYouTubeLink, uriImagePath, uriVideoPath, progressCallback
        )
    }

    override fun getExternalListForAdmin(): LiveData<ApiResponse<ExternalPostResponse>> {
        return repository.getExternalListForAdmin()!!
    }

    override fun getMoodResponseCounsellingDataByAdmin(): LiveData<ApiResponse<MoodInfoResponse>> {
        return repository.getMoodResponseCounsellingDataByAdmin()!!
    }

    override fun getNextSurveyActivityListForAdmin(surveyIds: String?): LiveData<ApiResponse<SurveyActivityResponse>> {
        return repository.getNextSurveyActivityListForAdmin(surveyIds)
    }

    override fun getSurveyActivityListForAdmin(): LiveData<ApiResponse<SurveyActivityResponse>> {
        return repository.getSurveyActivityListForAdmin()
    }

    override fun getNextExternalListForAdmin(externalIds: String?): LiveData<ApiResponse<ExternalPostResponse>> {
        return repository.getNextExternalListForAdmin(externalIds)!!
    }

    override fun deleteExternalActivityByAdmin(externalId: String?): LiveData<ApiResponse<StandardResponse>> {
        return repository.deleteExternalActivityByAdmin(externalId)
    }

    override fun updateExternalContentByAdmin(updateExternalContent: UpdateExternalContent?): LiveData<ApiResponse<StandardResponse>> {
        return repository.updateExternalContentByAdmin(updateExternalContent)!!
    }

    override fun publicPostIdList(key: String?): LiveData<ApiResponse<PublicPostListResponse>> {
        return repository.publicPostIdList(key)
    }

    override fun getNextPublicPostIdData(postIds: String?): LiveData<ApiResponse<PublicPostListResponse>> {
        return repository.getNextPublicPostIdData(postIds)!!
    }

    override fun postPushToSky(pushStatus: String?, postId: String?): LiveData<ApiResponse<StandardResponse>> {
        return repository.postPushToSky(pushStatus, postId)
    }

    override fun deleteAccountByAdmin(email: String?): LiveData<ApiResponse<StandardResponse>> {
        return repository.deleteAccountByAdmin(email)
    }

    override fun createSubscriptionPackageByAdmin(subscription: Subscription?): LiveData<ApiResponse<StandardResponse>> {
        return repository.createSubscriptionPackageByAdmin(subscription)
    }

    override fun createSubscriptionDiscountByAdmin(subscription: SubscriptionDiscount?): LiveData<ApiResponse<StandardResponse>> {
        return repository.createSubscriptionDiscountByAdmin(subscription)
    }

    override fun createInstitutionByAdmin(institute: Institute?): LiveData<ApiResponse<StandardResponse>> {
        return repository.createInstitutionByAdmin(institute)
    }

    override fun getPackageAndInstitutionCode(): LiveData<ApiResponse<GetPackageAndInstitutionCodeResponse>> {
        return repository.getPackageAndInstitutionCode()
    }

    override fun generateRandomCode(number: String?): LiveData<ApiResponse<StandardResponse>> {
        return repository.generateRandomCode(number)
    }

    override fun getTaskMessageCreatedByAdmin(): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        return repository.getTaskMessageCreatedByAdmin()
    }

    override fun getNextTaskMessageCreatedByAdmin(taskMessageIds: String?): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        return repository.getNextTaskMessageCreatedByAdmin(taskMessageIds)
    }

    override fun scheduleOnouremActivityByAdmin(
        timezone: String?,
        activityId: String?,
        playGroupIds: String?,
        triggerDateAndTime: String?,
        activityType: String?,
        userIds: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.scheduleOnouremActivityByAdmin(
            timezone, activityId, playGroupIds, triggerDateAndTime, activityType, userIds
        )
    }

    override fun searchAdminActivityById(
        activityId: String?, activityType: String?
    ): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        return repository.searchAdminActivityById(activityId, activityType)
    }

    override fun searchAdminActivityByDate(searchDate: String?): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        return repository.searchAdminActivityByDate(searchDate)
    }

    override fun getPlaygroupCreatedByAdmin(): LiveData<ApiResponse<GetOClubSettingsResponse>> {
        return repository.getPlaygroupCreatedByAdmin()
    }

    override fun blockLinkSharingOnPlaygroupByAdmin(
        inviteLinkEnabled: String?, playgroupId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.blockLinkSharingOnPlaygroupByAdmin(inviteLinkEnabled, playgroupId)
    }

    override fun blockCommentOnPlaygroupByAdmin(
        commentsEnabled: String?, playgroupId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.blockCommentOnPlaygroupByAdmin(commentsEnabled, playgroupId)
    }

    override fun getUserQueryByAdmin(): LiveData<ApiResponse<GetUserQueryResponse>> {
        return repository.getUserQueryByAdmin()
    }

    override fun getReportAbuseQueryInfoByAdmin(): LiveData<ApiResponse<GetReportResponse>> {
        return repository.getReportAbuseQueryInfoByAdmin()
    }

    override fun createPortalUserByAdmin(params: PortalSignUpRequest?): LiveData<ApiResponse<StandardResponse>> {
        return repository.createPortalUserByAdmin(params!!)
    }

    override fun getPortalUserListByAdmin(instituteId: String?): LiveData<ApiResponse<PortalUsersResponse>> {
        return repository.getPortalUserListByAdmin(instituteId)
    }

    override fun loadAIMoodIntoDBByAdmin(): LiveData<ApiResponse<StandardResponse>> {
        return repository.loadAIMoodIntoDBByAdmin()
    }

    override fun addOclubAutoTriggerDailyActivityInBulkByAdmin(): LiveData<ApiResponse<StandardResponse>> {
        return repository.addOclubAutoTriggerDailyActivityInBulkByAdmin()
    }

    override fun addOclubAutoTriggerDailyActivityByAdmin(
        oclubCategoryId: String?,
        dayNumber: String?,
        dayPriority: String?,
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.addOclubAutoTriggerDailyActivityByAdmin(oclubCategoryId, dayNumber, dayPriority, activityId, activityType)
    }

    override fun updateActivityStatusByAdmin(
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.updateActivityStatusByAdmin(activityId, activityType)

    }

    override fun updateOclubAutoTriggerDailyActivityByAdmin(
        oclubCategoryName: String?,
        id: String?,
        dayNumber: String?,
        dayPriority: String?,
        status: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.updateOclubAutoTriggerDailyActivityByAdmin(oclubCategoryName, id, dayNumber, dayPriority, status)
    }

    override fun getOclubAutoTriggerDailyActivityByAdmin(oclubCategoryId: String?): LiveData<ApiResponse<GetOclubAutoTriggerDailyActivityByAdminResponse>> {
        return repository.getOclubAutoTriggerDailyActivityByAdmin(oclubCategoryId)
    }

    val selectedCategory: LiveData<CategoryList?>
        get() = categoryMutableLiveData

    fun setSelectedCategory(category: CategoryList?) {
        categoryMutableLiveData.postValue(category)
    }

    override fun getPostCategoryListNew(cityId: String): LiveData<ApiResponse<GetPostCategoryListNewResponse>> {
        return repository.getPostCategoryListNew(cityId)
    }

    override fun uploadPost(
        uploadPostRequest: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>> {
        return repository.uploadPost(
            uploadPostRequest,
            uriImagePath,
            uriVideoPath,
            progressCallback
        )
    }

    override fun getGlobalSearchResult(searchText: String): LiveData<ApiResponse<UserListResponse>> {
        return repository.getGlobalSearchResult(searchText)
    }

    override fun createQuestion(
        text: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>  {
        return repository.createQuestion(
            text,
            uriImagePath,
            uriVideoPath,
            progressCallback
        )
    }

}