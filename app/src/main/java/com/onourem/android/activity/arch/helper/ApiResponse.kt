package com.onourem.android.activity.arch.helper

import android.text.TextUtils
import com.onourem.android.activity.arch.helper.ApiResponse
import retrofit2.Response
import java.io.IOException

class ApiResponse<R> {
    @JvmField
    val code: Int

    @JvmField
    val body: R?

    @JvmField
    val errorMessage: String?

    @JvmField
    var loading = false

    constructor(error: Throwable) {
        code = 500
        body = null
        loading = false
        errorMessage = error.message
    }

    constructor(isLoading: Boolean) {
        code = 0
        body = null
        loading = isLoading
        errorMessage = null
    }

    constructor(response: Response<R>) {
        code = response.code()
        if (isSuccess) {
            body = response.body()
            errorMessage = null
        } else {
            var message: String? = null
            try {
                message = response.errorBody()!!.string()
            } catch (ex: IOException) {
//                Log.d(TAG, "Error Captured");
                ex.printStackTrace()
            }
            if (TextUtils.isEmpty(message)) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
    }

    val isSuccess: Boolean
        get() = code in 200..299

    companion object {
        private val TAG = ApiResponse::class.java.simpleName
    }
}