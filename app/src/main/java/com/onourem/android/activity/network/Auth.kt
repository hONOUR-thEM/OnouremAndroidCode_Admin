package com.onourem.android.activity.network

import com.onourem.android.activity.ui.utils.AppUtilities
import java.util.HashMap

abstract class Auth internal constructor() {

    val headers = HashMap<String, String>()

    internal constructor(authorization: String) : this() {
        headers["Authorization"] = authorization
    }

    fun getHeaders(): Map<String, String> {
        return headers
    }

    init {
        headers["Content-Type"] = "application/json; charset=utf-8"
        headers["source"] = "ANDROID"
        headers["appVersion"] = AppUtilities.getAppVersion().toString()
    }
}