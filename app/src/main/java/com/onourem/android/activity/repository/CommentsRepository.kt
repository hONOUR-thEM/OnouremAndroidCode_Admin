package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.CommentsResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.AudioCommentResponse

interface CommentsRepository {
    fun getPostComments(
        gameId: String?,
        postId: String?,
        participantId: String?
    ): LiveData<ApiResponse<CommentsResponse>>

    fun deleteComment(
        commentId: String?,
        postId: String?,
        gameId: String?
    ): LiveData<ApiResponse<StandardResponse>>

    fun reportInappropriateAudioComment(
        commentId: String?,
    ): LiveData<ApiResponse<StandardResponse>>

    fun reportInappropriateComment(
        commentId: String?,
        from: String?,
    ): LiveData<ApiResponse<StandardResponse>>

    fun updateActivityNotificationStatus(
        activityId: String,
        playGroupId: String,
        gameId: String,
        activityGameResId: String
    ): LiveData<ApiResponse<StandardResponse>>

    fun getAudioComments(audioId: String): LiveData<ApiResponse<AudioCommentResponse>>
    fun getNextAudioComments(commentIds: String): LiveData<ApiResponse<AudioCommentResponse>>
    fun writeAudioComments(
        audioId: String,
        text: String
    ): LiveData<ApiResponse<StandardResponse>>

    fun deleteComment(commentId: String): LiveData<ApiResponse<StandardResponse>> //Parameter : "activityId" : activity.activityID,"screenId" : "28", "serviceName" : "updateActivityNotificationStatus", "action": "comment", "playGroupId" : self.playgroupId,"gameId": self.gameId, "activityGameResId" : activityGameResId
}