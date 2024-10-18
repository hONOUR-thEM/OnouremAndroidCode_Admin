package com.onourem.android.activity.repository

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.LiveData
import com.google.gson.Gson
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
import com.onourem.android.activity.network.AdminApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.network.DefaultHeaders
import com.onourem.android.activity.network.FileUploadProgressRequestBody
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalPostResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.create.surveys.SurveyActivityResponse
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Constants.KEY_AUTH_TOKEN
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class AdminRepositoryImpl @Inject constructor(private val adminApiService: AdminApiService) :
    AdminRepository {

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun createSystemQuestion(
        text: String,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        futureQuestionDetails: FutureQuestionDetails?,
        progressCallback: ProgressCallback?
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> {
        //{"text": messageBase64, "templateId": "111", "image": largeBase64String, "smallPostImage": smallBase64String, "screenId" : "18", "serviceName" : "createNewQuestion"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(KEY_AUTH_TOKEN))
        if (futureQuestionDetails?.questionTo == "Task" || futureQuestionDetails?.questionTo == "Message") {
            if (!TextUtils.isEmpty(uriVideoPath)) {
                val params: MutableMap<String, Any> = HashMap()
                params["serviceName"] = "uploadTaskOrMessageByAdmin"
                params["text"] = text ?: ""
                params["timezone"] = futureQuestionDetails.timezone
                params["categoryId"] = futureQuestionDetails.taskCategory.id!!
                params["userIds"] = futureQuestionDetails.userIds
                if (futureQuestionDetails.questionTo.equals("Task", ignoreCase = true)) {
                    params["gameType"] = "Task"
                } else if (futureQuestionDetails.questionTo.equals("Message", ignoreCase = true)) {
                    params["gameType"] = "Message"
                }
                params["playGroupIds"] = futureQuestionDetails.playGroupIds
                params["triggerDateAndTime"] = futureQuestionDetails.triggerDateAndTime
                if (uriImagePath != null) {
                    params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                    params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
                } else {
                    params["image"] = ""
                    params["smallPostImage"] = ""
                }
                params["templateId"] = ""
                params["screenId"] = "18"
                if (uriVideoPath != null && uriVideoPath.isNotEmpty()) {
                    val file = File(uriVideoPath)
                    val fileUploadProgressRequestBody = FileUploadProgressRequestBody(
                        file, "video/mp4".toMediaType(),
                        progressCallback!!
                    )

                    val boundary = "===" + System.currentTimeMillis() + "==="
                    basicAuth.headers["boundary"] = boundary
                    val json = Gson().toJson(params)
                    basicAuth.headers.remove("Content-Type")
                    return adminApiService.uploadTaskOrMessageByAdmin(
                        basicAuth.getHeaders(), createFormData("questionData", json),
                        createFormData("filename", file.name),
                        createFormData("questionVideoUrl", file.name, fileUploadProgressRequestBody)
                    )
                }
            }
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "createTaskOrMessageByAdmin"
            params["text"] = text ?: ""
            params["timezone"] = futureQuestionDetails.timezone
            params["categoryId"] = futureQuestionDetails.taskCategory.id!!
            params["userIds"] = futureQuestionDetails.userIds
            if (futureQuestionDetails.questionTo.equals("Task", ignoreCase = true)) {
                params["gameType"] = "Task"
            } else if (futureQuestionDetails.questionTo.equals("Message", ignoreCase = true)) {
                params["gameType"] = "Message"
            }
            params["playGroupIds"] = futureQuestionDetails.playGroupIds
            params["triggerDateAndTime"] = futureQuestionDetails.triggerDateAndTime

            if (uriImagePath != null) {
                params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
            } else {
                params["image"] = ""
                params["smallPostImage"] = ""
            }
            params["templateId"] = ""
            params["screenId"] = "18"
            params["youtubeLink"] = futureQuestionDetails.youtubeVideoLink ?: ""
            return adminApiService.createTaskOrMessageByAdmin(basicAuth.getHeaders(), params)
        } else {
            if (!TextUtils.isEmpty(uriVideoPath)) {
                val params: MutableMap<String, Any> = HashMap()
                params["serviceName"] = "uploadQuestionDataByAdmin"
                params["text"] = text ?: ""
                params["timezone"] = futureQuestionDetails!!.timezone

                if (!futureQuestionDetails.categoryId.equals("-2", ignoreCase = true)) {
                    params["categoryId"] = futureQuestionDetails.categoryId
                } else {
                    params["categoryId"] = ""
                }

                if (futureQuestionDetails.questionTo.equals("Card", ignoreCase = true)) {
                    params["gameType"] = "Card"
                } else {
                    params["gameType"] = "1toMany"
                }
                params["playGroupIds"] = futureQuestionDetails.playGroupIds
                params["triggerDateAndTime"] = futureQuestionDetails.triggerDateAndTime

                if (uriImagePath != null) {
                    params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                    params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
                } else {
                    params["image"] = ""
                    params["smallPostImage"] = ""
                }
                params["templateId"] = ""
                params["screenId"] = "18"

                if (uriVideoPath != null && uriVideoPath.isNotEmpty()) {
                    val file = File(uriVideoPath)
                    val fileUploadProgressRequestBody = FileUploadProgressRequestBody(
                        file, "video/mp4".toMediaType(),
                        progressCallback!!
                    )

                    val boundary = "===" + System.currentTimeMillis() + "==="
                    basicAuth.headers["boundary"] = boundary
                    val json = Gson().toJson(params)
                    basicAuth.headers.remove("Content-Type")
                    return adminApiService.uploadQuestionDataByAdmin(
                        basicAuth.getHeaders(), createFormData("questionData", json),
                        createFormData("filename", file.name),
                        createFormData("questionVideoUrl", file.name, fileUploadProgressRequestBody)
                    )
                }
            }
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "createQuestionForOneToManyByAdmin"
            params["text"] = text ?: ""
            params["timezone"] = futureQuestionDetails!!.timezone
            if (!futureQuestionDetails.categoryId.equals("-2", ignoreCase = true)) {
                params["categoryId"] = futureQuestionDetails.categoryId
            } else {
                params["categoryId"] = ""
            }
            if (futureQuestionDetails.questionTo.equals("Card", ignoreCase = true)) {
                params["gameType"] = "Card"
            } else {
                params["gameType"] = "1toMany"
            }
            params["playGroupIds"] = futureQuestionDetails.playGroupIds
            params["triggerDateAndTime"] = futureQuestionDetails.triggerDateAndTime

            if (uriImagePath != null) {
                params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
            } else {
                params["image"] = ""
                params["smallPostImage"] = ""
            }
            params["templateId"] = ""
            params["screenId"] = "18"
            return adminApiService.createQuestionForOneToManyByAdmin(basicAuth.getHeaders(), params)
        }
    }

    override fun getActivityCreatedByAdmin(
        searchDate: String?,
        activityType: String?
    ): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        //body["searchDate"] = searchDate ?: ""

        return if (activityType == "Card") {
            adminApiService.getCardCreatedByAdmin(basicAuth.getHeaders(), body)
        } else {
            adminApiService.getActivityCreatedByAdmin(basicAuth.getHeaders(), body)
        }
    }

    override fun getNextActivityCreatedByAdmin(
        ids: String?,
        activityType: String?
    ): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"

        return if (activityType == "Card") {
            body["cardIds"] = ids ?: ""
            adminApiService.getNextCardCreatedByAdmin(basicAuth.getHeaders(), body)
        } else {
            body["questionIds"] = ids ?: ""
            adminApiService.getNextActivityCreatedByAdmin(basicAuth.getHeaders(), body)
        }
    }

    override fun deleteActivityCreatedByAdmin(activityId: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "36"
        body["activityId"] = activityId ?: ""
        return adminApiService.deleteActivityCreatedByAdmin(basicAuth.getHeaders(), body)
    }


    override fun addSurveyInfo(
        requestAddSurvey: RequestAddSurvey?,
        uri: String?,
        progressCallback: ProgressCallback?
    ): LiveData<ApiResponse<UploadSurveyResponse>> {
        val basicAuth: Auth = DefaultHeaders()
        if (!TextUtils.isEmpty(uri)) {
            val file = File(uri)
            val fileUploadProgressRequestBody =
                progressCallback?.let { FileUploadProgressRequestBody(file, "image/jpeg".toMediaType(), it) }


            // String boundary = "===" + System.currentTimeMillis() + "===";

            // basicAuth.getHeaders().put("boundary", boundary);
            val json = Gson().toJson(requestAddSurvey)
            basicAuth.headers.remove("Content-Type")
            basicAuth.headers.remove("source")
            basicAuth.headers.remove("appVersion")
            basicAuth.headers.remove("Authorization")
            // basicAuth.getHeaders().put("Content-Type", "multipart/form-data");
            return adminApiService.addSurveyByAdmin(
                basicAuth.headers,
                MultipartBody.Part.Companion.createFormData("postData", json),
                fileUploadProgressRequestBody?.let {
                    MultipartBody.Part.Companion.createFormData(
                        "postVideoUrl",
                        file.name,
                        it
                    )
                }
            )
        }
        val json = Gson().toJson(requestAddSurvey)
        //        String boundary = "===" + System.currentTimeMillis() + "===";
//
//        basicAuth.getHeaders().put("boundary", boundary);
        basicAuth.headers.remove("Content-Type")
        basicAuth.headers.remove("source")
        basicAuth.headers.remove("appVersion")
        basicAuth.headers.remove("Authorization")
        return adminApiService.addSurveyInfo(
            basicAuth.headers,
            MultipartBody.Part.Companion.createFormData("postData", json)
        )
    }

    override fun notifyAllUserByAdmin(
        type: String?,
        htmlSubject: String?,
        htmlBodyContent: String?
    ): LiveData<ApiResponse<StandardResponse>> {

        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "notifyAllUserByAdmin"
        params["emailType"] = type ?: ""
        params["htmlSubject"] = htmlSubject ?: ""
        params["htmlBodyContent"] = htmlBodyContent ?: ""
        return adminApiService.notifyAllUserByAdmin(basicAuth.headers, params)
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
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["summary"] = summary ?: ""
        params["videoLink"] = videoLink ?: ""
        params["externalLink"] = externalLink ?: ""
        params["sourceName"] = sourceName ?: ""
        params["isYouTubeLink"] = isYouTubeLink ?: ""
        params["smallPostImage"] = smallPostImage ?: ""
        params["image"] = image ?: ""

        return adminApiService.addExternalContentByAdmin(basicAuth.headers, params)
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
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()

        if (!TextUtils.isEmpty(uriVideoPath)) {
            params["serviceName"] = "addMoodResponseCounsellingVideoByAdmin"
            params["screenId"] = "18"
            params["description"] = summary ?: ""
//            params["videoLink"] = videoLink ?: ""
            params["externalLink"] = externalLink ?: ""
            params["sourceName"] = sourceName ?: ""
            params["isYouTubeLink"] = isYouTubeLink ?: ""
            params["moodId"] = moodId ?: ""
            if (uriImagePath != null) {
                params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
            } else {
                params["image"] = ""
                params["smallPostImage"] = ""
            }
            val file = File(uriVideoPath!!)
            val fileUploadProgressRequestBody =
                FileUploadProgressRequestBody(file, "video/mp4".toMediaType(), progressCallback!!)
            val boundary = "===" + System.currentTimeMillis() + "==="
            basicAuth.headers["boundary"] = boundary
            val json = Gson().toJson(params)
            basicAuth.headers.remove("Content-Type")
            return adminApiService.addMoodResponseCounsellingVideoByAdmin(
                basicAuth.getHeaders(), MultipartBody.Part.createFormData("moodCounsellingData", json),
                MultipartBody.Part.createFormData("filename", file.name),
                MultipartBody.Part.createFormData(
                    "moodVideoUrl",
                    file.name,
                    fileUploadProgressRequestBody
                )
            )
        }
        if (uriImagePath != null) {
            params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
            params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
        } else {
            params["image"] = ""
            params["smallPostImage"] = ""
        }


        params["serviceName"] = "addMoodResponseCounsellingDataByAdmin"
        params["text"] = summary ?: ""
        params["screenId"] = "18"
        params["description"] = summary ?: ""
//        params["videoLink"] = videoLink ?: ""
        params["externalLink"] = externalLink ?: ""
        params["sourceName"] = sourceName ?: ""
        params["isYouTubeLink"] = isYouTubeLink ?: ""
        params["moodId"] = moodId ?: ""

        return adminApiService.addMoodResponseCounsellingCardByAdmin(basicAuth.getHeaders(), params)

    }

    override fun getExternalListForAdmin(): LiveData<ApiResponse<ExternalPostResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getExternalListForAdmin"

        return adminApiService.getExternalListForAdmin(basicAuth.headers, params)
    }

    override fun getMoodResponseCounsellingDataByAdmin(): LiveData<ApiResponse<MoodInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getMoodResponseCounsellingDataByAdmin"

        return adminApiService.getMoodResponseCounsellingDataByAdmin(basicAuth.headers, params)
    }

    override fun getNextSurveyActivityListForAdmin(surveyIds: String?): LiveData<ApiResponse<SurveyActivityResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getExternalListForAdmin"
        params["surveyIds"] = surveyIds ?: ""

        return adminApiService.getNextSurveyActivityListForAdmin(basicAuth.headers, params)
    }

    override fun getSurveyActivityListForAdmin(): LiveData<ApiResponse<SurveyActivityResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getSurveyActivityListForAdmin"

        return adminApiService.getSurveyActivityListForAdmin(basicAuth.headers, params)
    }

    override fun getNextExternalListForAdmin(externalIds: String?): LiveData<ApiResponse<ExternalPostResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextExternalListForAdmin"
        params["externalIds"] = externalIds ?: ""

        return adminApiService.getNextExternalListForAdmin(basicAuth.headers, params)
    }

    override fun deleteExternalActivityByAdmin(externalId: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteExternalActivityByAdmin"
        params["externalId"] = externalId ?: ""

        return adminApiService.deleteExternalActivityByAdmin(basicAuth.headers, params)
    }

    override fun updateExternalContentByAdmin(updateExternalContent: UpdateExternalContent?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateExternalContentByAdmin"

        return adminApiService.updateExternalContentByAdmin(basicAuth.headers, updateExternalContent)
    }

    override fun publicPostIdList(key: String?): LiveData<ApiResponse<PublicPostListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "publicPostIdList"
        params["pushToDiscover"] = key ?: ""

        return adminApiService.publicPostIdList(basicAuth.headers, params)
    }

    override fun getNextPublicPostIdData(postIds: String?): LiveData<ApiResponse<PublicPostListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextPublicPostIdData"
        params["postIds"] = postIds ?: ""

        return adminApiService.getNextPublicPostIdData(basicAuth.headers, params)
    }

    override fun postPushToSky(
        pushStatus: String?,
        postId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "postPushToSky"
        params["postId"] = postId ?: ""
        params["pushStatus"] = pushStatus ?: ""

        return adminApiService.postPushToSky(basicAuth.headers, params)
    }

    override fun deleteAccountByAdmin(email: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteAccountByAdmin"
        params["emailAddress"] = email ?: ""

        return adminApiService.deleteAccountByAdmin(basicAuth.headers, params)
    }

    override fun createSubscriptionPackageByAdmin(subscription: Subscription?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createSubscriptionPackageByAdmin"
        params["name"] = subscription?.packageName ?: ""
        params["cost"] = subscription?.cost ?: ""
        params["discountCost"] = subscription?.discountedCost ?: ""
        params["durationMonth"] = subscription?.durationMonths ?: ""
        params["durationDays"] = subscription?.durationDays ?: ""
        params["freeInviteNumber"] = subscription?.freeInviteNumber ?: ""
        params["freeInvitePackageId"] = subscription?.freeInvitePackageID ?: ""
        params["description"] = subscription?.descriptionText ?: ""
        params["image"] = subscription?.image ?: ""
        params["packageCategoryId"] = subscription?.packageCategoryId ?: ""
        params["oClubRequired"] = subscription?.isOclub ?: ""
        params["oClubName"] = subscription?.oclubName ?: ""
        params["oClubCategoryId"] = subscription?.oClubCategoryId ?: ""
        params["currency"] = subscription?.currency ?: ""
        params["oClubCommentsEnable"] = subscription?.oClubCommentsEnable ?: ""
        params["oClubInviteLinkEnable"] = subscription?.oClubInviteLinkEnable ?: ""

        return adminApiService.createSubscriptionPackageByAdmin(basicAuth.headers, params)
    }

    override fun createSubscriptionDiscountByAdmin(subscription: SubscriptionDiscount?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createSubscriptionDiscountByAdmin"
        params["codeType"] = subscription?.codeType ?: ""
        params["maxUsers"] = subscription?.maxUsers ?: ""
        params["packageId"] = subscription?.packageCode ?: ""
        params["institutionId"] = subscription?.institutionCode ?: ""
        params["numberOfCode"] = subscription?.numberOfCode ?: ""
        params["codeValidTill"] = subscription?.codeValidTill ?: ""

        return adminApiService.createSubscriptionDiscountByAdmin(basicAuth.headers, params)
    }

    override fun createInstitutionByAdmin(institute: Institute?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createInstitutionByAdmin"
        params["name"] = institute?.name ?: ""
        params["city"] = institute?.city ?: ""
        params["state"] = institute?.state ?: ""
        params["country"] = institute?.country ?: ""
        params["address"] = institute?.address ?: ""
        params["officialHelpLink"] = institute?.officialHelpLink ?: ""
        params["logoLink"] = institute?.logoLink ?: ""
        params["appointmentLink"] = institute?.appointmentLink ?: ""
        params["teamPage"] = institute?.teamPage ?: ""
        params["instituteOnline"] = institute?.instituteOnline ?: ""
        params["instituteOffline"] = institute?.instituteOffline ?: ""
        params["instituteOnlinePartnerLink"] = institute?.instituteOnlinePartnerLink ?: ""
        params["instituteOnlinePartnerName"] = institute?.instituteOnlinePartnerName ?: ""
        params["onouremOnline"] = institute?.onouremOnline ?: ""
        params["instituteOnlinePartnerPhone"] = institute?.instituteOnlinePartnerPhone ?: ""
        params["instituteOnlinePartnerEmail"] = institute?.instituteOnlinePartnerEmail ?: ""
        params["instituteOnlinePartnerImage"] = institute?.instituteOnlinePartnerImage ?: ""
        params["onouremOnlinePartnerName"] = institute?.onouremOnlinePartnerName ?: ""
        params["onouremOnlinePartnerLink"] = institute?.onouremOnlinePartnerLink ?: ""
        params["onouremOnlinePartnerPhone"] = institute?.onouremOnlinePartnerPhone ?: ""
        params["onouremOnlinePartnerEmail"] = institute?.onouremOnlinePartnerEmail ?: ""
        params["onouremOnlinePartnerImage"] = institute?.onouremOnlinePartnerImage ?: ""
        params["counsellingContactNumber"] = institute?.counsellingContactNumber ?: ""
        params["timings"] = institute?.timings ?: ""
        params["pocUsers"] = institute?.pocUsers ?: ""

        return adminApiService.createInstitutionByAdmin(basicAuth.headers, params)
    }

    override fun getPackageAndInstitutionCode(): LiveData<ApiResponse<GetPackageAndInstitutionCodeResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPackageAndInstitutionCode"

        return adminApiService.getPackageAndInstitutionCode(basicAuth.headers, params)
    }

    override fun generateRandomCode(number: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "generateRandomCode"
        params["numberOfCode"] = number ?: ""

        return adminApiService.generateRandomCode(basicAuth.headers, params)
    }

    override fun getTaskMessageCreatedByAdmin(): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTaskMessageCreatedByAdmin"

        return adminApiService.getTaskMessageCreatedByAdmin(basicAuth.headers, params)

    }

    override fun getNextTaskMessageCreatedByAdmin(taskMessageIds: String?): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getTaskMessageCreatedByAdmin"
        params["taskMessageIds"] = taskMessageIds ?: ""

        return adminApiService.getNextTaskMessageCreatedByAdmin(basicAuth.headers, params)

    }

    override fun scheduleOnouremActivityByAdmin(
        timezone: String?,
        activityId: String?,
        playGroupIds: String?,
        triggerDateAndTime: String?,
        activityType: String?,
        userIds: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "scheduleOnouremActivityByAdmin"
        params["timezone"] = timezone ?: ""
        params["activityId"] = activityId ?: ""
        params["playGroupIds"] = playGroupIds ?: ""
        params["triggerDateAndTime"] = triggerDateAndTime ?: ""
        params["activityType"] = activityType ?: ""
        params["userIds"] = userIds ?: ""
        return adminApiService.scheduleOnouremActivityByAdmin(basicAuth.headers, params)

    }

    override fun searchAdminActivityById(
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["activityId"] = activityId ?: ""
        params["type"] = activityType ?: ""
        return adminApiService.searchAdminActivityById(basicAuth.headers, params)
    }

    override fun searchAdminActivityByDate(searchDate: String?): LiveData<ApiResponse<GetAdminActivityListResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["searchDate"] = searchDate ?: ""
        return adminApiService.searchAdminActivityByDate(basicAuth.headers, params)
    }

    override fun getPlaygroupCreatedByAdmin(): LiveData<ApiResponse<GetOClubSettingsResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPlaygroupCreatedByAdmin"
        return adminApiService.getPlaygroupCreatedByAdmin(basicAuth.headers, params)
    }

    override fun blockLinkSharingOnPlaygroupByAdmin(
        inviteLinkEnabled: String?,
        playgroupId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "blockLinkSharingOnPlaygroupByAdmin"
        params["inviteLinkEnabled"] = inviteLinkEnabled ?: ""
        params["playgroupId"] = playgroupId ?: ""
        return adminApiService.blockLinkSharingOnPlaygroupByAdmin(basicAuth.headers, params)
    }

    override fun blockCommentOnPlaygroupByAdmin(
        commentsEnabled: String?,
        playgroupId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "blockCommentOnPlaygroupByAdmin"
        params["commentsEnabled"] = commentsEnabled ?: ""
        params["playgroupId"] = playgroupId ?: ""
        return adminApiService.blockCommentOnPlaygroupByAdmin(basicAuth.headers, params)
    }

    override fun getUserQueryByAdmin(): LiveData<ApiResponse<GetUserQueryResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getUserQueryByAdmin"
        return adminApiService.getUserQueryByAdmin(basicAuth.headers, params)
    }

    override fun getReportAbuseQueryInfoByAdmin(): LiveData<ApiResponse<GetReportResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getReportAbuseQueryInfoByAdmin"
        return adminApiService.getReportAbuseQueryInfoByAdmin(basicAuth.headers, params)
    }

    override fun createPortalUserByAdmin(request: PortalSignUpRequest?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        return adminApiService.createPortalUserByAdmin(basicAuth.headers, request)
    }

    override fun getPortalUserListByAdmin(instituteId: String?): LiveData<ApiResponse<PortalUsersResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["institutionId"] = instituteId ?: ""
        return adminApiService.getPortalUserListByAdmin(basicAuth.headers, params)
    }

    override fun loadAIMoodIntoDBByAdmin(): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "loadAIMoodIntoDBByAdmin"
        return adminApiService.loadAIMoodIntoDBByAdmin(basicAuth.headers, params)
    }

    override fun addOclubAutoTriggerDailyActivityInBulkByAdmin(): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "addOclubAutoTriggerDailyActivityInBulkByAdmin"
        return adminApiService.addOclubAutoTriggerDailyActivityInBulkByAdmin(basicAuth.headers, params)
    }

    override fun addOclubAutoTriggerDailyActivityByAdmin(
        oclubCategoryId: String?,
        dayNumber: String?,
        dayPriority: String?,
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["oclubCategoryName"] = oclubCategoryId ?: ""
        params["dayNumber"] = dayNumber ?: ""
        params["dayPriority"] = dayPriority ?: ""
        params["activityId"] = activityId ?: ""
        params["activityType"] = activityType ?: ""

        return adminApiService.addOclubAutoTriggerDailyActivityByAdmin(basicAuth.headers, params)
    }

    override fun updateActivityStatusByAdmin(
        activityId: String?,
        activityType: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["activityId"] = activityId ?: ""
        params["activityType"] = activityType ?: ""

        return adminApiService.updateActivityStatusByAdmin(basicAuth.headers, params)
    }

    override fun updateOclubAutoTriggerDailyActivityByAdmin(
        oclubCategoryName: String?,
        id: String?,
        dayNumber: String?,
        dayPriority: String?,
        status: String?,
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["oclubCategoryName"] = id ?: ""
        params["id"] = id ?: ""
        params["dayNumber"] = dayNumber ?: ""
        params["dayPriority"] = dayPriority ?: ""
        params["status"] = status ?: ""
        return adminApiService.updateOclubAutoTriggerDailyActivityByAdmin(basicAuth.headers, params)

    }

    override fun getOclubAutoTriggerDailyActivityByAdmin(oclubCategoryId: String?): LiveData<ApiResponse<GetOclubAutoTriggerDailyActivityByAdminResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["oclubCategoryName"] = oclubCategoryId ?: ""
        return adminApiService.getOclubAutoTriggerDailyActivityByAdmin(basicAuth.headers, params)
    }

    override fun getPostCategoryListNew(cityId: String): LiveData<ApiResponse<GetPostCategoryListNewResponse>> {

        //{
        // "cityId" : "", "deviceId": deviceId,"screenId" : "18", "serviceName" : "getPostCategoryList"
        //}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = java.util.HashMap()
        params["serviceName"] = "getPostCategoryList"
        params["screenId"] = "23"
        params["deviceId"] = uniqueDeviceId!!
        params["cityId"] = cityId
        return adminApiService.getPostCategoryListNew(basicAuth.headers, params)
    }

    override fun uploadPost(
        uploadPostRequest: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        uploadPostRequest.serviceName = "uploadJsonPost"
        /*        if (!TextUtils.isEmpty(uriImagePath)) {
        } else*/if (!TextUtils.isEmpty(uriVideoPath)) {
            val file = File(uriVideoPath)
            val fileUploadProgressRequestBody =
                FileUploadProgressRequestBody(file, "video/mp4".toMediaType(), progressCallback)
            uploadPostRequest.serviceName = "uploadVideoPost"
            val boundary = "===" + System.currentTimeMillis() + "==="
            basicAuth.headers["boundary"] = boundary
            val json = Gson().toJson(uploadPostRequest)
            basicAuth.headers.remove("Content-Type")
            return adminApiService.uploadVideoPost(
                basicAuth.headers, createFormData("postData", json),
                createFormData("filename", file.name),
                createFormData(
                    "postVideoUrl",
                    file.name,
                    fileUploadProgressRequestBody
                )
            )
        }
        basicAuth.headers["Content-Type"] = "application/json"
        val objectMap: MutableMap<String, Any> = java.util.HashMap()
        val json = Gson().toJson(uploadPostRequest)
        objectMap["postData"] = json
        return adminApiService.uploadJsonPost(basicAuth.headers, objectMap)
    }

    override fun getGlobalSearchResult(searchText: String): LiveData<ApiResponse<UserListResponse>> {
        //{"searchText" : searchString, "screenId" : "17", "serviceName" : "searchUsers"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = java.util.HashMap()
        params["serviceName"] = "searchUsers"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId ?: ""
        params["searchText"] = searchText
        return adminApiService.getGlobalSearchResult(basicAuth.getHeaders(), params)
    }

    override fun createQuestion(
        text: String?,
        uriImagePath: Bitmap?,
        uriVideoPath: String?,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<CreateQuestionForOneToManyResponse>> {
        //{"text": messageBase64, "templateId": "111", "image": largeBase64String?, "smallPostImage": smallBase64String?, "screenId" : "18", "serviceName" : "createNewQuestion"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        if (!TextUtils.isEmpty(uriVideoPath)) {
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "uploadQuestionData"
            params["text"] = text ?: ""
            if (uriImagePath != null) {
                params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
                params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
            } else {
                params["image"] = ""
                params["smallPostImage"] = ""
            }
            params["templateId"] = ""
            params["screenId"] = "18"
            val file = File(uriVideoPath)
            val fileUploadProgressRequestBody =
                FileUploadProgressRequestBody(file, "video/mp4".toMediaType(), progressCallback)
            val boundary = "===" + System.currentTimeMillis() + "==="
            basicAuth.headers["boundary"] = boundary
            val json = Gson().toJson(params)
            basicAuth.headers.remove("Content-Type")
            return adminApiService.uploadQuestionData(
                basicAuth.getHeaders(), createFormData("questionData", json),
                createFormData("filename", file.name),
                createFormData(
                    "questionVideoUrl",
                    file.name,
                    fileUploadProgressRequestBody
                )
            )
        }
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "createNewQuestion"
        params["text"] = text ?: ""

//        try {
//            Objects.requireNonNull(params.put("text", Base64Utility.encodeToString?(text.getBytes(ISysConfig.APP_CHARACTER_ENCODING), Base64.NO_WRAP)))?: "";
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        if (uriImagePath != null) {
            params["image"] = AppUtilities.getBase64String(uriImagePath, 500)
            params["smallPostImage"] = AppUtilities.getBase64String(uriImagePath, 100)
        } else {
            params["image"] = ""
            params["smallPostImage"] = ""
        }
        params["templateId"] = ""
        params["screenId"] = "18"
        return adminApiService.createQuestionForOneToMany(basicAuth.getHeaders(), params)
    }

}