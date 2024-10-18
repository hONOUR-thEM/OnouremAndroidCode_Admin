package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.TaskAndMessageRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.TaskAndMessageGameActivityResResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.GetUserActivityAndGameInfoResponse
import com.onourem.android.activity.models.PostsActionResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap

class TaskAndMessageRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    TaskAndMessageRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getTaskAndMessageGameActivityRes(activityId: String?): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>> {
        //{"activityId":activity.activityID,"screenId" : "22", "serviceName" : "getTaskAndMessageGameActivityRes"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "22"
        body["serviceName"] = "getTaskAndMessageGameActivityRes"
        body["activityId"] = activityId ?: ""
        return apiService.getTaskAndMessageGameActivityRes(basicAuth.getHeaders(), body)
    }

    override fun getTaskMessageForPlaygroup(
        activityId: String?,
        oclubActivityId: String?,
        playgroupId: String?,
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "22"
        body["serviceName"] = "getTaskMessageForPlaygroup"
        body["activityId"] = activityId ?: ""
        body["oclubActivityId"] = oclubActivityId ?: ""
        body["playgroupId"] = playgroupId ?: ""
        return apiService.getTaskMessageForPlaygroup(basicAuth.getHeaders(), body)
    }

    override fun getUserActivityAndGameInfo(
        activityId: String?,
        profileUserId: String?
    ): LiveData<ApiResponse<GetUserActivityAndGameInfoResponse>> {
        //{"activityId":activityId,"profileUserId":profileUserId,"screenId" : "22", "serviceName" : "getUserActivityAndGameInfo"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "22"
        body["serviceName"] = "getUserActivityAndGameInfo"
        body["activityId"] = activityId ?: ""
        body["profileUserId"] = profileUserId?: ""
        return apiService.getUserActivityAndGameInfo(basicAuth.getHeaders(), body)
    }

    override fun reportAbuse(postId: String?): LiveData<ApiResponse<PostsActionResponse>> {
        //{"postId":postId,"screenId" : "22", "serviceName" : "reportAbuse"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "22"
        body["serviceName"] = "reportAbuse"
        body["postId"] = postId?: ""
        return apiService.reportAbuse(basicAuth.getHeaders(), body)
    }

    override fun deletePosts(postId: String?): LiveData<ApiResponse<PostsActionResponse>> {
        //{"postId":postId,"screenId" : "22", "serviceName" : "deletePosts"}
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val body: MutableMap<String, Any> = HashMap()
        body["screenId"] = "22"
        body["serviceName"] = "deletePosts"
        body["postId"] = postId?: ""
        return apiService.deletePosts(basicAuth.getHeaders(), body)
    }
}