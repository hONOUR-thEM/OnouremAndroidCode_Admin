package com.onourem.android.activity.arch.helper

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<ResultType?>>()

    @MainThread
    private fun setValue(newValue: Resource<ResultType?>) {
        if (result.value !== newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData: ResultType ->
            setValue(
                Resource.Companion.loading<ResultType?>(
                    newData
                )
            )
        }
        result.addSource(apiResponse) { response: ApiResponse<RequestType> ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            if (response.loading) {
            } else if (response.isSuccess) {
                appExecutors.diskIO().execute {
                    saveCallResult(processResponse(response)!!)
                    appExecutors.mainThread().execute { // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(
                            loadFromDb()
                        ) { newData: ResultType ->
                            setValue(
                                Resource.Companion.success<ResultType?>(
                                    newData
                                )
                            )
                        }
                    }
                }
            } else {
//                onFetchFailed();
                result.addSource(
                    dbSource
                ) { newData: ResultType ->
                    setValue(
                        Resource.Companion.error<ResultType?>(
                            response.errorMessage,
                            newData
                        )
                    )
                }
            }
        }
    }

    //    protected void onFetchFailed() {
    //
    //    }
    fun asLiveData(): LiveData<Resource<ResultType?>> {
        return result
    }

    @WorkerThread
    protected fun processResponse(response: ApiResponse<RequestType>): RequestType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    init {
        result.setValue(Resource.Companion.loading<ResultType?>(null))
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data: ResultType ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData: ResultType ->
                    setValue(
                        Resource.Companion.success<ResultType?>(
                            newData
                        )
                    )
                }
            }
        }
    }
}