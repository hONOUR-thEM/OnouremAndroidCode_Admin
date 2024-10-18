package com.onourem.android.activity.arch.helper

import android.text.TextUtils
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.arch.helper.AppExecutors
import android.os.Looper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.network.NoConnectivityException
import com.onourem.android.activity.arch.helper.LiveDataCallAdapter
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MediatorLiveData
import retrofit2.*
import java.lang.Exception
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R> internal constructor(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiResponse<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            val started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    postValue(ApiResponse(true))
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call1: Call<R>, response: Response<R>) {
//                            Log.d(TAG, "on Response " + response.toString());
                            if (response.code() == 500 || response.code() == 502 || response.code() == 503 || response.code() == 504) {
                                postValue(ApiResponse(Exception("Bad Gateway")))
                            } else {
                                postValue(ApiResponse(response))
                                //postValue(new ApiResponse<>(new Exception("Bad Gateway")));
                            }
                        }

                        override fun onFailure(call1: Call<R>, throwable: Throwable) {
//                            Log.d(TAG, "On Failure " + throwable.getMessage());
                            if (throwable is NoConnectivityException) {
                                postValue(ApiResponse(Exception("Unable to resolve host")))
                            } else {
                                postValue(ApiResponse(Exception("Could not connect to server")))
                            }
                        }
                    })
                }
            }
        }
    }

    companion object {
        private val TAG = LiveDataCallAdapter::class.java.simpleName
    }
}