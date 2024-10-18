package com.onourem.android.activity.network

import com.onourem.android.activity.ui.utils.Base64Utility

class BasicAuth(private val username: String,private val password: String) : Auth(
    "Basic " + Base64Utility.encodeToString(
        "$username:$password"
    )
)