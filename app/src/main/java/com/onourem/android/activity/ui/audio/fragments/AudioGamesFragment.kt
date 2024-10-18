package com.onourem.android.activity.ui.audio.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogUpdateTitleBinding
import com.onourem.android.activity.databinding.FragmentAudioGamesBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.models.AudioResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse
import com.onourem.android.activity.ui.admin.vocals.AdminAudioAdapter
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio.playback.SongProvider
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.data.OtherVocalsData
import com.onourem.android.activity.ui.data.VocalsDataHold
import com.onourem.android.activity.ui.games.viewmodel.*
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants.*
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject


class AudioGamesFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentAudioGamesBinding>() {

    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var isValidLink: Boolean = false
    private var vocalsData: VocalsDataHold? = null
    private var otherVocalsData: OtherVocalsData? = null

    private lateinit var commentsViewModel: CommentsViewModel

    //    private lateinit var mPlayerAdapter: PlayerAdapter
    private var isLastPagePagination: Boolean = false
    private var isLoadingPage: Boolean = false
    private var displayNumberOfAudios: Long = 20L
    private lateinit var layoutManager: LinearLayoutManager
    private var vocalUserId: String = ""
    private lateinit var linkUserId: String
    private lateinit var audioIdFromNotification: String
    private var selectedFilter: String = ""
    private var selectedFilterInt: String = ""
    private lateinit var dashboardViewModel: DashboardViewModel
    private var editPrivacyAction: Action<Any?>? = null
    private var privacyIds: String = ""
    private lateinit var selectPrivacyViewModel: SelectPrivacyViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    private var songAdapter: AdminAudioAdapter? = null
    private var songsFromServer = mutableListOf<Song>()

    var privacyGroupsDefault: ArrayList<PrivacyGroup> = ArrayList()
    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList()

    val ffmpegQueryExtension = FFmpegQueryExtension()

    var audioPlaybackHistoryList = ArrayList<AudioPlaybackHistory>()

    private var isDataLoading = false

    private val audioGameIdList: ArrayList<Int> = ArrayList()

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    private var friendList: ArrayList<UserList>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        commentsViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[CommentsViewModel::class.java]

        dashboardViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        selectPrivacyViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[SelectPrivacyViewModel::class.java]

        questionGamesViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[QuestionGamesViewModel::class.java]

