package com.onourem.android.activity.repository


import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.ConversationsRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetConversationsResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.GetMessagesResponse
import com.onourem.android.activity.models.SendMessageResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.DeletedMessageResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap

class ConversationsRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    ConversationsRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun conversations(): LiveData<ApiResponse<GetConversationsResponse>>{
            val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["screenId"] = "54"
            params["serviceName"] = "getConversationsList"
            return apiService.getConversations(basicAuth.getHeaders(), params)
        }

    override fun getUserMessages(
        conversationId: String,
        messageTo: String
    ): LiveData<ApiResponse<GetMessagesResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "getUserMessages"
        params["messageTo"] = messageTo
        params["conversationId"] = conversationId
        return apiService.getUserMessages(basicAuth.getHeaders(), params)
    }

    override fun getNextMessages(messageIds: String): LiveData<ApiResponse<GetMessagesResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "getNextMessages"
        params["messageIds"] = messageIds
        return apiService.getNextMessages(basicAuth.getHeaders(), params)
    }

    override fun postMessage(
        conversationId: String,
        messageTo: String,
        messageText: String
    ): LiveData<ApiResponse<SendMessageResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "sendMessage"
        params["conversationId"] = conversationId
        params["messageTo"] = messageTo
        params["messageText"] = messageText
        return apiService.postMessage(basicAuth.getHeaders(), params)
    }

    override fun updateUserMessageReadStatus(conversationId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "updateUserMessageReadStatus"
        params["conversationId"] = conversationId
        return apiService.updateUserMessageReadStatus(basicAuth.getHeaders(), params)
    }

    override fun deleteUserMessage(messageId: String): LiveData<ApiResponse<DeletedMessageResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "deleteUserMessage"
        params["messageId"] = messageId
        return apiService.deleteUserMessage(basicAuth.getHeaders(), params)
    }

    override fun deleteConversation(conversationId: String): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "deleteConversation"
        params["conversationId"] = conversationId
        return apiService.deleteConversation(basicAuth.getHeaders(), params)
    }

    override fun updateUserChatNotificationReadStatus(): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "55"
        params["serviceName"] = "updateUserChatNotificationReadStatus"
        return apiService.updateUserChatNotificationReadStatus(basicAuth.getHeaders(), params)
    }
}