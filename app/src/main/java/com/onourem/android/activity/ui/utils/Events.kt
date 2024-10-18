package com.onourem.android.activity.ui.utils

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.Message
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Events {
    val SERVICE_EVENT: MutableLiveData<Message> by lazy {
        MutableLiveData<Message>()
    }

    val MESSAGE_RECEIVED: MutableLiveData<Message> by lazy {
        MutableLiveData<Message>()
    }

    fun getConversationAdminUser(loginUserId: String?): Conversation {

        //2023 nt1 jain dev admin
        //4264 info@Onourem prod admin

        val conversationDevAdmin = Conversation()
        conversationDevAdmin.id = "EMPTY"
        conversationDevAdmin.userName = "Onourem Dev Support"
//        conversationDevAdmin.userOne = loginUserId
        conversationDevAdmin.userOne = "2023"
        conversationDevAdmin.profilePicture = "https://d22rs2xug5qey8.cloudfront.net/images/smallprofile/d20ac239-af6d-4c5b-bc41-448d471864eb1622438410770.jpeg"
        conversationDevAdmin.userTypeId = "1"

        val conversationProdAdmin = Conversation()
        conversationProdAdmin.id = "EMPTY"
        conversationProdAdmin.userName = "Onourem Support"
//        conversationProdAdmin.userOne = loginUserId
        conversationProdAdmin.userOne = "4262"
        conversationProdAdmin.profilePicture = "https://d3p7rbpxwoh9vq.cloudfront.net/images/smallprofile/cf7ede8b-2c64-4f2e-9b04-cfd58b0ba0ed1654586682994.jpeg"
        conversationProdAdmin.userTypeId = "1"

        return when (BuildConfig.FLAVOR) {
            "Dev" -> conversationDevAdmin
            "AdminDev" -> conversationDevAdmin
            "Prod" -> conversationProdAdmin
            "AdminProd" -> conversationProdAdmin
            else -> {
                conversationProdAdmin
            }
        }
    }


    fun getServerTimeConversation(serverTime: String): String {

        val dateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TimeUtil.getRelatedTime(serverTime)
        } else {
            TimeUtilBackward.getRelatedTime(serverTime)
        }
//        val utc = TimeZone.getTimeZone("UTC")
//        val inputFormat: DateFormat = SimpleDateFormat(
//            "yyyy-MM-dd HH:mm:ss",
//            Locale.ENGLISH
//        )
//        inputFormat.timeZone = utc
//        val outputFormat: DateFormat = SimpleDateFormat(
//            "MMM d, h:mm a",
//            Locale.ENGLISH
//        )
//        outputFormat.timeZone = TimeZone.getDefault()
//        val dateInput = inputFormat.parse(serverTime)

        return dateTime
    }

    fun getServerTimeConversationSubscription(serverTime: String): String {
        val utc = TimeZone.getTimeZone("UTC")
        val inputFormat: DateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        )
        inputFormat.timeZone = utc
        val outputFormat: DateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getDefault()
        val dateInput = inputFormat.parse(serverTime)

        return outputFormat.format(dateInput!!)
    }

    fun getServerTimeConversationRelativeDate(serverTime: String): Date? {
        val utc = TimeZone.getTimeZone("UTC")
        val inputFormat: DateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        )
        inputFormat.timeZone = utc
        val outputFormat: DateFormat = SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss aa",
            Locale.ENGLISH
        )
        outputFormat.timeZone = TimeZone.getDefault()
        val dateInput = inputFormat.parse(serverTime)

        val dateIST = outputFormat.parse(serverTime)
        return dateInput
    }
}