package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetConversationsResponse
import com.onourem.android.activity.models.GetMessagesResponse
import com.onourem.android.activity.models.SendMessageResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.DeletedMessageResponse

interface ConversationsRepository {
    fun conversations(): LiveData<ApiResponse<GetConversationsResponse>>
    fun getUserMessages(
        conversationId: String,
        messageTo: String
    ): LiveData<ApiResponse<GetMessagesResponse>>

    fun getNextMessages(messageIds: String): LiveData<ApiResponse<GetMessagesResponse>>
    fun postMessage(
        conversationId: String,
        messageTo: String,
        messageText: String
    ): LiveData<ApiResponse<SendMessageResponse>>

    fun updateUserMessageReadStatus(conversationId: String): LiveData<ApiResponse<StandardResponse>>
    fun deleteUserMessage(messageId: String): LiveData<ApiResponse<DeletedMessageResponse>>
    fun deleteConversation(conversationId: String): LiveData<ApiResponse<StandardResponse>>
    fun updateUserChatNotificationReadStatus(): LiveData<ApiResponse<StandardResponse>>
}