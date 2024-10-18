package com.onourem.android.activity.ui.audio_exo.internal

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerNotificationManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.onourem.android.activity.R
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.repository.MediaOperationRepositoryImpl
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio_exo.CurrentContentIntentProvider
import com.onourem.android.activity.ui.audio_exo.MusicWorker
import com.onourem.android.activity.ui.audio_exo.PlayerCommunication
import com.onourem.android.activity.ui.utils.AppUtilities
import dagger.android.DaggerService
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AudioPlayerService : DaggerService(), PlayerCommunication {

    companion object {
        const val KEY_SHOULD_PLAY = "shouldPlay"
        const val KEY_SHOULD_NOT_PLAY = "shouldNotPlay"
        const val INTENT_KEY_SHOULD_PLAY = "shouldPlay"
        const val INTENT_KEY_SHOULD_RELEASE = "shouldRelease"
        const val INTENT_KEY_NEXT = "next"
        const val INTENT_KEY_PREVIOUS = "previous"
        const val INTENT_KEY_SET_MEDIA_LIST = "set_media_list"
        const val INTENT_KEY_MEDIA_ITEM_POSITION = "media_item_position"
        const val INTENT_KEY_MEDIA_ITEM_POSITION_FOR_NEXT_PREV = "media_item_position_for_next_prev"
        const val INTENT_KEY_SET_MEDIA_ITEM = "set_media_item"
        const val INTENT_KEY_ADD_MEDIA_LIST = "add_media_list"
        const val INTENT_KEY_CLEAR_MEDIA_LIST = "clear_media_list"
        const val INTENT_KEY_RESET_PLAYER = "reset"

        private const val PLAYBACK_CHANNEL_ID = "playback_channel_id"
        private const val PLAYBACK_NOTIFICATION_ID = 1246
    }

    private val NOTIFICATION_ID: Int = 123
    private lateinit var bitmapListOfMedia: ArrayList<Bitmap>
    private lateinit var idsList: List<List<String>>
    private var shouldPlay: Boolean = false
    private var mMediaPlayerBeep: MediaPlayer? = null
    private lateinit var mediaItemList: ArrayList<MediaSource>
    private lateinit var songs: ArrayList<Song>
    private lateinit var addSongs: ArrayList<Song>
    private var concatenatingMediaSource: MediaSource? = null
    private var TAG = "PlayerState"

    @JvmField
    @Inject
    var mediaOperationRepositoryImpl: MediaOperationRepositoryImpl? = null

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    @Inject
    internal lateinit var currentContentIntentProvider: CurrentContentIntentProvider

    private var player: ExoPlayer? = null

    private var playerNotificationManager: PlayerNotificationManager? = null

    private var currentlyDisplayedNotification: Notification? = null

    private var playerPrepared: Boolean = false

    private val playerEventListener = @UnstableApi object : Player.Listener {

        override fun onPlayerError(error: PlaybackException) {
            //Timber.w(error, "onPlayerError type: ${error.type}")
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            //Timber.d("onPlayerStateChanged -> playWhenReady: $playWhenReady, playbackState: $playbackState, playbackError: ${player?.playbackError}")

            if (player?.playerError != null) {
                //Timber.d("Error detected - stopping service and notification")
                stopForeground(true)
                clearPlayerNotificationManager()
                playerPrepared = false
                return
            }

//            if (shouldPreparePlayerAgain(playWhenReady, playbackState)) {
//                //Timber.d("Preparing player again")
//                preparePlayer()
//            }

            when {
                currentlyDisplayedNotification != null -> {
                    //Timber.d("Stopping foreground Service")
                    //stopForeground(false)
                    // playerNotificationManager?.setOngoing(false)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        startForeground(PLAYBACK_NOTIFICATION_ID, currentlyDisplayedNotification!!)
                    } else {
                        startForeground(PLAYBACK_NOTIFICATION_ID, currentlyDisplayedNotification!!, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                    }
                }

//                currentlyDisplayedNotification != null -> {
//                    //Timber.d("Starting foreground Service")
//
//                    //playerNotificationManager?.setOngoing(true)
//                }
            }

            when (playbackState) {
                STATE_BUFFERING -> {
                    Log.d(TAG, "STATE_BUFFERING")
                }

                STATE_ENDED -> {
                    Log.d(TAG, "STATE_ENDED")
                }

                STATE_IDLE -> {
                    Log.d(TAG, "STATE_IDLE")
                }

                STATE_READY -> {
                    idsList = listOf(player?.currentMediaItem!!.mediaId.split(","))
//                    if (playWhenReady) {
//                        Log.d(TAG, "PlaybackStatus.PLAYING")
//                    } else {
//                        Log.d(TAG, "PlaybackStatus.PAUSED")
//                    }
                }

                else -> {
                    Log.d(TAG, "PlaybackStatus.IDLE")
                }
            }
        }
    }
//    private val notificationListener = object : NotificationListener {
//
//        fun onNotificationStarted(notificationId: Int, notification: Notification) {
//            //Timber.i("Notification started: $notification")
//            currentlyDisplayedNotification = notification
//            startForeground(notificationId, notification)
//        }
//
//        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
//            //Timber.i("Notification cancelled")
//            currentlyDisplayedNotification = null
//            clearPlayerNotificationManager()
//            stopSelf()
//        }
//    }

    @UnstableApi
    private inner class NotificationListener : PlayerNotificationManager.NotificationListener {
        private var isForegroundService = false

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            when {
                ongoing && !isForegroundService -> {

                    val serviceIntent = Intent(this@AudioPlayerService, AudioPlayerService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val request: OneTimeWorkRequest =
                            OneTimeWorkRequest.Builder(MusicWorker::class.java).addTag("BACKUP_WORKER_TAG").build()
                        WorkManager.getInstance(this@AudioPlayerService).enqueue(request)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this@AudioPlayerService.startForegroundService(serviceIntent)
                    } else {
                        this@AudioPlayerService.startService(serviceIntent)
                    }
//                    ContextCompat.startForegroundService(
//                        this@AudioPlayerService,
//                        Intent(this@AudioPlayerService, this@AudioPlayerService.javaClass)
//                    )
                    currentlyDisplayedNotification = notification
                    //startForeground(notificationId, notification)

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        startForeground(notificationId, notification)
                    } else {
                        startForeground(notificationId, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                    }
                    isForegroundService = true
                }
//                !ongoing && isForegroundService -> {
//                    stopForeground(false)
//                    isForegroundService = false
//                }
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            currentlyDisplayedNotification = null
            clearPlayerNotificationManager()
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }


    override fun onCreate() {
        super.onCreate()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notification = createNotification()
//            startForeground(NOTIFICATION_ID, notification)
//        }
        setupExoPlayer()
    }


    //Setting Up Exoplayer
    @OptIn(UnstableApi::class)
    private fun setupExoPlayer() {

        songs = ArrayList()
        addSongs = ArrayList()
        mediaItemList = ArrayList()
//        concatenatingMediaSource = MediaSource()


//        // Passing Load Control
//        val loadControl = DefaultLoadControl.Builder()
//            .setBufferDurationsMs(25000, 50000, 1500, 2000).build()
//        @ExtensionRendererMode val extensionRendererMode =
//            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//        val renderersFactory =
//            DefaultRenderersFactory(this).setExtensionRendererMode(extensionRendererMode)
//        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(this)

        this.player = ExoPlayer.Builder(this).build()

        this.player?.addListener(playerEventListener)

        this.player?.addAnalyticsListener(
            PlaybackStatsListener( /* keepHistory = */false) { eventTime: AnalyticsListener.EventTime?, playbackStats: PlaybackStats? ->
                playbackStats!!.totalPlayTimeMs
                val seconds = TimeUnit.MILLISECONDS.toSeconds(playbackStats.totalPlayTimeMs)
                val secondsPaused = TimeUnit.MILLISECONDS.toSeconds(playbackStats.totalPausedTimeMs)
                if (seconds > 0 && idsList.isNotEmpty()) {
                    AppUtilities.showLog(
                        "SongSeconds:",
                        seconds.toString() + "mediaId:" + idsList[0][0]
                    )
                    mediaOperationRepositoryImpl!!.addAudioInfoHistory(
                        idsList[0][0],
                        seconds.toString(),
                        idsList[0][1]
                    )
                }

            })
        //this.player!!.playWhenReady = true

    }

    override fun onDestroy() {
        //Timber.i("Destroyed")
        resetExoPlayer()
        super.onDestroy()
    }

    private fun resetExoPlayer() {

        clearPlayerNotificationManager()
        if (player != null) {
            player?.run {
                removeListener(playerEventListener)
                release()
            }
            player = null
            currentlyDisplayedNotification = null
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
            onDestroy()
        } else {
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(intent: Intent): IBinder = LocalBinder()


//    private fun createNotification(): Notification {
//        // Create a notification for your foreground service
//
//        val chan = NotificationChannel(
//            applicationContext.packageName,
//            "My Foreground Service",
//            NotificationManager.IMPORTANCE_LOW
//        )
//        chan.lightColor = Color.BLUE
//        chan.lockscreenVisibility = Notification.VISIBILITY_SECRET
//
//        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//        manager.createNotificationChannel(chan)
//
//        val notificationBuilder = NotificationCompat.Builder(
//            this, "MyChannelId"
//        )
//        return notificationBuilder.setOngoing(true)
//            .setSmallIcon(R.drawable.ic_app_logo_orange_24)
//            .setContentTitle("Onourem")
//            .setContentText("Vocals Service Running in the foreground")
//            .setPriority(NotificationManager.IMPORTANCE_LOW)
//            .setCategory(Notification.CATEGORY_SERVICE)
//            .setChannelId("MyChannelId")
//            .build()
//    }

    @Suppress("DEPRECATION")
    private inline fun <reified T : Parcelable> Intent.getParcelableArrayList(key: String): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getParcelableArrayListExtra(key, T::class.java)
        } else {
            this.getParcelableArrayListExtra(key)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null && intent.extras != null) {
            try {
                // Your existing code
                when {
                    intent.extras != null && intent.hasExtra(INTENT_KEY_SHOULD_PLAY) -> {
                        val shouldPlay = intent.getBooleanExtra(INTENT_KEY_SHOULD_PLAY, true)
                        player?.playWhenReady = shouldPlay
                        this.shouldPlay = shouldPlay

                        //if (!playerPrepared) preparePlayer()
                        playerNotificationManager ?: preparePlayerNotificationManager()

                    }

                    intent.extras != null && intent.hasExtra(INTENT_KEY_SET_MEDIA_LIST) -> {

                        val productList: List<Parcelable> =
                            intent.getParcelableArrayList(INTENT_KEY_SET_MEDIA_LIST) ?: emptyList()

                        if (productList.isNotEmpty()) {
                            songs.clear()
                            productList.map {
                                songs.add(it as Song)
                            }
                            player?.clearMediaItems()
                            preparePlayer()
                        }

                    }

                    intent.extras != null && intent.hasExtra(
                        INTENT_KEY_MEDIA_ITEM_POSITION_FOR_NEXT_PREV
                    ) -> {
                        //prevSongDuration = TimeUnit.MILLISECONDS.toSeconds(player?.currentPosition!!)
                        val pos = intent.getIntExtra(INTENT_KEY_MEDIA_ITEM_POSITION_FOR_NEXT_PREV, -1)
                        goToMediaItemPosition(pos)
                        intent.removeExtra(INTENT_KEY_MEDIA_ITEM_POSITION_FOR_NEXT_PREV)

                    }
                }
            } catch (e: NullPointerException) {
                // Log the exception and relevant information
                e.printStackTrace()
                Log.e("AudioPlayerService", "NullPointerException occurred: ${e.message}")
            }
        } else {
            Log.e("AudioPlayerService", "Intent or extras are null")
        }

        return Service.START_STICKY
    }

    private fun goToMediaItemPosition(pos: Int) {

        if (pos != -1) {
            player?.seekTo(pos, C.TIME_UNSET)
            player?.playWhenReady = false

            //player!!.playWhenReady = true
            if (mMediaPlayerBeep == null) {
                mMediaPlayerBeep = MediaPlayer()
                try {
                    val musicUri: Uri =
                        Uri.parse("android.resource://" + packageName.toString() + "/" + R.raw.beep)
                    //Log.d("MP", "musicUri: $musicUri")
                    mMediaPlayerBeep?.reset()
                    mMediaPlayerBeep?.setDataSource(this@AudioPlayerService, musicUri)
                    mMediaPlayerBeep?.prepare()
                } catch (e: IllegalStateException) {
                    mMediaPlayerBeep?.reset()
                    mMediaPlayerBeep = null
                } catch (e: IOException) {
                    mMediaPlayerBeep?.reset()
                    mMediaPlayerBeep = null
                } catch (e: IllegalArgumentException) {
                    mMediaPlayerBeep?.reset()
                    mMediaPlayerBeep = null
                }
                mMediaPlayerBeep!!.setOnCompletionListener {
                    if (mMediaPlayerBeep != null) {
                        mMediaPlayerBeep?.reset()
                        mMediaPlayerBeep = null
                    }
                    player?.playWhenReady = true
                }

                mMediaPlayerBeep?.start()
            }
        }

    }

    private fun goToPreviousOrBeginning() {
        player?.run {
            if (hasPreviousMediaItem()) {
                seekToPreviousMediaItem()
            } else {
                seekToDefaultPosition()
            }
        }
    }

    private fun goToNext() {
        player?.seekToNextMediaItem()
    }

    @OptIn(UnstableApi::class)
    private fun preparePlayerNotificationManager() {
        //Timber.d("Preparing PlayerNotificationManager")
        val context: Context = this
//        playerNotificationManager = createWithNotificationChannel(
//            context,
//            PLAYBACK_CHANNEL_ID,
//            R.string.channel_name,
//            PLAYBACK_NOTIFICATION_ID,
//            createMediaDescriptionAdapter(context),
//            NotificationListener()
//        ).apply {
//            //setNotificationListener(notificationListener)
//            setPlayer(player)
//            setColor(ContextCompat.getColor(context, R.color.color_white))
//            setPriority(PRIORITY_LOW)
////            setControlDispatcher(DefaultControlDispatcher(0L, 0L))
//            setUsePreviousActionInCompactView(true)
//            setUseNextActionInCompactView(true)
//        }


        playerNotificationManager = PlayerNotificationManager.Builder(
            context,
            PLAYBACK_NOTIFICATION_ID, PLAYBACK_CHANNEL_ID
        )
            .setChannelNameResourceId(R.string.channel_name)
            .setChannelDescriptionResourceId(R.string.channel_des)
            .setMediaDescriptionAdapter(createMediaDescriptionAdapter(context))
            .setNotificationListener(NotificationListener())
            .setSmallIconResourceId(R.drawable.ic_app_logo_orange_24)
//            .setNextActionIconResourceId(R.drawable.next)
//            .setPreviousActionIconResourceId(R.drawable.previous)
//            .setPlayActionIconResourceId(R.drawable.play)
            .build().apply {
                setPlayer(player)
                setColor(ContextCompat.getColor(context, R.color.color_white))
                setPriority(PRIORITY_DEFAULT)
                setUsePreviousActionInCompactView(true)
                setUseNextActionInCompactView(true)
                setUseFastForwardAction(false)
                setUseRewindAction(false)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setUseRewindActionInCompactView(false)
                setColorized(false)
                setColor(R.color.colorAccent)
            }

//        playerNotificationManager.setPlayer(player)
    }

    @OptIn(UnstableApi::class)
    private fun createMediaDescriptionAdapter(context: Context): PlayerNotificationManager.MediaDescriptionAdapter =
        object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): String =
                if (IntRange(0, songs.lastIndex).contains(player.currentMediaItemIndex)) {
                    songs[player.currentMediaItemIndex].title ?: ""
                } else {
                    ""
                }


            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val provideCurrentContentIntent =
                    currentContentIntentProvider.provideCurrentContentIntent(context)

                return PendingIntent.getActivity(
                    context,
                    0,
                    provideCurrentContentIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

            }

            override fun getCurrentContentText(player: Player): String = ""
            // songs[player.currentWindowIndex].description

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback,
            ): Bitmap? {
//                return null
                return getBitmapFromVectorDrawable(R.drawable.ic_app_logo_orange_24)
            }

        }

    @OptIn(UnstableApi::class)
    private fun clearPlayerNotificationManager() {
        playerNotificationManager?.setPlayer(null)
        playerNotificationManager = null
    }

    private fun shouldPreparePlayerAgain(playWhenReady: Boolean, playbackState: Int) =
        playWhenReady && (playbackState == STATE_IDLE || playbackState == STATE_ENDED)

    @OptIn(UnstableApi::class)
    private fun preparePlayer() {

        if (songs.size > 0) {
            playerPrepared = true
            player?.setMediaSources(createMediaSource(songs))
            player?.playWhenReady = true
            player?.prepare()
            playerNotificationManager ?: preparePlayerNotificationManager()
        }

    }

    @UnstableApi
