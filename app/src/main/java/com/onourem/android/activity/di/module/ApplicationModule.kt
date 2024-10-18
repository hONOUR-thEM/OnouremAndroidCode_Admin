package com.onourem.android.activity.di.module

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.OnouremApp
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.helper.AppExecutors
import com.onourem.android.activity.arch.helper.LiveDataCallAdapterFactory
import com.onourem.android.activity.dao.AudioPlayBackHistoryDao
import com.onourem.android.activity.dao.NotificationCounterDao
import com.onourem.android.activity.dao.QuestionContactsDao
import com.onourem.android.activity.db.AppDatabase
import com.onourem.android.activity.di.module.viewmodel.ViewModelsModule
import com.onourem.android.activity.network.AdminApiService
import com.onourem.android.activity.network.ApiService
import com.onourem.android.activity.network.CheckNullValuesEnabler
import com.onourem.android.activity.network.NetworkConnectionInterceptor
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.*
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelsModule::class])
class ApplicationModule(private val application: OnouremApp) {
//    @Singleton
//    @Provides
//    fun providesApplication(): OnouremApp {
//        return application
//    }

    @Singleton
    @Provides
    fun provideExecutors(): AppExecutors {
        return AppExecutors()
    }

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder() // .baseUrl(app.getString(BuildConfig.FLAVOR.equalsIgnoreCase("dev") ? R.string.base_url_development : R.string.base_url_production))
            .baseUrl(application.getString(apiBaseUrl))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapterFactory(
                        CheckNullValuesEnabler()
                    ).create()
                )
            )
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(provideOkHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAdminApiService(app: OnouremApp): AdminApiService {
        return Retrofit.Builder() // .baseUrl(app.getString(BuildConfig.FLAVOR.equalsIgnoreCase("dev") ? R.string.base_url_development : R.string.base_url_production))
            .baseUrl(application.getString(apiBaseUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(provideOkHttpClient())
            .build()
            .create(AdminApiService::class.java)
    }

    private val apiBaseUrl: Int
        get() {
            var baseUrl = 0
            when (BuildConfig.FLAVOR) {
//                "Dev"       -> baseUrl = R.string.base_url_development
                "AdminDev"  -> baseUrl = R.string.base_url_development
                "AdminProd" -> baseUrl = R.string.base_url_production
//                "Prod"      -> baseUrl = R.string.base_url_production
            }
            return baseUrl
        }

    private fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(120, TimeUnit.SECONDS)
        builder.writeTimeout(120, TimeUnit.SECONDS)
        builder.connectTimeout(120, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(interceptor)
        }
        builder.addInterceptor(NetworkConnectionInterceptor(application))
        return builder.build()
    }

    @SuppressLint("HardwareIds")
    @Provides
    @Named("uniqueDeviceId")
    fun uniqueDeviceId(application: OnouremApp): String {
        return Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @Singleton
    @Provides
    fun provideSharedPreferenceHelper(app: OnouremApp): SharedPreferenceHelper {
        return SharedPreferenceHelper(app)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(app: OnouremApp): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java, AppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideNotificationCounterDao(appDatabase: AppDatabase): NotificationCounterDao {
        return appDatabase.notificationCounterDao()
    }

    @Singleton
    @Provides
    fun provideAudioPlayBackHistoryDao(appDatabase: AppDatabase): AudioPlayBackHistoryDao {
        return appDatabase.audioPlayBackHistoryDao()
    }

    @Singleton
    @Provides
    fun provideQuestionContactsDaoDao(appDatabase: AppDatabase): QuestionContactsDao {
        return appDatabase.questionContactsDao()
    }

}