        viewModel.reloadUI().observe(this) { reload: Boolean ->
            if (reload) {
                if (!isDataLoading) refreshUI()
            }
        }

    }

    private fun refreshUI() {
        selectedFilterInt = if (linkUserId == "") {
            preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
        } else {
            preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "")
            preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
        }
        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songsFromServer.clear()
        if (!isDataLoading) loadData(false, true)
    }

    private fun loadData(showProgress: Boolean, refreshing: Boolean) {
        setViews(showProgress, refreshing)
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_games
    }


    private fun setVocalsDataDataToActivity() {

        if (vocalUserId == "" && selectedFilterInt != "4") {

            when (selectedFilterInt) {
                "0" -> {
                    selectedFilter = "Trending Vocals"
                }
                "1" -> {
                    selectedFilter = "Friends Vocals"
                }
                "2" -> {
                    selectedFilter = "My Vocals"
                }
                "3" -> {
                    selectedFilter = "Favorite Vocals"
                }

                else -> {
                    selectedFilter = "Trending Vocals"
                    preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "0")
                }
            }
//            val vocalsData =
//                VocalsDataHold(
//                    isLastPagePagination,
//                    isLoadingPage,
//                    displayNumberOfAudios,
//                    selectedFilter,
//                    selectedFilterInt,
//                    privacyIds,
//                    songsFromServer,
//                    privacyGroupsDefault,
//                    privacyGroups,
//                    isDataLoading,
//                    audioGameIdList,
//                    selectedFilter,
//                    vocalUserId
//                )
//            (fragmentContext as DashboardActivity).setVocalsData(vocalsData)
        } else {
            val vocalsData =
                OtherVocalsData(
                    isLastPagePagination,
                    isLoadingPage,
                    displayNumberOfAudios,
                    selectedFilter,
                    selectedFilterInt,
                    privacyIds,
                    songsFromServer,
                    privacyGroupsDefault,
                    privacyGroups,
                    isDataLoading,
                    audioGameIdList,
                    selectedFilter,
                    vocalUserId
                )
            (fragmentContext as DashboardActivity).setOtherVocalsData(vocalsData)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        vocalUserId = AudioGamesFragmentArgs.fromBundle(requireArguments()).vocalUserId
//        linkUserId = AudioGamesFragmentArgs.fromBundle(requireArguments()).linkUserId
//        audioIdFromNotification =
//            AudioGamesFragmentArgs.fromBundle(requireArguments()).audioIdFromNotification

//        mPlayerAdapter = (fragmentContext as DashboardActivity).getPlayerAdapter()!!
        if (TextUtils.isEmpty(audioIdFromNotification)) {
            audioIdFromNotification = ""
        }

        dashboardViewModel!!.updateUI.observe(viewLifecycleOwner) {
            if (it != null) {

                for (item in songsFromServer) { // data item will available right away
                    if (item.audioId == it.audioId) {
                        // your code
//                        binding.rvAudioGames.smoothScrollToPosition(songAdapter!!.getPlayingItemPosition(
//                            it))
                        songAdapter!!.updatePlayingItem(it)
                        songAdapter!!.updateAnimatePlayingItem(null)

                    } else {
                        item.isAudioPreparing = false
                        item.isPlaying = false
                    }
                }

            }
        }

        dashboardViewModel!!.updateScrollPosition.observe(viewLifecycleOwner) {
            if (it != null) {

                for (item in songsFromServer) { // data item will available right away
                    if (item.audioId == it.audioId) {
                        // your code
                        binding.rvAudioGames.smoothScrollToPosition(
                            songAdapter!!.getPlayingItemPosition(
                                it
                            )
                        )
                        break
                    }
                }

            }
        }

        if ((fragmentContext as DashboardActivity).getPrevAudioList().size > 0) {
            (fragmentContext as DashboardActivity).setMusicPlayerVisibility(true)
        } else {
            (fragmentContext as DashboardActivity).setMusicPlayerVisibility(false)
        }

        // checkReadStoragePermissions()
        // checkRecordPermissions()

        selectedFilterInt = if (linkUserId == "") {
            preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
        } else {
            preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "")
            preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
        }
        viewModel.reloadUI(true)

    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun onPause() {
        super.onPause()
        val positionIndex: Int = layoutManager.findFirstVisibleItemPosition()
        var startView: View? = null
        startView = binding.rvAudioGames.getChildAt(0)
        val topView =
            if (startView == null) 0 else startView.top - binding.rvAudioGames.paddingTop
        (fragmentContext as DashboardActivity).setVocalsRecyclerViewPosition(positionIndex, topView)
    }


    override fun onResume() {
        setViews(false, false)
        super.onResume()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setViews(showProgress: Boolean, refreshing: Boolean) {

        vocalsData = (fragmentContext as DashboardActivity).getVocalsData(6)
        otherVocalsData = (fragmentContext as DashboardActivity).getOtherVocalsData()

        setAdapter()

        binding.swipeRefreshLayout.setOnRefreshListener {
            isDataLoading = false
            audioIdFromNotification = ""
            refreshUI()

            if (vocalsData != null || otherVocalsData != null) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        binding.ibFilter.setOnClickListener(ViewClickListener {
            addFilters()
        })



        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                when (action.actionType) {
                    ActionType.AUDIO_UPDATE_TITLE -> {
                        updateTitleDialog(action)
                    }
                    ActionType.EDIT_PRIVACY -> {
                        editPrivacy(action)
                    }
                    ActionType.DELETE_AUDIO -> {
                        deleteAudio(action)
                    }
                    ActionType.STATS -> {
                        audioStats(action)
                    }
                    ActionType.DELETE_AUDIO_CONFIRMATION -> {
                        deleteAudioConfirmed(action)
                    }
                    ActionType.REPORT_INAPPROPRIATE -> {
                        reportInappropriateAudioTakeConfirmation(action)
                    }
                    ActionType.REPORT_INAPPROPRIATE_CONFIRM -> {
                        reportInappropriateAudio(action)
                    }
                    ActionType.SHOW_AUDIOS_OF_THIS_USERS -> {
                        showAudiosOfThisUser(action)
                    }
                    ActionType.SHARE_AUDIO_LINK -> {
                        shareAudioLink(action)
                    }
                    ActionType.AUDIO_TRENDING -> {
                        showTrendingAudios()
                    }
                    ActionType.AUDIO_FRIENDS -> {
                        showFriendsAudios()
                    }
                    ActionType.AUDIO_MY_VOCALS -> {
                        showMyAudios()
                    }
                    ActionType.AUDIO_FAVORITE -> {
                        showFavAudios()
                    }
                    ActionType.AUDIO_UNFOLLOW -> {
                        unfollowForCreatorOfThisAudio(action)
                    }
                    ActionType.AUDIO_FOLLOW -> {
                        followForCreatorOfThisAudio(action)
                    }
                    ActionType.SEND_PRIVATE_MESSAGE -> {
                        sendToMessageScreen(action)
                    }
                    else -> {

                    }
                }

            }
        }

        viewModel.selectedPrivacyGroups.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                viewModel.setSelectedPrivacyGroup(null)
                val ids: ArrayList<Int> = ArrayList()
                it.forEach { item ->
                    ids.add(item.groupId)
                }

                privacyIds = Utilities.getTokenSeparatedString(ids, ",")

                if (editPrivacyAction != null) {
                    val editPrivacy = editPrivacyAction
                    editPrivacyAction = null
                    updateAudioVisibility(editPrivacy, privacyIds)
                }

            }
        }

        viewModel.privacyUpdatedItem.observe(viewLifecycleOwner) {
            if (it != null) {
                if (preferenceHelper.getString(KEY_SELECTED_FILTER_INT) == "0") {
                    if (it.privacyId != "1") {
                        songAdapter!!.removeItem(it)
                    }
                } else {
                    songAdapter!!.updateItem(it)
                }
                if (it.title != null) {
                    Toast.makeText(
                        fragmentContext,
                        "${it.title}'s privacy has been updated.",
                        Toast.LENGTH_LONG
                    ).show()

                }

                viewModel.setPrivacyUpdatedItem(null)
            }
        }

        commentsViewModel!!.commentCountLiveData.observe(this) { item: Triple<String?, Int, String?>? ->
            if (item != null) {
                updateComment(item.first!!, item.second!!)
                commentsViewModel!!.resetCommentCountLiveData()
            }
        }

        if ((fragmentContext as DashboardActivity).getVocalsData(6) != null) {

//            isDataLoading = vocalsData!!.isDataLoading
////                isLastPagePagination = data.isLastPagePagination
////                isLoadingPage = data.isLoadingPage
//            displayNumberOfAudios = vocalsData!!.displayNumberOfAudios
//            selectedFilter = vocalsData!!.selectedFilter
//            selectedFilterInt = vocalsData!!.selectedFilterInt
//            privacyIds = vocalsData!!.privacyIds
            //vocalUserId = vocalsData!!.vocalUserId
        } else {
            selectedFilterInt = if (linkUserId == "") {
                preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
            } else {
                preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "")
                preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
            }
        }

        if (vocalUserId == "" && selectedFilterInt != "4") {
            binding.edtSearchQuery.visibility = View.VISIBLE
            binding.ibFilter.visibility = View.VISIBLE
            binding.edtSearchQuery.setOnClickListener(ViewClickListener {
                vocalsData = null
//                navController.navigate(AudioGamesFragmentDirections.actionNavAudioGamesToNavSearchVocal())
            })


            var selectedFilterService = ""
            when (selectedFilterInt) {
                "0" -> {
                    selectedFilter = "Trending Vocals"
                    selectedFilterService = "getAudioInfo"
                }
                "1" -> {
                    selectedFilter = "Friends Vocals"
                    selectedFilterService = "getFriendAudioInfo"
                    linkUserId = ""
                    audioIdFromNotification = ""
                }
                "2" -> {
                    selectedFilter = "My Vocals"
                    selectedFilterService = "getLoginUserAudioInfo"
                    linkUserId = ""
                    audioIdFromNotification = ""
                }
                "3" -> {
                    selectedFilter = "Favorite Vocals"
                    selectedFilterService = "getAudioInfoLikedByLoginUser"
                    linkUserId = ""
                    audioIdFromNotification = ""
                }
                "4" -> {
                    selectedFilter = "Other's Vocals"
                    selectedFilterService = "getAudioForOtherUser"
                    linkUserId = ""
                    audioIdFromNotification = ""
                }

                else -> {
                    selectedFilter = "Trending Vocals"
                    preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "0")
                    selectedFilterService = "getAudioInfo"
                }

            }

            if (vocalsData != null && linkUserId == "" && audioIdFromNotification == "") {
                isLastPagePagination = false
                isLoadingPage = false
                isDataLoading = true

                songsFromServer = ArrayList()
                //isDataLoading = data.isDataLoading
//                isLastPagePagination = data.isLastPagePagination
//                isLoadingPage = data.isLoadingPage
                displayNumberOfAudios = vocalsData!!.displayNumberOfAudios
//                selectedFilter = vocalsData!!.screenTitle
//                selectedFilterInt = vocalsData!!.selectedFilterInt
                privacyIds = vocalsData!!.privacyIds
                songsFromServer.addAll(vocalsData!!.songsFromServer)
                audioGameIdList.addAll(vocalsData!!.audioGameIdList)
                privacyGroups.addAll(vocalsData!!.privacyGroups)
                privacyGroupsDefault.addAll(vocalsData!!.privacyGroupsDefault)
                setAdapter()

            } else {
                if (songsFromServer == null || songsFromServer.isEmpty() && !isDataLoading) {
//                    if (selectedFilterInt == "4" && vocalUserId != "") {
//                        getAudioForOtherUser(vocalUserId)
//                    } else {
                    (fragmentContext as DashboardActivity).setTitle(selectedFilter)
                    getAudioShorts(selectedFilterService)
//                    }
                }
            }

            (fragmentContext as DashboardActivity).setTitle(selectedFilter)

        } else {

            if (vocalUserId == preferenceHelper.getString(KEY_LOGGED_IN_USER_ID)) {
                selectedFilterInt = "2"
                selectedFilter = "My Vocals"
                val selectedFilterService = "getLoginUserAudioInfo"
                linkUserId = ""
                audioIdFromNotification = ""
                getAudioShorts(selectedFilterService)
                preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "2")
                (fragmentContext as DashboardActivity).setTitle(selectedFilter)
            } else {
                selectedFilterInt = "4"
                preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "4")
                binding.edtSearchQuery.visibility = View.GONE
                binding.ibFilter.visibility = View.GONE
                linkUserId = ""
                audioIdFromNotification = ""
                if (songsFromServer == null || songsFromServer.isEmpty()) {
                    getAudioForOtherUser(vocalUserId)
                }

            }

