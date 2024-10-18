package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.UserListRepository
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.BlockListResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap
import javax.inject.Named

class UserListRepositoryImpl //        appExecutors.diskIO().execute(() -> userListDao.removeUser(user));
//    }
//
//    public void eraseUserDatabase() {
//        appExecutors.diskIO().execute(() -> appDatabase.clearAllTables());
//    }
@Inject constructor(
    private val preferenceHelper: SharedPreferenceHelper,
    private val apiService: ApiService,
    @param:Named(
        "uniqueDeviceId"
    ) private val uniqueDeviceId: String
) : UserListRepository {
    override fun getFriendSuggestionList(userId: String): LiveData<ApiResponse<UserListResponse>> {
        //{"screenId" : "17", "serviceName" : "getFriendSuggetionList", "inputUserId":inputUserID}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getFriendSuggetionList"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId
        params["inputUserId"] = userId
        return apiService.getFriendSuggetionList(basicAuth.getHeaders(), params)
    }

    override fun getNextFriendSuggestionList(userIds: String): LiveData<ApiResponse<UserListResponse>> {

        //{"userIds":requestData,"screenId" : "17", "serviceName" : "getNextFriendSuggetionList"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextFriendSuggetionList"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId
        params["userIds"] = userIds
        return apiService.getNextFriendSuggestionList(basicAuth.getHeaders(), params)
    }

    override fun getMyFriendList(inputUserID: String): LiveData<ApiResponse<UserListResponse>> {
        //{"screenId" : "17", "serviceName" : "getMyFriendList", "inputUserId":inputUserID}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getMyFriendList"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId
        params["inputUserId"] = inputUserID
        return apiService.getMyFriendList(basicAuth.getHeaders(), params)
    }

    override fun getUserFriendList(inputUserID: String): LiveData<ApiResponse<UserListResponse>> {
        //"userId":profileUserId,"screenId" : "20", "serviceName" : "getUserFriendList"
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getMyFriendList"
        params["screenId"] = "20"
        params["deviceId"] = uniqueDeviceId
        params["userId"] = inputUserID
        return apiService.getUserFriendList(basicAuth.getHeaders(), params)
    }

    override fun getNextMyFriendList(userIds: String): LiveData<ApiResponse<UserListResponse>> {
        //{"userIds":requestData,"screenId" : "17", "serviceName" : "getNextMyFriendList"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "getNextMyFriendList"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId
        params["userIds"] = userIds
        return apiService.getNextMyFriendList(basicAuth.getHeaders(), params)
    }

    override fun getGlobalSearchResult(searchText: String): LiveData<ApiResponse<UserListResponse>> {
        //{"searchText" : searchString, "screenId" : "17", "serviceName" : "searchUsers"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "searchUsers"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId
        params["searchText"] = searchText
        return apiService.getGlobalSearchResult(basicAuth.getHeaders(), params)
    }

    override fun getNextGlobalSearchList(userIds: String): LiveData<ApiResponse<UserListResponse>> {
        //{"userIds":requestData,"screenId" : "17", "serviceName" : "performNextGlobalSearch"}
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "performNextGlobalSearch"
        params["screenId"] = "17"
        params["deviceId"] = uniqueDeviceId
        params["userIds"] = userIds
        return apiService.getNextGlobalSearchList(basicAuth.getHeaders(), params)
    }

    override val blockedUserListFromServer: LiveData<ApiResponse<BlockListResponse>>
        get() {
            val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["serviceName"] = "getBlockUserList"
            params["screenId"] = "2"
            return apiService.getBlockUserList(basicAuth.getHeaders(), params)
        }

    //    @Override
    //    public LiveData<Resource<List<UserList>>> getAllUsersBlockedByMe() {
    //        return Transformations.switchMap(userListDao.getUsersBlockedByMe(), input -> {
    //            MutableLiveData<Resource<List<UserList>>> mutableLiveData = new MutableLiveData<>();
    //            appExecutors.diskIO().execute(() -> {
    //                if (input != null && !input.isEmpty())
    //                    mutableLiveData.postValue(Resource.success(input));
    //                else
    //                    mutableLiveData.postValue(Resource.error("No results found", null));
    //            });
    //            return mutableLiveData;
    //        });
    //    }
    //    @Override
    //    public LiveData<Resource<List<UserList>>> getAllLocalBlockedUsers() {
    //        return Transformations.switchMap(userListDao.getAllLocalBlockedUsers(), input -> {
    //            MutableLiveData<Resource<List<UserList>>> mutableLiveData = new MutableLiveData<>();
    //            appExecutors.diskIO().execute(() -> {
    //                if (input != null && !input.isEmpty())
    //                    mutableLiveData.postValue(Resource.success(input));
    //                else
    //                    mutableLiveData.postValue(Resource.error("No results found", null));
    //            });
    //            return mutableLiveData;
    //        });
    //    }
    override fun blockUser(userList: UserList): LiveData<ApiResponse<StandardResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "20"
        params["serviceName"] = "blockUser"
        params["userId"] = userList.userId
        return apiService.blockUser(authToken.getHeaders(), params)
    }

    override fun blockAndReportUser(user: UserList): LiveData<ApiResponse<StandardResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "20"
        params["serviceName"] = "blockAndReportUser"
        params["userId"] = user.userId
        return apiService.blockAndReportUser(authToken.getHeaders(), params)
    }

    override fun unBlockUser(user: UserList): LiveData<ApiResponse<StandardResponse>> {
        val basicAuth: Auth = AuthToken(preferenceHelper.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "unBlockUser"
        params["screenId"] = "44"
        params["userId"] = user.userId
        return apiService.unBlockUser(basicAuth.getHeaders(), params)
    } //    public void removeLocalBlockedUser(UserList user) {
}