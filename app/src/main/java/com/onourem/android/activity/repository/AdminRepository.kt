package com.onourem.android.activity.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
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
import com.onourem.android.activity.network.FileUploadProgressRequestBody
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalPostResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.create.surveys.SurveyActivityResponse

interface AdminRepository {

    fun createSystemQuestion(
        text: String,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        futureQuestionDetails: FutureQuestionDetails?,
        progressCallback: ProgressCallback?
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>

    fun getActivityCreatedByAdmin(searchDate: String?, activityType: String?): LiveData<ApiResponse<GetAdminActivityListResponse>>

    fun getNextActivityCreatedByAdmin(ids: String?, activityType : String?): LiveData<ApiResponse<GetAdminActivityListResponse>>

    fun deleteActivityCreatedByAdmin(activityId: String?): LiveData<ApiResponse<StandardResponse>>
    
    fun addSurveyInfo(
        requestAddSurvey: RequestAddSurvey?,
        uri: String?,
        progressCallback: FileUploadProgressRequestBody.ProgressCallback?
    ): LiveData<ApiResponse<UploadSurveyResponse>>

    fun notifyAllUserByAdmin(
        type: String?,
        htmlSubject: String?,
        htmlBodyContent: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun addExternalContentByAdmin(
        summary: String?,
        videoLink: String?,
        externalLink: String?,
        sourceName: String?,
        isYouTubeLink: String?,
        smallPostImage: String?,
        image: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun addMoodResponseCounsellingCardByAdmin(
        moodId: String?,
        summary: String?,
        videoLink: String?,
        externalLink: String?,
        sourceName: String?,
        isYouTubeLink: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: FileUploadProgressRequestBody.ProgressCallback?
    ): LiveData<ApiResponse<StandardResponse>>

    fun getExternalListForAdmin(): LiveData<ApiResponse<ExternalPostResponse>>

    fun getMoodResponseCounsellingDataByAdmin(): LiveData<ApiResponse<MoodInfoResponse>>

    fun getNextSurveyActivityListForAdmin(surveyIds: String?): LiveData<ApiResponse<SurveyActivityResponse>>

    fun getSurveyActivityListForAdmin(): LiveData<ApiResponse<SurveyActivityResponse>>

    fun getNextExternalListForAdmin(externalIds: String?): LiveData<ApiResponse<ExternalPostResponse>>

    fun deleteExternalActivityByAdmin(externalId: String?): LiveData<ApiResponse<StandardResponse>>

    fun updateExternalContentByAdmin(updateExternalContent: UpdateExternalContent?): LiveData<ApiResponse<StandardResponse>>

    //Post
    fun publicPostIdList(key: String?): LiveData<ApiResponse<PublicPostListResponse>>

    fun getNextPublicPostIdData(postIds: String?): LiveData<ApiResponse<PublicPostListResponse>>

    fun postPushToSky(pushStatus: String?, postId: String?): LiveData<ApiResponse<StandardResponse>>

    fun deleteAccountByAdmin(email: String?): LiveData<ApiResponse<StandardResponse>>

    fun createSubscriptionPackageByAdmin(subscription: Subscription?): LiveData<ApiResponse<StandardResponse>>

    fun createSubscriptionDiscountByAdmin(subscription: SubscriptionDiscount?): LiveData<ApiResponse<StandardResponse>>

    fun createInstitutionByAdmin(institute: Institute?): LiveData<ApiResponse<StandardResponse>>

    fun getPackageAndInstitutionCode(): LiveData<ApiResponse<GetPackageAndInstitutionCodeResponse>>

    fun generateRandomCode(number: String?): LiveData<ApiResponse<StandardResponse>>

    fun getTaskMessageCreatedByAdmin(): LiveData<ApiResponse<GetAdminActivityListResponse>>

    fun getNextTaskMessageCreatedByAdmin(taskMessageIds: String?): LiveData<ApiResponse<GetAdminActivityListResponse>>

    fun scheduleOnouremActivityByAdmin(
        timezone: String?,
        activityId: String?,
        playGroupIds: String?,
        triggerDateAndTime: String?,
        activityType: String?,
        userIds: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun searchAdminActivityById(
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<GetAdminActivityListResponse>>

    fun searchAdminActivityByDate(searchDate: String?): LiveData<ApiResponse<GetAdminActivityListResponse>>

    fun getPlaygroupCreatedByAdmin(): LiveData<ApiResponse<GetOClubSettingsResponse>>

    fun blockLinkSharingOnPlaygroupByAdmin(
        inviteLinkEnabled: String?,
        playgroupId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun blockCommentOnPlaygroupByAdmin(commentsEnabled: String?, playgroupId: String?): LiveData<ApiResponse<StandardResponse>>

    fun getUserQueryByAdmin(): LiveData<ApiResponse<GetUserQueryResponse>>

    fun getReportAbuseQueryInfoByAdmin(): LiveData<ApiResponse<GetReportResponse>>

    fun createPortalUserByAdmin(params: PortalSignUpRequest?): LiveData<ApiResponse<StandardResponse>>

    fun getPortalUserListByAdmin(instituteId: String?): LiveData<ApiResponse<PortalUsersResponse>>

    fun loadAIMoodIntoDBByAdmin(): LiveData<ApiResponse<StandardResponse>>

    fun addOclubAutoTriggerDailyActivityInBulkByAdmin(): LiveData<ApiResponse<StandardResponse>>

    fun addOclubAutoTriggerDailyActivityByAdmin(
        oclubCategoryId: String?,
        dayNumber: String?,
        dayPriority: String?,
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun updateActivityStatusByAdmin(activityId: String?, activityType: String?): LiveData<ApiResponse<StandardResponse>>

    fun updateOclubAutoTriggerDailyActivityByAdmin(
        oclubCategoryName: String?,
        id: String?,
        dayNumber: String?,
        dayPriority: String?,
        status: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun getOclubAutoTriggerDailyActivityByAdmin(oclubCategoryId: String?): LiveData<ApiResponse<GetOclubAutoTriggerDailyActivityByAdminResponse>>

    fun getPostCategoryListNew(cityId: String): LiveData<ApiResponse<GetPostCategoryListNewResponse>>


    fun uploadPost(
        uploadPostRequest: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>>

    fun getGlobalSearchResult(searchText: String): LiveData<ApiResponse<UserListResponse>>

    fun createQuestion(
        text: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>>
}


