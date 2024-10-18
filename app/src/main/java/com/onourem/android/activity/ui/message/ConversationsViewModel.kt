package com.onourem.android.activity.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.DeletedMessageResponse
import com.onourem.android.activity.models.GetConversationsResponse
import com.onourem.android.activity.models.GetMessagesResponse
import com.onourem.android.activity.models.GetUserLinkInfoResponse
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.SendMessageResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.repository.ConversationsRepository
import com.onourem.android.activity.repository.ConversationsRepositoryImpl
import com.onourem.android.activity.repository.NotificationCounterRepository
import com.onourem.android.activity.repository.NotificationCounterRepositoryImpl
import com.onourem.android.activity.ui.message.share.ShareContentSheetFragmentArgs
import javax.inject.Inject

class ConversationsViewModel @Inject constructor(
    repository: ConversationsRepositoryImpl,
    notificationCounterRepository: NotificationCounterRepositoryImpl
) : ViewModel(), ConversationsRepository {

    private val repository: ConversationsRepository
    private val notificationCounterRepository: NotificationCounterRepository
    val conversationReadStatus = MutableLiveData<Conversation?>()


    override fun conversations(): LiveData<ApiResponse<GetConversationsResponse>> {
        return repository.conversations()
    }

    override fun getUserMessages(
        conversationId: String,
        messageTo: String
    ): LiveData<ApiResponse<GetMessagesResponse>> {
        return repository.getUserMessages(conversationId, messageTo)
    }

    override fun getNextMessages(messageIds: String): LiveData<ApiResponse<GetMessagesResponse>> {
        return repository.getNextMessages(messageIds)
    }

    override fun postMessage(
        conversationId: String,
        messageTo: String,
        messageText: String
    ): LiveData<ApiResponse<SendMessageResponse>> {
        return repository.postMessage(conversationId, messageTo, messageText)
    }

    override fun updateUserMessageReadStatus(conversationId: String): LiveData<ApiResponse<StandardResponse>> {
        return repository.updateUserMessageReadStatus(conversationId)
    }

    override fun deleteUserMessage(messageId: String): LiveData<ApiResponse<DeletedMessageResponse>> {
        return repository.deleteUserMessage(messageId)
    }

    override fun deleteConversation(conversationId: String): LiveData<ApiResponse<StandardResponse>> {
        return repository.deleteConversation(conversationId)
    }

    override fun updateUserChatNotificationReadStatus(): LiveData<ApiResponse<StandardResponse>> {
        return repository.updateUserChatNotificationReadStatus()
    }

    fun updateConversationReadStatus(conversation: Conversation?) {
        conversationReadStatus.value = conversation
    }

    fun updateConversationReadStatusConsumed() {
        conversationReadStatus.value = null
    }

    init {
        this.repository = repository
        this.notificationCounterRepository = notificationCounterRepository
    }
}