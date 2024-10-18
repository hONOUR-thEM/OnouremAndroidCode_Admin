package com.onourem.android.activity.ui.audio.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class Extensions {

    fun formatTimeUnit(timeInMilliseconds: Long): String {

        var value = ""
        try {
            if (TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) > 0) {
                value = String.format(
                    Locale.getDefault(),
                    "%2d min %2d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                    )
                )
            } else {
                value = String.format(
                    Locale.getDefault(),
                    "%2d sec",
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                    )
                )
            }

        } catch (e: Exception) {
            value = "00 min 00 sec"
        }
        return value
    }


    fun formatTimeUnitForSeekbar(timeInMilliseconds: Long): String {

        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
            TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
            )
        )
    }

}

fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}

@SuppressLint("SetTextI18n")
fun TextView.makeHyperLink(url: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml("<a href='${url}'>${text}</a>", Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml("<a href='${url}'>${text}</a>")
    }
    movementMethod = LinkMovementMethod.getInstance()
    isClickable = true
}

fun <R> CoroutineScope.executeAsyncTask(
    onPreExecute: () -> Unit,
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit
) = launch {
    onPreExecute()
    val result =
        withContext(Dispatchers.IO) { // runs in background thread without blocking the Main Thread
            doInBackground()
        }
    withContext(Dispatchers.Main){
        onPostExecute(result)
    }

}

fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    activity?.runOnUiThread(action)
}

fun String.extractUrls() : String {
    val urlRegex = "(?i)\\b((?:https?://|www\\d?\\.)[a-z0-9-]+(?:\\.[a-z]{2,})+(?:/[^\\s]*)?)"
    val pattern = Pattern.compile(urlRegex)
    val matcher = pattern.matcher(this)
    while (matcher.find()) {
        val url = matcher.group()
        println("Extracted URL: $url")
        return url
    }

    return ""
}

fun String.removeFractionalSeconds(): String {
    return if (this.endsWith(".0")) {
        this.dropLast(2)
    } else {
        this
    }
}