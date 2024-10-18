package com.onourem.android.activity.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.widget.ImageViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.activity.AbstractBaseActivity
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.ActivityType
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.CustomScreenPopup
import com.onourem.android.activity.models.GetAppUpgradeInfoResponse
import com.onourem.android.activity.models.GetInstituteCounsellingInfoResponse
import com.onourem.android.activity.models.GetQuestionFilterInfoResponse
import com.onourem.android.activity.models.GetUserLinkInfoResponse
import com.onourem.android.activity.models.GetUserMoodResponseMsgResponse
import com.onourem.android.activity.models.GetWatchListResponse
import com.onourem.android.activity.models.LinkData
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.models.SendMessageResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.WebContent
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.AudioPlayerAdapter
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio_exo.AudioPlayerServiceManager
import com.onourem.android.activity.ui.audio_exo.AudioPlayerStateListener
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService.Companion.KEY_SHOULD_NOT_PLAY
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService.Companion.KEY_SHOULD_PLAY
import com.onourem.android.activity.ui.audio_exo.internal.exoplayer.ProgressTracker
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.circle.models.ContactItem
import com.onourem.android.activity.ui.counselling.CounsellingViewModel
import com.onourem.android.activity.ui.data.DashboardData
import com.onourem.android.activity.ui.data.NotificationData
import com.onourem.android.activity.ui.data.OtherVocalsData
import com.onourem.android.activity.ui.data.VocalsData
import com.onourem.android.activity.ui.data.VocalsDataHold
import com.onourem.android.activity.ui.games.dialogs.SubscriptionStatusDialog
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.message.ConversationsViewModel
import com.onourem.android.activity.ui.message.share.ShareContentSheetFragmentArgs
import com.onourem.android.activity.ui.onboarding.OnboardingActivity
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Events
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.glide.loadCircularImage
import com.onourem.android.activity.ui.utils.listners.RVScrollListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import com.onourem.android.activity.ui.utils.snackbar.NoSwipeBehavior
import com.onourem.android.activity.ui.utils.snackbar.OnouremSnackBar
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

open class DashboardActivity : AbstractBaseActivity<DashboardViewModel>(), NavigationBarView.OnItemSelectedListener {

    private lateinit var counsellingViewModel: CounsellingViewModel
    private var apiResponseUserMood: GetUserMoodResponseMsgResponse? = null
    private var needToShowCounsellingPopup: Boolean = false
    private lateinit var conversationsViewModel: ConversationsViewModel
    private var getWatchListResponse: GetWatchListResponse? = null
    private lateinit var ivMood: AppCompatImageView
    private lateinit var cvMood: MaterialCardView
    private var rvScrollListener: RVScrollListener? = null


    fun getRVScrollListenerReference(): RVScrollListener? {
        return rvScrollListener
    }

    fun setRVScrollListenerReference(rvScrollListener: RVScrollListener) {
        this.rvScrollListener = rvScrollListener
    }

    private var isOnProfileScreen: Boolean = false
    private var userImageBitmap: RoundedBitmapDrawable? = null
    private var navigationView: NavigationView? = null
    private var watchListResponse: GetWatchListResponse? = null
    private var isUserPlayedGame: Int = -1
    private var isBond003AvailableForUsers: Int = -1

    private lateinit var constraintLayoutToolbar: ConstraintLayout
    private var toolbar: Toolbar? = null
    private lateinit var appEndTime: String
    private var displayPhoneButton: String = ""
    private var displayCounsellingPopup: String = ""
    private var phoneInfoImageUrl: String = ""
    private lateinit var appStartTime: String
    private lateinit var userActionViewModel: UserActionViewModel
    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var ivAppLogo: AppCompatImageView
    private lateinit var ivUserPic: AppCompatImageView
    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var hasNext: Boolean = false
    private var exoPlayer: ExoPlayer? = null
    private var tracker: ProgressTracker? = null
    private var TAG = "PlayerState"

    @JvmField
    var dashboardData: DashboardData? = null

    @JvmField
    var dashboardDataAnswered: DashboardData? = null

    @JvmField
    var dashboardDataMQ: DashboardData? = null

    private var vocalsData: VocalsData? = null
    private var vocalsDataHold: VocalsDataHold? = null
    private var vocalsDataHoldFriends: VocalsDataHold? = null
    private var vocalsDataHoldMV: VocalsDataHold? = null
    private var vocalsDataHoldFav: VocalsDataHold? = null
    private var vocalsDataHoldOther: VocalsDataHold? = null
    private var otherVocalsData: OtherVocalsData? = null
    private var notificationData: NotificationData? = null

    private var bufferingCounter = 0
    private var recyclerViewPositionDashboard = 0
    private var recyclerViewPositionFriendsPlaying = 0
    private var recyclerViewPositionPlayGames = 0
    private var recyclerViewPositionDashboardTopView = 0
    private var recyclerViewPositionFriendsPlayingTopView = 0
    private var recyclerViewPositionPlayGamesTopView = 0
    private var recyclerViewPositionDashboardAnswered = 0
    private var recyclerViewPositionDashboardTopViewAnswered = 0
    private var recyclerViewPositionDashboardMQ = 0
    private var recyclerViewPositionDashboardTopViewMQ = 0
    private var recyclerViewPositionNotification = 0

    private var dashboardSelectedFilter = 0
    private var vocalsSelectedFilter = 0
    private var recyclerViewPositionNotificationTopView = 0

    private var recyclerViewPositionVocals = 0
    private var recyclerViewPositionVocalsTopView = 0

    private var recyclerViewPositionVocalsFriends = 0
    private var recyclerViewPositionVocalsTopViewFriends = 0

    private var recyclerViewPositionVocalsMyVocals = 0
    private var recyclerViewPositionVocalsTopViewMyVocals = 0

    private var recyclerViewPositionVocalsFav = 0
    private var recyclerViewPositionVocalsTopViewFav = 0

    private var recyclerViewPositionVocalsOther = 0
    private var recyclerViewPositionVocalsTopViewOther = 0

    private var readyCounter = 0

    private var contactList: ArrayList<ContactItem>? = ArrayList()

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)
    private var jobToCounselling: Job? = null
    private var userInteractionJob: Job? = null
    private val lastInteractionTime = AtomicLong(0)


    private var playingStatus: Int = -1
    private lateinit var rivProfile: AppCompatImageView
    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private var audioPlayerAdapter: AudioPlayerAdapter? = null
    private lateinit var playPause: AppCompatImageView
    var options = RequestOptions().fitCenter().transform(CircleCrop()).error(R.drawable.ic_error)
        .placeholder(R.drawable.default_user_profile_image).error(R.drawable.default_user_profile_image)


    // private var deviceSongs: MutableList<Song>? = null
    private var songsFromServer = mutableListOf<Song>()

    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList()

    val ffmpegQueryExtension = FFmpegQueryExtension()

    private lateinit var clPlayRecording: ConstraintLayout
    private lateinit var cvClose: MaterialCardView
    private lateinit var seekBarCurrentHint: MaterialTextView
    private lateinit var seekBarHint: MaterialTextView
    private lateinit var layoutManager: LinearLayoutManager

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var drawerLayout: DrawerLayout? = null
    private var rvAudioPlayer: ViewPager2? = null
    private var navController: NavController? = null
    private var parentLayout: View? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var ivProfileImage: AppCompatImageView? = null
    private var ivWave: AppCompatImageView? = null
    private var tvUserName: AppCompatTextView? = null
    private var tvAppTitle: AppCompatTextView? = null
    private var tvAppSubscriptionStatus: MaterialTextView? = null
    private var isMoodDialogShowing = false
    private var hasPopupBannerTrue = false
    private var customScreenPopup: CustomScreenPopup? = null
    private var ivIconVerified: RoundedImageView? = null
    private var silentNotificationCount = 0
    private var moodsPopupShownAfterOneDayCount = 0
    private var showFunCardCount = 0
    private var showAudioVocals = 0
    private var mediaOperationViewModel: MediaOperationViewModel? = null
    private var linkUserId = ""
    private var linkType = ""
    private var isLinkVerified = ""


    var freeTrialRemainingDays = -1
    var canUserAccessApp = true
    private var gotFreeTrialRemainingDaysResponse = false

//    private fun startCounsellingTaskAfterDelay() {
//        // Cancel the existing job if any
//        jobToCounselling?.cancel()
//
//        // Create a new coroutine
//        jobToCounselling = CoroutineScope(Dispatchers.Main).launch {
//            // Delay for a specific period of time (e.g., 5 seconds)
//            delay(5000)
//
//            // Execute your task here
//            executeCounsellingTask()
//
//        }
//    }
//
//    private fun executeCounsellingTask() {
//
//    }
//
//    private fun startIdleCheck() {
//        userInteractionJob = CoroutineScope(Dispatchers.Main).launch {
//            while (isActive) {
//                delay(IDLE_CHECK_INTERVAL)
//
//                // Check for user interaction
//                if (!hasUserInteraction()) {
//                    // Nothing is happening, perform your action
//                    performIdleAction()
//                }
//            }
//        }
//    }
//
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        // Update the last interaction time whenever there is a touch event
//        lastInteractionTime.set(System.currentTimeMillis())
//        return super.dispatchTouchEvent(ev)
//    }
//
//    private fun hasUserInteraction(): Boolean {
//        val currentTime = System.currentTimeMillis()
//        val lastInteraction = lastInteractionTime.get()
//
//        // Check if there has been any user interaction in the last IDLE_THRESHOLD milliseconds
//        return currentTime - lastInteraction < IDLE_THRESHOLD
//    }

