package com.onourem.android.activity.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.utils.media.Common
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


var YOUTUBE_VIDEO_ID = ""

class YoutubeActivity : AppCompatActivity() {
    private var enterFullscreenButton: ExtendedFloatingActionButton? = null
    private var fullscreenViewContainer: FrameLayout? = null
    private var youTubePlayerView: YouTubePlayerView? = null
    private val TAG = "YoutubeActivity"
    private var isFullscreen = false
    private var youTubePlayer: YouTubePlayer? = null


    var youTubeEmbeddedUrl = "https://www.youtube.com/embed/"


    @SuppressLint("InflateParams", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("youtubeId")) {

            YOUTUBE_VIDEO_ID = intent.getStringExtra("youtubeId").toString()

            if (YOUTUBE_VIDEO_ID.contains("://")) {
                YOUTUBE_VIDEO_ID = Common.extractVideoId(YOUTUBE_VIDEO_ID).toString()
            }
            val layout = layoutInflater.inflate(R.layout.activity_youtube, null) as ConstraintLayout
            setContentView(layout)


//            val webView = findViewById<View>(com.onourem.android.activity.R.id.webView) as WebView
//
//            webView.webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    return false
//                }
//            }
//            //web settings for JavaScript Mode
//            val webSettings: WebSettings = webView.settings
//            webSettings.javaScriptEnabled = true
//            webSettings.domStorageEnabled = true
//            webView.loadData(getHTML(YOUTUBE_VIDEO_ID), "text/html", "utf-8")


            youTubePlayerView = findViewById(R.id.youtube_player_view)
            fullscreenViewContainer = findViewById(R.id.full_screen_view_container)
            enterFullscreenButton = findViewById(R.id.enter_fullscreen_button)

            lifecycle.addObserver(youTubePlayerView!!)

            youTubePlayerView?.enableAutomaticInitialization = false

            val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
                .controls(1) // enable full screen button
                .fullscreen(1)
                .build()

            youTubePlayerView?.addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    isFullscreen = true

                    // the video will continue playing in fullscreenView
                    youTubePlayerView?.visibility = View.GONE
                    fullscreenViewContainer?.visibility = View.VISIBLE
                    fullscreenViewContainer?.addView(fullscreenView)

                    // optionally request landscape orientation
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                    enterFullscreenButton?.setIconResource(R.drawable.ic_fullscreen_exit);
                }

                @SuppressLint("SourceLockedOrientationActivity")
                override fun onExitFullscreen() {
                    isFullscreen = false

                    // the video will continue playing in the player
                    youTubePlayerView?.visibility = View.VISIBLE
                    fullscreenViewContainer?.visibility = View.GONE
                    fullscreenViewContainer?.removeAllViews()
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                    enterFullscreenButton?.setIconResource(R.drawable.ic_fullscreen);

                }
            })

            enterFullscreenButton?.setOnClickListener {
                youTubePlayer?.toggleFullscreen()
            }

            youTubePlayerView?.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(YOUTUBE_VIDEO_ID, 0f)
                    this@YoutubeActivity.youTubePlayer = youTubePlayer

                }
            }, true, iFramePlayerOptions)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView?.release()
    }

    private fun getHTML(videoId: String): String {
        return """<iframe class="youtube-player" 
            style="border: 0; width: 100%; height: 100%;padding:0px; margin:0px" 
            id="ytplayer" 
            type="text/html" 
            src="https://www.youtube.com/embed/$videoId"
            theme=dark 
            autohide=2 
            modestbranding=0 
            showinfo=0 
            autoplay=1 
            fs=0 
            frameborder=0 
            allowfullscreen=1" 
            referrerpolicy="strict-origin-when-cross-origin"
            autobuffer controls 
            onclick="this.play()"></iframe>"""
    }

    //allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture;fullscreen"

//    override fun onInitializationSuccess(
//        provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?,
//        wasRestored: Boolean
//    ) {
////        Log.d(TAG, "onInitializationSuccess: provider is ${provider?.javaClass}")
////        Log.d(TAG, "onInitializationSuccess: youTubePlayer is ${youTubePlayer?.javaClass}")
////        Toast.makeText(this, "Initialized Youtube Player successfully", Toast.LENGTH_SHORT).show()
//
//        youTubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
//        youTubePlayer?.setPlaybackEventListener(playbackEventListener)
//
//        if (!wasRestored) {
//            youTubePlayer?.cueVideo(YOUTUBE_VIDEO_ID)
//        }
//    }

//    override fun onInitializationFailure(
//        provider: YouTubePlayer.Provider?,
//        youTubeInitializationResult: YouTubeInitializationResult?
//    ) {
//        val REQUEST_CODE = 0
//
//        if (youTubeInitializationResult?.isUserRecoverableError == true) {
//            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show()
//        } else {
//            val errorMessage =
//                "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
//            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private val playbackEventListener = object : YouTubePlayer.PlaybackEventListener {
//        override fun onSeekTo(p0: Int) {
//        }
//
//        override fun onBuffering(p0: Boolean) {
//        }
//
//        override fun onPlaying() {
//            // Toast.makeText(this@YoutubeActivity, "Good, video is playing ok", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onStopped() {
//            // Toast.makeText(this@YoutubeActivity, "Video has stopped", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onPaused() {
//            // Toast.makeText(this@YoutubeActivity, "Video has paused", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private val playerStateChangeListener = object : YouTubePlayer.PlayerStateChangeListener {
//        override fun onAdStarted() {
//            //Toast.makeText(this@YoutubeActivity, "Click Ad now, make the video creator rich!", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onLoading() {
//        }
//
//        override fun onVideoStarted() {
//            // Toast.makeText(this@YoutubeActivity, "Video has started", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onLoaded(p0: String?) {
//        }
//
//        override fun onVideoEnded() {
//            //  Toast.makeText(this@YoutubeActivity, "Congratulations! You've completed another video.", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onError(p0: YouTubePlayer.ErrorReason?) {
//        }
//    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayerView?.matchParent()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youTubePlayerView?.wrapContent()
        }
    }
}