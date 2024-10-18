package com.onourem.android.activity.network;

import androidx.lifecycle.LiveData;

import com.onourem.android.activity.arch.helper.ApiResponse;
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse;
import com.onourem.android.activity.models.GetOClubSettingsResponse;
import com.onourem.android.activity.models.GetOclubAutoTriggerDailyActivityByAdminResponse;
import com.onourem.android.activity.models.GetPackageAndInstitutionCodeResponse;
import com.onourem.android.activity.models.GetPostCategoryListNewResponse;
import com.onourem.android.activity.models.GetReportResponse;
import com.onourem.android.activity.models.GetUserQueryResponse;
import com.onourem.android.activity.models.MoodInfoResponse;
import com.onourem.android.activity.models.PortalSignUpRequest;
import com.onourem.android.activity.models.PortalUsersResponse;
import com.onourem.android.activity.models.PublicPostListResponse;
import com.onourem.android.activity.models.StandardResponse;
import com.onourem.android.activity.models.UpdateExternalContent;
import com.onourem.android.activity.models.UploadPostResponse;
import com.onourem.android.activity.models.UploadSurveyResponse;
import com.onourem.android.activity.models.UserListResponse;
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalPostResponse;
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse;
import com.onourem.android.activity.ui.admin.create.surveys.SurveyActivityResponse;
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse;

import java.util.Map;