//    private fun performIdleAction() {
//        // Do something when nothing is happening on the screen
//        if (needToShowCounsellingPopup) {
//            needToShowCounsellingPopup = false
//            jobToCounselling?.cancel()
//            userInteractionJob?.cancel()
//            navController?.navigate(MobileNavigationDirections.actionGlobalNavAskCounselling())
//        }
//    }

    companion object {
        private const val IDLE_CHECK_INTERVAL = 6000L // 5 seconds (adjust as needed)
        private const val IDLE_THRESHOLD = 6000L // 15 seconds (adjust as needed)
    }

    internal val audioPlayerStateListener = object : AudioPlayerStateListener {

        override fun onPlaybackStateUpdated(
            playWhenReady: Boolean,
            hasError: Boolean,
            playbackState: Int,
            currentMediaItem: MediaItem?,
            exoPlayer: ExoPlayer,
            mediaPlayer: MediaPlayer?
        ) = updatePlaybackState(playWhenReady, playbackState, exoPlayer, mediaPlayer)

        override fun onCurrentWindowUpdated(showNextAction: Boolean, showPreviousAction: Boolean) {
            updateCurrentWindow(hasNext = showNextAction)
        }

        override fun onMediaItemChanged(newMediaItemIndex: Int?) {
            updatePLayerView(newMediaItemIndex!!)
        }
    }

    fun exoPlayerPause(pause: Boolean) {
        if (exoPlayer != null) {
            if (pause) {
                exoPlayer?.pause()
            }
        }
    }

    fun exoPlayerPlay(play: Boolean) {
        if (exoPlayer != null) {
            if (play) {
                exoPlayer?.play()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val menuItemProfile = menu.findItem(R.id.profile_nav)
        val item = MenuItemCompat.getActionView(menuItemProfile)
//        val viewItemProfile = menuItemProfile.actionView

        val profile = item.findViewById<ImageView>(R.id.profile)

        val loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
        )

//        profile.loadCircularImage(loginResponse.profilePicture, 0f, Color.parseColor("#008080"))
        //glideUserProfile()

        profile.setOnClickListener {
//            if (canUserAccessApp) {
//                onOptionsItemSelected(menuItemProfile)
//            } else {
//                openSubscriptionStatusDialog()
//            }

            onLogout()
        }

        val menuItemSearch = menu.findItem(R.id.search_nav)
        val view = menuItemSearch.actionView
        view?.setOnClickListener {
            if (canUserAccessApp) {
                onOptionsItemSelected(menuItemSearch)
            } else {
                openSubscriptionStatusDialog()
            }

        }
        val menuItemPhone = menu.findItem(R.id.phone_nav)
        val viewItemPhone = menuItemPhone.actionView
        viewItemPhone?.setOnClickListener {
            if (canUserAccessApp) {
                onOptionsItemSelected(menuItemPhone)
            } else {
                openSubscriptionStatusDialog()
            }
        }

        menuItemProfile.icon = userImageBitmap

        menu!!.findItem(R.id.phone_nav).isVisible = false
        menu!!.findItem(R.id.search_nav).isVisible = false
        menu!!.findItem(R.id.profile_nav).isVisible = true

        return true
    }

    fun hideShowBondGameMenu() {
        val menu = navigationView!!.menu

        val target = menu.findItem(R.id.nav_friends_circle_main)

        target.isVisible = isBond003AvailableForUsers() > 0
    }

    private fun hideShowSubscriptionMenu() {
        val menu = navigationView!!.menu

        val target = menu.findItem(R.id.nav_subscription)

        target.isVisible = isSubscriptionAvailableForLoginUser()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu!!.findItem(R.id.search_nav).isVisible = true
//
//        menu.findItem(R.id.phone_nav).isVisible = displayPhoneButton == "Y" && displayCounsellingPopup == "Y"

        menu!!.findItem(R.id.phone_nav).isVisible = false
        menu!!.findItem(R.id.search_nav).isVisible = false
        menu!!.findItem(R.id.profile_nav).isVisible = true


        return super.onPrepareOptionsMenu(menu)

    }

    /**
     * Create a new bordered bitmap with the specified borderSize and borderColor
     *
     * @param borderSize - The border size in pixel
     * @param borderColor - The border color
     * @return A new bordered bitmap with the specified borderSize and borderColor
     */
    private fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int): Bitmap {
        val borderOffset = (borderSize * 2).toInt()
        val halfWidth = width / 2
        val halfHeight = height / 2
        val circleRadius = Math.min(halfWidth, halfHeight).toFloat()
        val newBitmap = Bitmap.createBitmap(
            width + borderOffset, height + borderOffset, Bitmap.Config.ARGB_8888
        )

        // Center coordinates of the image
        val centerX = halfWidth + borderSize
        val centerY = halfHeight + borderSize

        val paint = Paint()
        val canvas = Canvas(newBitmap).apply {
            // Set transparent initial area
            drawARGB(0, 0, 0, 0)
        }

        // Draw the transparent initial area
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, circleRadius, paint)

        // Draw the image
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(this, borderSize, borderSize, paint)

        // Draw the createBitmapWithBorder
        paint.xfermode = null
        paint.style = Paint.Style.STROKE
        paint.color = borderColor
        paint.strokeWidth = borderSize
        canvas.drawCircle(centerX, centerY, circleRadius, paint)
        return newBitmap
    }

    open fun updateMenuItems(displayPhoneButton: String?, displayCounsellingPopup: String?) {
        this.displayPhoneButton = displayPhoneButton ?: ""
        this.displayCounsellingPopup = displayCounsellingPopup ?: ""
        invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.profile_nav -> {
                if (!isGuestUser()) {
                    bottomNavigationView!!.visibility = View.GONE
                    if (!isOnProfileScreen) {
                        navController!!.navigate(
                            MobileNavigationDirections.actionGlobalNavProfile(
                                null, preferenceHelper!!.getString(
                                    Constants.KEY_LOGGED_IN_USER_ID
                                )
                            )
                        )
                    }
                    true
                } else {
                    showGuestPopup("Profile", "You can see your here.")
                    false
                }
            }

            R.id.search_nav -> {
                if (!isGuestUser()) {
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavSearch(
                            true, preferenceHelper!!.getString(
                                Constants.KEY_LOGGED_IN_USER_ID
                            ), true, null
                        )
                    )
                    true
                } else {
                    showGuestPopup("Search", "You can search people on Onourem here.")
                    false
                }
            }

            R.id.phone_nav -> {
                if (!isGuestUser()) {
//
                    if (apiResponseUserMood != null && displayPhoneButton == "Y") {
                        getInstitutionCounsellingInfo()
                    }

                    true
                } else {
                    showGuestPopup("Phone", "You can call people on Onourem here.")
                    false
                }
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getInstitutionCounsellingInfo() {

        counsellingViewModel.getInstitutionCounsellingInfo()
            .observe(this) { apiResponse: ApiResponse<GetInstituteCounsellingInfoResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        hideProgress()

//                        navController!!.navigate(
//                            MobileNavigationDirections.actionGlobalNavInfoCounselling(apiResponseUserMood!!)
//                        )
                        navController!!.navigate(
                            MobileNavigationDirections.actionGlobalNavAskCounselling(
                                apiResponseUserMood as GetUserMoodResponseMsgResponse,
                                apiResponse.body
                            )
                        )
                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                }
            }

    }

    //
    private fun getUserMoodResponseMsg() {

        viewModel.userMoodResponseMsg().observe(this) { apiResponse: ApiResponse<GetUserMoodResponseMsgResponse> ->
                if (apiResponse.loading) {
                    //showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        canUserAccessApp = apiResponse.body.canUserAccessApp ?: true
                        freeTrialRemainingDays = apiResponse.body.freeTrialRemainingDays ?: -1
                        gotFreeTrialRemainingDaysResponse = true

                        apiResponseUserMood = apiResponse.body
//                        freeTrialRemainingDays = 6
                        setAppSubscriptionStatus()
                        preferenceHelper!!.putValue(Constants.CHANGED_MOOD, Gson().toJson(apiResponse.body))
                        updateMenuItems(
                            apiResponse.body.displayPhoneButton,
                            apiResponse.body.displayCounsellingPopup
                        )
                        hideShowSubscriptionMenu()
                        //hideProgress()
                    } else {
                        //hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    //hideProgress()
                }
            }

    }


    private fun updatePLayerView(newMediaItemIndex: Int) {
        rvAudioPlayer!!.setCurrentItem(newMediaItemIndex, false)
    }

    @Inject
    lateinit var audioManager: AudioPlayerServiceManager

    private var currentlyPlaying: Boolean = false


    private fun updateCurrentWindow(hasNext: Boolean) {
        this.hasNext = hasNext
    }

    private fun updatePlaybackState(
        playWhenReady: Boolean,
        playbackState: Int,
        exoPlayer: ExoPlayer,
        mediaPlayer: MediaPlayer?,
    ) {


        if (this.mediaPlayer == null) {
            this.mediaPlayer = mediaPlayer
        }

        if (this.exoPlayer == null) {
            this.exoPlayer = exoPlayer
        }

        if (this.tracker == null) {
            tracker = ProgressTracker(this.exoPlayer) { currentPosition, total ->
                run {
                    if (total > 0) {
                        seekBarHint.text = Utilities.milliSecondsToTimer(total)
                    } else {
                        seekBarHint.text = "00:00"
                    }

                    if (currentPosition > 0) {
                        seekBarCurrentHint.text = Utilities.milliSecondsToTimer(currentPosition)
                    } else {
                        seekBarCurrentHint.text = "00:00"
                    }
                    // Displaying time completed playing

                    if (seekBarHint.text == seekBarCurrentHint.text && !hasNext) {
                        updatePlayingStatus(false)
                    }
                }
            }
        }



        when (playbackState) {
            Player.STATE_BUFFERING -> {
                Log.d(TAG, "STATE_BUFFERING")
                playingStatus = Player.STATE_BUFFERING
                circularProgressIndicator.visibility = View.VISIBLE
                if (songsFromServer.size > 0 && exoPlayer.mediaItemCount > 0) {

                    if (IntRange(0, songsFromServer.size).contains(exoPlayer.currentMediaItemIndex)) {
                        val song = songsFromServer[exoPlayer.currentMediaItemIndex]
                        song.isAudioPreparing = false
                        song.isPlaying = false
                        if (bufferingCounter == 0) {
                            // viewModel.setUpdateUI(song)
                            bufferingCounter++
                        }
                        readyCounter = 0
                        setMusicPlayerVisibility(true)
                    }

                }

            }

            Player.STATE_ENDED -> {
                Log.d(TAG, "STATE_ENDED")
                playingStatus = Player.STATE_ENDED
                circularProgressIndicator.visibility = View.GONE
                bufferingCounter = 0
                readyCounter = 0
            }

            Player.STATE_IDLE -> {
                setMusicPlayerVisibility(false)
                Log.d(TAG, "STATE_IDLE")
                playingStatus = Player.STATE_IDLE
                circularProgressIndicator.visibility = View.GONE
            }

            Player.STATE_READY -> {

                circularProgressIndicator.visibility = View.GONE

//                var percentageWatched = (100 * position / duration);

                if (songsFromServer.size > 0 && exoPlayer.mediaItemCount > 0) {
                    if (IntRange(0, songsFromServer.lastIndex).contains(exoPlayer.currentMediaItemIndex)) {
                        val song = songsFromServer[exoPlayer.currentMediaItemIndex]
                        song.isAudioPreparing = false
                        song.isPlaying = true
                        if (readyCounter == 0) {
                            viewModel.setUpdateUI(song)
                            playPause.setImageResource(R.drawable.pause)
                            readyCounter++
                        }
                        bufferingCounter = 0

                        setMusicPlayerVisibility(true)

                        playingStatus = if (playWhenReady) {
                            Log.d(TAG, "PlaybackStatus.PLAYING")
                            Player.STATE_READY

                        } else {
                            Log.d(TAG, "PlaybackStatus.PAUSED")
                            5

                        }
                    }
                }


            }

            else -> {
                setMusicPlayerVisibility(false)
                circularProgressIndicator.visibility = View.GONE
                playingStatus = Player.STATE_IDLE
                Log.d(TAG, "PlaybackStatus.IDLE")
                bufferingCounter = 0
                readyCounter = 0
            }
        }

        currentlyPlaying = playWhenReady
        updatePlayingStatusExoPlayer()
    }

    override fun layoutResource(): Int {
        return R.layout.activity_dashboard
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val response = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION)
        val date = preferenceHelper!!.getLong(Constants.KEY_TIME_MILIES_LAST_MOOD_SYNC)
        viewModel.setIsMoodsDialogShowing(
            TextUtils.isEmpty(response) || date > 0 && AppUtilities.getDayDifference(
                Date(date)
            ) > 0
        )
        linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
        linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
        isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)

        if (intent.hasExtra("linkData")) {
            val linkData = intent.getParcelableExtra<LinkData>("linkData")

            if (linkData != null) {
                preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkData.linkUserId)
                preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkData.linkType)
                preferenceHelper!!.putValue(
                    Constants.KEY_DYNAMIC_LINK, linkData.pendingDynamicLinkData
                )
            }
        }

//        if (!linkType.equals("", ignoreCase = true) && linkType.equals("Card", ignoreCase = true)) {
//            linkType = ""
//            if (!linkUserId.equals("", ignoreCase = true)) {
//                viewModel.setShowFunCards(true)
//            }
//        }
        if (!linkType.equals("", ignoreCase = true) && linkType.equals(
                "Audio", ignoreCase = true
            )
        ) {
            linkType = ""
            if (!linkUserId.equals("", ignoreCase = true)) {
                viewModel.setShowAudioVocals(true)
            }
        }
        //Toast.makeText(this, "new intent", Toast.LENGTH_SHORT).show();
    }


    override fun onResume() {
        super.onResume()
        if (isInActivity) {
            preferenceHelper!!.putValue(
                Constants.KEY_SP_LAST_INTERACTION_TIME, System.currentTimeMillis()
            )
        } else {
            showReloadDialog()
        }

        loadMessageBadge()

        if (navController!!.currentDestination!!.id == R.id.nav_admin_menu || navController!!.currentDestination!!.id == R.id.nav_vocals_main || navController!!.currentDestination!!.id == R.id.nav_audio_games_previous_list || navController!!.currentDestination!!.id == R.id.nav_notifications || navController!!.currentDestination!!.id == R.id.nav_conversations) {
            bottomNavigationView!!.visibility = View.GONE
            setupHomeScreenLayout()
        } else {
            bottomNavigationView!!.visibility = View.GONE
            setupOtherScreenLayout()
        }

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        appStartTime = df.format(Date()).toString()
        appEndTime = ""
        updateAppTime(appStartTime, appEndTime)


    }

    override fun onStart() {
        super.onStart()

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        appStartTime = df.format(Date()).toString()
        //preferenceHelper!!.putValue(Constants.APP_START_TIME, appStartTime)

    }

    override fun onPause() {
        super.onPause()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        appEndTime = df.format(Date()).toString()
        //preferenceHelper!!.putValue(Constants.APP_END_TIME, appEndTime)
        updateAppTime(appStartTime, appEndTime)

        appStartTime = ""
        appEndTime = ""


    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.

            if (Build.VERSION.SDK_INT >= 33) {

                TwoActionAlertDialog.showAlert(
                    this, "Alert", "Notifications are blocked, you can go to settings to change", null, "Cancel", "Settings"
                ) { item1: Pair<Int?, Any?> ->
                    if (TwoActionAlertDialog.ACTION_RIGHT == item1.first) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }

//                Snackbar.make(
//                    findViewById(R.id.parentLayout),
//                    "Notifications are blocked",
//                    Snackbar.LENGTH_INDEFINITE
//                ).setAction("Close") {
//
//                }.setAction("Settings") {
//                    // Responds to click on the action
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    val uri: Uri = Uri.fromParts("package", packageName, null)
//                    intent.data = uri
//                    startActivity(intent)
//                }.show()

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= 33) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    Log.e(TAG, "onCreate: PERMISSION GRANTED")
                    //sendNotification(this)
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
//                    Snackbar.make(
//                        findViewById(R.id.parentLayout),
//                        "Notifications are blocked",
//                        Snackbar.LENGTH_INDEFINITE
//                    ).setAction("Settings") {
//                        // Responds to click on the action
//                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        val uri: Uri = Uri.fromParts("package", packageName, null)
//                        intent.data = uri
//                        startActivity(intent)
//                    }.show()

                    TwoActionAlertDialog.showAlert(
                        this, "Alert", "Notifications are blocked, you can go to settings to change", null, "Cancel", "Settings"
                    ) { item1: Pair<Int?, Any?> ->
                        if (TwoActionAlertDialog.ACTION_RIGHT == item1.first) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }
                    //  Toast.makeText(this, "NOT ALLOWED", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Log.e(TAG, "onCreate: ask for permissions")
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    //  if (Build.VERSION.SDK_INT >= 33) {
                    //   Log.e(TAG, "onCreate: 33" )
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                    // }
                }
            }
        }

        userActionViewModel = ViewModelProvider(this, viewModelFactory!!)[UserActionViewModel::class.java]

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                acquire(5 * 60 * 1000L /*5 minutes*/)
            }
        }

//        viewModel.selectedExpressionNeedCounselling.observe(this) {
//            if (it != null) {
//                viewModel.setSelectedExpressionNeedCounselling(null)
//                needToShowCounsellingPopup = true
//                startIdleCheck()
//            }
//        }

//        viewModel.updatePaymentStatus.observe(this) { payment ->
//            if (payment != null) {
//
//            }
//        }

        viewModel.updateFreePaymentStatus.observe(this) { payment ->
            if (payment != null && payment == "Y") {
                navController!!.navigate(MobileNavigationDirections.actionGlobalNavFreePurchaseStatus())
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (intent.hasExtra("linkData")) {
            val linkData = intent.getParcelableExtra<LinkData>("linkData")

            if (linkData != null) {
                preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkData.linkUserId)
                preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkData.linkType)
                preferenceHelper!!.putValue(
                    Constants.KEY_DYNAMIC_LINK, linkData.pendingDynamicLinkData
                )
            }
        }

        val crashlytics = FirebaseCrashlytics.getInstance()
        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
        mediaOperationViewModel = ViewModelProvider(this, viewModelFactory!!)[MediaOperationViewModel::class.java]
        questionGamesViewModel = ViewModelProvider(this, viewModelFactory!!)[QuestionGamesViewModel::class.java]
        counsellingViewModel = ViewModelProvider(this, viewModelFactory!!)[CounsellingViewModel::class.java]
        conversationsViewModel = ViewModelProvider(this, viewModelFactory!!)[ConversationsViewModel::class.java]
        //initializeViews()
        if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN)) {
            val loginResponse = Gson().fromJson(
                preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
            )
            if (loginResponse != null) {
                crashlytics.setUserId(loginResponse.userId)
            }
        }

        getUserMoodResponseMsg()


        val navHostFragment = NavHostFragment.create(R.navigation.mobile_navigation)
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, navHostFragment, "dashboard-nav-host")
            .commitNow()
        navController = navHostFragment.navController
        val playSoloGroup = PlayGroup()
        playSoloGroup.playGroupId = "FFF"
        playSoloGroup.playGroupName = getString(R.string.label_friends_playing)
        playSoloGroup.isDummyGroup = true
        val bundle = Bundle()
        bundle.putParcelable("selectedGroup", playSoloGroup)
        bundle.putString("isFrom", "Solo")
        navController!!.setGraph(R.navigation.mobile_navigation, bundle)
