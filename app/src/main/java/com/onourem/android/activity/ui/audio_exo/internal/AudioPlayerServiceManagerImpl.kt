package com.onourem.android.activity.ui.audio_exo.internal

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.util.Util
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio_exo.AudioPlayerServiceManager
import com.onourem.android.activity.ui.audio_exo.AudioPlayerStateListener
import com.onourem.android.activity.ui.audio_exo.internal.factory.AudioPlayerServiceConnectionFactory
import com.onourem.android.activity.ui.audio_exo.internal.factory.AudioPlayerServiceIntentFactory

@SuppressLint("RestrictedApi")
internal class AudioPlayerServiceManagerImpl(
    private val activity: ComponentActivity,
    private val audioPlayerServiceConnectionFactory: AudioPlayerServiceConnectionFactory,
    private val audioPlayerServiceIntentFactory: AudioPlayerServiceIntentFactory,
    stateListener: AudioPlayerStateListener
) : AudioPlayerServiceManager {

    init {
        @Suppress("unused")
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                connect(stateListener)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                disconnect()
            }

//            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//            fun onCreate() {
//                connect(stateListener)
//            }
//
//            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//            fun onDestroy() {
//                disconnect()
//            }
        })
    }

    private var serviceConnection: AudioPlayerServiceConnection? = null

    @androidx.media3.common.util.UnstableApi
    override fun changePlayback(shouldPlay: Boolean) {

        val intent = audioPlayerServiceIntentFactory.createChangePlaybackIntent(shouldPlay)
        Util.startForegroundService(activity, intent)
//        if (shouldPlay){
//            val intent = audioPlayerServiceIntentFactory.createChangePlaybackIntent(shouldPlay)
//            Util.startForegroundService(activity, intent)
//        }else{
//            val intent = audioPlayerServiceIntentFactory.createChangePlaybackPauseIntent(shouldPlay)
//            Util.startForegroundService(activity, intent)
//        }
    }

    @androidx.media3.common.util.UnstableApi
    override fun next() {
        val intent = audioPlayerServiceIntentFactory.createPlayNextIntent()
        Util.startForegroundService(activity, intent)
    }

    @androidx.media3.common.util.UnstableApi
    override fun previous() {
        val intent = audioPlayerServiceIntentFactory.createPlayPreviousIntent()
        Util.startForegroundService(activity, intent)
    }
    @androidx.media3.common.util.UnstableApi
    override fun release() {
        val intent = audioPlayerServiceIntentFactory.createReleaseIntent(true)
        Util.startForegroundService(activity, intent)
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    @androidx.media3.common.util.UnstableApi
    override fun clearMediaList() {
        val intent = audioPlayerServiceIntentFactory.createClearMediaListIntent()
        Util.startForegroundService(activity, intent)
    }


    @androidx.media3.common.util.UnstableApi
    override fun setMediaItemPos(pos: Int) {
        val intent = audioPlayerServiceIntentFactory.createMediaItemPositionIntent(pos)
        Util.startForegroundService(activity, intent)
    }

    @androidx.media3.common.util.UnstableApi
    override fun setMediaList(songs: ArrayList<Song>) {
        val intent = audioPlayerServiceIntentFactory.createSetMediaListIntent(songs)
        Util.startForegroundService(activity, intent)
    }

    @androidx.media3.common.util.UnstableApi
    override fun addMediaList(songs: ArrayList<Song>) {
        val intent = audioPlayerServiceIntentFactory.createAddMediaListIntent(songs)
        Util.startForegroundService(activity, intent)
    }

    private fun connect(stateListener: AudioPlayerStateListener) {
        //Timber.d("Connecting to AudioPlayerServiceConnection")
        serviceConnection =
            audioPlayerServiceConnectionFactory.createConnection(stateListener).also {
                val intent = audioPlayerServiceIntentFactory.createBaseIntent()
                activity.bindService(intent, it, Context.BIND_AUTO_CREATE)
            }
    }

    private fun disconnect() {
        serviceConnection?.let {
            //Timber.d("Disconnecting from AudioPlayerServiceConnection")
            it.onUnbind()
            activity.unbindService(it)
            serviceConnection = null
            return
        }

        //Timber.w("Service was not connected when trying to disconnect!")
    }
}
