package com.onourem.android.activity.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class OnboardingPage(
    @field:StringRes val title: Int,
    @field:StringRes val desc: Int,
    @field:DrawableRes val image: Int,
    var isVideoAvailable: Boolean,
    var videoLink1: String,
    var videoLink2: String
)