//    private fun createMediaSource(songs: ArrayList<Song>): List<MediaSource> {
//        mediaItemList.clear()
//        //player!!.clearMediaItems()
//        val agent =
//            Util.getUserAgent(this@AudioPlayerService, getString(R.string.app_name))
//
//        if (agent.isNotEmpty()) {
//            val dataSourceFactory = DefaultDataSource.Factory(this)
//
//            // TODO: load data from some real-life source
//            for (song in songs) {
//
//                val str = "${song.audioId},${song.creatorId}"
//                val mediaItem: MediaItem =
//                    MediaItem.Builder().setUri(song.audioUrl)
//                        .setMediaId(str)
//                        .build()
//                val mediaSource =
//                    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//
//                mediaItemList.add(mediaSource)
//            }
//        }
//
//        return mediaItemList
//    }

    private fun createMediaSource(songs: List<Song>): List<MediaSource> {
        mediaItemList.clear()
        val agent = Util.getUserAgent(this@AudioPlayerService, getString(R.string.app_name))

        if (agent.isNotEmpty()) {
            val dataSourceFactory = DefaultDataSource.Factory(this)

            for (song in songs) {

                // Add a null check for song.audioUrl
                if (song.audioUrl != null) {
                    val str = "${song.audioId},${song.creatorId}"
                    val mediaItem: MediaItem = MediaItem.Builder()
                        .setUri(song.audioUrl)
                        .setMediaId(str)
                        .build()
                    val mediaSource =
                        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

                    mediaItemList.add(mediaSource)
                }
            }
        }

        return mediaItemList
    }


//    private fun createMediaSource(songs: List<Song>): List<MediaSource> {
//        return Util.getUserAgent(this@AudioPlayerService, getString(R.string.app_name))
//            .takeIf { it.isNotEmpty() }
//            ?.let { agent ->
//                val dataSourceFactory = DefaultDataSource.Factory(this)
//                songs.map { song ->
//                    val str = "${song.audioId},${song.creatorId}"
//                    val mediaItem: MediaItem = MediaItem.Builder()
//                        .setUri(song.audioUrl)
//                        .setMediaId(str)
//                        .build()
//                    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//                }
//            } ?: emptyList()
//    }


    internal inner class LocalBinder : Binder() {

        internal val boundPlayer: ExoPlayer?
            get() = player

        internal val mediaPlayer: MediaPlayer?
            get() = mMediaPlayerBeep
    }

    fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = AppCompatResources.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    override fun isMediaPlayer(): Boolean {
        return mMediaPlayerBeep != null
    }

    override fun isPlaying(): Boolean {
        return isMediaPlayer() && mMediaPlayerBeep!!.isPlaying
    }
}