import kotlin.jvm.JvmSuppressWildcards;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface AdminApiService {

    @POST("notifyAllUserByAdmin")
    LiveData<ApiResponse<StandardResponse>> notifyAllUserByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @Multipart
    @POST("addSurveyByAdmin")
    LiveData<ApiResponse<UploadSurveyResponse>> addSurveyByAdmin(@PartMap Map<String, String> headers, @Part MultipartBody.Part data, @Part MultipartBody.Part file);

    @Multipart
    @POST("addSurveyInfoByAdmin")
    LiveData<ApiResponse<UploadSurveyResponse>> addSurveyInfo(@PartMap Map<String, String> headers, @Part MultipartBody.Part data);

    @POST("addExternalContentByAdmin")
    LiveData<ApiResponse<StandardResponse>> addExternalContentByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @Multipart
    @POST("addMoodResponseCounsellingVideoByAdmin")
    LiveData<ApiResponse<StandardResponse>> addMoodResponseCounsellingVideoByAdmin(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part data, @Part MultipartBody.Part body, @Part MultipartBody.Part file);

    @POST("addMoodResponseCounsellingDataByAdmin")
    LiveData<ApiResponse<StandardResponse>> addMoodResponseCounsellingCardByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getExternalListForAdmin")
    LiveData<ApiResponse<ExternalPostResponse>> getExternalListForAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getMoodResponseCounsellingDataByAdmin")
    LiveData<ApiResponse<MoodInfoResponse>> getMoodResponseCounsellingDataByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextExternalListForAdmin")
    LiveData<ApiResponse<ExternalPostResponse>> getNextExternalListForAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getSurveyActivityListForAdmin")
    LiveData<ApiResponse<SurveyActivityResponse>> getSurveyActivityListForAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextSurveyActivityListForAdmin")
    LiveData<ApiResponse<SurveyActivityResponse>> getNextSurveyActivityListForAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("deleteExternalActivityByAdmin")
    LiveData<ApiResponse<StandardResponse>> deleteExternalActivityByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("updateExternalContentByAdmin")
    LiveData<ApiResponse<StandardResponse>> updateExternalContentByAdmin(@HeaderMap Map<String, String> headers, @Body UpdateExternalContent updateExternalContent);

    @POST("publicPostIdList")
    LiveData<ApiResponse<PublicPostListResponse>> publicPostIdList(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextPublicPostIdData")
    LiveData<ApiResponse<PublicPostListResponse>> getNextPublicPostIdData(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("postPushToSky")
    LiveData<ApiResponse<StandardResponse>> postPushToSky(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("deleteAccountByAdmin")
    LiveData<ApiResponse<StandardResponse>> deleteAccountByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextAudioInfoForAdmin")
    LiveData<ApiResponse<GetAudioInfoResponse>> getNextAudioInfoForAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getAudioInfoForAdmin")
    LiveData<ApiResponse<GetAudioInfoResponse>> getAudioInfoForAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getApprovedAudioScheduleByAdmin")
    LiveData<ApiResponse<GetAudioInfoResponse>> getApprovedAudioScheduleByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextApprovedAudioScheduleByAdmin")
    LiveData<ApiResponse<GetAudioInfoResponse>> getNextApprovedAudioScheduleByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("approveAudioRequest")
    LiveData<ApiResponse<StandardResponse>> approveAudioRequest(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);

    @POST("rejectAudioRequest")
    LiveData<ApiResponse<StandardResponse>> rejectAudioRequest(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);

    @POST("updateAudioRating")
    LiveData<ApiResponse<StandardResponse>> updateAudioRating(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);

    @POST("blackListAudio")
    LiveData<ApiResponse<StandardResponse>> blackListAudio(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);

    @POST("scheduleAudioByAdmin")
    LiveData<ApiResponse<StandardResponse>> scheduleAudioByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);

    @POST("updateScheduleAudioTimeByAdmin")
    LiveData<ApiResponse<StandardResponse>> updateScheduleAudioTimeByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);


    @POST("getActivityCreatedByAdmin")
    LiveData<ApiResponse<GetAdminActivityListResponse>> getActivityCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextActivityCreatedByAdmin")
    LiveData<ApiResponse<GetAdminActivityListResponse>> getNextActivityCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getCardCreatedByAdmin")
    LiveData<ApiResponse<GetAdminActivityListResponse>> getCardCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextCardCreatedByAdmin")
    LiveData<ApiResponse<GetAdminActivityListResponse>> getNextCardCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("deleteActivityCreatedByAdmin")
    LiveData<ApiResponse<StandardResponse>> deleteActivityCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @Multipart
    @POST("uploadQuestionDataByAdmin")
    LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> uploadQuestionDataByAdmin(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part data, @Part MultipartBody.Part body, @Part MultipartBody.Part file);

    @Multipart
    @POST("uploadTaskOrMessageByAdmin")
    LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> uploadTaskOrMessageByAdmin(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part data, @Part MultipartBody.Part body, @Part MultipartBody.Part file);

    @POST("createQuestionForOneToManyByAdmin")
    LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> createQuestionForOneToManyByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("createTaskOrMessageByAdmin")
    LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> createTaskOrMessageByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("createSubscriptionPackageByAdmin")
    LiveData<ApiResponse<StandardResponse>> createSubscriptionPackageByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("createSubscriptionDiscountByAdmin")
    LiveData<ApiResponse<StandardResponse>> createSubscriptionDiscountByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("createInstitutionByAdmin")
    LiveData<ApiResponse<StandardResponse>> createInstitutionByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getPackageAndInstitutionCode")
    LiveData<ApiResponse<GetPackageAndInstitutionCodeResponse>> getPackageAndInstitutionCode(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("generateRandomCode")
    LiveData<ApiResponse<StandardResponse>> generateRandomCode(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getTaskMessageCreatedByAdmin")
    LiveData<ApiResponse<GetAdminActivityListResponse>> getTaskMessageCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getNextTaskMessageCreatedByAdmin")
    LiveData<ApiResponse<GetAdminActivityListResponse>> getNextTaskMessageCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("scheduleOnouremActivityByAdmin")
    LiveData<ApiResponse<StandardResponse>> scheduleOnouremActivityByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("searchAdminActivityById")
    LiveData<ApiResponse<GetAdminActivityListResponse>> searchAdminActivityById(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("searchAdminActivityByDate")
    LiveData<ApiResponse<GetAdminActivityListResponse>> searchAdminActivityByDate(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getPlaygroupCreatedByAdmin")
    LiveData<ApiResponse<GetOClubSettingsResponse>> getPlaygroupCreatedByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("blockLinkSharingOnPlaygroupByAdmin")
    LiveData<ApiResponse<StandardResponse>> blockLinkSharingOnPlaygroupByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);


    @POST("blockCommentOnPlaygroupByAdmin")
    LiveData<ApiResponse<StandardResponse>> blockCommentOnPlaygroupByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getUserQueryByAdmin")
    LiveData<ApiResponse<GetUserQueryResponse>> getUserQueryByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getReportAbuseQueryInfoByAdmin")
    LiveData<ApiResponse<GetReportResponse>> getReportAbuseQueryInfoByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getPortalUserListByAdmin")
    LiveData<ApiResponse<PortalUsersResponse>> getPortalUserListByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("createPortalUserByAdmin")
    LiveData<ApiResponse<StandardResponse>> createPortalUserByAdmin(@HeaderMap Map<String, String> headers, @Body PortalSignUpRequest params);

    @POST("loadAIMoodIntoDBByAdmin")
    LiveData<ApiResponse<StandardResponse>> loadAIMoodIntoDBByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("addOclubAutoTriggerDailyActivityInBulkByAdmin")
    LiveData<ApiResponse<StandardResponse>> addOclubAutoTriggerDailyActivityInBulkByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("updateOclubAutoTriggerDailyActivityByAdmin")
    LiveData<ApiResponse<StandardResponse>> updateOclubAutoTriggerDailyActivityByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("addOclubAutoTriggerDailyActivityByAdmin")
    LiveData<ApiResponse<StandardResponse>> addOclubAutoTriggerDailyActivityByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("updateActivityStatusByAdmin")
    LiveData<ApiResponse<StandardResponse>> updateActivityStatusByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @POST("getOclubAutoTriggerDailyActivityByAdmin")
    LiveData<ApiResponse<GetOclubAutoTriggerDailyActivityByAdminResponse>> getOclubAutoTriggerDailyActivityByAdmin(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @JvmSuppressWildcards
    @POST("getPostCategoryListNew")
    LiveData<ApiResponse<GetPostCategoryListNewResponse>> getPostCategoryListNew(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @Multipart
    @JvmSuppressWildcards
    @POST("uploadPost")
    LiveData<ApiResponse<UploadPostResponse>> uploadPost(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @JvmSuppressWildcards
    @POST("uploadJsonPost")
    LiveData<ApiResponse<UploadPostResponse>> uploadJsonPost(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @Multipart
    @JvmSuppressWildcards
    @POST("uploadVideoPost")
    LiveData<ApiResponse<UploadPostResponse>> uploadVideoPost(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part data,
                                                              @Part MultipartBody.Part body,
                                                              @Part MultipartBody.Part file);

    @POST("searchUsersForWriteNew")
    @JvmSuppressWildcards
    LiveData<ApiResponse<UserListResponse>> getGlobalSearchResult(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

    @Multipart
    @JvmSuppressWildcards
    @POST("uploadQuestionData")
    LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> uploadQuestionData(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part data,
                                                              @Part MultipartBody.Part body,
                                                              @Part MultipartBody.Part file);

    @POST("createQuestionForOneToMany")
    @JvmSuppressWildcards
    LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> createQuestionForOneToMany(@HeaderMap Map<String, String> headers, @Body Map<String, Object> params);

}