//        val ivClouds = findViewById<AppCompatImageView>(R.id.ivClouds)
//        ivClouds.visibility = View.GONE
        drawerLayout = findViewById(R.id.drawer_layout)
        ivWave = findViewById(R.id.ivHeader)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        constraintLayoutToolbar = findViewById(R.id.constraintLayoutToolbar)
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener { onSupportNavigateUp() }
        Objects.requireNonNull(supportActionBar!!).setDisplayShowTitleEnabled(false)
        preferenceHelper!!.putValue(
            Constants.KEY_SP_LAST_INTERACTION_TIME, System.currentTimeMillis()
        )
        parentLayout = findViewById(R.id.parentLayout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationView = findViewById<NavigationView>(R.id.nav_view)
        ivMood = findViewById<AppCompatImageView>(R.id.ivMood)
        cvMood = findViewById(R.id.cvMood)
        ivProfileImage = navigationView!!.getHeaderView(0).findViewById(R.id.ivProfile)
        ivIconVerified = navigationView!!.getHeaderView(0).findViewById(R.id.ivIconVerified)
        tvUserName = navigationView!!.getHeaderView(0).findViewById(R.id.tvName)

        val txtVersion: MaterialTextView = navigationView!!.findViewById(R.id.txtVersion)
        txtVersion.text = String.format(
            "%s%s (%s)", "v", AppUtilities.getAppVersionName(this), AppUtilities.getAppVersion()
        )
        getAppUpgradeInfo()
        if (preferenceHelper!!.getString(Constants.KEY_NEW_USER).equals("Y", ignoreCase = true)) {
            viewModel.setIsMoodsDialogShowing(true)
//            viewModel.setShowFunCards(true)
        }
        viewModel.isMoodsDialogShowing.observe(this) { show: Boolean ->
            isMoodDialogShowing = show
        }

        navigationView!!.getHeaderView(0).setOnClickListener(ViewClickListener {

            if (canUserAccessApp) {
                if (!isGuestUser()) {
                    drawerLayout!!.closeDrawer(GravityCompat.START, true)
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavProfile(
                            null, preferenceHelper!!.getString(
                                Constants.KEY_LOGGED_IN_USER_ID
                            )
                        )
                    )
                } else {
                    showGuestPopup("Profile", "After you login, You can see your profile here")
                }
            } else {
                openSubscriptionStatusDialog()
            }


        })

        NavigationUI.setupWithNavController(navigationView!!, navController!!)
        NavigationUI.setupWithNavController(bottomNavigationView!!, navController!!)
        bottomNavigationView!!.setOnItemSelectedListener(this)
        bottomNavigationView!!.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    dashboardData = null
                    navController!!.navigate(R.id.nav_home)
                    val navBuilder = NavOptions.Builder()
                    val navOptions = navBuilder.setPopUpTo(R.id.nav_home, false).build()

                    navController!!.navigate(R.id.nav_home, null, navOptions)
                }

                R.id.nav_notifications -> {
                    if (!isGuestUser()) {
                        notificationData = null
                        navController!!.navigate(R.id.nav_notifications)
                        val navBuilder = NavOptions.Builder()
                        val navOptions = navBuilder.setPopUpTo(R.id.nav_home, false).build()

                        navController!!.navigate(R.id.nav_notifications, null, navOptions)
                    } else {
                        //showGuestPopup("Notification", "You can see notifications from your friends")
                        navController!!.navigate(
                            MobileNavigationDirections.actionGlobalNavDummyGuest(
                                "Notification", "You can access your notifications after you login."
                            )
                        )
                    }
                }

                R.id.nav_vocals_main -> {
                    vocalsData = null
                    val bundleVocal = Bundle()
                    bundleVocal.putString("vocalUserId", "")
                    bundleVocal.putString("userName", "")
                    bundleVocal.putString("linkUserId", "")
                    bundleVocal.putString("audioIdFromNotification", "")
                    navController!!.navigate(R.id.nav_vocals_main, bundleVocal)
                    val navBuilder = NavOptions.Builder()
                    val navOptions = navBuilder.setPopUpTo(R.id.nav_home, false).build()

                    navController!!.navigate(R.id.nav_vocals_main, bundleVocal, navOptions)
                }
            }
        }
        navigationView!!.setNavigationItemSelectedListener { item: MenuItem ->
            drawerLayout!!.closeDrawer(GravityCompat.START, true)
            if (item.itemId == R.id.nav_review_onourem) {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(
                    String.format(
                        "https://play.google.com/store/apps/details?id=%s", packageName
                    )
                )
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(this, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                }
                bottomNavigationView!!.visibility = View.GONE
                return@setNavigationItemSelectedListener false

            } else if (item.itemId == R.id.nav_conversations) {

                if (!isGuestUser()) {
                    val conversation = Conversation()
                    conversation.id = ""
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavConversations(conversation)
                    )
                    bottomNavigationView!!.visibility = View.VISIBLE
                    //loadMessageBadge()
                    return@setNavigationItemSelectedListener true
                } else {
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavDummyGuest(
                            "Chats", "Start chatting privately with friends after you login."
                        )
                    )
                    // showGuestPopup("Chats", "You can chat with friends here")
                }


            } else if (item.itemId == R.id.nav_profile) {

                if (!isGuestUser()) {

                    bottomNavigationView!!.visibility = View.GONE
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavProfile(
                            null, preferenceHelper!!.getString(
                                Constants.KEY_LOGGED_IN_USER_ID
                            )
                        )
                    )
                    return@setNavigationItemSelectedListener true
                } else {
                    showGuestPopup("Profile", "After you login, You can see your profile here")
                }

            } else if (item.itemId == R.id.nav_dashboard_new) {
                if (!isGuestUser()) {

                    bottomNavigationView!!.visibility = View.GONE
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavDashboardNew(
                            GetWatchListResponse()
                        )
                    )
                    return@setNavigationItemSelectedListener true
                } else {
                    showGuestPopup("Dashboard", "")
                }

            } else if (item.itemId == R.id.nav_create_own_question) {

                if (!isGuestUser()) {
                    val playGroup = PlayGroup()
                    playGroup.playGroupId = "YYY"
                    playGroup.playGroupName = "My Questions"
                    playGroup.isDummyGroup = true
                    navController!!.navigate(MobileNavigationDirections.actionGlobalNavCreateOwnQuestion(playGroup))
                    return@setNavigationItemSelectedListener true
                } else {
                    showGuestPopup("Create Q", "After you login, You can create your own questions here")
                }

            } else if (item.itemId == R.id.nav_question_games) {

                if (!isGuestUser()) {
                    navController!!.navigate(MobileNavigationDirections.actionGlobalNavQuestionGames(null, "Solo"))
                    return@setNavigationItemSelectedListener true
                } else {
                    showGuestPopup("My-O-Clubs", "After you login, You can see My-O-Clubs here")
                }

            } else if (item.itemId == R.id.nav_appreciate_dialog) {

                if (!isGuestUser()) {
                    navController!!.navigate(MobileNavigationDirections.actionGlobalNavAppreciateDialog())
                    return@setNavigationItemSelectedListener true
                } else {
                    showGuestPopup("Appreciate", "After you login, You can appreciate friends here")
                }

            }
//            else if (item.itemId == R.id.nav_ambassador) {
//
//                if (!isGuestUser()) {
//                    navController!!.navigate(MobileNavigationDirections.actionGlobalNavAmbassador())
//                    return@setNavigationItemSelectedListener true
//                } else {
//                    showGuestPopup("Apply Ambassador", "After you login, You can apply ambassador here")
//                }
//
//            }
            else if (item.itemId == R.id.nav_friends_circle_main) {
                if (isUserPlayedGame() > 0) {
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavFriendsThoughts(true)
                    )
                    return@setNavigationItemSelectedListener false
                } else {
                    if (isUserPlayedGame() == -1) {
                        Toast.makeText(
                            this, "Fetching Game data, please retry in few seconds", Toast.LENGTH_SHORT
                        ).show()
                        return@setNavigationItemSelectedListener false
                    } else {
                        navController!!.navigate(
                            MobileNavigationDirections.actionGlobalNavFriendsCircleMain()
                        )
                        return@setNavigationItemSelectedListener false
                    }
                }
            } else if (item.itemId == R.id.nav_search) {
                if (!isGuestUser()) {
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavSearch(
                            true, preferenceHelper!!.getString(
                                Constants.KEY_LOGGED_IN_USER_ID
                            ), true, null
                        )
                    )
                    return@setNavigationItemSelectedListener true
                } else {
                    showGuestPopup("Search", "You can search people on Onourem here.")
                }
            } else if (item.itemId == R.id.nav_contact_us) {

                navController!!.navigate(MobileNavigationDirections.actionGlobalNavContactUs("ContactUs"))
                setupOtherScreenLayout()
                bottomNavigationView!!.visibility = View.GONE
                return@setNavigationItemSelectedListener true

            } else if (item.itemId == R.id.nav_subscription) {

                navController!!.navigate(MobileNavigationDirections.actionGlobalNavMySubscriptions())
                setupOtherScreenLayout()
                bottomNavigationView!!.visibility = View.GONE
                return@setNavigationItemSelectedListener true

            } else if (item.itemId == R.id.nav_onourem_intro) {

                navController!!.navigate(MobileNavigationDirections.actionGlobalNavOnouremIntro())
                setupOtherScreenLayout()
                bottomNavigationView!!.visibility = View.GONE
                return@setNavigationItemSelectedListener true

            } else if (item.itemId == R.id.nav_report_problem) {

                navController!!.navigate(MobileNavigationDirections.actionGlobalNavReportProblem("ReportProblem"))
                setupOtherScreenLayout()
                bottomNavigationView!!.visibility = View.GONE
                return@setNavigationItemSelectedListener true

            } else if (item.itemId == R.id.nav_logout) {

                if (!isGuestUser()) {
                    viewModel.logout()
                }
                onLogout()
                drawerLayout!!.closeDrawer(GravityCompat.START, true)
                bottomNavigationView!!.visibility = View.GONE
                return@setNavigationItemSelectedListener false

            }

            if (item.itemId == R.id.nav_home) {

                viewModel.setShowHomeBadge(false)
                tvAppTitle!!.visibility = View.GONE
                //setupHomeScreenLayout(tvAppTitle)
                bottomNavigationView!!.visibility = View.VISIBLE
            } else {
                //setupOtherScreenLayout()
            }

            NavigationUI.onNavDestinationSelected(item, navController!!)

        }


        if (isGuestUser()) {
            userActionViewModel!!.actionMutableLiveData.observe(this) { action: Action<Any?>? ->
                if (action != null && action.actionType != ActionType.DISMISS) {
                    // userActionViewModel!!.actionConsumed()
                    when (action.actionType) {
                        ActionType.GUEST -> {
                            run {
                                onLogout()
                            }
                        }

                        else -> {}
                    }

                }
            }
        }


        tvAppTitle = findViewById(R.id.tvAppTitle)
        tvAppSubscriptionStatus = findViewById(R.id.tvAppSubscriptionStatus)
        ivAppLogo = findViewById(R.id.ivAppLogo)
        ivUserPic = findViewById(R.id.ivUserPic)
        //setupHomeScreenLayout(tvAppTitle)
        //MusicPlayer Implementation
        clPlayRecording = findViewById(R.id.clPlayRecording)
        cvClose = findViewById(R.id.cvClose)
        rvAudioPlayer = findViewById(R.id.rvAudioPlayer)
        playPause = findViewById(R.id.playPause)
        seekBarCurrentHint = findViewById(R.id.seekBarCurrentHint)
        seekBarHint = findViewById(R.id.seekBarHint)
        rivProfile = findViewById(R.id.rivProfile)
        circularProgressIndicator = findViewById(R.id.circularProgressView)
        layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        songsFromServer = ArrayList()

        //setAppSubscriptionStatus()

        tvAppSubscriptionStatus!!.setOnClickListener {
            openSubscriptionStatusDialog()
        }

        playPause.setOnClickListener(ViewClickListener {

            if (canUserAccessApp) {
                updatePlayingStatus(true)
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                    updatePlayingStatus(false)
                } else {
                    audioManager.changePlayback(!currentlyPlaying)
                    updatePlayingStatus(!currentlyPlaying)
                }
            } else {
                openSubscriptionStatusDialog()
            }


        })

//        ivAppLogo.setOnClickListener(ViewClickListener {
//            if (navController!!.currentDestination!!.id == R.id.nav_conversation_details) {
//                val args =
//                    navController!!.getBackStackEntry(navController!!.currentDestination!!.id).arguments
//                if (args != null) {
//                    val conversation = args.getParcelable<Conversation>("conversation")
//                    navController!!.navigate(
//                        MobileNavigationDirections.actionGlobalNavMediaView(
//                            1,
//                            conversation!!.profilePicture!!
//                        )
//                    )
//                }
//            }
//            else {
////                if (navController!!.currentDestination!!.id != R.id.nav_profile  ) {
////
////                }
//
//                bottomNavigationView!!.visibility = View.GONE
//                navController!!.navigate(
//                    MobileNavigationDirections.actionGlobalNavProfile(
//                        null,
//                        preferenceHelper!!.getString(
//                            Constants.KEY_LOGGED_IN_USER_ID
//                        )
//                    )
//                )
//            }
//        })

        cvClose.setOnClickListener(ViewClickListener {

            if (canUserAccessApp) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                }
                exoPlayer!!.pause()
                updatePlayingStatus(false)
                setMusicPlayerVisibility(false)

                if (navController!!.currentDestination!!.id == R.id.nav_vocals_main) {
                    if (IntRange(0, songsFromServer.lastIndex).contains(exoPlayer?.currentMediaItemIndex)) {
                        val song = songsFromServer[exoPlayer!!.currentMediaItemIndex]
                        song.isPlaying = false
                        song.isAudioPreparing = false
                    }
                }
            } else {
                openSubscriptionStatusDialog()
            }

        })

        rvAudioPlayer!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        rvAudioPlayer!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                //rvAudioPlayer!!.isUserInputEnabled = false
//                skipByPosition(audioPlayerAdapter!!.getPlayingItem(position))
//                seekBarCurrentHint.text = "--:--"
//                seekBarHint.text = "--:--"
//                playPause.isEnabled = false
//                clPlayRecording.isEnabled = false
//                seekBar.progress = 0
//                seekBar.isEnabled = false
                val song = songsFromServer[position]

                audioManager.setMediaItemPos(position)
                AppUtilities.showLog("mediaItemPos", position.toString())
//                rvAudioPlayer!!.setCurrentItem(position, false)

                AppUtilities.setUserProfile(
                    this@DashboardActivity, rivProfile, song.profilePictureUrl
                )
//                if (song.profilePictureUrl.contains("userProfileDefaultPic.jpeg")) {
//                    Glide.with(this@DashboardActivity)
//                        .load(R.drawable.ic_profile_placeholder)
//                        .apply(options)
//                        .into(rivProfile)
//                } else {
//                    Glide.with(this@DashboardActivity)
//                        .load(song.profilePictureUrl)
//                        .apply(options)
//                        .into(rivProfile)
//                }

