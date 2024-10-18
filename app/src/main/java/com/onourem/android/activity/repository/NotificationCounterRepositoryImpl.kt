package com.onourem.android.activity.repository

import android.content.Context
import javax.inject.Inject
import com.onourem.android.activity.arch.helper.AppExecutors
import com.onourem.android.activity.dao.NotificationCounterDao
import com.onourem.android.activity.repository.NotificationCounterRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.network.AuthToken
import com.onourem.android.activity.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.onourem.android.activity.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.onourem.android.activity.network.NetworkConnectionInterceptor
import com.onourem.android.activity.ui.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import java.util.concurrent.TimeUnit

class NotificationCounterRepositoryImpl @Inject internal constructor(
    private val appExecutors: AppExecutors,
    private val counterDao: NotificationCounterDao
) : NotificationCounterRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun notificationCount(): LiveData<Int> {
        return counterDao.notificationCount
    }

    override fun notificationSilentCount(): LiveData<String> {
        return counterDao.notificationSilentCount
    }

    override fun messageNotificationCount(): LiveData<String> {
        return counterDao.messageCount
    }

    override fun reset() {
        appExecutors.diskIO().execute { counterDao.reset() }
    }

    override fun resetSilent() {
        appExecutors.diskIO().execute { counterDao.resetSilent() }
    }

    override fun increaseCount(userId: String) {
        appExecutors.diskIO().execute { counterDao.increaseCount(userId) }
    }

    override fun increaseSilentCount(userId: String) {
        appExecutors.diskIO().execute { counterDao.increaseSilentCount(userId) }
    }

    override fun increaseMessageNotificationCount(userId: String, conversationId: String) {
        appExecutors.diskIO().execute { counterDao.increaseMessageCount(userId, conversationId) }
    }

    override fun updateFirebaseToken(registrationId: String, app: Context) {
        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
        val params: MutableMap<String, Any> = HashMap()
        params["serviceName"] = "updateAndroidDeviceInfo"
        params["screenId"] = "2"
        params["registrationId"] = registrationId
        //params.put("deviceId", uniqueDeviceId);
        //return apiService.updateFirebaseToken(basicAuth.getHeaders(), params);
        val service = provideApiService(app)
        val call = service.updateFirebaseToken(basicAuth.getHeaders(), params)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    //Log.d("###", "onResponse: " + response.body());
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //Log.d("###", "onFailure: " + t.getLocalizedMessage());
            }
        })
    }

    fun provideApiService(app: Context): ApiService {
        return Retrofit.Builder()
            .baseUrl(app.getString(apiBaseUrl))
            .addConverterFactory(GsonConverterFactory.create()) //                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
            .client(provideOkHttpClient(app))
            .build()
            .create(ApiService::class.java)
    }

    private val apiBaseUrl: Int
        private get() {
            var baseUrl = 0
            when (BuildConfig.FLAVOR) {
                "Dev", "AdminDev" -> baseUrl = R.string.base_url_development
                "AdminProd", "Prod" -> baseUrl = R.string.base_url_production
            }
            return baseUrl
        }

    private fun provideOkHttpClient(app: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        builder.connectTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(interceptor)
        }
        builder.addInterceptor(NetworkConnectionInterceptor(app))
        return builder.build()
    }
}