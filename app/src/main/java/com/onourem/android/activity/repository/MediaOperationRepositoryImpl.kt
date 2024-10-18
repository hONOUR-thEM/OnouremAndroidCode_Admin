package com.onourem.android.activity.repository


import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.arch.helper.AppExecutors
import com.onourem.android.activity.dao.AudioPlayBackHistoryDao
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.network.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Constants.KEY_AUTH_TOKEN
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part.Companion.createFormData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class MediaOperationRepositoryImpl @Inject constructor(
    val audioPlayBackHistoryDao: AudioPlayBackHistoryDao,
    val appExecutors: AppExecutors,
    var apiService: ApiService,
    var apiServiceAdmin: AdminApiService
) : MediaOperationRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun audioCategory(): LiveData<ApiResponse<GetAudioCategoryResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val body: MutableMap<String, Any> = HashMap()
            body["screenId"] = "51"
            body["serviceName"] = "getAudioCategory"
            return apiService.getAudioCategory(basicAuth.getHeaders(), body)
        }

    override fun getAudioInfo(
        service: String,
        linkUserId: String,
        audioIdFromNotification: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "53"
        body["serviceName"] = service
        if (!audioIdFromNotification.equals("", ignoreCase = true)) {
            body["linkUserId"] = ""
        } else {
            body["linkUserId"] = linkUserId
        }
        return when (service) {
            "getAudioInfo" -> {
                body["audioIdFromNotification"] = audioIdFromNotification
                apiService.getAudioInfo(basicAuth.getHeaders(), body)
            }
            "getFriendAudioInfo" -> apiService.getFriendAudioInfo(basicAuth.getHeaders(), body)
            "getLoginUserAudioInfo" -> apiService.getLoginUserAudioInfo(
                basicAuth.getHeaders(),
                body
            )
            "getAudioInfoLikedByLoginUser" -> apiService.getAudioInfoLikedByLoginUser(
                basicAuth.getHeaders(),
                body
            )
            "getAudioInfoForGuestUser" -> apiService.getAudioInfoForGuestUser(
                basicAuth.getHeaders(),
                body
            )
            else -> {
                body["audioIdFromNotification"] = audioIdFromNotification
                apiService.getAudioInfo(basicAuth.getHeaders(), body)
            }
        }
    }

    override fun getNextAudioInfo(audioIds: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "53"
        body["serviceName"] = "getNextAudioInfo"
        body["audioIds"] = audioIds
        return apiService.getNextAudioInfo(basicAuth.getHeaders(), body)
    }

    override fun likeAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["serviceName"] = "likeAudio"
        return apiService.likeAudio(basicAuth.getHeaders(), body)
    }

    override fun unLikeAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["serviceName"] = "unlikeAudio"
        return apiService.unLikeAudio(basicAuth.getHeaders(), body)
    }

    override fun updateAudioVisibility(
        privacyIds: String,
        audioId: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["privacyId"] = privacyIds
        body["serviceName"] = "updateAudioVisibility"
        return apiService.updateAudioVisibility(basicAuth.getHeaders(), body)
    }

    override fun addAudioInfoHistory(audioIds: String, audioDurations: String, creatorId: String) {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioIds"] = audioIds
        body["screenId"] = "53"
        body["audioDurations"] = audioDurations
        body["creatorId"] = creatorId
        body["serviceName"] = "addAudioInfoHistory"
        val call = apiService.addAudioInfoHistory(basicAuth.getHeaders(), body)
        call.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    //Log.d("###", "onResponse: " + response.body());
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //Log.d("###", "onFailure: " + t.getLocalizedMessage());
            }
        })
    }

    override fun deleteAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["serviceName"] = "deleteAudio"
        return apiService.deleteAudio(basicAuth.getHeaders(), body)
    }

    override fun followAudioCreator(audioCreator: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioCreator"] = audioCreator
        body["screenId"] = "53"
        body["serviceName"] = "followAudioCreator"
        return apiService.followAudioCreator(basicAuth.getHeaders(), body)
    }

    override fun unfollowAudioCreator(audioCreator: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioCreator"] = audioCreator
        body["screenId"] = "53"
        body["serviceName"] = "unfollowAudioCreator"
        return apiService.unfollowAudioCreator(basicAuth.getHeaders(), body)
    }

    override fun reportInappropriateAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["serviceName"] = "reportInappropriateAudio"
        return apiService.reportInappropriateAudio(basicAuth.getHeaders(), body)
    }

    override fun updateAudioTitle(
        audioId: String,
        title: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["title"] = title
        body["screenId"] = "53"
        body["serviceName"] = "updateAudioTitle"
        return apiService.updateAudioTitle(basicAuth.getHeaders(), body)
    }

    override fun getAudioForOtherUser(otherUserId: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["otherUserId"] = otherUserId
        body["screenId"] = "53"
        body["serviceName"] = "getAudioForOtherUser"
        return apiService.getAudioForOtherUser(basicAuth.getHeaders(), body)
    }

    override fun searchAudioByTitle(title: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["titleText"] = title
        body["screenId"] = "53"
        body["serviceName"] = "searchAudioByTitle"
        return apiService.searchAudioByTitle(basicAuth.getHeaders(), body)
    }

    override fun uploadAudioInfo(
        title: String,
        uriAudioPath: String,
        creatorId: String,
        audioCategoryId: String,
        audioDuration: String,
        languageId: String,
        privacyId: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "uploadAudioInfo"
        params["screenId"] = "52"
        params["creatorId"] = creatorId
        params["audioCategoryId"] = audioCategoryId
        params["audioDuration"] = audioDuration
        params["languageId"] = languageId
        params["privacyId"] = privacyId
        params["title"] = title

        val file = File(uriAudioPath)
        val fileUploadProgressRequestBody =
            FileUploadProgressRequestBody(file, "audio/*".toMediaType(), progressCallback)
        val boundary = "===" + System.currentTimeMillis() + "==="
        basicAuth.headers["boundary"] = boundary
        val json = Gson().toJson(params)
        basicAuth.headers.remove("Content-Type")

        return apiService.uploadAudioInfo(
            basicAuth.getHeaders(), createFormData("audioData", json),
            createFormData("filename", file.name),
            createFormData("audioUrl", file.name, fileUploadProgressRequestBody)
        )
    }

    override fun getAudioInfoForAdmin(service: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "53"
        body["serviceName"] = "getAudioInfoForAdmin"
        body["audioStatus"] = service
        body["userFor"] = userFor
        body["audioForUserId"] = audioForUserId
        return apiServiceAdmin.getAudioInfoForAdmin(basicAuth.getHeaders(), body)
    }

    override fun blackListAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["serviceName"] = "blackListAudio"
        return apiServiceAdmin.blackListAudio(basicAuth.getHeaders(), body)
    }


    override fun getApprovedAudioScheduleByAdmin(
        audioFor: String,
        date: String,
        audioForUserId: String,
        userFor: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "53"
        body["serviceName"] = "getApprovedAudioScheduleByAdmin"
        body["audioFor"] = audioFor
        body["scheduledDate"] = date
        body["userFor"] = userFor
        body["audioForUserId"] = audioForUserId
        return apiServiceAdmin.getApprovedAudioScheduleByAdmin(basicAuth.getHeaders(), body)
    }

    override fun getNextApprovedAudioScheduleByAdmin(
        audioIds: String,
        audioForUserId: String,
        userFor: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "53"
        body["serviceName"] = "getNextApprovedAudioScheduleByAdmin"
        body["audioIds"] = audioIds
        body["userFor"] = userFor
        body["audioForUserId"] = audioForUserId
        return apiServiceAdmin.getNextApprovedAudioScheduleByAdmin(basicAuth.getHeaders(), body)
    }

    override fun approveAudioRequest(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["rating"] = ratings
        body["serviceName"] = "approveAudioRequest"
        return apiServiceAdmin.approveAudioRequest(basicAuth.getHeaders(), body)
    }

    override fun rejectAudioRequest(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["rating"] = ratings
        body["serviceName"] = "rejectAudioRequest"
        return apiServiceAdmin.rejectAudioRequest(basicAuth.getHeaders(), body)
    }

    override fun updateAudioRating(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "53"
        body["rating"] = ratings
        body["serviceName"] = "updateAudioRating"
        return apiServiceAdmin.updateAudioRating(basicAuth.getHeaders(), body)
    }

    override fun scheduleAudioByAdmin(audioIds: String, timezone: String, triggerDateAndTime: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioIds"] = audioIds
        body["screenId"] = "53"
        body["timezone"] = timezone
        body["triggerDateAndTime"] = triggerDateAndTime
        body["serviceName"] = "scheduleAudioByAdmin"
        return apiServiceAdmin.scheduleAudioByAdmin(basicAuth.getHeaders(), body)
    }

    override fun updateScheduleAudioTimeByAdmin(audioIds: String, timezone: String, triggerDateAndTime: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioIds
        body["screenId"] = "53"
        body["timezone"] = timezone
        body["triggerDateAndTime"] = triggerDateAndTime
        body["serviceName"] = "updateScheduleAudioTimeByAdmin"
        return apiServiceAdmin.updateScheduleAudioTimeByAdmin(basicAuth.getHeaders(), body)
    }

    override fun getNextAudioInfoForAdmin(audioIds: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "53"
        body["serviceName"] = "getNextAudioInfo"
        body["audioIds"] = audioIds
        body["userFor"] = userFor
        body["audioForUserId"] = audioForUserId
        return apiServiceAdmin.getNextAudioInfoForAdmin(basicAuth.getHeaders(), body)
    }
}