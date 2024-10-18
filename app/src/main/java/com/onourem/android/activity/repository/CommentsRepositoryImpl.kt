package com.onourem.android.activity.repository

import android.text.TextUtils
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.AudioCommentResponse
import com.onourem.android.activity.models.CommentsResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class CommentsRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    CommentsRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getPostComments(
        gameId: String?,
        postId: String?,
        participantId: String?
    ): LiveData<ApiResponse<CommentsResponse>> {
//        {"gameId" : gameId, "postId": postId,"screenId" : "28", "serviceName" : "getPostComments","participantId":participantId}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getPostComments"
        params["screenId"] = "28"
        params["deviceId"] = uniqueDeviceId!!
        params["gameId"] = gameId.toString()
        params["postId"] = postId.toString()
        params["participantId"] = participantId.toString()
        return apiService.getPostComments(basicAuth.getHeaders(), params)
    }

    override fun deleteComment(
        commentId: String?,
        postId: String?,
        gameId: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "deleteComment"
        params["screenId"] = "28"
        if (!TextUtils.isEmpty(postId)) {
            params["postId"] = postId.toString()
        }
        else {
            params["gameId"] = gameId.toString()
        }
        params["commentId"] = commentId.toString()
        return apiService.deleteComment(basicAuth.getHeaders(), params)
    }

    override fun getAudioComments(audioId: String): LiveData<ApiResponse<AudioCommentResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "54"
        body["serviceName"] = "getAudioComments"
        return apiService.getAudioComments(basicAuth.getHeaders(), body)
    }

    override fun getNextAudioComments(commentIds: String): LiveData<ApiResponse<AudioCommentResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioCommentIds"] = commentIds
        body["screenId"] = "54"
        body["serviceName"] = "getNextAudioComments"
        return apiService.getNextAudioComments(basicAuth.getHeaders(), body)
    }

    override fun writeAudioComments(
        audioId: String,
        text: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioId"] = audioId
        body["screenId"] = "54"
        body["text"] = text
        body["serviceName"] = "writeAudioComments"
        return apiService.writeAudioComments(basicAuth.getHeaders(), body)
    }

    override fun deleteComment(commentId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioCommentId"] = commentId
        body["screenId"] = "54"
        body["serviceName"] = "deleteAudioComments"
        return apiService.deleteAudioComments(basicAuth.getHeaders(), body)
    }

    override fun reportInappropriateAudioComment(commentId: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["audioCommentId"] = commentId!!
        body["screenId"] = "54"
        body["serviceName"] = "reportInappropriateAudioComment"
        return apiService.reportInappropriateAudioComment(basicAuth.getHeaders(), body)
    }

    override fun reportInappropriateComment(commentId: String?, from: String?): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "54"

        return if (from == "Post") {
            body["postCommentId"] = commentId!!
            body["serviceName"] = "reportInappropriatePostComment"
            apiService.reportInappropriatePostComment(basicAuth.getHeaders(), body)
        }
        else {
            body["onetToManyResCommentId"] = commentId!!
            body["serviceName"] = "reportInappropriateOneToManyResponseComment"
            apiService.reportInappropriateOneToManyResponseComment(basicAuth.getHeaders(), body)
        }

    }

    override fun updateActivityNotificationStatus(
        activityId: String,
        playGroupId: String,
        gameId: String,
        activityGameResId: String
    ): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateActivityNotificationStatus"
        params["screenId"] = "28"
        params["gameId"] = gameId
        params["activityGameResId"] = activityGameResId
        params["playGroupId"] = playGroupId
        params["activityId"] = activityId
        params["action"] = "comment"
        return apiService.updateActivityNotificationStatus(basicAuth.getHeaders(), params)
    }
}