//                Glide.with(this@DashboardActivity)
//                    .load(song.profilePictureUrl)
//                    .apply(options)
//                    .into(rivProfile)
//                if (navController!!.currentDestination!!.id == R.id.nav_audio_games){
//                    viewModel.setUpdateScrollPosition(song)
//                }

                super.onPageSelected(position)
            }
        })

        navController!!.addOnDestinationChangedListener { controller: NavController, destination: NavDestination, _: Bundle? ->
            if (destination is FloatingWindow) {
                //tvAppTitle!!.visibility = View.GONE
                return@addOnDestinationChangedListener
            }
            isOnProfileScreen = false
            if (destination.id == R.id.nav_bootom_sheet || destination.id == R.id.nav_user_action_bottom_sheet || destination.id == R.id.nav_picker_bottomsheet_dialog_fragment || destination.id == R.id.nav_edit_add_watch_list || destination.id == R.id.nav_watch_list || destination.id == R.id.nav_fun_cards || destination.id == R.id.nav_friends_circle_question_view_pager || destination.id == R.id.nav_question_games || destination.id == R.id.nav_invite_friends) {
                return@addOnDestinationChangedListener
            }

            if (destination.id == R.id.nav_home) {
                viewModel.setShowHomeBadge(false)
                tvAppTitle!!.visibility = View.GONE
                setAppSubscriptionStatus()
                //setupHomeScreenLayout(tvAppTitle)
            } else {
                tvAppSubscriptionStatus!!.visibility = View.GONE
                //setupOtherScreenLayout()
            }

            if (destination.id == R.id.nav_admin_menu || destination.id == R.id.nav_vocals_main || destination.id == R.id.nav_audio_games_previous_list || destination.id == R.id.nav_notifications || destination.id == R.id.nav_conversations) {
                bottomNavigationView!!.visibility = View.GONE
                setupHomeScreenLayout()
                if (destination.id != R.id.nav_conversations) {
                    loadMessageBadge()
                }
            } else {
                bottomNavigationView!!.visibility = View.GONE
                setupOtherScreenLayout()
            }

            if (destination.id == R.id.nav_web_external_content) {
                bottomNavigationView!!.visibility = View.GONE
                tvAppTitle!!.visibility = View.GONE
                constraintLayoutToolbar.visibility = View.GONE
            } else {
                constraintLayoutToolbar.visibility = View.VISIBLE
            }



            if (destination.id == R.id.nav_play_games) {
                tvAppTitle!!.visibility = View.GONE
                setupOtherScreenLayout()
                val playGroup = controller.getBackStackEntry(R.id.nav_play_games).arguments!!["selectedGroup"] as PlayGroup?
                if (playGroup != null) {
                    if (playGroup.playGroupId.equals("AAA", ignoreCase = true)) {
                        tvAppTitle!!.visibility = View.GONE
                        tvAppTitle!!.text = getString(R.string.label_solo_games)
                    } else if (playGroup.playGroupId.equals("ZZZ", ignoreCase = true)) {
                        tvAppTitle!!.visibility = View.GONE
                        tvAppTitle!!.text = getString(R.string.label_all_questions_played)
                    } else if (playGroup.playGroupId.equals("YYY", ignoreCase = true)) {
                        tvAppTitle!!.visibility = View.GONE
                        tvAppTitle!!.text = getString(R.string.label_questions_i_created)
                    } else if (!playGroup.isDummyGroup) {
                        tvAppTitle!!.visibility = View.VISIBLE
                        tvAppTitle!!.text = "${playGroup.playGroupName} O-Club"
                    }
                }
                return@addOnDestinationChangedListener
            }
            if (destination.id == R.id.nav_question_games) {
                tvAppTitle!!.text = getString(R.string.app_name)
                //setupOtherScreenLayout();
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_one_to_many_question_details || destination.id == R.id.nav_d_to_one_question_details || destination.id == R.id.nav_one_to_one_question_details) {
                setupOtherScreenLayout()
                tvAppTitle!!.visibility = View.VISIBLE
                val args = controller.getBackStackEntry(destination.id).arguments
                val playGroup = args!!["playGroup"] as PlayGroup?
                if (playGroup != null) {
                    if (!(playGroup.playGroupId.equals(
                            "AAA", ignoreCase = true
                        ) || playGroup.playGroupId.equals(
                            "YYY", ignoreCase = true
                        ) || playGroup.playGroupId.equals(
                            "ZZZ", ignoreCase = true
                        ))
                    ) {
                        if (playGroup.playGroupId.equals("0", ignoreCase = true)) {
                            playGroup.playGroupName = getString(R.string.label_solo_games)
                        }
                        tvAppTitle!!.text = playGroup.playGroupName
                    } else if (destination.id == R.id.nav_one_to_many_question_details) {
                        bottomNavigationView!!.visibility = View.GONE
                        tvAppTitle!!.text = getString(R.string.label_solo_games)
                    } else if (destination.id == R.id.nav_d_to_one_question_details) {
                        bottomNavigationView!!.visibility = View.GONE
                        tvAppTitle!!.text = getString(R.string.title_one_way_question)
                    } else if (destination.id == R.id.nav_one_to_one_question_details) {
                        bottomNavigationView!!.visibility = View.GONE
                        tvAppTitle!!.text = getString(R.string.title_two_way_question)
                    }
                }
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_audio_comments_list || destination.id == R.id.nav_post_comment_list || destination.id == R.id.nav_audio_games_previous_list) {
                bottomNavigationView!!.visibility = View.GONE
            } else if (destination.id == R.id.nav_notification_external_post_details_fragment) {
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.text = getString(R.string.title_external_post_details)
                bottomNavigationView!!.visibility = View.GONE
            } else if (destination.id == R.id.nav_notifications_message_details) {
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.text = getString(R.string.title_message_details)
                bottomNavigationView!!.visibility = View.GONE
            }else if (destination.id == R.id.nav_schedule_question) {
                tvAppTitle!!.visibility = View.GONE
                bottomNavigationView!!.visibility = View.GONE
            } else if (destination.id == R.id.nav_task_message_question_details) {
                tvAppTitle!!.visibility = View.VISIBLE
                bottomNavigationView!!.visibility = View.GONE
                val args = controller.getBackStackEntry(destination.id).arguments
                val question = args!!["question"] as LoginDayActivityInfoList?
                if (question != null) {
                    if (ActivityType.getActivityType(question.activityType!!) == ActivityType.TASK) {
                        tvAppTitle!!.text = getString(R.string.title_act_of_kindness)
                    } else if (ActivityType.getActivityType(question.activityType!!) == ActivityType.MESSAGE) {
                        tvAppTitle!!.text = getString(R.string.title_appreciate_message)
                    } else {
                        tvAppTitle!!.text = ""
                    }
                }
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_search) {
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.text = getString(R.string.title_people)
            } else if (destination.id == R.id.nav_conversations) {
                bottomNavigationView!!.removeBadge(R.id.nav_conversations)
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.text = getString(R.string.title_conversations)
            } else if (destination.id == R.id.nav_f_of_f_search) {
                tvAppTitle!!.visibility = View.VISIBLE
                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val userId = args.getString("inputUserId")
                    val friendName = args.getString("friendName")
                    if (!preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                            .equals(userId, ignoreCase = true) && !TextUtils.isEmpty(friendName)
                    ) {
                        tvAppTitle!!.text = String.format(
                            "%s's Friends", friendName!!.split(" ".toRegex()).toTypedArray()[0]
                        )
                    } else tvAppTitle!!.text = getString(R.string.title_people)
                } else tvAppTitle!!.text = getString(R.string.title_people)
            } else if (destination.id == R.id.nav_profile) {
                tvAppTitle!!.visibility = View.VISIBLE
                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val userId = args.getString("profileId")
//                    val friendName = args.getString("friendName")
                    if (!preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID).equals(userId, ignoreCase = true)) {
                        tvAppTitle!!.text = getString(R.string.title_friends_profile)
                        isOnProfileScreen = false
                    } else {
                        tvAppTitle!!.text = getString(R.string.title_my_profile)
                        isOnProfileScreen = true
                    }
                } else {
                    tvAppTitle!!.text = getString(R.string.title_my_profile)
                    isOnProfileScreen = true
                }
            } else if (destination.id == R.id.nav_web_content) {
                tvAppTitle!!.visibility = View.VISIBLE
                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val webContent: WebContent = args.getParcelable("webContent")!!
                    if (webContent.title.equals(
                            getString(R.string.label_settings_row_3), ignoreCase = true
                        )
                    ) {
                        tvAppTitle!!.text = getString(R.string.label_settings_row_3)
                    } else if (webContent.title.equals(
                            getString(R.string.label_settings_row_4), ignoreCase = true
                        )
                    ) {
                        tvAppTitle!!.text = getString(R.string.label_settings_row_4)
                    } else if (webContent.title.equals(
                            getString(R.string.label_settings_row_5), ignoreCase = true
                        )
                    ) {
                        tvAppTitle!!.text = getString(R.string.label_settings_row_5)
                    }
                }
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_profile_settings) {
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.text = getString(R.string.label_profile_settings)
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_push_notifications_settings) {
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.text = getString(R.string.label_push_notification_settings)
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_fun_cards) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_invite_friends) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_vocals_main) {
                bottomNavigationView!!.visibility = View.VISIBLE
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_onourem_intro) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_phone_call) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_friends_circle_question_view_pager) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_friends_thoughts) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_select_contacts) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_contacts_list) {
                tvAppTitle!!.visibility = View.GONE
                tvAppTitle!!.text = ""
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_otp) {
                tvAppTitle!!.visibility = View.VISIBLE
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_create_activities_main) {
                tvAppTitle!!.visibility = View.GONE
                return@addOnDestinationChangedListener
            }
//            else if =(destination.id == R.id.nav_ambassador) {
//                tvAppTitle!!.visibility = View.VISIBLE
//                tvAppTitle!!.text = "Apply to be an Onourem Ambassador"
//                return@addOnDestinationChangedListener
//            }
            else if (destination.id == R.id.nav_others_vocals) {
                bottomNavigationView!!.visibility = View.GONE
//                loadMessageBadge()
                tvAppTitle!!.visibility = View.VISIBLE

                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val userId = args.getString("vocalUserId")
                    val userName = args.getString("userName")
                    if (userId.equals("", ignoreCase = true)) {
                        tvAppTitle!!.text = "Trending Vocals"
                    } else {
                        tvAppTitle!!.text = "$userName's Vocals"
                    }
                }
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_dummy_guest) {
                bottomNavigationView!!.visibility = View.VISIBLE
//                loadMessageBadge()
                tvAppTitle!!.visibility = View.VISIBLE

                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val title = args.getString("title")
                    tvAppTitle!!.text = title
                }
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_conversation_details) {
                bottomNavigationView!!.visibility = View.GONE
                tvAppTitle!!.visibility = View.VISIBLE
                bottomNavigationView!!.removeBadge(R.id.nav_conversations)

                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val conversation: Conversation? = args.getParcelable("conversation")
                    tvAppTitle!!.text = conversation!!.userName

                    ivAppLogo.visibility = View.GONE
                    ivUserPic.visibility = View.VISIBLE
                    AppUtilities.setUserProfile(
                        this@DashboardActivity, ivUserPic, conversation.profilePicture
                    )
                }
                return@addOnDestinationChangedListener
            } else if (destination.id == R.id.nav_recorder || destination.id == R.id.nav_recorder_settings || destination.id == R.id.nav_audio_editor) {
                tvAppTitle!!.visibility = View.VISIBLE
                tvAppTitle!!.setText(R.string.lebal_record)
                bottomNavigationView!!.visibility = View.GONE
                return@addOnDestinationChangedListener
            }else if (destination.id == R.id.nav_admin_vocals) {
                tvAppTitle!!.setVisibility(View.VISIBLE)
                tvAppTitle!!.setText(R.string.lebal_vocals)

                val args = controller.getBackStackEntry(destination.id).arguments
                if (args != null) {
                    val userId = args.getString("vocalUserId")
                    val userName = args.getString("userName")
                    if (userId.equals("", ignoreCase = true)) {
                        val selectedFilterInt =
                            preferenceHelper!!.getString(Constants.KEY_SELECTED_FILTER_INT)
                        var selectedFilter = ""
                        when (selectedFilterInt) {
                            "0" -> selectedFilter = "Pending"
                            "1" -> selectedFilter = "Approved & UnScheduled"
                            "2" -> selectedFilter = "Approved & Scheduled"
                            "3" -> selectedFilter = "Approved & Published"
                            "4" -> selectedFilter = "BlackListed"
                            else -> {
                                selectedFilter = "Pending"
                                preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
                            }
                        }
                        tvAppTitle!!.text = selectedFilter
                    } else {
                        tvAppTitle!!.text = "$userName's Vocals"
                    }
                }
                return@addOnDestinationChangedListener
            }
            if (!TextUtils.isEmpty(destination.label)) tvAppTitle!!.text = destination.label

            if (songsFromServer.size > 0 && playingStatus == 3) {
                setMusicPlayerVisibility(true)
            } else {
                setMusicPlayerVisibility(false)
            }

            if (destination.id == R.id.nav_ask_counselling) {
                tvAppTitle!!.visibility = View.GONE
            }

            setUserProfileOnWave()

        }

        viewModel.selectedExpression.observe(this) {
            updateMood(it)
            setMood()
        }

        viewModel.notificationCount.observe(this) { integer: Int? ->
            if (integer != null && integer > 0) {
                val badge = bottomNavigationView!!.getOrCreateBadge(R.id.nav_notifications)
                badge.number = integer
                badge.maxCharacterCount = 3
                badge.backgroundColor = ContextCompat.getColor(this, R.color.color_red)
            } else {
                bottomNavigationView!!.removeBadge(R.id.nav_notifications)
            }
        }

        Events.MESSAGE_RECEIVED.observe(this) {
            val badge = bottomNavigationView!!.getOrCreateBadge(R.id.nav_conversations)
            badge.backgroundColor = ContextCompat.getColor(this, R.color.color_6)
        }

        viewModel.showHomeBadge.observe(this) { showBadge: Boolean ->
            if (showBadge) {
                val badge = bottomNavigationView!!.getOrCreateBadge(R.id.nav_home)
                badge.backgroundColor = ContextCompat.getColor(this, R.color.color_red)
            } else {
                bottomNavigationView!!.removeBadge(R.id.nav_home)
            }
        }
        viewModel.showInAppReview.observe(this) { show: Boolean ->
            if (show) {
                showInAppReviewPopup()
            }
        }

        //mediaOperationViewModel!!.getAllAudioPlayBackHistory(preferenceHelper.getString(KEY_LOGGED_IN_USER_ID)).observe(this, this::updateAudioHistory10bySize);
        viewModel.showFunCards.observe(this) { show: Boolean ->
            if (show) {
                showFunCardCount += 1
                if (!isMoodDialogShowing) {
                    showFunCards()
                }
            }
        }

        viewModel.showVocalsFragment.observe(this) { show: Boolean ->
            if (show) {
                viewModel.setShowVocalsFragment(false)
                val navBuilder = NavOptions.Builder()
                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                navController!!.navigate(
                    MobileNavigationDirections.actionGlobalNavVocalsMain(
                        "", "", "", ""
                    ), navOptions
                )
            }
        }
        viewModel.showAudioVocals.observe(this) { show: Boolean ->
            if (show) {
                showAudioVocals += 1
                if (!isMoodDialogShowing) {
                    showAudioVocals()
                }
            }
        }

//        conversationsViewModel.sendInvite.observe(this) { pair: kotlin.Pair<List<UserList>, LoginDayActivityInfoList> ->
//            if (pair.first.isNotEmpty()){
//                inviteFriends(pair.second, pair.first)
//            }
//        }

//        conversationsViewModel.sendPendingMessagesForInvite.observe(this) { pair: kotlin.Pair<List<UserList>, GetUserLinkInfoResponse> ->
//            if (pair.first.isNotEmpty()){
//                sendMultipleMessages(pair.first, pair.second)
//            }
//        }

//        conversationsViewModel.sendPendingMessagesForArgs.observe(this) { pair: kotlin.Pair<List<UserList>, ShareContentSheetFragmentArgs> ->
//            if (pair.first.isNotEmpty()){
//                sendMultipleMessages(pair.first, pair.second)
//            }
//        }

        viewModel.moodsDialogShowingAfterOneDay.observe(this) { show: Boolean ->
            if (show) {
                moodsPopupShownAfterOneDay()
            }
        }

