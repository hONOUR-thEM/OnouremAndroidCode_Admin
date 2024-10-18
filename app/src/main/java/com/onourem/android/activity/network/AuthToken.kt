package com.onourem.android.activity.network

import com.onourem.android.activity.network.Auth
import com.onourem.android.activity.ui.utils.Base64Utility

class AuthToken(token: String?) : Auth("Token " + Base64Utility.encodeToString(token))