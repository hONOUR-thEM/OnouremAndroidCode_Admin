package com.onourem.android.activity.ui.utils.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.NetworkConnectionInterceptor
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskService : Service() {


    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private val TAG = "***TaskService"
    private val CHANNEL_ID = "NOTIFICATION_CHANNEL"
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate called")
        createNotificationChannel()
        isServiceRunning = true
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        val notificationIntent = Intent(this, DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service is Running")
            .setContentText("Listening for Screen Off/On events")
            .setSmallIcon(R.drawable.ic_app_logo_orange_24)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appName = getString(R.string.app_name)
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                appName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        isServiceRunning = false
        stopForeground(true)

        // call MyReceiver which will restart this service via a worker
        val broadcastIntent = Intent(this, TaskReceiver::class.java)
        sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
//
//        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val appEndTime = df.format(Date()).toString()
//        preferenceHelper!!.putValue(Constants.APP_END_TIME, appEndTime)
//
//        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
//        val params: MutableMap<String, Any> = HashMap()
//        params["serviceName"] = "updateAverageTimeInfo"
//        params["startTime"] = preferenceHelper!!.getString(Constants.APP_START_TIME)
//        params["endTime"] = preferenceHelper!!.getString(Constants.APP_END_TIME)
//        //params.put("deviceId", uniqueDeviceId);
//        //return apiService.updateFirebaseToken(basicAuth.getHeaders(), params);
//
//        //params.put("deviceId", uniqueDeviceId);
//        //return apiService.updateFirebaseToken(basicAuth.getHeaders(), params);
//        val service = provideApiService(this)
//        val call = service!!.updateAverageTimeInfo(basicAuth.headers, params)
//        call.enqueue(object : Callback<String?> {
//            override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                if (response.isSuccessful) {
//                    //Log.d("###", "onResponse: " + response.body());
//                }
//            }
//
//            override fun onFailure(call: Call<String?>, t: Throwable) {
//                //Log.d("###", "onFailure: " + t.getLocalizedMessage());
//            }
//        })
//
//        stopSelf()
//        Log.d(TAG, "onTaskRemoved called")
    }

    companion object {
        var isServiceRunning: Boolean = false
    }

    init {
        Log.d(TAG, "constructor called")
        isServiceRunning = false
    }


    private fun provideApiService(app: Context): ApiService? {
        return provideOkHttpClient(app)?.let {
            Retrofit.Builder()
                .baseUrl(app.getString(getApiBaseUrl()))
                .addConverterFactory(GsonConverterFactory.create()) //                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(it)
                .build()
                .create(ApiService::class.java)
        }
    }

    private fun getApiBaseUrl(): Int {
        var baseUrl = 0
        when (BuildConfig.FLAVOR) {
            "Dev" -> baseUrl = R.string.base_url_development
            "AdminDev" -> baseUrl = R.string.base_url_development
            "AdminProd" -> baseUrl = R.string.base_url_production
            "Prod" -> baseUrl = R.string.base_url_production
        }
        return baseUrl
    }

    private fun provideOkHttpClient(app: Context): OkHttpClient? {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(120, TimeUnit.SECONDS)
        builder.writeTimeout(120, TimeUnit.SECONDS)
        builder.connectTimeout(120, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(interceptor)
        }
        builder.addInterceptor(NetworkConnectionInterceptor(app))
        return builder.build()
    }
}