//        viewModel.notificationSilentCount.observe(this) { countSilent: String? ->
//
//            if (countSilent != null && !TextUtils.isEmpty(countSilent)) {
//                if (countSilent.toInt() > 0) {
//                    silentNotificationCount = countSilent.toInt()
//                    if (!isMoodDialogShowing) {
//                        userWatchList
//                    } else {
//                        if (moodsPopupShownAfterOneDayCount > 0) {
//                            userWatchList
//                        }
//                    }
//                }
//            }
//        }

        mediaOperationViewModel!!.playerOperation.observe(this) {
            if (it.isNotEmpty() && it == KEY_SHOULD_NOT_PLAY && exoPlayer != null) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                    updatePlayingStatus(false)
                }
                exoPlayer!!.pause()
                setMusicPlayerVisibility(false)
            } else if (it.isNotEmpty() && it == KEY_SHOULD_PLAY && exoPlayer != null) {
                exoPlayer!!.play()
            }
        }

        hideShowBondGameMenu()

        //glideUserProfile()

        getUserWatchList()

        val value = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION)
        val expressionList = Gson().fromJson(value, UserExpressionList::class.java)
        expressionList?.let { updateMood(it) }


        ivMood.setOnClickListener(ViewClickListener {

            if (canUserAccessApp) {
                if (this.getWatchListResponse != null) {
                    navController!!.navigate(MobileNavigationDirections.actionGlobalNavDashboardNew(getWatchListResponse!!))
                }
            } else {
                openSubscriptionStatusDialog()
            }

        })
    }

    private fun inviteFriends(loginDayActivityInfoList: LoginDayActivityInfoList, friendsList: List<UserList>) {
        viewModel.getUserLinkInfo(
            loginDayActivityInfoList?.activityType,
            loginDayActivityInfoList?.activityId,
            "48",
            loginDayActivityInfoList?.activityText
        ).observe(this) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    sendMultipleMessages(friendsList, apiResponse.body)
                    hideProgress()
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun setAppSubscriptionStatus() {

        defaultEnabledMenuItems()
        enableDisableMenuItems(true)

        if (freeTrialRemainingDays > 7) {
            tvAppSubscriptionStatus!!.visibility = View.GONE
        } else {
            if (gotFreeTrialRemainingDaysResponse) {
                tvAppSubscriptionStatus!!.visibility = View.VISIBLE
                if (freeTrialRemainingDays >= 0) {
                    enableDisableMenuItems(true)
                    tvAppSubscriptionStatus!!.background = ContextCompat.getDrawable(this, R.drawable.rounded_left_green)
                    tvAppSubscriptionStatus!!.text = if (freeTrialRemainingDays > 1) {
                        "$freeTrialRemainingDays Days Remaining"
                    } else {
                        if (freeTrialRemainingDays == 1) {
                            "1 Day Remaining"
                        } else {
                            "Last Day Remaining"
                        }
                    }
                } else {
                    tvAppSubscriptionStatus!!.background = ContextCompat.getDrawable(this, R.drawable.rounded_left_red)
                    tvAppSubscriptionStatus!!.text = "Subscription Expired"
                    enableDisableMenuItems(false)
                }
            } else {
                tvAppSubscriptionStatus!!.visibility = View.GONE
            }
        }
    }

    private fun defaultEnabledMenuItems() {
        navigationView!!.menu.findItem(R.id.nav_profile).isEnabled = true
        navigationView!!.menu.findItem(R.id.nav_settings).isEnabled = true
        navigationView!!.menu.findItem(R.id.nav_report_problem).isEnabled = true
        navigationView!!.menu.findItem(R.id.nav_contact_us).isEnabled = true
        navigationView!!.menu.findItem(R.id.nav_logout).isEnabled = true
    }

    private fun enableDisableMenuItems(value: Boolean) {
        navigationView!!.menu.findItem(R.id.nav_dashboard_new).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_friends_circle_main).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_appreciate_dialog).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_create_own_question).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_search).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_privacy_groups).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_question_games).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_invite_friends).isEnabled = value
//        navigationView!!.menu.findItem(R.id.nav_ambassador).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_subscription).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_onourem_intro).isEnabled = value
        navigationView!!.menu.findItem(R.id.nav_review_onourem).isEnabled = value
    }


    private fun getUserWatchList() {
        viewModel.getUserWatchList("").observe(
            this
        ) { apiResponse: ApiResponse<GetWatchListResponse> ->
            if (apiResponse.loading) {
                //showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                //hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    preferenceHelper!!.putValue(
                        Constants.KEY_GAME_POINTS, apiResponse.body.gamePoint
                    )
                    getWatchListResponse = apiResponse.body
                    //                    viewClicksEnabled(false);
                    //navController.navigate(DashboardFragmentDirections.actionNavHomeToWatchListDialogFragment(apiResponse.body, "DashboardFragment"));
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                //  hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    private fun updateMood(userExpressionList: UserExpressionList) {
//        try {
//            val drawable = AppCompatResources.getDrawable(this, userExpressionList.moodImage)
////        val drawable = ContextCompat.getDrawable(this, userExpressionList.moodImage)
//            if (drawable != null) {
//                ivMood.setImageDrawable(drawable)
//            }
//        }catch (e : Exception) {
//            e.printStackTrace()
//        }

        GlideApp.with(this).load(userExpressionList.moodImage).apply(options).into(ivMood)

    }

    private fun glideUserProfile() {
        val loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
        )

        Glide.with(this).asBitmap().load(loginResponse.profilePicture).apply(RequestOptions.circleCropTransform())
            .override(100, 100).placeholder(R.drawable.default_user_profile_image).error(R.drawable.default_user_profile_image)
            .addListener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Bitmap?>, isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap, model: Any, target: Target<Bitmap?>?, dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {

                    resource.run {
                        RoundedBitmapDrawableFactory.create(
                            resources, createBitmapWithBorder(4f, Color.RED)
                        ).apply {
                            isCircular = true
                            userImageBitmap = this
                            invalidateOptionsMenu()
                        }
                    }

                    return true
                }
            }).submit()
    }

    private fun setUserProfileOnWave() {
        ivAppLogo.visibility = View.VISIBLE
        ivUserPic.visibility = View.GONE
    }

    private fun showWatchListUpdate(response: GetWatchListResponse?) {
        val totalSize = response!!.userWatchList!!.size
        val message: String
        if (totalSize > 0) {
            val userWatchList = response.userWatchList!![0]
            message = if (totalSize == 1) {
                userWatchList.firstName + " has updated the mood recently."
            } else {
                userWatchList.firstName + " & " + (totalSize - 1) + " others have updated their mood recently."
            }
            val snackBar = OnouremSnackBar.make((parentLayout as ViewGroup?)!!, Snackbar.LENGTH_INDEFINITE).setText(message)
                .setAction("View", {
                    viewModel.resetSilent()
                    silentNotificationCount = 0
                    moodsPopupShownAfterOneDayCount = 0
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavMoodsToday(
                            response
                        )
                    )
                }, View.VISIBLE).setCloseAction {
                    silentNotificationCount = 0
                    moodsPopupShownAfterOneDayCount = 0
                    viewModel.resetSilent()
                }
            snackBar.view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            snackBar.behavior = NoSwipeBehavior()
            snackBar.show()
        }
    }

    fun showCampaignRewards() {
        val snackBar = OnouremSnackBar.make((parentLayout as ViewGroup?)!!, Snackbar.LENGTH_INDEFINITE).setText("Rewards Here")
            .setAction("View", {
//                viewModel.resetSilent()
//                silentNotificationCount = 0
//                moodsPopupShownAfterOneDayCount = 0
                navController!!.navigate(MobileNavigationDirections.actionGlobalNavFunCards(""))
            }, View.VISIBLE).setCloseAction {
//                silentNotificationCount = 0
//                moodsPopupShownAfterOneDayCount = 0
//                viewModel.resetSilent()
            }
        snackBar.view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        snackBar.behavior = NoSwipeBehavior()
        snackBar.show()

        updateAppTime(appStartTime, appEndTime)
    }

    private fun updateAppTime(appStartTime: String, appEndTime: String) {

        viewModel.updateAverageTimeInfo(appStartTime, appEndTime).observe(this) {
            if (it.isSuccess && it.body != null) {
                if (it.body.errorCode.equals("000", ignoreCase = true)) {
//                    Toast.makeText(
//                        this,
//                        "StartTime : ${this.appStartTime} EndTime : ${this.appEndTime}",
//                        Toast.LENGTH_LONG
//                    ).show()
                } else {
                    showAlert(it.body.errorMessage)
                }
            }
        }

    }
