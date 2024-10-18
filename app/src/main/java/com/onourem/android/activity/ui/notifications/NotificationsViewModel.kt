package com.onourem.android.activity.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.*
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(
    notificationRepository: NotificationRepositoryImpl,
    friendsRepository: FriendsRepositoryImpl,
    userListRepository: UserListRepositoryImpl,
    notificationCounterRepository: NotificationCounterRepositoryImpl
) : ViewModel(), NotificationRepository {
    private val notificationRepository: NotificationRepository
    private val friendsRepository: FriendsRepositoryImpl

    //    private UserListRepositoryImpl userListRepository;
    private val notificationCounterRepository: NotificationCounterRepositoryImpl
    override fun newNotificationInfo(): LiveData<ApiResponse<GetNewNotificationInfo>> {
        return notificationRepository.newNotificationInfo()
    }

    override fun updateNotificationStaus(
        notificationId: String?,
        readStatus: String?,
        activityId: String?,
        actionType: String?
    ): LiveData<ApiResponse<UpdateNotificationStaus>> {
        return notificationRepository.updateNotificationStaus(
            notificationId,
            readStatus,
            activityId,
            actionType
        )
    }

    override fun deleteNotification(
        notificationId: String?,
        activityId: String?,
        actionType: String?
    ): LiveData<ApiResponse<StandardResponse>> {
        return notificationRepository.deleteNotification(notificationId, activityId, actionType)
    }

    override fun getNextNotificationInfo(notificationRequestData: String?): LiveData<ApiResponse<GetNextNoficationInfo>> {
        return notificationRepository.getNextNotificationInfo(notificationRequestData)
    }

    override fun getUserProfileFeeds(
        userId: String?,
        postIds: String?,
        notificationId: String?
    ): LiveData<ApiResponse<GetUserProfileFeeds>> {
        return notificationRepository.getUserProfileFeeds(userId, postIds, notificationId)
    }

    override fun getActivityInfoForNotificataion(
        activityId: String?,
        activityGameRespId: String?,
        notificationId: String?
    ): LiveData<ApiResponse<GetActivityInfoForNotificataion>> {
        return notificationRepository.getActivityInfoForNotificataion(
            activityId,
            activityGameRespId,
            notificationId
        )
    }

    override fun getExternalActivityFromNotification(externalIds: String?): LiveData<ApiResponse<GetActivityInfoForNotificataion>> {
        return notificationRepository.getExternalActivityFromNotification(externalIds)
    }

    override fun pullNewNotificationInfo(topNotificationTime: String?): LiveData<ApiResponse<PullNewNotificationInfo>> {
        return notificationRepository.pullNewNotificationInfo(topNotificationTime)
    }

    fun cancelPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.cancelPendingRequest(friendUserId, screenId)
    }

    fun acceptPendingRequest(
        friendUserId: String?,
        screenId: String?
    ): LiveData<ApiResponse<UserActionStandardResponse>> {
        return friendsRepository.acceptPendingRequest(friendUserId, screenId)
    }

    val actionMutableLiveData: LiveData<Any>?
        get() = null

    //    public LiveData<Resource<List<UserList>>> getLocalBlockUserList() {
    //        return userListRepository.getAllLocalBlockedUsers();
    //    }
    fun resetNotificationCount() {
        notificationCounterRepository.reset()
    }

    fun notificationCount(): LiveData<Int> {
        return notificationCounterRepository.notificationCount()
    }

    init {
        this.notificationRepository = notificationRepository
        this.friendsRepository = friendsRepository
        //        this.userListRepository = userListRepository;
        this.notificationCounterRepository = notificationCounterRepository
    }
}