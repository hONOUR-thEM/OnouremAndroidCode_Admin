package com.onourem.android.activity.push

import android.Manifest
import com.onourem.android.activity.ui.utils.Events.SERVICE_EVENT
import com.onourem.android.activity.ui.utils.Events.MESSAGE_RECEIVED
import androidx.room.PrimaryKey
import com.google.firebase.messaging.FirebaseMessagingService
import android.text.style.StyleSpan
import android.graphics.Typeface
import javax.inject.Inject
import com.onourem.android.activity.repository.NotificationCounterRepositoryImpl
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import dagger.android.AndroidInjection
import com.google.firebase.messaging.RemoteMessage
import com.onourem.android.activity.ui.utils.AppUtilities
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.GlideException
import com.onourem.android.activity.push.OnouremFirebaseService
import android.text.SpannableString
import android.text.Spannable
import android.app.PendingIntent
import com.onourem.android.activity.models.LoginResponse
import com.google.gson.Gson
import androidx.navigation.NavDeepLinkBuilder
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.onboarding.OnboardingActivity
import com.onourem.android.activity.ui.DashboardActivity
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import com.onourem.android.activity.models.Message
import com.onourem.android.activity.ui.utils.Constants
import java.util.*

class OnouremFirebaseService : FirebaseMessagingService() {
    private val mBoldSpan = StyleSpan(Typeface.BOLD)

    @JvmField
    @Inject
    var counterRepository: NotificationCounterRepositoryImpl? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            AppUtilities.showLog("Message data payload: ", remoteMessage.data.toString())
            if (!TextUtils.isEmpty(remoteMessage.data["activityImageUrl"])) {
                Glide.with(this).asBitmap().load(remoteMessage.data["activityImageUrl"])
                    .addListener(object : RequestListener<Bitmap?> {

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any,
                            target: Target<Bitmap?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            sendNotification(remoteMessage.data, resource)
                            return true
                        }
                    }).submit()
            } else {
                sendNotification(remoteMessage.data, null)
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        preferenceHelper!!.putValue(Constants.KEY_FCM_TOKEN, token)
        counterRepository!!.updateFirebaseToken(token, applicationContext)
    }

    private fun makeNotificationBold(text: String?): SpannableString {
        //Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_medium.ttf");
        val spannableString: SpannableString
        if (text != null && text.length > 0) {
            spannableString = SpannableString(String.format("%s", text))
            spannableString.setSpan(mBoldSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            spannableString = SpannableString(text)
        }
        return spannableString
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: Map<String, String?>, bitmap: Bitmap?) {
        val isSilent = messageBody["silent"]
        if (isSilent != null && isSilent.equals("false", ignoreCase = true)) {
            val pendingIntent: PendingIntent = if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN)) {
                    val loginResponse = Gson().fromJson(
                        preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                        LoginResponse::class.java
                    )
                    if (loginResponse != null) {
                        if (loginResponse.isRegistrationStaus) {
                            if (TextUtils.isEmpty(loginResponse.dateOfBirth)) {
                                NavDeepLinkBuilder(this)
                                    .setGraph(R.navigation.onboarding_navigation)
                                    .setDestination(R.id.nav_date_picker)
                                    .setComponentName(OnboardingActivity::class.java)
                                    .createPendingIntent()
                            } else {
                                NavDeepLinkBuilder(this)
                                    .setGraph(R.navigation.mobile_navigation)
                                    .setDestination(R.id.nav_notifications)
                                    .setComponentName(DashboardActivity::class.java)
                                    .createPendingIntent()
                            }
                        } else {
                            val bundle = Bundle()
                            bundle.putString("EmailId", loginResponse.emailAddress)
                            NavDeepLinkBuilder(this)
                                .setGraph(R.navigation.onboarding_navigation)
                                .setDestination(R.id.nav_verification)
                                .setArguments(bundle)
                                .setComponentName(OnboardingActivity::class.java)
                                .createPendingIntent()
                        }
                    } else {
                        val bundle = Bundle()
                        bundle.putString("EmailId", "")
                        NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.onboarding_navigation)
                            .setDestination(R.id.nav_splash)
                            .setArguments(bundle)
                            .setComponentName(OnboardingActivity::class.java)
                            .createPendingIntent()
                    }
                } else {
                    val bundle = Bundle()
                    bundle.putString("EmailId", "")
                    NavDeepLinkBuilder(this)
                        .setGraph(R.navigation.onboarding_navigation)
                        .setDestination(R.id.nav_splash)
                        .setArguments(bundle)
                        .setComponentName(OnboardingActivity::class.java)
                        .createPendingIntent()
                }
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Onourem",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.setShowBadge(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            builder.setSmallIcon(R.drawable.ic_logo)
            builder.color = ContextCompat.getColor(this, R.color.colorPrimary)
            builder.setAutoCancel(true)
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            builder.priority =
                NotificationCompat.PRIORITY_HIGH //Important for heads-up notification
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(messageBody["detail"]))
            builder.setContentTitle(makeNotificationBold(messageBody["title"]))
            builder.setContentText(messageBody["detail"])
            if (bitmap != null) {
                builder.setLargeIcon(bitmap)
                builder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setBigContentTitle(makeNotificationBold(messageBody["title"]))
                        .setSummaryText(messageBody["detail"])
                        .bigLargeIcon(null)
                )
            }
            builder.setContentIntent(pendingIntent)
            val notification = builder.build()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(Random().nextInt(), notification)
            counterRepository!!.increaseCount(preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID))
        } else if (isSilent != null && isSilent.equals("true", ignoreCase = true)) {
            if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN) && messageBody["conversationId"] != null && messageBody["blockUser"] != null) {
                //counterRepository.increaseMessageNotificationCount(preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID), messageBody.get("conversationId"));
                val message = Message()
                message.conversationId = messageBody["conversationId"]
                message.blockedUser = messageBody["blockUser"]
                SERVICE_EVENT.postValue(message)
            }
            if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN) && messageBody["conversationId"] != null && !Objects.requireNonNull(
                    messageBody["conversationId"]
                ).equals(
                    "",
                    ignoreCase = true
                ) && messageBody["id"] != null && !Objects.requireNonNull(
                    messageBody["id"]
                ).equals("", ignoreCase = true)
            ) {
                //counterRepository.increaseMessageNotificationCount(preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID), messageBody.get("conversationId"));
                val message = Message()
                message.id = messageBody["id"]
                message.conversationId = messageBody["conversationId"]
                message.createdDateTime = messageBody["createdDateTime"]
                message.isLoginUserSame = messageBody["isLoginUserSame"]
                message.messageText = messageBody["messageText"]
                message.messageBy = messageBody["messageBy"]
                message.status = messageBody["status"]
                message.blockedUser = "false"
                SERVICE_EVENT.postValue(message)
                MESSAGE_RECEIVED.postValue(message)
            } else if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN) && messageBody["conversationId"] == null) {
                counterRepository!!.increaseSilentCount(preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID))
                AppUtilities.showLog(TAG, "Count Increased")
            }
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val NOTIFICATION_CHANNEL_ID = "1212"
    }
}