//
//    private fun handlePermissionResult(permissionResult: PermissionResult) {
//        when (permissionResult) {
//            is PermissionResult.PermissionGranted -> {
//
//            }
//            is PermissionResult.PermissionDenied -> {
//                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
//            }
//            is PermissionResult.ShowRational -> {
//                showAlert(
//                    "Permissions Needed",
//                    "We need permission to work functionalities like record audio, capture images/video.",
//                    ViewClickListener {
//                        when (permissionResult.requestCode) {
//
//                            5 -> {
//                                coroutineScope.launch(Dispatchers.Main) {
//                                    handlePermissionResult(
//                                        PermissionManager.requestPermissions(
//                                            this@DashboardActivity,
//                                            5,
//                                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                                            Manifest.permission.CAMERA,
//                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                            Manifest.permission.RECORD_AUDIO,
//                                            Manifest.permission.READ_CONTACTS,
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                    })
//
//            }
//            is PermissionResult.PermissionDeniedPermanently -> {
//                showAlert(
//                    "Permissions Needed",
//                    "You have denied app permissions permanently, We need permissions to work functionalities like record audio, capture images/video. Please go to Settings and give Onourem App required permissions to use the app.",
//                    ViewClickListener {
//                        openAppSystemSettings()
//                    })
//            }
//        }
//    }

    private fun setMood() {
        val expressionMessage = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION_MESSAGE)
        if (!TextUtils.isEmpty(expressionMessage)) {
            preferenceHelper!!.putValue(Constants.KEY_SELECTED_EXPRESSION_MESSAGE, "")
            //            new Handler(Looper.getMainLooper()).postDelayed(() -> showAlert(expressionMessage), 300);

            navController!!.navigate(
                MobileNavigationDirections.actionGlobalNavMoodInfoSheet(
                    expressionMessage, ""
                )
            )

//            navController!!.navigate(
//                MobileNavigationDirections.actionGlobalNavMoodReason(
//                    expressionMessage
//                )
//            )

//            showAlert(null, expressionMessage) {
//                //PermissionsUtil.askPermissions(this)
//                coroutineScope.launch {
//                    withContext(Dispatchers.Main) {
//                        handlePermissionResult(
//                            PermissionManager.requestPermissions(
//                                this@DashboardActivity, 4,
//                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.CAMERA,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.RECORD_AUDIO
//                            )
//                        )
//                    }
//                }
//
//                viewModel.setIsMoodsDialogShowing(false)
//                if (hasPopupBannerTrue) {
//                    if (customScreenPopup != null) {
//                        showBanner(true, customScreenPopup)
//                    }
//                }
//                if (silentNotificationCount > 0 && moodsPopupShownAfterOneDayCount > 0) {
//                    viewModel.increaseSilentCount(preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID))
//                }
//                if (showFunCardCount > 0) {
//                    viewModel.setShowFunCards(true)
//                }
//                if (showAudioVocals > 0) {
//                    viewModel.setShowAudioVocals(true)
//                }
//            }
        }
    }

    fun loadAfterSetMood() {

//        coroutineScope.launch {
//            withContext(Dispatchers.Main) {
//                handlePermissionResult(
//                    PermissionManager.requestPermissions(
//                        this@DashboardActivity, 5,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.READ_CONTACTS,
//                    )
//                )
//            }
//        }

        viewModel.setIsMoodsDialogShowing(false)
        if (hasPopupBannerTrue) {
            if (customScreenPopup != null) {
                showBanner(true, customScreenPopup)
            }
        }

        if (hasPopupBannerTrue) {
            if (customScreenPopup != null) {
                showBanner(true, customScreenPopup)
            }
        }
        if (silentNotificationCount > 0 && moodsPopupShownAfterOneDayCount > 0) {
            viewModel.increaseSilentCount(preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID))
        }
        if (showFunCardCount > 0) {
//            viewModel.setShowFunCards(true)
        }
        if (showAudioVocals > 0) {
            viewModel.setShowAudioVocals(true)
        }
    }

    private fun loadProfileImage() {
        val loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
        )
        if (loginResponse != null) {
            val options = RequestOptions().fitCenter().transform(CircleCrop()).placeholder(R.drawable.default_user_profile_image)
                .error(R.drawable.default_user_profile_image)
            Glide.with(this).load(loginResponse.largeProfilePicture).apply(options).into(ivProfileImage!!)
            tvUserName!!.text = String.format("%s %s", loginResponse.firstName, loginResponse.lastName)
            Utilities.verifiedUserType(this, loginResponse.userTypeId, ivIconVerified)
        }
    }

    private fun loadMessageBadge() {

        if (!isGuestUser()) if (bottomNavigationView!!.getBadge(R.id.nav_conversations) == null) {
            questionGamesViewModel!!.questionFilterInfo().observe(
                this
            ) { apiResponse: ApiResponse<GetQuestionFilterInfoResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        loadMessageBadgeFromFilter(apiResponse)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
            }
        }

    }

    private fun loadMessageBadgeFromFilter(apiResponse: ApiResponse<GetQuestionFilterInfoResponse>) {
        if (apiResponse.body!!.messagingBubbleTag.equals("Y", ignoreCase = true)) {
            if (navController!!.currentDestination!!.id != R.id.nav_conversations && navController!!.currentDestination!!.id != R.id.nav_conversation_details) {
                val badge = bottomNavigationView!!.getOrCreateBadge(R.id.nav_conversations)
                badge.backgroundColor = ContextCompat.getColor(this, R.color.color_red)
            } else {
                bottomNavigationView!!.removeBadge(R.id.nav_conversations)

            }

        } else {
            bottomNavigationView!!.removeBadge(R.id.nav_conversations)
        }
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    private fun setupOtherScreenLayout() {

        cvMood.visibility = View.GONE
        if (navController!!.currentDestination!!.id == R.id.nav_offline_counselling) {
            setupScreenLayoutForIIT()
        } else if (navController!!.currentDestination!!.id == R.id.nav_friends_circle_main || navController!!.currentDestination!!.id == R.id.nav_friends_circle_add_number || navController!!.currentDestination!!.id == R.id.nav_select_contacts || navController!!.currentDestination!!.id == R.id.nav_otp || navController!!.currentDestination!!.id == R.id.nav_contacts_list || navController!!.currentDestination!!.id == R.id.nav_friends_circle_question_view_pager || navController!!.currentDestination!!.id == R.id.nav_friends_thoughts) {
            setupScreenLayoutForFC()
        } else {
            ivWave!!.colorFilter = null

            ImageViewCompat.setImageTintList(
                ivWave!!, ColorStateList.valueOf(Color.parseColor("#008080"))
            )

            if (Build.VERSION.SDK_INT >= 21) {
                val window = this.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = Color.parseColor("#008080")
            }

            tvAppTitle!!.isAllCaps = false
            tvAppTitle!!.visibility = View.VISIBLE
            tvAppTitle!!.setTextAppearance(R.style.AppTextView_TitleTextViewAppearance)
            tvAppTitle!!.setTypeface(
                ResourcesCompat.getFont(this, R.font.montserrat_medium), 0 + Typeface.NORMAL
            )
            tvAppTitle!!.setTextColor(ContextCompat.getColor(this, R.color.color_black))
            Objects.requireNonNull(supportActionBar!!).setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            bottomNavigationView!!.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity, android.R.color.white
                )
            )
            parentLayout!!.setBackgroundColor(
                ContextCompat.getColor(
                    this@DashboardActivity, android.R.color.white
                )
            )
            val states = arrayOf(
                intArrayOf(android.R.attr.state_selected), intArrayOf(-android.R.attr.state_selected)
            )
            val colors = intArrayOf(
                ContextCompat.getColor(this, R.color.color_black), ContextCompat.getColor(this, R.color.colorAccent)
            )
            val colorStateList = ColorStateList(states, colors)
            bottomNavigationView!!.itemIconTintList = colorStateList
            bottomNavigationView!!.visibility = View.GONE
        }

    }

    private fun setupHomeScreenLayout() {

        cvMood.visibility = View.GONE
        ivWave!!.colorFilter = null

        ImageViewCompat.setImageTintList(
            ivWave!!,
            ColorStateList.valueOf(Color.parseColor("#008080"))
        )

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#008080")
        }

        tvAppTitle!!.isAllCaps = false
        if (navController!!.currentDestination!!.id == R.id.nav_home || navController!!.currentDestination!!.id == R.id.nav_vocals_main) {
            tvAppTitle!!.visibility = View.GONE
            tvAppTitle!!.setTextAppearance(R.style.AppTextView_TitleTextViewAppearance_White)
            tvAppTitle!!.setTypeface(
                ResourcesCompat.getFont(this, R.font.montserrat_bold), Typeface.BOLD
            )
            tvAppTitle!!.setTextColor(ContextCompat.getColor(this, R.color.color_white))
        } else {
            tvAppTitle!!.visibility = View.VISIBLE
            tvAppTitle!!.setTextAppearance(R.style.AppTextView_TitleTextViewAppearance)
            tvAppTitle!!.setTypeface(
                ResourcesCompat.getFont(this, R.font.montserrat_medium), Typeface.NORMAL
            )
            tvAppTitle!!.setTextColor(ContextCompat.getColor(this, R.color.color_black))
        }

        Objects.requireNonNull(supportActionBar!!).setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        bottomNavigationView!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, R.color.white
            )
        )
        parentLayout!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, R.color.white
            )
        )
        val states = arrayOf(
            intArrayOf(android.R.attr.state_selected), intArrayOf(-android.R.attr.state_selected)
        )
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.colorAccent), ContextCompat.getColor(this, R.color.color_black)
        )
        val colorStateList = ColorStateList(states, colors)
        bottomNavigationView!!.itemIconTintList = colorStateList

        bottomNavigationView!!.visibility = View.GONE
    }

    private fun setupScreenLayoutForFC() {

        cvMood.visibility = View.GONE
        ivWave!!.colorFilter = null

        ImageViewCompat.setImageTintList(
            ivWave!!,
            ColorStateList.valueOf(Color.parseColor("#008080"))
        )

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#008080")
        }

        tvAppTitle!!.visibility = View.VISIBLE
        tvAppTitle!!.isAllCaps = false
        tvAppTitle!!.setTextAppearance(R.style.AppTextView_TitleTextViewAppearance)
        tvAppTitle!!.setTypeface(
            ResourcesCompat.getFont(this, R.font.montserrat_medium), Typeface.NORMAL
        )
        tvAppTitle!!.setTextColor(ContextCompat.getColor(this, R.color.color_black))

        Objects.requireNonNull(supportActionBar!!).setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        bottomNavigationView!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, android.R.color.white
            )
        )
        parentLayout!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, android.R.color.white
            )
        )

        bottomNavigationView!!.visibility = View.GONE
    }

    private fun setupScreenLayoutForIIT() {

        cvMood.visibility = View.GONE
        ivWave!!.colorFilter = null

        ImageViewCompat.setImageTintList(
            ivWave!!, ColorStateList.valueOf(Color.parseColor("#5117D5"))
        )

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.parseColor("#5117D5")

        tvAppTitle!!.visibility = View.VISIBLE
        tvAppTitle!!.isAllCaps = false
        tvAppTitle!!.setTextAppearance(R.style.AppTextView_TitleTextViewAppearance)
        tvAppTitle!!.setTypeface(
            ResourcesCompat.getFont(this, R.font.montserrat_medium), Typeface.NORMAL
        )
        tvAppTitle!!.setTextColor(ContextCompat.getColor(this, R.color.color_black))

        Objects.requireNonNull(supportActionBar!!).setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        bottomNavigationView!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, android.R.color.white
            )
        )
        parentLayout!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, android.R.color.white
            )
        )

        bottomNavigationView!!.visibility = View.GONE
    }

    fun setupScreenLayoutForFriendsCircleViewPager(backgroundColor: Int, waveColor: Int) {

        cvMood.visibility = View.GONE
        tvAppTitle!!.visibility = View.VISIBLE
        tvAppTitle!!.isAllCaps = false
        tvAppTitle!!.setTextAppearance(R.style.AppTextView_TitleTextViewAppearance)
        tvAppTitle!!.setTypeface(
            ResourcesCompat.getFont(this, R.font.montserrat_medium), Typeface.NORMAL
        )
        tvAppTitle!!.setTextColor(ContextCompat.getColor(this, R.color.color_black))

        Objects.requireNonNull(supportActionBar!!).setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        bottomNavigationView!!.setBackgroundColor(
            ContextCompat.getColor(
                this@DashboardActivity, R.color.color_bg_dashboard
            )
        )
        parentLayout!!.setBackgroundColor(
            backgroundColor
        )

        ImageViewCompat.setImageTintList(ivWave!!, ColorStateList.valueOf(waveColor))

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = waveColor
        }


        bottomNavigationView!!.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        if (navController!!.currentDestination != null && R.id.nav_recorder == navController!!.currentDestination!!.id) {
            exitScreenConfirmation("Exit Recorder Screen?")
        } else if (navController!!.currentDestination != null && R.id.nav_recorder_settings == navController!!.currentDestination!!.id) {
            exitScreenConfirmation("Exit Recorder Settings Screen?")
        } else {
            return NavigationUI.navigateUp(navController!!, drawerLayout)
        }
        return false
    }

    private fun exitScreenConfirmation(message: String) {
        TwoActionAlertDialog.showAlert(
            this, getString(R.string.label_confirm), message, null, "No", "Yes"
        ) { item1: Pair<Int?, Any?> ->
            if (TwoActionAlertDialog.ACTION_RIGHT == item1.first) {
//                    setMusicPlayerVisibility(true)
                if (navController!!.currentDestination != null && R.id.nav_recorder == navController!!.currentDestination!!.id) {
                    val navBuilder = NavOptions.Builder()
                    val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavVocalsMain(
                            "", "", "", ""
                        ), navOptions
                    )
                } else {
                    navController!!.navigateUp()
                }
            }
        }
    }

    fun openSubscriptionStatusDialog() {

        val textForPopUp = if (freeTrialRemainingDays >= 0) {
            if (freeTrialRemainingDays > 1) {
                "You have $freeTrialRemainingDays days remaining in your free trial. Buy subscription today to avoid any inconvenience."
            } else if (freeTrialRemainingDays == 1) {
                "Your subscription will expire tomorrow. Buy subscription now to avoid any inconvenience."
            } else {
                "Today is your last day of free trial. Buy subscription now to avoid any inconvenience."
            }
        } else {
            "Your subscription has expired. Purchase a subscription now to access all Onourem features again."
        }

//        val textForPopUp = if (freeTrialRemainingDays == 0) {
//            "Your subscription has expired. Purchase a subscription now to access all Onourem features again."
//        } else {
//            if (freeTrialRemainingDays > 1) {
//                "You have $freeTrialRemainingDays days remaining in your free trial. Buy subscription today to avoid any inconvenience."
//            } else {
//                "Today is your last day of free trial. Buy subscription now to avoid any inconvenience."
//            }
//        }

        SubscriptionStatusDialog.showAlert(
            this, "Subscription", textForPopUp, null, "Not Now", "Purchase"
        ) { item1: Pair<Int, Any?>? ->
            if (SubscriptionStatusDialog.ACTION_RIGHT == item1!!.first) {
                navController!!.navigate(MobileNavigationDirections.actionGlobalNavSubscriptionPackage())
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.nav_hamburger -> {
                if (!isGuestUser()) {
                    loadProfileImage()
                    drawerLayout!!.openDrawer(GravityCompat.START, true)
                    false
                } else {
                    showGuestPopup(
                        "Menu", "You can access a number of other features from this menu after you login."
                    )
                    false
                }
            }

            R.id.nav_conversations -> {
                if (!isGuestUser()) {
                    val conversation = Conversation()
                    conversation.id = ""
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavConversations(conversation)
                    )
                    true
                } else {
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavDummyGuest(
                            "Chats", "Start chatting privately with friends after you login."
                        )
                    )
                    //showGuestPopup("Chats", "You can chat with friends here")
                    false
                }

            }

            R.id.nav_home -> {
                navController!!.navigate(R.id.nav_home)
                val navBuilder = NavOptions.Builder()
                val navOptions = navBuilder.setPopUpTo(R.id.nav_home, true).build()
                navController!!.navigate(R.id.nav_home, null, navOptions)
                true
            }

            R.id.nav_notifications -> {
                if (!isGuestUser()) {
                    navController!!.navigate(R.id.nav_notifications)
                    val navBuilder = NavOptions.Builder()
                    val navOptions = navBuilder.setPopUpTo(R.id.nav_home, false).build()
                    navController!!.navigate(R.id.nav_notifications, null, navOptions)
                    true
                } else {
                    navController!!.navigate(
                        MobileNavigationDirections.actionGlobalNavDummyGuest(
                            "Notification", "You can access your notifications after you login."
                        )
                    )
//                    showGuestPopup("Notifications", "You can see notifications from your friends")
                    false
                }
            }
//            R.id.nav_audio_games -> {
//                val navBuilder = NavOptions.Builder()
//                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
//                preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
//                navController!!.navigate(
//                    MobileNavigationDirections.actionGlobalNavAudioGames(
//                        "",
//                        "Vocals",
//                        "",
//                        ""
//                    ), navOptions
//                )
//                //navController.navigate(MobileNavigationDirections.actionGlobalNavProfile(null, preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID)));
//                true
//            }

            R.id.nav_vocals_main -> {
//                val navBuilder = NavOptions.Builder()
//                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                navController!!.navigate(
                    MobileNavigationDirections.actionGlobalNavVocalsMain(
                        "", "", "", ""
                    )
                )

                true
            }

            else -> {
                NavigationUI.onNavDestinationSelected(item, navController!!)
            }
        }
    }

    override fun onLogout() {
        finalLogout()
    }

    //    public void updateAudioHistory() {
    //        mediaOperationViewModel!!.getAllAudioPlayBackHistory(preferenceHelper.getString(KEY_LOGGED_IN_USER_ID)).observe(
    //                this, this::updateAudioHistoryByUser
    //        );
    //    }
    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START, true)
        } else {
            if (navController!!.currentDestination != null && R.id.nav_recorder == navController!!.currentDestination!!.id) {
                exitScreenConfirmation("Exit Recorder Screen?")
            } else if (navController!!.currentDestination != null && R.id.nav_recorder_settings == navController!!.currentDestination!!.id) {
                exitScreenConfirmation("Exit Recorder Settings Screen?")
            } else if (navController!!.currentDestination != null && R.id.nav_home != Objects.requireNonNull(
                    navController!!.currentDestination!!
                ).id
            ) {
                navController!!.navigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    private val isInActivity: Boolean
        get() {
            val last_edit_time = preferenceHelper!!.getLong(Constants.KEY_SP_LAST_INTERACTION_TIME)
            return last_edit_time == 0L || System.currentTimeMillis() - last_edit_time < 900000L
        }

    private fun showReloadDialog() {
        preferenceHelper!!.putValue(Constants.KEY_SP_LAST_INTERACTION_TIME, 0L)
        navController!!.popBackStack(R.id.nav_home, false)
        getAppUpgradeInfo()
    }

    private fun getAppUpgradeInfo() {
        if (!isGuestUser()) {
            viewModel.appUpgradeInfo().observe(this) { apiResponse: ApiResponse<GetAppUpgradeInfoResponse> ->
                if (apiResponse.loading) {
                    //showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    //hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        preferenceHelper!!.putValue(
                            Constants.KEY_GET_APP_UPDATE_RESPONSE, Gson().toJson(apiResponse.body)
                        )
                        val gson = Gson()
                        val loginResponse = gson.fromJson(
                            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
                        )
                        loginResponse.userTypeId = apiResponse.body.userTypeId ?: ""
                        preferenceHelper!!.putValue(
                            Constants.KEY_USER_OBJECT, gson.toJson(loginResponse)
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_INTRO_VIDEO_HINDI, apiResponse.body.productTourVideo1
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_INTRO_VIDEO_ENGLISH, apiResponse.body.productTourVideo2
                        )
                        loadProfileImage()
                        val points: String?
                        if (!TextUtils.isEmpty(apiResponse.body.gamePoint)) {
                            points = apiResponse.body.gamePoint
                            preferenceHelper!!.putValue(Constants.KEY_GAME_POINTS, points)
                        }
                        var videoDuration: String? = "1"
                        if (!TextUtils.isEmpty(apiResponse.body.videoDuration)) {
                            videoDuration = apiResponse.body.videoDuration
                        }
                        preferenceHelper!!.putValue(Constants.KEY_VIDEO_DURATION, videoDuration)
                    } else {
                        //showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    //  hideProgress();
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                            getString(R.string.unable_to_connect_host_message)
                        ) || apiResponse.errorMessage.contains(
                            getString(R.string.unable_to_connect_host_message1)
                        ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getAppUpgradeInfo")
                        }
                        addNetworkErrorUserInfo(
                            "getAppUpgradeInfo", apiResponse.code.toString()
                        )
                    }
                }
            }
        }
    }

    fun addNetworkErrorUserInfo(serviceName: String?, networkErrorCode: String?) {
        if (!isGuestUser()) {
            viewModel.addNetworkErrorUserInfo(
                serviceName ?: "", networkErrorCode ?: "", preferenceHelper!!.getString(
                    Constants.KEY_LOGGED_IN_USER_ID
                )
            ).observe(this) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        //showProgress();
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        //hideProgress();
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        //  hideProgress();
                        showAlert(apiResponse.errorMessage)
                    }
                }
        }
    }

    fun setTitle(title: String?) {
        tvAppTitle!!.text = title
    }

    fun showBanner(isPopupRequired: Boolean, customScreenPopup: CustomScreenPopup?) {
        hasPopupBannerTrue = isPopupRequired
        this.customScreenPopup = customScreenPopup

        if (!isMoodDialogShowing && isPopupRequired) {
            Handler(Looper.getMainLooper()).postDelayed({
                navController!!.navigate(MobileNavigationDirections.actionGlobalNavInviteFriends())
            }, 3000)
        }
    }

    private fun restartFirstActivity() {
        val i = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    private fun showFunCards() {
        showFunCardCount = 0
        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, "")
        preferenceHelper!!.putValue(Constants.KEY_NEW_USER, "")
        navController!!.navigate(
            MobileNavigationDirections.actionGlobalNavFunCards(
                preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
            )
        )
    }

    private fun showAudioVocals() {
        showAudioVocals = 0
        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, "")
        preferenceHelper!!.putValue(Constants.KEY_NEW_USER, "")

        navController!!.navigate(
            (MobileNavigationDirections.actionGlobalNavVocalsMain(
                "", "", preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID), ""
            ))
        )
