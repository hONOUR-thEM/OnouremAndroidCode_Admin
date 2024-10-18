package com.onourem.android.activity.ui.audio_exo

import android.content.Context
import android.content.Intent

interface CurrentContentIntentProvider {
    fun provideCurrentContentIntent(context: Context): Intent
}
