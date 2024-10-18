package com.onourem.android.activity.repository


import javax.inject.Inject
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.repository.ProfileRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetUserProfileDataResponse
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.models.GetUserProfilePostsResponse
import com.onourem.android.activity.models.GetFriendIdListResponse
import com.onourem.android.activity.models.TaskAndMessageGameActivityResResponse
import com.onourem.android.activity.models.UpdateUserCoverAndProfileImageResponse
import com.onourem.android.activity.models.GetGeoList
import com.onourem.android.activity.models.UpdateProfileRequest
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.GetUserReferenceList
import com.onourem.android.activity.models.SetUserReferenceResponse
import com.onourem.android.activity.ui.utils.Constants
import java.util.HashMap
import javax.inject.Named

class ProfileRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    ProfileRepository {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getUserProfileData(userId: String?): LiveData<ApiResponse<GetUserProfileDataResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "20"
        params["serviceName"] = "getUserProfileaData"
        params["userId"] = userId!!
        return apiService.getUserProfileData(authToken.getHeaders(), params)
    }

    //{"userId":userId,"screenId" : "20", "serviceName" : "getUserProfileaData"}
    //{"userId":self.profileUserId,"screenId" : "20", "serviceName" : "blockUser"}
    //{"friendUserId":self.profileUserId,"screenId" : "20", "serviceName" : "removeFriend" }
    //{"friendUserId":profileUserId,"screenId" : "20", "serviceName" : "acceptPendingRequest" }
    //{"friendUserId":profileUserId,"screenId" : "20", "serviceName" : "cancelPendingRequest"}
    //{"friendUserId":profileUserId,"screenId" : "20", "serviceName" : "sendFriendRequest"}
    //{"friendUserId":profileUserId,"screenId" : "20", "serviceName" : "cancelSentRequest"}
    //{"smallImage":smallImageString?,"largeImage":largeImageString?,"typeOfImage": "profile","screenId" : "49", "serviceName" : "updateUserCoverAndProfileImage"}
    override fun getUserProfilePosts(userId: String?): LiveData<ApiResponse<GetUserProfilePostsResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "20"
        params["serviceName"] = "getUserProfileFeeds"
        params["userId"] = userId!!
        return apiService.getUserProfilePosts(authToken.getHeaders(), params)
        //{"userId":userId,"screenId" : "20", "serviceName" : "getUserProfileFeeds"}
    }

    override fun getFriendIdList(profileUserId: String?): LiveData<ApiResponse<GetFriendIdListResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "20"
        params["serviceName"] = "getFriendIdList"
        params["userId"] = profileUserId!!
        return apiService.getFriendIdList(authToken.getHeaders(), params)
        //{"screenId" : "20", "serviceName" : "getFriendIdList", "userId" : profileUserId}
    }

    override fun getNextUserProfilePosts(
        userId: String?,
        postIds: String?
    ): LiveData<ApiResponse<TaskAndMessageGameActivityResResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "20"
        params["serviceName"] = "getNextUserProfilePosts"
        params["userId"] = userId!!
        params["postIds"] = postIds!!
        return apiService.getNextUserProfilePosts(authToken.getHeaders(), params)
        //{"userId":profileUserId,"postIds":requestData,"screenId" : "20", "serviceName" : "getNextUserProfilePosts" }
    }

    override fun updateUserCoverAndProfileImage(
        smallImage: String?,
        largeImage: String?,
        typeOfImage: String?
    ): LiveData<ApiResponse<UpdateUserCoverAndProfileImageResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["screenId"] = "49"
        params["serviceName"] = "updateUserCoverAndProfileImage"
        params["smallImage"] = smallImage!!
        params["largeImage"] = largeImage!!
        params["typeOfImage"] = typeOfImage!!
        return apiService.updateUserCoverAndProfileImage(authToken.getHeaders(), params)
        //{"smallImage":smallImageString?,"largeImage":largeImageString?,"typeOfImage": "cover","screenId" : "49", "serviceName" : "updateUserCoverAndProfileImage"}
    }

    override val countryList: LiveData<ApiResponse<GetGeoList>>
        get() {
            val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: MutableMap<String, Any> = HashMap()
            params["screenId"] = "37"
            params["serviceName"] = "getGeoList"
            params["dummy"] = ""
            return apiService.getCountryList(authToken.getHeaders(), params)
        }

    override fun getStateList(countryId: String?): LiveData<ApiResponse<GetGeoList>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["countryId"] = countryId!!
        return apiService.getStateList(authToken.getHeaders(), params)
    }

    override fun getCityList(stateId: String?): LiveData<ApiResponse<GetGeoList>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["stateId"] = stateId!!
        return apiService.getCityList(authToken.getHeaders(), params)
    }

    override fun updateProfile(updateProfileRequest: UpdateProfileRequest): LiveData<ApiResponse<LoginResponse>> {

//        String? newPass = updateProfileRequest.getPassword();
//        if (newPass.equalsIgnoreCase("")) {
//            newPass = "*****";
//        }
        val authToken: Auth =
            AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN) + ":" + "*****")
        return apiService.updateProfile(authToken.getHeaders(), updateProfileRequest)
    }

    override fun validatePassword(updateProfileRequest: UpdateProfileRequest): LiveData<ApiResponse<LoginResponse>> {
        val authToken: Auth =
            AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN) + ":" + updateProfileRequest.password + ":" + updateProfileRequest.oldPassword)
        return apiService.validatePassword(authToken.getHeaders(), updateProfileRequest)
    }

    override val userReferenceList: LiveData<ApiResponse<GetUserReferenceList>>
        get() {
            val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
            val params: Map<String, Any> = HashMap()
            return apiService.getUserReferenceList(authToken.getHeaders(), params)
        }

    override fun setUserReference(refId: String?): LiveData<ApiResponse<SetUserReferenceResponse>> {
        val authToken: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["refId"] = refId!!
        params["screenId"] = "14"
        params["serviceName"] = "UpdateAboutOnouremInfo"
        return apiService.setUserReference(authToken.getHeaders(), params)
    }
}