//        navController!!.navigate(
//            (MobileNavigationDirections.actionGlobalNavOthersVocals(
//                "",
//                "Trending Vocals", preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID), ""
//            ))
//        )

//        val navBuilder = NavOptions.Builder()
//        val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
//        navController!!.navigate(
//            MobileNavigationDirections.actionGlobalNavVocalsMain(
//                "",
//                "",
//                "",
//                ""
//            ), navOptions
//        )
//        navController!!.navigate(
//            MobileNavigationDirections.actionGlobalNavAudioGames(
//                "",
//                "Vocals",
//                preferenceHelper!!.getString(
//                    Constants.KEY_LINK_USER_ID
//                ),
//                ""
//            )
//        )
    }

    private fun showInAppReviewPopup() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task: com.google.android.play.core.tasks.Task<ReviewInfo?> ->
            try {
                if (task.isSuccessful) {

                    // We can get the ReviewInfo object
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        val date = Calendar.getInstance().time
                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val strDate = dateFormat.format(date)
                        var noOfTimeRequestReviewRaised =
                            preferenceHelper!!.getInt(Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED)
                        noOfTimeRequestReviewRaised = noOfTimeRequestReviewRaised + 1
                        preferenceHelper!!.putValue(
                            Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED, noOfTimeRequestReviewRaised
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN, strDate
                        )
                    }
                } else {
                    // There was some problem, continue regardless of the result.
                }
            } catch (ex: Exception) {
            }
        }
    }//hideProgress();//hideProgress();

    // showProgress();
    private val userWatchList: Unit
        get() {
            if (!isGuestUser()) {
                viewModel.getUserWatchList("").observe(this) { apiResponse: ApiResponse<GetWatchListResponse> ->
                        if (apiResponse.loading) {
                            // showProgress();
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            //hideProgress();
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                showWatchListUpdate(apiResponse.body)
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }
                        } else {
                            //hideProgress();
                            showAlert(apiResponse.errorMessage)
                        }
                    }
            }
        }

    private fun moodsPopupShownAfterOneDay() {
        moodsPopupShownAfterOneDayCount += 1
    }

    private fun finalLogout() {
        val loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
        )
        if (loginResponse != null) {
            startActivity(
                Intent(this@DashboardActivity, OnboardingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(Constants.KEY_USER_EMAIL, loginResponse.emailAddress)
            )
        } else {
            startActivity(
                Intent(this@DashboardActivity, OnboardingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(Constants.KEY_USER_EMAIL, "")
            )
        }
        NotificationManagerCompat.from(this).cancelAll()
        //viewModel.resetSilentCount(preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID));
        val canAppInstalledDirectly = preferenceHelper!!.getString(Constants.KEY_CAN_APP_INSTALLED_DIRECTLY)
        val todayAnswerCount = preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY)
        val totalAnswerCount = preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY)
        val noOfTimeRequestReviewRaised = preferenceHelper!!.getInt(Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED)
        val lastTimeReviewShownDate = preferenceHelper!!.getString(Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN)
        preferenceHelper!!.clear()
        preferenceHelper!!.putValue(
            Constants.KEY_CAN_APP_INSTALLED_DIRECTLY, canAppInstalledDirectly
        )
        preferenceHelper!!.putValue(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY, todayAnswerCount)
        preferenceHelper!!.putValue(
            Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY, totalAnswerCount
        )
        preferenceHelper!!.putValue(
            Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED, noOfTimeRequestReviewRaised
        )
        preferenceHelper!!.putValue(
            Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN, lastTimeReviewShownDate
        )
        if (exoPlayer != null) {
            exoPlayer!!.stop()
            val intent = Intent(this@DashboardActivity, AudioPlayerService::class.java)
            stopService(intent)
        }

        finish()
    }

    override fun onDestroy() {
        wakeLock.release()
        // Cancel the coroutine when the activity is destroyed to avoid memory leaks
        jobToCounselling?.cancel()
        userInteractionJob?.cancel()
        parentJob.cancel()
        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, "")
        if (tracker != null) {
            tracker!!.purgeHandler()
        }

//        stopService()

        preferenceHelper!!.putValue(Constants.WATCHLIST, "")
        preferenceHelper!!.putValue(Constants.VOCALS, "")

        super.onDestroy()
    }

    fun setMusicPlayerVisibility(visible: Boolean) {
        if (visible) {
            clPlayRecording.visibility = View.VISIBLE
            cvClose.visibility = View.VISIBLE
        } else {
            clPlayRecording.visibility = View.GONE
            cvClose.visibility = View.GONE
        }
    }

    //Music Player Implementation
    fun setMusicPlayerList(pos: Int, song: Song, songs: ArrayList<Song>) {
        songsFromServer.clear()
        songsFromServer.addAll(songs)
//        seekBarCurrentHint.text = "--:--"
//        seekBarHint.text = "--:--"
//        seekBar.progress = 0
        audioPlayerAdapter = null
        audioPlayerAdapter = AudioPlayerAdapter(songsFromServer as ArrayList<Song>) {
            //viewModel.setAudioCategoryObject(it)
//            if (navController!!.currentDestination!!.id != R.id.nav_audio_games_previous_list) {
//                val navBuilder = NavOptions.Builder()
//                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
//                navController!!.navigate(MobileNavigationDirections.actionGlobalNavAudioGamesPreviousList(songsFromServer.toTypedArray()), navOptions)
//                viewModel.setUpdateUI(it)
//            }

        }
        rvAudioPlayer!!.adapter = null
        rvAudioPlayer!!.adapter = audioPlayerAdapter
        rvAudioPlayer!!.isUserInputEnabled = true

        onSongSelected(pos, song)

        // setMusicPlayerVisibility(true)

    }

    //Music Player Implementation
    fun addMusicPlayerList(songs: ArrayList<Song>) {
//        songsFromServer.clear()
//        songsFromServer.addAll(songs)
//        audioPlayerAdapter!!.notifyDataSetChanged()
//        audioManager.addMediaList(songs)
    }


    private fun updatePlayingStatus(updatePlayingStatus: Boolean) {

        val drawable = if (updatePlayingStatus) R.drawable.pause
        else R.drawable.play
        playPause.post { playPause.setImageResource(drawable) }
    }

    private fun updatePlayingStatusExoPlayer() {

        val drawable =
            if (exoPlayer != null && exoPlayer!!.isPlaying || mediaPlayer != null && mediaPlayer!!.isPlaying) R.drawable.pause
            else R.drawable.play
        playPause.post { playPause.setImageResource(drawable) }
    }


    private fun onSongSelected(pos: Int, song: Song) {

        song.isPlaying = true
        song.isAudioPreparing = true
        songsFromServer.forEach { item ->
            item.isPlaying = item.audioId == song.audioId
            item.isAudioPreparing = item.audioId == song.audioId
        }

        // viewModel.setUpdateUI(song)

        Glide.with(this@DashboardActivity).load(song.profilePictureUrl).apply(options).into(rivProfile)

        audioManager.setMediaList(songsFromServer as ArrayList<Song>)
        updatePLayerView(pos)
//        Handler(Looper.myLooper()!!).postDelayed({
//            audioManager.setMediaItemPos(pos)
//        }, 500)
    }


    fun getPrevAudioList(): MutableList<Song> {
        return songsFromServer as ArrayList<Song>
    }

    fun getPlayingStatus(): Int {
        return playingStatus
    }

    fun getPlayingItem(): Song? {
        return if (songsFromServer.size > 0) {
            if (IntRange(0, songsFromServer.lastIndex).contains(exoPlayer?.currentMediaItemIndex)) {
                songsFromServer[exoPlayer!!.currentMediaItemIndex]
            } else {
                null
            }
        } else {
            null
        }
    }

    fun getDashboardData(): DashboardData? {
        return dashboardData
    }

    fun getDashboardDataAnswered(): DashboardData? {
        return dashboardDataAnswered
    }

    fun getDashboardDataMQ(): DashboardData? {
        return dashboardDataMQ
    }

    fun setDashboardData(data: DashboardData?) {
        dashboardData = data
    }

    fun setDashboardDataMQ(data: DashboardData?) {
        dashboardDataMQ = data
    }

    fun setDashboardDataAnswered(data: DashboardData?) {
        dashboardDataAnswered = data
    }

    fun getDashboardRecyclerViewPosition(): Int {
        return recyclerViewPositionDashboard
    }

    fun getFriendsPlayingRecyclerViewPosition(): Int {
        return recyclerViewPositionFriendsPlaying
    }

    fun getPlayGamesRecyclerViewPosition(): Int {
        return recyclerViewPositionPlayGames
    }

    fun getDashboardRecyclerViewPositionTopView(): Int {
        return recyclerViewPositionDashboardTopView
    }

    fun getFriendsPlayingRecyclerViewPositionTopView(): Int {
        return recyclerViewPositionFriendsPlayingTopView
    }

    fun getPlayGamesRecyclerViewPositionTopView(): Int {
        return recyclerViewPositionPlayGamesTopView
    }


    fun getDashboardRecyclerViewPositionAnswered(): Int {
        return recyclerViewPositionDashboardAnswered
    }

    fun getDashboardRecyclerViewPositionTopViewAnswered(): Int {
        return recyclerViewPositionDashboardTopViewAnswered
    }


    fun getDashboardRecyclerViewPositionMQ(): Int {
        return recyclerViewPositionDashboardMQ
    }

    fun getDashboardRecyclerViewPositionTopViewMQ(): Int {
        return recyclerViewPositionDashboardTopViewMQ
    }


    fun setDashboardRecyclerViewPosition(pos: Int, topView: Int) {
        recyclerViewPositionDashboard = pos
        recyclerViewPositionDashboardTopView = topView
    }

    fun setFriendsPlayingRecyclerViewPosition(pos: Int, topView: Int) {
        recyclerViewPositionFriendsPlaying = pos
        recyclerViewPositionFriendsPlayingTopView = topView
    }

    fun setPlayGamesRecyclerViewPosition(pos: Int, topView: Int) {
        recyclerViewPositionPlayGames = pos
        recyclerViewPositionPlayGamesTopView = topView
    }

    fun setDashboardRecyclerViewPositionAnswered(pos: Int, topView: Int) {
        recyclerViewPositionDashboardAnswered = pos
        recyclerViewPositionDashboardTopViewAnswered = topView
    }

    fun setDashboardRecyclerViewPositionMQ(pos: Int, topView: Int) {
        recyclerViewPositionDashboardMQ = pos
        recyclerViewPositionDashboardTopViewMQ = topView
    }

    fun getDashboardSelectedFilter(): Int {
        return dashboardSelectedFilter
    }

    fun setDashboardSelectedFilter(pos: Int) {
        dashboardSelectedFilter = pos
    }

    fun getNotificationData(): NotificationData? {
        return notificationData
    }

    fun setNotificationData(data: NotificationData) {
        notificationData = NotificationData()
        notificationData!!.isLastPage = data.isLastPage
        notificationData!!.isLoading = data.isLoading
        notificationData!!.isReadyToMove = data.isReadyToMove
        notificationData!!.list = data.list
        notificationData!!.notificationIdList = data.notificationIdList
    }

    fun getNotificationRecyclerViewPosition(): Int {
        return recyclerViewPositionNotification
    }

    fun getNotificationRecyclerViewPositionTopView(): Int {
        return recyclerViewPositionNotificationTopView
    }

    fun setNotificationRecyclerViewPosition(pos: Int, topView: Int) {
        recyclerViewPositionNotification = pos
        recyclerViewPositionNotificationTopView = topView
    }

    fun getVocalsData(selectedFilter: Int): VocalsDataHold? {
        return when (selectedFilter) {
            0 -> {
                vocalsDataHold
            }

            1 -> {
                vocalsDataHoldFriends
            }

            2 -> {
                vocalsDataHoldMV
            }

            3 -> {
                vocalsDataHoldFav
            }

            4 -> {
                vocalsDataHoldOther
            }

            else -> {
                null
            }
        }
    }

    fun getVocalsRecyclerViewPosition(selectedFilter: Int): Int? {
        return when (selectedFilter) {
            0 -> {
                recyclerViewPositionVocals
            }

            1 -> {
                recyclerViewPositionVocalsFriends
            }

            2 -> {
                recyclerViewPositionVocalsMyVocals
            }

            3 -> {
                recyclerViewPositionVocalsFav
            }

            4 -> {
                recyclerViewPositionVocalsOther
            }

            else -> {
                null
            }
        }
    }

    fun getVocalsRecyclerViewPositionTopView(selectedFilter: Int): Int? {
        return when (selectedFilter) {
            0 -> {
                recyclerViewPositionVocalsTopView
            }

            1 -> {
                recyclerViewPositionVocalsTopViewFriends
            }

            2 -> {
                recyclerViewPositionVocalsTopViewMyVocals
            }

            3 -> {
                recyclerViewPositionVocalsTopViewFav
            }

            4 -> {
                recyclerViewPositionVocalsTopViewOther
            }

            else -> {
                null
            }
        }
    }

    fun getVocalsSelectedFilter(): Int {
        return vocalsSelectedFilter
    }

    fun setVocalsSelectedFilter(pos: Int) {
        vocalsSelectedFilter = pos
    }

    fun setVocalsRecyclerViewPosition(pos: Int, topView: Int, selectedFilter: Int) {

        when (selectedFilter) {
            0 -> {
                recyclerViewPositionVocals = pos
                recyclerViewPositionVocalsTopView = topView
            }

            1 -> {
                recyclerViewPositionVocalsFriends = pos
                recyclerViewPositionVocalsTopViewFriends = topView
            }

            2 -> {
                recyclerViewPositionVocalsMyVocals = pos
                recyclerViewPositionVocalsTopViewMyVocals = topView
            }

            3 -> {
                recyclerViewPositionVocalsFav = pos
                recyclerViewPositionVocalsTopViewFav = topView
            }

            4 -> {
                recyclerViewPositionVocalsOther = pos
                recyclerViewPositionVocalsTopViewOther = topView
            }

        }

    }

    fun getOtherVocalsData(): OtherVocalsData? {
        return otherVocalsData
    }

    fun setVocalsData(vocalsData: VocalsDataHold, selectedFilter: Int) {

        when (selectedFilter) {

            0 -> {
                this.vocalsDataHold = VocalsDataHold()
                this.vocalsDataHold = VocalsDataHold()
                this.vocalsDataHold!!.audioGameIdList.addAll(vocalsData.audioGameIdList)
                this.vocalsDataHold!!.songsFromServer.addAll(vocalsData.songsFromServer)
                this.vocalsDataHold!!.privacyGroups.addAll(vocalsData.privacyGroups)
                this.vocalsDataHold!!.privacyGroupsDefault.addAll(vocalsData.privacyGroupsDefault)
                this.vocalsDataHold!!.displayNumberOfAudios = vocalsData.displayNumberOfAudios
                this.vocalsDataHold!!.isLastPagePagination = vocalsData.isLastPagePagination
                this.vocalsDataHold!!.isLoadingPage = vocalsData.isLoadingPage
                this.vocalsDataHold!!.privacyIds = vocalsData.privacyIds
            }

            1 -> {
                this.vocalsDataHoldFriends = VocalsDataHold()
                this.vocalsDataHoldFriends!!.audioGameIdList.addAll(vocalsData.audioGameIdList)
                this.vocalsDataHoldFriends!!.songsFromServer.addAll(vocalsData.songsFromServer)
                this.vocalsDataHoldFriends!!.privacyGroups.addAll(vocalsData.privacyGroups)
                this.vocalsDataHoldFriends!!.privacyGroupsDefault.addAll(vocalsData.privacyGroupsDefault)
                this.vocalsDataHoldFriends!!.displayNumberOfAudios = vocalsData.displayNumberOfAudios
                this.vocalsDataHoldFriends!!.isLastPagePagination = vocalsData.isLastPagePagination
                this.vocalsDataHoldFriends!!.isLoadingPage = vocalsData.isLoadingPage
                this.vocalsDataHoldFriends!!.privacyIds = vocalsData.privacyIds
            }

            2 -> {
                this.vocalsDataHoldMV = VocalsDataHold()
                this.vocalsDataHoldMV!!.audioGameIdList.addAll(vocalsData.audioGameIdList)
                this.vocalsDataHoldMV!!.songsFromServer.addAll(vocalsData.songsFromServer)
                this.vocalsDataHoldMV!!.privacyGroups.addAll(vocalsData.privacyGroups)
                this.vocalsDataHoldMV!!.privacyGroupsDefault.addAll(vocalsData.privacyGroupsDefault)
                this.vocalsDataHoldMV!!.displayNumberOfAudios = vocalsData.displayNumberOfAudios
                this.vocalsDataHoldMV!!.isLastPagePagination = vocalsData.isLastPagePagination
                this.vocalsDataHoldMV!!.isLoadingPage = vocalsData.isLoadingPage
                this.vocalsDataHoldMV!!.privacyIds = vocalsData.privacyIds
            }

            3 -> {
                this.vocalsDataHoldFav = VocalsDataHold()
                this.vocalsDataHoldFav!!.audioGameIdList.addAll(vocalsData.audioGameIdList)
                this.vocalsDataHoldFav!!.songsFromServer.addAll(vocalsData.songsFromServer)
                this.vocalsDataHoldFav!!.privacyGroups.addAll(vocalsData.privacyGroups)
                this.vocalsDataHoldFav!!.privacyGroupsDefault.addAll(vocalsData.privacyGroupsDefault)
                this.vocalsDataHoldFav!!.displayNumberOfAudios = vocalsData.displayNumberOfAudios
                this.vocalsDataHoldFav!!.isLastPagePagination = vocalsData.isLastPagePagination
                this.vocalsDataHoldFav!!.isLoadingPage = vocalsData.isLoadingPage
                this.vocalsDataHoldFav!!.privacyIds = vocalsData.privacyIds
            }

            4 -> {
                this.vocalsDataHoldOther = VocalsDataHold()
                this.vocalsDataHoldOther!!.audioGameIdList.addAll(vocalsData.audioGameIdList)
                this.vocalsDataHoldOther!!.songsFromServer.addAll(vocalsData.songsFromServer)
                this.vocalsDataHoldOther!!.privacyGroups.addAll(vocalsData.privacyGroups)
                this.vocalsDataHoldOther!!.privacyGroupsDefault.addAll(vocalsData.privacyGroupsDefault)
                this.vocalsDataHoldOther!!.displayNumberOfAudios = vocalsData.displayNumberOfAudios
                this.vocalsDataHoldOther!!.isLastPagePagination = vocalsData.isLastPagePagination
                this.vocalsDataHoldOther!!.isLoadingPage = vocalsData.isLoadingPage
                this.vocalsDataHoldOther!!.privacyIds = vocalsData.privacyIds
            }

        }

    }


    fun setOtherVocalsData(vocalsData: OtherVocalsData) {
        this.otherVocalsData = OtherVocalsData()
        this.otherVocalsData!!.audioGameIdList.addAll(vocalsData.audioGameIdList)
        this.otherVocalsData!!.songsFromServer.addAll(vocalsData.songsFromServer)
        this.otherVocalsData!!.privacyGroups.addAll(vocalsData.privacyGroups)
        this.otherVocalsData!!.privacyGroupsDefault.addAll(vocalsData.privacyGroupsDefault)
        this.otherVocalsData!!.displayNumberOfAudios = vocalsData.displayNumberOfAudios
        this.otherVocalsData!!.isDataLoading = vocalsData.isDataLoading
        this.otherVocalsData!!.isLastPagePagination = vocalsData.isLastPagePagination
        this.otherVocalsData!!.isLoadingPage = vocalsData.isLoadingPage
        this.otherVocalsData!!.privacyIds = vocalsData.privacyIds
        this.otherVocalsData!!.screenTitle = vocalsData.screenTitle
        this.otherVocalsData!!.vocalUserId = vocalsData.vocalUserId
    }

    fun getVocalsRecyclerViewPosition(): Int {
        return recyclerViewPositionVocals
    }

    fun getVocalsRecyclerViewPositionTopView(): Int {
        return recyclerViewPositionVocalsTopView
    }

    fun setVocalsRecyclerViewPosition(pos: Int, topView: Int) {
        recyclerViewPositionVocals = pos
        recyclerViewPositionVocalsTopView = topView
    }

    fun isGuestUser(): Boolean {
        return preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN_AS_GUEST)
    }

    fun setWatchListData(watchListResponse: GetWatchListResponse?) {
        this.watchListResponse = watchListResponse
    }

    fun getWatchListData(): GetWatchListResponse? {
        return watchListResponse
    }

    fun setHasUserPlayedGame(isUserPlayedGame: Int) {
        this.isUserPlayedGame = isUserPlayedGame
    }

    fun isUserPlayedGame(): Int {
        return isUserPlayedGame
    }

    fun setHasBond003AvailableForUsers(isBond003AvailableForUsers: Int) {
        this.isBond003AvailableForUsers = isBond003AvailableForUsers
    }

    fun isBond003AvailableForUsers(): Int {
        return isBond003AvailableForUsers
    }

    private fun isSubscriptionAvailableForLoginUser(): Boolean {
        return freeTrialRemainingDays != 10000
    }

    fun showGuestPopup(from: String, message: String) {
        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_guest), R.color.color_black, ActionType.GUEST, null
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController!!.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                from, bundle, message
            )
        )

