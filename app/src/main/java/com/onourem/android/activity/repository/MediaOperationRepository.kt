package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse


interface MediaOperationRepository {
    fun audioCategory(): LiveData<ApiResponse<GetAudioCategoryResponse>> 
    fun getAudioInfo(
        service: String,
        linkUserId: String,
        audioIdFromNotification: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>> 

    fun getNextAudioInfo(audioIds: String): LiveData<ApiResponse<GetAudioInfoResponse>> 
    fun likeAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> 
    fun unLikeAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> 
    fun updateAudioVisibility(
        privacyIds: String,
        audioId: String
    ): LiveData<ApiResponse<StandardResponse>> 

    fun addAudioInfoHistory(audioIds: String, audioDurations: String, creatorId: String)
    fun deleteAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> 
    fun followAudioCreator(audioCreator: String): LiveData<ApiResponse<StandardResponse>> 
    fun unfollowAudioCreator(audioCreator: String): LiveData<ApiResponse<StandardResponse>> 
    fun reportInappropriateAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> 
    fun updateAudioTitle(
        audioId: String,
        title: String
    ): LiveData<ApiResponse<StandardResponse>>

    //Admin
    fun getNextAudioInfoForAdmin(audioIds: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>>
    fun getAudioForOtherUser(otherUserId: String): LiveData<ApiResponse<GetAudioInfoResponse>> 
    fun searchAudioByTitle(title: String): LiveData<ApiResponse<GetAudioInfoResponse>> 
    fun uploadAudioInfo(
        title: String,
        uriAudioPath: String,
        creatorId: String,
        audioCategoryId: String,
        audioDuration: String,
        languageId: String,
        privacyId: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<StandardResponse>>

    fun getAudioInfoForAdmin(service: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>>

    fun getApprovedAudioScheduleByAdmin(
        audioFor: String,
        date: String,
        audioForUserId: String,
        userFor: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>>

    fun getNextApprovedAudioScheduleByAdmin(audioIds: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>>

    fun blackListAudio(audioId: String): LiveData<ApiResponse<StandardResponse>>

    fun approveAudioRequest(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>>

    fun rejectAudioRequest(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>>

    fun updateAudioRating(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>>

    fun scheduleAudioByAdmin(audioIds: String, timezone: String, triggerDateAndTime: String): LiveData<ApiResponse<StandardResponse>>

    fun updateScheduleAudioTimeByAdmin(audioIds: String, timezone: String, triggerDateAndTime: String): LiveData<ApiResponse<StandardResponse>>
}