//            if (otherVocalsData != null) {
//                isLastPagePagination = false
//                isLoadingPage = false
//                isDataLoading = true
//
//                songsFromServer = ArrayList()
//                //isDataLoading = data.isDataLoading
////                isLastPagePagination = data.isLastPagePagination
////                isLoadingPage = data.isLoadingPage
//                displayNumberOfAudios = otherVocalsData!!.displayNumberOfAudios
//                selectedFilter = otherVocalsData!!.selectedFilter
//                selectedFilterInt = otherVocalsData!!.selectedFilterInt
//                privacyIds = otherVocalsData!!.privacyIds
//                songsFromServer.addAll(otherVocalsData!!.songsFromServer)
//                audioGameIdList.addAll(otherVocalsData!!.audioGameIdList)
//                privacyGroups.addAll(otherVocalsData!!.privacyGroups)
//                privacyGroupsDefault.addAll(otherVocalsData!!.privacyGroupsDefault)
//                setAdapter()
//                if (vocalUserId == preferenceHelper.getString(KEY_LOGGED_IN_USER_ID)) {
//                    selectedFilterInt = "2"
//                    selectedFilter = "My Vocals"
//                    preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, selectedFilterInt)
//                } else {
//                    selectedFilterInt = "4"
//                    selectedFilter = otherVocalsData!!.screenTitle
//                    preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "4")
//                }
//                (fragmentContext as DashboardActivity).setTitle(selectedFilter)
//
//            } else {
//
//            }

        }

        binding.rvAudioGames.addOnScrollListener(object : PaginationListener(layoutManager) {

            override fun loadMoreItems() {
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AudioGamesFragment.isLastPagePagination
            }

            override fun isLoading(): Boolean {
                return this@AudioGamesFragment.isLoadingPage
            }
        })

        loadFriendsList()
    }

    private fun sendToMessageScreen(action: Action<Any?>) {
        val item = action.data as Song

        val conversation = Conversation()
        conversation.id = "EMPTY"
        conversation.userName = item.userName
        conversation.userOne = preferenceHelper.getString(KEY_LOGGED_IN_USER_ID)
        conversation.userTwo = item.creatorId
        conversation.profilePicture = item.profilePictureUrl
        conversation.userTypeId = item.userType
        navController.navigate(MobileNavigationDirections.actionGlobalNavConversations(conversation))
    }

    private fun loadFriendsList() {
        friendList = ArrayList()
        questionGamesViewModel!!.friendList.observe(
            viewLifecycleOwner
        ) { friendsListResponseApiResponse: ApiResponse<UserListResponse> ->
            if (friendsListResponseApiResponse.isSuccess && friendsListResponseApiResponse.body != null) {
                if (friendsListResponseApiResponse.body.errorCode
                        .equals("000", ignoreCase = true)
                ) {
                    friendList!!.addAll(friendsListResponseApiResponse.body.userList as ArrayList)
                }
            } else if (!friendsListResponseApiResponse.loading) {
                hideProgress()
            }
        }
    }

    private fun setAdapter() {

        songAdapter =
            AdminAudioAdapter(songsFromServer, preferenceHelper) { item ->

                when (item.first) {

                    CLICK_WHOLE_ITEM -> {
                        (fragmentContext as DashboardActivity).setMusicPlayerVisibility(true)
                        // onSongSelected(item.second, songsFromServer)
                        preferenceHelper.putValue(
                            KEY_SELECTED_FILTER_INT_WHILE_PLAYING,
                            preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
                        )
                        songAdapter!!.updatePlayingItem(item.second)
                        (fragmentContext as DashboardActivity).setMusicPlayerList(
                            songAdapter!!.getPlayingItemPosition(
                                item.second
                            ), item.second,
                            songAdapter!!.items as ArrayList<Song>
                        )

                    }
                    CLICK_PROFILE -> {
                        if (!item.second.creatorId.equals("4264", ignoreCase = true)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavProfile(
                                    null,
                                    item.second.creatorId
                                )
                            )
                        } else {
                            showAlert("You can't access profile of this admin user")
                        }

                    }
                    CLICK_LIKE -> {
                        if (item.second.isAudioLiked.isNotEmpty() && item.second.isAudioLiked == "Y") {
                            item.second.isAudioLiked = "N"
                            unLikeAudio(item.second)
                        } else {
                            item.second.isAudioLiked = "Y"
                            likeAudio(item.second)
                        }
                    }
                    CLICK_COMMENT -> {
//                        navController.navigate(
//                            AudioGamesFragmentDirections.actionNavAudioGamesToNavAudioComment(
//                                item.second.audioId
//                            )
//                        )
//                        commentsViewModel!!.setReloadUi(null)
                    }
                    CLICK_COMMENT_LIST -> {
//                        val navBuilder = NavOptions.Builder()
//                        val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
//                        navController.navigate(
//                            AudioGamesFragmentDirections.actionNavAudioGamesToNavAudioCommentsList(
//                                item.second.audioId
//                            ), navOptions
//                        )
//                        commentsViewModel!!.setReloadUi(null)
                    }
                    CLICK_MORE -> {
                        val titleText = "Select Option"
                        val actions = ArrayList<Action<Any?>?>()

                        if (item.second.creatorId == preferenceHelper.getString(
                                KEY_LOGGED_IN_USER_ID
                            )
                        ) {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_update_title),
                                    R.color.color_black,
                                    ActionType.AUDIO_UPDATE_TITLE,
                                    item.second
                                )
                            )
                            actions.add(
                                Action(
                                    getString(R.string.action_label_edit_privacy),
                                    R.color.color_black,
                                    ActionType.EDIT_PRIVACY,
                                    item.second
                                )
                            )
                            actions.add(
                                Action(
                                    getString(R.string.action_label_delete),
                                    R.color.color_black,
                                    ActionType.DELETE_AUDIO,
                                    item.second
                                )
                            )
                            actions.add(
                                Action(
                                    getString(R.string.action_label_stats),
                                    R.color.color_black,
                                    ActionType.STATS,
                                    item.second
                                )
                            )

                        } else if (item.second.creatorId != preferenceHelper.getString(
                                KEY_LOGGED_IN_USER_ID
                            )
                        ) {

                            if (friendList != null && friendList!!.isNotEmpty()) {
                                friendList!!.forEach {

                                    if (item.second.creatorId == it.userId) {
                                        actions.add(
                                            Action(
                                                getString(R.string.action_label_send_private_message),
                                                R.color.color_black,
                                                ActionType.SEND_PRIVATE_MESSAGE,
                                                item.second
                                            )
                                        )
                                        return@forEach
                                    }
                                }
                            }

                            actions.add(
                                Action(
                                    getString(R.string.action_label_report_inappropriate),
                                    R.color.color_black,
                                    ActionType.REPORT_INAPPROPRIATE,
                                    item.second
                                )
                            )
                            if (vocalUserId == "") {
                                actions.add(
                                    Action(
                                        getString(R.string.action_label_show_audios),
                                        R.color.color_black,
                                        ActionType.SHOW_AUDIOS_OF_THIS_USERS,
                                        item.second
                                    )
                                )
                            }
                        }

                        if (item.second.userFollowingCreator == "Y") {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_un_follow),
                                    R.color.color_black,
                                    ActionType.AUDIO_UNFOLLOW,
                                    item.second
                                )
                            )
                        } else if (item.second.userFollowingCreator == "N") {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_follow),
                                    R.color.color_black,
                                    ActionType.AUDIO_FOLLOW,
                                    item.second
                                )
                            )
                        }


                        actions.add(
                            Action(
                                getString(R.string.action_label_share_audios),
                                R.color.color_black,
                                ActionType.SHARE_AUDIO_LINK,
                                item.second
                            )
                        )


                        val bundle = Bundle()
                        bundle.putParcelableArrayList(
                            KEY_BOTTOM_SHEET_ACTIONS,
                            actions
                        )
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                titleText,
                                bundle, ""
                            )
                        )

                    }

                }
            }

        layoutManager = LinearLayoutManager(fragmentContext)
        binding.rvAudioGames.layoutManager = layoutManager
        binding.rvAudioGames.setHasFixedSize(true)
        binding.rvAudioGames.adapter = songAdapter

        if ((fragmentContext as DashboardActivity).getVocalsRecyclerViewPosition() != -1) {
            layoutManager.scrollToPositionWithOffset(
                (fragmentContext as DashboardActivity).getVocalsRecyclerViewPosition(),
                (fragmentContext as DashboardActivity).getVocalsRecyclerViewPositionTopView()
            )
        }

    }

    private fun handleResponse(
        apiResponse: ApiResponse<GetAudioInfoResponse>,
        showProgress: Boolean,
        serviceName: String,
    ) {
        if (apiResponse.loading) {
            isDataLoading = true
            if (showProgress) showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            isDataLoading = false
            hideProgress()
            binding.swipeRefreshLayout.isRefreshing = false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                displayNumberOfAudios = apiResponse.body.displayNumberOfAudios.toLong()
                if (displayNumberOfAudios == 0L) {
                    displayNumberOfAudios = 20L
                }

                if (apiResponse.body.audioResponseList != null) {

                    if (apiResponse.body.audioIdList != null && !apiResponse.body.audioIdList.isEmpty()) {
                        audioGameIdList.clear()
                        audioGameIdList.addAll(apiResponse.body.audioIdList)
                    }
                    setMusic(apiResponse.body.audioResponseList as ArrayList<AudioResponse>)
                } else {
//                    val footerMessage: String = when (selectedFilterInt) {
//                        "0" -> {
//                            "These are the trending vocals. Want to play more? Check other filters"
//                        }
//                        "1" -> {
//                            "These are the Vocals of your friends."
//                        }
//                        "2" -> {
//                            "These are the Vocals of your own."
//                        }
//                        "3" -> {
//                            "These are the Vocals you liked."
//                        }
//                        else -> {
//                            "No Vocals"
//                        }
//                    }
                    // binding.tvMessage.text = footerMessage
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvAudioGames.visibility = View.GONE
                }
//                if (!TextUtils.isEmpty(linkUserId)) {
//                    sendPushNotificationToLinkUser()
//                }
            } else {
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            isDataLoading = false
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            hideProgress()
            showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message1)
                )
                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
            ) {
                if (BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", serviceName)
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName,
                    apiResponse.code.toString()
                )
            }
        }
    }


    private fun loadMoreGames() {
        isLoadingPage = true
        var start = 0
        var end = 0

        if (audioGameIdList == null || audioGameIdList.isEmpty()) {
            isLastPagePagination = true
            isLoadingPage = false
            setFooterMessage()
            return
        }

        start = songAdapter!!.itemCount
        end = audioGameIdList.size
        if (start >= end) {
            isLastPagePagination = true
            isLoadingPage = false
            setFooterMessage()
            return
        }

        val audioIds = ArrayList<Int>()
        val gameIdList1 = getDisplayGameIdListElements(audioGameIdList, start) as List<*>
        AppUtilities.showLog("**audioGameIdList1:", gameIdList1.size.toString())

        for (i in gameIdList1.indices) {
            val item = gameIdList1[i]
            audioIds.add(item as Int)
        }

        if (audioIds.size > 0) {
            viewModel.getNextAudioInfo(Utilities.getTokenSeparatedString(audioIds, ",")).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetAudioInfoResponse> ->
                if (apiResponse.loading) {
                    if (songAdapter != null) {
                        songAdapter!!.addLoading()
                    }
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (songAdapter != null) {
                        songAdapter!!.removeLoading()
                    }
                    isLoadingPage = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getNextUserLoginDayActivityInfo" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                        if (apiResponse.body.audioResponseList == null || apiResponse.body.audioResponseList
                                .isEmpty()
                        ) {
                            // isLastPage = true;
                            setFooterMessage()
                        } else {
                            songAdapter!!.addItems(SongProvider.getAllServerSongs(apiResponse.body.audioResponseList as ArrayList<AudioResponse>))
                            songsFromServer.addAll(SongProvider.getAllServerSongs(apiResponse.body.audioResponseList as ArrayList<AudioResponse>))
                            val modifiedSongsList: ArrayList<Song> = ArrayList()
                            songsFromServer.forEach {
                                modifiedSongsList.add(it)
                            }
                            setMusicAgain(modifiedSongsList)
//                                if ((fragmentContext as DashboardActivity).getPlayingStatus() == 3) {
//                                    val selectedSong = mPlayerAdapter.getCurrentSong()
//                                    mPlayerAdapter.instantRefreshSongsList(selectedSong!!,
//                                        songsFromServer)
//                                    songsFromServer.forEach { item ->
//                                        item.isPlaying = item.audioId == selectedSong.audioId
//                                        item.isAudioPreparing = item.audioId == selectedSong.audioId
//                                    }
//                                    songAdapter!!.notifyDataSetChanged()
//                                }
                            if (apiResponse.body.audioResponseList.size < PaginationListener.PAGE_ITEM_SIZE) {
                                // isLastPage = true;
                                setFooterMessage()
                            } else {
                                isLastPagePagination = false
                            }
                            Log.e(
                                "####",
                                String.format(
                                    "server: %d",
                                    apiResponse.body.audioResponseList.size
                                )
                            )
                        }
                    } else {
                        // isLastPage = true;
                        setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    isLoadingPage = false
                    if (songAdapter != null) {
                        songAdapter!!.removeLoading()
                    }
                    showAlert(apiResponse.errorMessage)
                }
            }
        }


    }

    private fun setFooterMessage() {
        isLoadingPage = false
        val footerMessage: String
        if (songAdapter != null) {
            footerMessage =
                if (songAdapter!!.itemCount > 0 /*&& !soloGamesAdapter.hasValidItems()*/) {
                    when (selectedFilterInt) {
//                        "0" -> {
//                            "These are the trending vocals. Want to play more? Check other filters"
//                        }
//                        "1" -> {
//                            "These are the Vocals of your friends."
//                        }
//                        "2" -> {
//                            "These are the Vocals of your own."
//                        }
//                        "3" -> {
//                            "These are the Vocals you liked."
//                        }
                        else -> {
                            ""
                        }
                    }
                } else {
                    "No Vocals"
                }
//            binding.rvAudioGames.postDelayed({
//                if (songAdapter != null) {
//                    songAdapter!!.addFooter(footerMessage)
//                    songAdapter!!.notifyItemChanged(songAdapter!!.itemCount - 1)
//                }
//            }, 200)
        }
    }

    private fun getDisplayGameIdListElements(myList: ArrayList<Int>, startIndex: Int): List<Int?> {
        var sub: List<Int?> = ArrayList()
        sub = myList.subList(
            startIndex,
            Math.min(myList.size.toLong(), startIndex + displayNumberOfAudios).toInt()
        )
        return sub
    }

    private fun followForCreatorOfThisAudio(action: Action<Any?>) {

        val audioResponse = action.data as Song
        viewModel.followAudioCreator(audioResponse.creatorId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()

            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    songsFromServer.forEach(action = {
                        if (it.creatorId == audioResponse.creatorId) {
                            val song = it
                            song.userFollowingCreator = "Y"
                            songAdapter!!.updateItem(song)
                        }
                    })
                    songAdapter!!.notifyDataSetChanged()
                    Toast.makeText(
                        fragmentContext,
                        "You are following " + audioResponse.userName,
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }

    }

    private fun unfollowForCreatorOfThisAudio(action: Action<Any?>) {

        val audioResponse = action.data as Song
        viewModel.unfollowAudioCreator(audioResponse.creatorId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()

            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                    songsFromServer.forEach(action = {
                        if (it.creatorId == audioResponse.creatorId) {
                            val song = it
                            song.userFollowingCreator = "N"
                            songAdapter!!.updateItem(song)
                        }
                    })
                    songAdapter!!.notifyDataSetChanged()

                    Toast.makeText(
                        fragmentContext,
                        "You have unfollowed " + audioResponse.userName,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }

    }

    private fun updateTitleDialog(action: Action<Any?>?) {
        val song = action?.data as Song
        val builder = AlertDialog.Builder(binding.root.context)
        val dialogViewBinding: DialogUpdateTitleBinding =
            DialogUpdateTitleBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogViewBinding.root)
        builder.setCancelable(false)
        val alertDialog = builder.create()
        dialogViewBinding.tvDialogTitle.text = "Rename Title"
        if (!TextUtils.isEmpty(song.title)) {
            dialogViewBinding.edtGroupName.setText(song.title)
        }
        dialogViewBinding.btnDialogCancel.setOnClickListener(ViewClickListener { view1: View? ->
            alertDialog.dismiss()
        })

        dialogViewBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            song.title = dialogViewBinding.edtGroupName.text.toString()
            if (song.title.isNotEmpty()) {
                dialogViewBinding.edtGroupName.error = null
                viewModel.updateAudioTitle(
                    song.audioId,
                    dialogViewBinding.edtGroupName.text.toString()
                ).observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        showProgress()

                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                            songAdapter!!.updateAudioTitleOfItem(song)
                            alertDialog.dismiss()

                            Toast.makeText(
                                fragmentContext,
                                "Audio title has been updated",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                    }
                }

            } else {
                dialogViewBinding.edtGroupName.error = "Empty Title"
            }

        })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

    }

    private fun showFavAudios() {
        selectedFilter = "Favorite Vocals"
        selectedFilterInt = "3"
        audioIdFromNotification = ""
        linkUserId = ""
        layoutManager.smoothScrollToPosition(binding.rvAudioGames, null, 0)
        preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "3")
        (fragmentContext as DashboardActivity).setTitle(selectedFilter)
        getAudioShorts("getAudioInfoLikedByLoginUser")
    }

    private fun showMyAudios() {
        selectedFilter = "My Vocals"
        selectedFilterInt = "2"
        audioIdFromNotification = ""
        linkUserId = ""
        layoutManager.smoothScrollToPosition(binding.rvAudioGames, null, 0)
        preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "2")
        (fragmentContext as DashboardActivity).setTitle(selectedFilter)
        getAudioShorts("getLoginUserAudioInfo")
    }

    private fun showFriendsAudios() {
        selectedFilter = "Friends Vocals"
        selectedFilterInt = "1"
        audioIdFromNotification = ""
        linkUserId = ""
        layoutManager.smoothScrollToPosition(binding.rvAudioGames, null, 0)
        preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "1")
        (fragmentContext as DashboardActivity).setTitle(selectedFilter)
        getAudioShorts("getFriendAudioInfo")
    }

    private fun showTrendingAudios() {
        selectedFilter = "Trending Vocals"
        selectedFilterInt = "0"
        audioIdFromNotification = ""
        linkUserId = ""
        layoutManager.smoothScrollToPosition(binding.rvAudioGames, null, 0)
        preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "0")
        (fragmentContext as DashboardActivity).setTitle(selectedFilter)
        getAudioShorts("getAudioInfo")
    }

    private fun shareAudioLink(action: Action<Any?>) {

        val song = action.data as Song
        dashboardViewModel.getUserLinkInfo(
            "Audio",
            song.audioId,
            "53",
            ""
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        shortLink(
//                            apiResponse.body.title,
//                            apiResponse.body.linkUserId,
//                            apiResponse.body.userLink,
//                            song
//                        )

                    share(
                        apiResponse.body.title,
                        apiResponse.body.userLink,
                        apiResponse.body.shortLink,
                        "",
                        "3"
                    )
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getUserLinkInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserLinkInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun share(
        title: String?,
        linkMsg: String?,
        shortLink: String?,
        mediaUrl: String?,
        mediaType: String?,
    ) {
        if (!mediaUrl.equals("", ignoreCase = true)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMediaShare(
                    title?:"",
                    linkMsg?:"",
                    mediaUrl?:"",
                    mediaType?:""
                )
            )
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
            shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink)
            try {
                fragmentContext.startActivity(Intent.createChooser(shareIntent,title));
            } catch (ex : ActivityNotFoundException) {
                Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun addFilters() {

        val titleText = "Choose Filter"
        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_trending),
                R.color.color_black,
                ActionType.AUDIO_TRENDING,
                null
            )
        )
        actions.add(
            Action(
                getString(R.string.action_label_friends),
                R.color.color_black,
                ActionType.AUDIO_FRIENDS,
                null
            )
        )
        actions.add(
            Action(
                getString(R.string.action_label_my_vocals),
                R.color.color_black,
                ActionType.AUDIO_MY_VOCALS,
                null
            )
        )
        actions.add(
            Action(
                getString(R.string.action_label_fav),
                R.color.color_black,
                ActionType.AUDIO_FAVORITE,
                null
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )

    }

    private fun editPrivacy(action: Action<Any?>?) {

        editPrivacyAction = action!!
        val song = editPrivacyAction!!.data as Song

        val selectedPrivacyIdsList: List<String> =
            listOf(*song.privacyId.split(",").toTypedArray())

        val oldSelected = ArrayList<PrivacyGroup>()
        selectedPrivacyIdsList.forEach(action = {
            val privacyGroup = PrivacyGroup()
            privacyGroup.groupId = it.toInt()
            oldSelected.add(privacyGroup)
        })

        selectPrivacyViewModel!!.setSelected(oldSelected)

        selectPrivacyViewModel!!.getAllGroups("18").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetAllGroupsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    val privacyGroups =
                        apiResponse.body.privacyGroups
                    val iterator =
                        privacyGroups?.listIterator()
                    if (iterator != null) {
                        while (iterator.hasNext()) {
                            val privacyGroup = iterator.next()
                            if (privacyGroup.groupName
                                    .equals("Receivers Only", ignoreCase = true)
                            ) {
                                privacyGroup.groupName = "Private"
                                break
                            }
                        }
                    }
                    if (selectPrivacyViewModel!!.selectedPrivacyMutableLiveData!!
                            .value != null && !selectPrivacyViewModel!!.selectedPrivacyMutableLiveData!!
                            .value?.isEmpty()!!
                    ) {
                        val defaultSelected: MutableList<PrivacyGroup> =
                            ArrayList()
                        val oldSelectedAfterResponse: MutableList<PrivacyGroup> =
                            selectPrivacyViewModel!!.selectedPrivacyMutableLiveData!!
                                .value as MutableList<PrivacyGroup>
                        for (selectedGroup in oldSelectedAfterResponse) {
                            if (privacyGroups != null) {
                                for (group in privacyGroups) {
                                    if (selectedGroup.groupId == group.groupId) {
                                        defaultSelected.add(group)
                                    }
                                }
                            }
                        }
                        oldSelectedAfterResponse.clear()
                        oldSelectedAfterResponse.addAll(defaultSelected)
                        this@AudioGamesFragment.privacyGroupsDefault.clear()
                        this@AudioGamesFragment.privacyGroupsDefault.addAll(
                            oldSelectedAfterResponse
                        )
                        this@AudioGamesFragment.privacyGroups.clear()
                        if (privacyGroups != null) {
                            this@AudioGamesFragment.privacyGroups.addAll(privacyGroups)
                        }
                        //viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    } else {
                        val defaultSelected: MutableList<PrivacyGroup> =
                            ArrayList()
                        if (privacyGroups != null) {
                            for (i in privacyGroups.indices) {
                                val privacyGroup = privacyGroups[i]
                                if (privacyGroup.groupId == 1) {
                                    defaultSelected.add(privacyGroup)
                                } else if (privacyGroup.groupName
                                        .equals("Receivers Only", ignoreCase = true)
                                ) {
                                    privacyGroup.groupName = "Private"
                                }
                            }
                        }
                        this@AudioGamesFragment.privacyGroupsDefault.clear()
                        this@AudioGamesFragment.privacyGroupsDefault.addAll(defaultSelected)
                        this@AudioGamesFragment.privacyGroups.clear()
                        if (privacyGroups != null) {
                            this@AudioGamesFragment.privacyGroups.addAll(privacyGroups)
                        }
                        //viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    }

                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavAudioPrivacy(
                            this@AudioGamesFragment.privacyGroups.toTypedArray(),
                            this@AudioGamesFragment.privacyGroupsDefault.toTypedArray(), song
                        )
                    )
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else if (!apiResponse.loading) {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getAllGroups")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getAllGroups",
                        apiResponse.code.toString()
                    )
                }
            }
        }

    }

    private fun getAudioShorts(service: String) {
        isLastPagePagination = false
        isDataLoading = true
        viewModel.getAudioInfo(service, linkUserId, audioIdFromNotification)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetAudioInfoResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    isDataLoading = false
                    hideProgress()
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (service == "getAudioInfo") {
                            if (apiResponse.body.message != null && apiResponse.body.message.isNotEmpty()) {
                                isValidLink = true
                                showAlert(apiResponse.body.message)
                            }
                            preferenceHelper.putValue(
                                KEY_AUDIO_LIMIT,
                                apiResponse.body.audioDuration
                            )
                            preferenceHelper.putValue(
                                KEY_AUDIO_VIEW,
                                apiResponse.body.audioViewDuration
                            )
                        }
                        handleResponse(apiResponse, false, service)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }


    private fun getAudioForOtherUser(otherUserId: String) {
        isLastPagePagination = false
        isDataLoading = true
        viewModel.getAudioForOtherUser(otherUserId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetAudioInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                isDataLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    handleResponse(apiResponse, false, "getAudioForOtherUser")
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun likeAudio(audioResponse: Song) {
        viewModel.likeAudio(audioResponse.audioId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    audioResponse.numberOfLike =
                        (audioResponse.numberOfLike.toInt() + 1).toString()
                    songAdapter!!.updateItem(audioResponse)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun unLikeAudio(audioResponse: Song) {
        viewModel.unLikeAudio(audioResponse.audioId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    audioResponse.numberOfLike =
                        (if (audioResponse.numberOfLike.toInt() > 0) audioResponse.numberOfLike.toInt() - 1 else 0).toString()
                    songAdapter!!.updateItem(audioResponse)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun deleteAudioConfirmed(action: Action<Any?>?) {
        val audioResponse = action?.data as Song
        viewModel.deleteAudio(audioResponse.audioId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()

            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    songAdapter!!.removeItem(audioResponse)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun deleteAudio(action: Action<Any?>?) {
        val audioResponse = action?.data as Song
        val titleText = "Do you want to delete this audio?"
        val actions = ArrayList<Action<Any>>()
        actions.add(
            Action(
                getString(R.string.action_label_delete_confirm),
                R.color.color_black,
                ActionType.DELETE_AUDIO_CONFIRMATION,
                audioResponse
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )
    }

    private fun reportInappropriateAudioTakeConfirmation(action: Action<Any?>?) {
        val audioResponse = action?.data as Song
        val titleText = "Report Inappropriate?"
        val actions = ArrayList<Action<Any>>()
        actions.add(
            Action(
                getString(R.string.label_confirm),
                R.color.color_red,
                ActionType.REPORT_INAPPROPRIATE_CONFIRM,
                audioResponse
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle,
                "This will inform team Onourem to look into the content for appropriateness. If the content is found to be violating the Onourem’s content policy, it will be removed from the platform. This may take a few hours. \n\nContent will remain available till the process is completed."
            )
        )
    }

    private fun reportInappropriateAudio(action: Action<Any?>?) {
        val audioResponse = action?.data as Song
        viewModel.reportInappropriateAudio(audioResponse.audioId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        songAdapter!!.removeItem(audioResponse)
                    Toast.makeText(
                        fragmentContext,
                        audioResponse.title + " reported as Inappropriate Audio",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun updateAudioVisibility(action: Action<Any?>?, gundu: String) {
        val audioResponse = action?.data as Song
        viewModel.updateAudioVisibility(gundu, audioResponse.audioId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    //songAdapter!!.updateItem(audioResponse)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    private fun audioStats(action: Action<Any?>?) {
        val audioResponse = action?.data as Song
        navController.navigate(MobileNavigationDirections.actionGlobalNavAudioStats(audioResponse))
    }

    private fun showAudiosOfThisUser(action: Action<Any?>?) {
        //vocalsData = null
        val audioResponse = action?.data as Song

        val navBuilder = NavOptions.Builder()
        val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
        navController!!.navigate(
            MobileNavigationDirections.actionGlobalNavVocalsMain(
                audioResponse.creatorId,
                audioResponse.userName, "", ""
            ), navOptions
        )
//        navController.navigate(
//            MobileNavigationDirections.actionGlobalNavAudioGames(
//                audioResponse.creatorId,
//                audioResponse.userName, "", ""
//            ), navOptions
//        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setMusic(songs: ArrayList<AudioResponse>) {

        songsFromServer.clear()
        songAdapter!!.notifyDataSetChanged()
        songsFromServer.addAll(SongProvider.getAllServerSongs(songs))
        songAdapter?.updateItems(songsFromServer)
        setVocalsDataDataToActivity()
        if (songsFromServer.size == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE

            if (!TextUtils.isEmpty(linkUserId) && !isValidLink) {
                //onSongSelected(songsFromServer[0], songsFromServer)
                //restorePlayerStatus()
                val selectedSong = songsFromServer[0]
//                binding.songTitle.visibility = View.VISIBLE
                //binding.songTitle.text = selectedSong.title
                selectedSong.isPlaying = true
                selectedSong.isAudioPreparing = false
                songAdapter!!.updateAnimatePlayingItem(selectedSong)
                songAdapter!!.updateItem(selectedSong)
                songAdapter!!.updatePlayingItem(selectedSong)
                preferenceHelper.putValue(KEY_LINK_USER_ID, "")
                linkUserId = ""
                preferenceHelper.putValue(
                    KEY_SELECTED_FILTER_INT_WHILE_PLAYING,
                    preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
                )
                (fragmentContext as DashboardActivity).setMusicPlayerList(
                    songAdapter!!.getPlayingItemPosition(
                        selectedSong
                    ), selectedSong,
                    songsFromServer as ArrayList<Song>
                )
            } else if (!TextUtils.isEmpty(audioIdFromNotification)) {
                //onSongSelected(songsFromServer[0], songsFromServer)
                //restorePlayerStatus()
                val selectedSong = songsFromServer[0]
//                binding.songTitle.visibility = View.VISIBLE
                //binding.songTitle.text = selectedSong.title
                selectedSong.isPlaying = true
                selectedSong.isAudioPreparing = false
                songAdapter!!.updateAnimatePlayingItem(selectedSong)
                songAdapter!!.updateItem(selectedSong)
                songAdapter!!.updatePlayingItem(selectedSong)
                preferenceHelper.putValue(KEY_LINK_USER_ID, "")
                // linkUserId = ""
                audioIdFromNotification = ""
                preferenceHelper.putValue(
                    KEY_SELECTED_FILTER_INT_WHILE_PLAYING,
                    preferenceHelper.getString(KEY_SELECTED_FILTER_INT)
                )
                (fragmentContext as DashboardActivity).setMusicPlayerList(
                    songAdapter!!.getPlayingItemPosition(
                        selectedSong
                    ), selectedSong,
                    songsFromServer as ArrayList<Song>
                )
            } else {
                if ((fragmentContext as DashboardActivity).getPlayingStatus() == 3) {
                    //restorePlayerStatus()
                    if ((fragmentContext as DashboardActivity).getPlayingItem() != null) {
                        val selectedSong = (fragmentContext as DashboardActivity).getPlayingItem()
                        selectedSong!!.isPlaying = true
                        selectedSong.isAudioPreparing = false
                        songAdapter!!.updatePlayingItem(selectedSong)
                    }

                }
            }
        }

    }

    private fun updateComment(audioId: String, count: Int) {
        for (i in songsFromServer.indices) {
            val item: Song = songsFromServer.get(i)
            if (item.audioId == audioId) {
                var currentCount = count
                if (currentCount <= 0) {
                    currentCount = 0
                }
                item.commentCount = currentCount.toString()
                songAdapter!!.notifyItemChanged(i)

                break
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setMusicAgain(songs: ArrayList<Song>) {
        songsFromServer.clear()
        songAdapter!!.notifyDataSetChanged()
        songsFromServer.addAll(songs)
        songAdapter?.updateItems(songsFromServer)
        (fragmentContext as DashboardActivity).addMusicPlayerList(songsFromServer as ArrayList<Song>)
        setVocalsDataDataToActivity()
    }

}