//        AppUtilities.showSnackbar(
//            parentLayout as ViewGroup?,
//            "You are a guest user, To see full experience please login",
//            View.VISIBLE,
//            Snackbar.LENGTH_LONG, "Login"
//        ) {
//            run {
//                onLogout()
//            }
//        }
    }

//    open fun onStartServiceClick(v: View?) {
//        startService()
//    }
//
//    open fun onStopServiceClick(v: View?) {
//        stopService()
//    }


//    open fun startService() {
//        Log.d("**TaskStart", "startService called")
//        if (!TaskService.isServiceRunning) {
//            val serviceIntent = Intent(this, TaskService::class.java)
//            ContextCompat.startForegroundService(this, serviceIntent)
//        }
//    }

//    open fun stopService() {
//        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val appEndTime = df.format(Date()).toString()
//        preferenceHelper!!.putValue(Constants.APP_END_TIME, appEndTime)
//
//        val basicAuth: Auth = AuthToken(preferenceHelper!!.getString(Constants.KEY_AUTH_TOKEN))
//        val params: MutableMap<String, Any> = HashMap()
//        params["serviceName"] = "updateAverageTimeInfo"
//        params["startTime"] = preferenceHelper!!.getString(Constants.APP_START_TIME)
//        params["endTime"] = preferenceHelper!!.getString(Constants.APP_END_TIME)
//        //params.put("deviceId", uniqueDeviceId);
//        //return apiService.updateFirebaseToken(basicAuth.getHeaders(), params);
//
//        //params.put("deviceId", uniqueDeviceId);
//        //return apiService.updateFirebaseToken(basicAuth.getHeaders(), params);
//        val service = provideApiService(this)
//        val call = service!!.updateAverageTimeInfo(basicAuth.headers, params)
//        call.enqueue(object : Callback<String?> {
//            override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                if (response.isSuccessful) {
//                    Log.d("***###", "onResponse: " + response.body());
//                }
//            }
//
//            override fun onFailure(call: Call<String?>, t: Throwable) {
//                //Log.d("###", "onFailure: " + t.getLocalizedMessage());
//            }
//        })
//
//        Log.d("***TaskStop", "stopService called from Activity")
//        if (TaskService.isServiceRunning) {
//            val serviceIntent = Intent(this, TaskService::class.java)
//            stopService(serviceIntent)
//        }
//    }

//    open fun startServiceViaWorker() {
//        Log.d("***Task", "startServiceViaWorker called")
//        val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"
//        val workManager = WorkManager.getInstance(this)
//
//        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
//        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
//        val request = PeriodicWorkRequest.Builder(
//            TaskWorker::class.java,
//            16,
//            TimeUnit.MINUTES
//        )
//            .build()
//
//        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
//        // do check for AutoStart permission
//        workManager.enqueueUniquePeriodicWork(
//            UNIQUE_WORK_NAME,
//            ExistingPeriodicWorkPolicy.KEEP,
//            request
//        )
//    }
//
//
//    private fun provideApiService(app: Context): ApiService? {
//        return provideOkHttpClient(app)?.let {
//            Retrofit.Builder()
//                .baseUrl(app.getString(getApiBaseUrl()))
//                .addConverterFactory(GsonConverterFactory.create()) //                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
//                .client(it)
//                .build()
//                .create(ApiService::class.java)
//        }
//    }
//
//    private fun getApiBaseUrl(): Int {
//        var baseUrl = 0
//        when (BuildConfig.FLAVOR) {
//            "Dev" -> baseUrl = R.string.base_url_development
//            "Admin" -> baseUrl = R.string.base_url_local
//            "Prod" -> baseUrl = R.string.base_url_production
//        }
//        return baseUrl
//    }
//
//    private fun provideOkHttpClient(app: Context): OkHttpClient? {
//        val builder = OkHttpClient.Builder()
//        builder.readTimeout(60, TimeUnit.SECONDS)
//        builder.writeTimeout(60, TimeUnit.SECONDS)
//        builder.connectTimeout(60, TimeUnit.SECONDS)
//        if (BuildConfig.DEBUG) {
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//            builder.addInterceptor(interceptor)
//        }
//        builder.addInterceptor(NetworkConnectionInterceptor(app))
//        return builder.build()
//    }

    fun updateUserMoodReason(reason: String) {

        if (reason.trim() != "") {

            viewModel.updateUserMoodReason(Base64Utility.encodeToString(reason)).observe(
                this
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
//                showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    when {
                        apiResponse.body.errorCode.equals("000", ignoreCase = true) -> {
                            //                    hideProgress()
                        }

                        else -> {
                            //                    hideProgress()
                        }
                    }
                } else {
//                hideProgress()
                }
            }
        }

    }

    fun updateExternalActivityInfo(item: LoginDayActivityInfoList) {

        viewModel.updateExternalActivityInfo(item).observe(
            this
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
//                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                when {
                    apiResponse.body.errorCode.equals("000", ignoreCase = true) -> {
                        //                    hideProgress()
                    }

                    else -> {
                        //                    hideProgress()
                    }
                }
            } else {
//                hideProgress()
            }
        }

    }

    fun setContactList(contactList: ArrayList<ContactItem>?) {
        this.contactList = contactList
    }

    fun getContactList(): ArrayList<ContactItem>? {
        return contactList
    }

    fun sendMultipleMessages(selected: List<UserList>, args: ShareContentSheetFragmentArgs) {
        val conversationList = ArrayList<Conversation>()
        selected.forEach {
            val conversation = Conversation()
            conversation.id = "EMPTY"
            conversation.userName = it.firstName + " " + it.lastName
            conversation.userOne = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
            conversation.userTwo = it.userId
            conversation.profilePicture = it.profilePicture
            conversation.userTypeId = it.userType

            conversationList.add(conversation)

            sendMessage(
                args.title + "\n\n" + args.linkMsg, conversation, preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
            )
//            Toast.makeText(this, "Sending Messages", Toast.LENGTH_LONG).show()
        }

        conversationList.forEach {
            conversationsViewModel.updateConversationReadStatus(it)
        }


    }

    private fun sendMultipleMessages(selected: List<UserList>, apiResponse: GetUserLinkInfoResponse) {
        val conversationList = ArrayList<Conversation>()
        selected.forEach {
            val conversation = Conversation()
            conversation.id = "EMPTY"
            conversation.userName = it.firstName + " " + it.lastName
            conversation.userOne = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
            conversation.userTwo = it.userId
            conversation.profilePicture = it.profilePicture
            conversation.userTypeId = it.userType

            conversationList.add(conversation)

            sendMessage(
                apiResponse.title + "\n\n" + apiResponse.shortLink,
                conversation,
                preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
            )

            //Toast.makeText(this, "Sending Messages", Toast.LENGTH_LONG).show()
        }

        conversationList.forEach {
            conversationsViewModel.updateConversationReadStatus(it)
            AppUtilities.showLog("***Api Call", "Success")
        }


    }

    fun sendMultipleMessages(selected: List<UserList>, apiResponse: LoginDayActivityInfoList) {
        inviteFriends(apiResponse, selected)
    }


    private fun sendMessage(text: String?, mConversation: Conversation, loginUserId: String) {

        var otherUserId: String? = ""
        if (!Objects.requireNonNull(mConversation.userTwo).equals(loginUserId, ignoreCase = true)) {
            otherUserId = mConversation.userTwo
        }

//        else if (!Objects.requireNonNull(mConversation.userOne)
//                .equals(loginUserId, ignoreCase = true)
//        ) {
//            otherUserId = mConversation.userOne
//        }

        conversationsViewModel.postMessage(mConversation.id!!, otherUserId!!, text!!)
            .observe(this) { apiResponse: ApiResponse<SendMessageResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
//                    if (counter == friendsAdapter?.selected?.size) {
//
//                    }else{
//                        counter++
//                    }
                    if (mConversation.id != null) {
                        conversationsViewModel.updateConversationReadStatus(mConversation)
                    }
                    hideProgress()
                    //showAlert("Shared Successfully")
                } else {
                    hideProgress()
                }
            }
    }


}