package com.onourem.android.activity.ui.games.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.repository.AppreciationRepository
import com.onourem.android.activity.repository.AppreciationRepositoryImpl
import com.onourem.android.activity.repository.CommentsRepository
import com.onourem.android.activity.repository.CommentsRepositoryImpl
import com.onourem.android.activity.ui.utils.Triple
import javax.inject.Inject

class CommentsViewModel @Inject constructor(
    appreciationRepository: AppreciationRepositoryImpl,
    commentsRepository: CommentsRepositoryImpl
) : ViewModel() {
    private val appreciationRepository: AppreciationRepository
    private val commentsRepository: CommentsRepository
    val commentCountLiveData = MutableLiveData<Triple<String?, Int, String?>?>()
    private val reloadUi = MutableLiveData<String?>()
    fun uploadPost(
        post: UploadPostRequest,
        uriImagePath: String,
        uriVideoPath: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<UploadPostResponse>> {
        return appreciationRepository.uploadPost(post, uriImagePath, uriVideoPath, progressCallback)
    }

    fun getPostComments(
        gameId: String?,
        postId: String?,
        participantId: String?
    ): LiveData<ApiResponse<CommentsResponse>> {
        return commentsRepository.getPostComments(gameId, postId, participantId)
    }

    fun deleteComment(commentId: String?, postId: String?, gameId: String?): LiveData<ApiResponse<StandardResponse>> {
        return commentsRepository.deleteComment(commentId, postId, gameId)
    }

    fun reportInappropriateAudioComment(commentId: String?): LiveData<ApiResponse<StandardResponse>> {
        return commentsRepository.reportInappropriateAudioComment(commentId)
    }

    fun reportInappropriateComment(commentId: String?, from: String?): LiveData<ApiResponse<StandardResponse>> {
        return commentsRepository.reportInappropriateComment(commentId, from)
    }

    fun updateActivityNotificationStatus(
        activityId: String?,
        playGroupId: String?,
        gameId: String?,
        activityGameResId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return commentsRepository.updateActivityNotificationStatus(
            activityId!!,
            playGroupId!!,
            gameId!!,
            activityGameResId!!
        )
    }

    fun publishCommentCount(postId: String?, gameId: String?, commentCount: Int, gameResId: String?) {
        commentCountLiveData.value =
            Triple(
                if (TextUtils.isEmpty(postId)) gameId else postId, commentCount, gameResId
            )
    }

    fun getCommentCountLiveData(): LiveData<Triple<String?, Int, String?>?> {
        return commentCountLiveData
    }

    fun setReloadUi(reload: String?) {
        reloadUi.value = reload
    }

    fun reloadUi(): LiveData<String?> {
        return reloadUi
    }

    fun resetCommentCountLiveData() {
        commentCountLiveData.value = null
    }

    fun getAudioComments(audioId: String): LiveData<ApiResponse<AudioCommentResponse>> {
        return commentsRepository.getAudioComments(audioId)
    }

    fun getNextAudioComments(audioId: String): LiveData<ApiResponse<AudioCommentResponse>> {
        return commentsRepository.getNextAudioComments(audioId)
    }

    fun writeAudioComments(
        audioId: String,
        text: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return commentsRepository.writeAudioComments(audioId, text)
    }

    fun deleteComments(commentId: String) : LiveData<ApiResponse<StandardResponse>> {
        return commentsRepository.deleteComment(commentId)

    }

    init {
        this.appreciationRepository = appreciationRepository
        this.commentsRepository = commentsRepository
    }
}