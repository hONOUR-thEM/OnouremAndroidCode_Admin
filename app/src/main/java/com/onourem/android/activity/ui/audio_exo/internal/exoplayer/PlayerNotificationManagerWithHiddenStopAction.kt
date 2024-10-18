package com.onourem.android.activity.ui.audio_exo.internal.exoplayer

//internal class PlayerNotificationManagerWithHiddenStopAction(
//    context: Context?,
//    channelId: String?,
//    notificationId: Int,
//    mediaDescriptionAdapter: MediaDescriptionAdapter?,
//    notificationListener: NotificationListener,
//    customActionReceiver: CustomActionReceiver? = null
//) : PlayerNotificationManager(
//    context!!,
//    channelId!!,
//    notificationId,
//    mediaDescriptionAdapter!!,
//    notificationListener,
//    customActionReceiver
//) {
//
//    override fun getActions(player: Player): MutableList<String> =
//        super.getActions(player).filter { it != PlayerNotificationManager.ACTION_STOP }
//            .toMutableList()
//}

///**
// * @see PlayerNotificationManager
// */
//internal fun createWithNotificationChannel(
//    context: Context,
//    channelId: String,
//    @StringRes channelName: Int,
//    notificationId: Int,
//    mediaDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter,
//    notificationListener: PlayerNotificationManager.NotificationListener
//): PlayerNotificationManagerWithHiddenStopAction {
//    NotificationUtil.createNotificationChannel(
//        context,
//        channelId,
//        channelName,
//        0,
//        NotificationUtil.IMPORTANCE_LOW
//    )
//    return PlayerNotificationManagerWithHiddenStopAction(
//        context,
//        channelId,
//        notificationId,
//        mediaDescriptionAdapter,
//        notificationListener
//    )
//}
