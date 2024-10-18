package com.onourem.android.activity.ui.audio_exo.internal

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.onourem.android.activity.ui.audio_exo.AudioPlayerStateListener

internal class AudioPlayerServiceConnection(private val stateListener: AudioPlayerStateListener) :
    ServiceConnection {

    private var serviceBinder: AudioPlayerService.LocalBinder? = null

    private val playerEventListener = @UnstableApi object : Player.Listener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            //Timber.i("onPlayerStateChanged -> playWhenReady: $playWhenReady, playbackState: $playbackState")
            if (serviceBinder?.boundPlayer != null && serviceBinder?.boundPlayer!!.mediaItemCount > 0) {
                stateListener.onPlaybackStateUpdated(
                    playWhenReady,
                    serviceBinder?.boundPlayer?.playerError != null,
                    playbackState,
                    serviceBinder?.boundPlayer!!.currentMediaItem,
                    serviceBinder?.boundPlayer!!,
                    serviceBinder?.mediaPlayer
                )
                serviceBinder?.boundPlayer?.let { updateCurrentWindowCallbackWithPlayer(it) }
            }

        }

        override fun onPositionDiscontinuity(reason: Int) {
            //Log.i("onPositionDiscontinuity -> reason: $reason")
            when (reason) {
                Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> {
                    //Log.e("onPositionDiscontinuity","DISCONTINUITY_REASON_PERIOD_TRANSITION")
//                    Log.e( "onPositionDiscontinuity", "player.currentWindowIndex ${player.currentWindowIndex}" )
                    val newIndex: Int? = serviceBinder?.boundPlayer?.currentMediaItemIndex
                    stateListener.onMediaItemChanged(newIndex)
                    if (serviceBinder?.boundPlayer != null) {
                        serviceBinder?.boundPlayer?.let {
                            updateCurrentWindowCallbackWithPlayer(it)
                        }
                    }

                }
                Player.DISCONTINUITY_REASON_INTERNAL -> {
                    //Log.e("onPositionDiscontinuity", "DISCONTINUITY_REASON_INTERNAL")
                }
                Player.DISCONTINUITY_REASON_SEEK -> {
                    //Log.e("onPositionDiscontinuity", "DISCONTINUITY_REASON_SEEK")
                    val newIndex: Int? = serviceBinder?.boundPlayer?.currentMediaItemIndex
                    stateListener.onMediaItemChanged(newIndex)
                    if (serviceBinder?.boundPlayer != null) {
                        serviceBinder?.boundPlayer?.let {
                            updateCurrentWindowCallbackWithPlayer(it)
                        }
                    }
                }
//                Player.TIMELINE_CHANGE_REASON_DYNAMIC -> {
//                    if (selectedFunction != ON_PROGRESS && selectedFunction != TRIM && selectedFunction != SPLIT && selectedFunction != SPEED_VIEW) {
//                        Log.e("onPlayerStateChanged", "TIMELINE_CHANGE_REASON_DYNAMIC")
//                        updateCurrentPosition()
//                    }
//                }
                Player.DISCONTINUITY_REASON_REMOVE -> {
                    //Log.e("onPositionDiscontinuity", "DISCONTINUITY_REASON_REMOVE")
                }
                Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> {
                    //Log.e("onPositionDiscontinuity", "DISCONTINUITY_REASON_SEEK_ADJUSTMENT")
                }
                Player.DISCONTINUITY_REASON_SKIP -> {
                    //Log.e("onPositionDiscontinuity", "DISCONTINUITY_REASON_SKIP")
                }
            }


        }

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //Timber.i("Disconnected: $name")
        if (serviceBinder?.boundPlayer != null) {
            serviceBinder?.boundPlayer?.removeListener(playerEventListener)
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service !is AudioPlayerService.LocalBinder) return

        //Timber.i("Connected: $name")
        serviceBinder = service

        val boundPlayer = service.boundPlayer

        if (boundPlayer != null) {
            stateListener.onPlaybackStateUpdated(
                boundPlayer.playWhenReady,
                serviceBinder?.boundPlayer?.playerError != null,
                boundPlayer.playbackState,
                serviceBinder?.boundPlayer?.currentMediaItem,
                serviceBinder?.boundPlayer!!,
                serviceBinder?.mediaPlayer
            )
            updateCurrentWindowCallbackWithPlayer(boundPlayer)
            boundPlayer.addListener(playerEventListener)
        }

    }

    /**
     * Must be called when we call [android.app.Activity.unbindService].
     */
    internal fun onUnbind() {
        serviceBinder?.boundPlayer?.removeListener(playerEventListener)
    }

    private fun updateCurrentWindowCallbackWithPlayer(boundPlayer: ExoPlayer) {
        stateListener.onCurrentWindowUpdated(
            showNextAction = boundPlayer.hasNextMediaItem(),
            showPreviousAction = boundPlayer.shouldShowPrevious()
        )
    }

    private fun ExoPlayer.shouldShowPrevious() =
        this.hasPreviousMediaItem() || this.playbackState == Player.STATE_READY
}
