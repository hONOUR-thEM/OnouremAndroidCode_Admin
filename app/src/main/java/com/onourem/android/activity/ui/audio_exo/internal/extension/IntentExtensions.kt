package com.onourem.android.activity.ui.audio_exo.internal.extension

import android.content.Intent
import com.onourem.android.activity.ui.utils.AppUtilities

internal fun Intent.printIntentExtras() {
    val extrasBundle = this.extras
    val sb = StringBuilder()
    if (extrasBundle != null) {
        val keys = extrasBundle.keySet()
        keys.forEach { key ->
            sb.append('[').append(key).append('=').append(extrasBundle.get(key)).append(']')
        }
    }
    val intentExtrasString = sb.toString()
    AppUtilities.showLog("IntentExtras", intentExtrasString)
}
