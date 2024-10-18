package com.onourem.android.activity.ui.admin.vocals

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogUpdateTitleBinding
import com.onourem.android.activity.databinding.FragmentAdminDashboardBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.models.AudioResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio.playback.SongProvider
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.*
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import com.onourem.android.activity.viewmodel.DashboardViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ScheduleVocalsFragment() :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentAdminDashboardBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private var selectedFilterString: String = ""
    private var selectedRadioButton: String = "All"
    private lateinit var selectedFilter: ActionType
    private var selectedFilterInt: String = ""
    private lateinit var scheduledItem: Song
    private var dpd: DatePickerDialog? = null
    private var tpd: TimePickerDialog? = null
    private lateinit var now: Calendar
    private var formattedDateTime: String = ""
    private var scheduleOrUpdate: Int = 0

    private var positionIndex: Int = -1
    private var topView: Int = -1
    private lateinit var questionGamesViewModel: QuestionGamesViewModel

    private lateinit var commentsViewModel: CommentsViewModel

    private var isLastPagePagination: Boolean = false
    private var isLoadingPage: Boolean = false
    private var displayNumberOfAudios: Long = 20L
    private var layoutManager: LinearLayoutManager? = null
    private var vocalUserId: String = ""
    private val blockSeekbar = false
    private var linkUserId: String = ""
    private var audioIdFromNotification: String = ""

    private lateinit var dashboardViewModel: DashboardViewModel
    private var editPrivacyAction: Action<Any?>? = null
    private var privacyIds: String = ""
    private lateinit var selectPrivacyViewModel: SelectPrivacyViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    private var songAdapter: ScheduleVocalsAdapter? = null
    private var songsFromServer = mutableListOf<Song>()

    var privacyGroupsDefault: ArrayList<PrivacyGroup> = ArrayList()
    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList()

    val ffmpegQueryExtension = FFmpegQueryExtension()

    private var isDataLoading = false

    private val audioGameIdList: ArrayList<Int> = ArrayList()

    private var friendList: ArrayList<UserList>? = null

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    companion object {

        fun create(): ScheduleVocalsFragment {
            val myFragment = ScheduleVocalsFragment()
            val args = Bundle()
            args.putString("vocalUserId", "")
            args.putString("userName", "")
            args.putString("linkUserId", "")
            args.putString("audioIdFromNotification", "")
            myFragment.arguments = args

            return myFragment
        }
    }


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vocalUserId = ScheduleVocalsFragmentArgs.fromBundle(requireArguments()).vocalUserId
        linkUserId = ScheduleVocalsFragmentArgs.fromBundle(requireArguments()).linkUserId
        now = Calendar.getInstance()

        audioIdFromNotification =
            ScheduleVocalsFragmentArgs.fromBundle(requireArguments()).audioIdFromNotification

        dashboardViewModel.updateUI.observe(viewLifecycleOwner) {
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

        dashboardViewModel.updateScrollPosition.observe(viewLifecycleOwner) {
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


        layoutManager = LinearLayoutManager(fragmentContext)
        binding.rvAudioGames.layoutManager = layoutManager
        binding.rvAudioGames.setHasFixedSize(true)
        binding.rvAudioGames.adapter = songAdapter

        binding.rvAudioGames.addOnScrollListener(object : PaginationListener(layoutManager!!) {

            override fun loadMoreItems() {
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@ScheduleVocalsFragment.isLastPagePagination
            }

            override fun isLoading(): Boolean {
                return this@ScheduleVocalsFragment.isLoadingPage
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            isDataLoading = false
            refreshUI()
        }

        binding.ibFilter.setOnClickListener(ViewClickListener {
            addFilters()
        })

        binding.fab.setOnClickListener(ViewClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavRecorder(
                    preferenceHelper.getInt(Constants.KEY_AUDIO_LIMIT).toLong()
                )
            )
        })

        if (vocalUserId == "") {
            binding.edtSearchQuery.visibility = View.VISIBLE
            binding.ibFilter.visibility = View.VISIBLE
            binding.edtSearchQuery.setOnClickListener(ViewClickListener {
                navController.navigate(MobileNavigationDirections.actionGlobalNavAdminSearchVocal())
            })
        } else {
            binding.edtSearchQuery.visibility = View.GONE
            binding.ibFilter.visibility = View.GONE
        }

        selectedFilterInt = if (linkUserId == "") {
            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
        } else {
            preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
        }



        userActionViewModel.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel.actionConsumed()
                if (action.actionType == ActionType.AUDIO_PENDING) {
                    showPendingAudios()
                }

                if (action.actionType == ActionType.AUDIO_APPROVED_SCHEDULED) {
                    showApprovedAndScheduledAudios()
                }

                if (action.actionType == ActionType.AUDIO_APPROVED_PUBLISHED) {
                    showApprovedAndPublishedAudios()
                }

                if (action.actionType == ActionType.AUDIO_APPROVED_UN_SCHEDULED) {
                    showApprovedAndUnScheduledAudios()
                }

                if (action.actionType == ActionType.AUDIO_BLACKLISTED) {
                    showBlackListedAudios()
                }

                if (action.actionType == ActionType.AUDIO_BLACKLISTED_CONFIRMATION) {
                    showBlackListedConfirmationAudios(action)
                }

                if (action.actionType == ActionType.REJECT_AUDIO_CONFIRMATION) {
                    rejectAudio(action.data as Song)
                }

                if (action.actionType == ActionType.EDIT_RATINGS) {
                    showRatingBar(action, "3Dots")
                }

                if (action.actionType == ActionType.AUDIO_REMOVE_FROM_TV) {
                    removeFromTV(action)
                }

                if (action.actionType == ActionType.AUDIO_ADD_TO_BLACKLIST) {
                    addToBlackList(action)
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
                if (preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT) == "0") {
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

        viewModel.fromAction.observe(viewLifecycleOwner) {

            if (it != null && songAdapter != null) {

                if (it.first == "Update" && it.second != null) {
                    songAdapter!!.updateItem(it.second)
                } else if ((it.first == "Approve" || it.first == "Reject") && it.second != null) {
                    songAdapter!!.removeItem(it.second)
                }

            }
        }


        if ((fragmentContext as DashboardActivity).getPrevAudioList().size > 0) {
            (fragmentContext as DashboardActivity).setMusicPlayerVisibility(true)
        } else {
            (fragmentContext as DashboardActivity).setMusicPlayerVisibility(false)
        }

//        if (songAdapter == null) {
//            loadData()
//        } else {
//            songsFromServer.forEach {
//                it.isPlaying = false
//                it.isAudioPreparing = false
//            }
//            setAdapter()
//        }
        //viewModel.reloadUI(true)

        binding.rdg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdbAll -> {
                    selectedRadioButton = "All"
                    refreshUI()
                }
                R.id.rdbPaid -> {
                    selectedRadioButton = "Paid"
                    refreshUI()
                }
                R.id.rdbIntern -> {
                    selectedRadioButton = "Intern"
                    refreshUI()
                }
                R.id.rdbPaidIntern -> {
                    selectedRadioButton = "Paid Intern"
                    refreshUI()
                }
                R.id.rdbOther -> {
                    selectedRadioButton = "Other"
                    refreshUI()
                }
            }
        }

        checkReadStoragePermissions()
        checkRecordPermissions()
        refreshUI()
    }

    private fun checkReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,

                    ), 1
            )
        }
    }

    private fun checkRecordPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,

                    ), 1
            )
        }
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_admin_dashboard
    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshUI() {
        selectedFilterInt = if (linkUserId == "") {
            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
        } else {
            preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
        }

        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songAdapter = null
        songsFromServer.clear()
        if (!isDataLoading) loadData()

    }

    private fun loadData() {

        isLastPagePagination = false
        isLoadingPage = false
        isDataLoading = true

        //getAudioShorts(Constants.ADMIN_VOCALS)

        selectedFilterInt = if (linkUserId == "") {
            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
        } else {
            preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
        }

        var selectedFilterService = ""
        selectedFilter = ActionType.AUDIO_APPROVED_PUBLISHED
        selectedFilterString = "Approved & Published"
        selectedFilterService = "ApprovedAndPublished"
        linkUserId = ""
//        when (selectedFilterInt) {
//
//            "0" -> {
//                selectedFilter = ActionType.AUDIO_PENDING
//                selectedFilterString = "Pending"
//                selectedFilterService = "Pending"
//            }
//            "1" -> {
//                selectedFilter = ActionType.AUDIO_APPROVED_UN_SCHEDULED
//                selectedFilterString = "Approved & UnScheduled"
//                selectedFilterService = "ApprovedAndUnSchedule"
//                linkUserId = ""
//            }
//            "2" -> {
//                selectedFilter = ActionType.AUDIO_APPROVED_SCHEDULED
//                selectedFilterString = "Approved & Scheduled"
//                selectedFilterService = "ApprovedAndScheduled"
//                linkUserId = ""
//            }
//            "3" -> {
//                selectedFilter = ActionType.AUDIO_APPROVED_PUBLISHED
//                selectedFilterString = "Approved & Published"
//                selectedFilterService = "ApprovedAndPublished"
//                linkUserId = ""
//            }
//            "4" -> {
//                selectedFilter = ActionType.AUDIO_BLACKLISTED
//                selectedFilterString = "BlackListed"
//                selectedFilterService = "BlackListed"
//                linkUserId = ""
//            }
//
//            else -> {
//                selectedFilter = ActionType.AUDIO_PENDING
//                selectedFilterString = "Pending"
//                preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, "0")
//                selectedFilterService = "Pending"
//            }
//
//        }

        if (songsFromServer.isEmpty()) {
            if (vocalUserId == "") {
                (fragmentContext as DashboardActivity).setTitle(selectedFilterString)
            }
            getAudioShorts(selectedFilterService)
        }

        loadFriendsList()

    }

    private fun sendToMessageScreen(action: Action<Any?>) {
        val item = action.data as Song

        val conversation = Conversation()
        conversation.id = "EMPTY"
        conversation.userName = item.userName
        conversation.userOne = preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID)
        conversation.userTwo = item.creatorId
        conversation.profilePicture = item.profilePictureUrl
        conversation.userTypeId = item.userType
        navController.navigate(MobileNavigationDirections.actionGlobalNavConversations(conversation))
    }

    private fun loadFriendsList() {
        friendList = ArrayList()
        questionGamesViewModel.friendList.observe(
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

    override fun onPause() {
        super.onPause()
        if (layoutManager != null) {
            positionIndex = layoutManager!!.findFirstVisibleItemPosition()
            var startView: View? = null
            startView = binding.rvAudioGames.getChildAt(0)
            topView = if (startView == null) 0 else startView.top - binding.rvAudioGames.paddingTop
        }

        //(fragmentContext as DashboardActivity).setVocalsRecyclerViewPosition(positionIndex, topView)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() {

        songAdapter =
            ScheduleVocalsAdapter(songsFromServer, preferenceHelper) { item ->

                when (item.first) {

                    Constants.CLICK_WHOLE_ITEM -> {
                        (fragmentContext as DashboardActivity).setMusicPlayerVisibility(true)
                        // onSongSelected(item.second, songsFromServer)
                        preferenceHelper.putValue(
                            Constants.KEY_SELECTED_FILTER_INT_WHILE_PLAYING,
                            preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)
                        )
                        songAdapter!!.updatePlayingItem(item.second)
                        (fragmentContext as DashboardActivity).setMusicPlayerList(
                            songAdapter!!.getPlayingItemPosition(
                                item.second
                            ), item.second,
                            songAdapter!!.items as ArrayList<Song>
                        )

                    }

                    Constants.CLICK_APPROVE -> {
                        if (item.second.audioRatings == "" || item.second.audioRatings == "0") {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavRatingBar(
                                    item.second,
                                    "Approve"
                                )
                            )
                        } else {
                            approveAudio(item.second)
                        }
                    }
                    Constants.CLICK_REJECT -> {
                        rejectAudioConfirm(item.second)
                    }
//                Constants.CLICK_LIKE -> {
//                    if (item.second.isAudioLiked.isNotEmpty() && item.second.isAudioLiked == "Y") {
//                        item.second.isAudioLiked = "N"
//                        unLikeAudio(item.second)
//                    } else {
//                        item.second.isAudioLiked = "Y"
//                        likeAudio(item.second)
//                    }
//                }
                    Constants.CLICK_PROFILE -> {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavProfile(
                                null,
                                item.second.creatorId
                            )
                        )

//                        if (!item.second.creatorId.equals("4264", ignoreCase = true)) {
//
//                        } else {
//                            showAlert("You can't access profile of this admin user")
//                        }
                    }
                    Constants.CLICK_SCHEDULE -> {
                        scheduledItem = item.second
                        scheduleAudioDatePicker(Constants.CLICK_SCHEDULE)
                    }
                    Constants.CLICK_RE_SCHEDULE -> {
                        TwoActionAlertDialog.showAlert(
                            activity,
                            getString(R.string.label_confirm),
                            getString(R.string.message_reschedule),
                            null,
                            "Cancel",
                            "Yes"
                        ) { item1: Pair<Int?, Song?>? ->
                            if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                                scheduledItem = item.second
                                scheduleAudioDatePicker(Constants.CLICK_RE_SCHEDULE)
                            }
                        }
                    }
                    Constants.CLICK_MORE -> {
                        val titleText = "Choose Options"
                        val actions = ArrayList<Action<Any?>?>()

                        if (selectedFilter != ActionType.AUDIO_PENDING) {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_edit_rating),
                                    R.color.color_black,
                                    ActionType.EDIT_RATINGS,
                                    item.second
                                )
                            )
//                    actions.add(Action(getString(R.string.action_label_edit_schedule),
//                        R.color.color_black,
//                        ActionType.EDIT_SCHEDULE,
//                        item.second))
                            actions.add(
                                Action(
                                    getString(R.string.action_label_remove_from_tv),
                                    R.color.color_black,
                                    ActionType.AUDIO_REMOVE_FROM_TV,
                                    item.second
                                )
                            )
                        }

                        actions.add(
                            Action(
                                getString(R.string.action_label_blacklist),
                                R.color.color_black,
                                ActionType.AUDIO_BLACKLISTED_CONFIRMATION,
                                item.second
                            )
                        )


//                    actions.add(Action(getString(R.string.action_label_share_audios),
//                        R.color.color_black,
//                        ActionType.SHARE_AUDIO_LINK,
//                        item.second))


                        val bundle = Bundle()
                        bundle.putParcelableArrayList(
                            Constants.KEY_BOTTOM_SHEET_ACTIONS,
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
        songAdapter!!.notifyDataSetChanged()

        setMusic()

        binding.rvAudioGames.addOnScrollListener(object : PaginationListener(layoutManager!!) {

            override fun loadMoreItems() {
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@ScheduleVocalsFragment.isLastPagePagination
            }

            override fun isLoading(): Boolean {
                return this@ScheduleVocalsFragment.isLoadingPage
            }
        })


        if (positionIndex != -1) {
            layoutManager!!.scrollToPositionWithOffset(positionIndex, topView)
        }


        userActionViewModel.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel.actionConsumed()
                if (action.actionType == ActionType.AUDIO_PENDING) {
                    showPendingAudios()
                }

                if (action.actionType == ActionType.AUDIO_APPROVED_SCHEDULED) {
                    showApprovedAndScheduledAudios()
                }

                if (action.actionType == ActionType.AUDIO_APPROVED_PUBLISHED) {
                    showApprovedAndPublishedAudios()
                }

                if (action.actionType == ActionType.AUDIO_APPROVED_UN_SCHEDULED) {
                    showApprovedAndUnScheduledAudios()
                }

                if (action.actionType == ActionType.AUDIO_BLACKLISTED) {
                    showBlackListedAudios()
                }

                if (action.actionType == ActionType.AUDIO_BLACKLISTED_CONFIRMATION) {
                    showBlackListedConfirmationAudios(action)
                }

                if (action.actionType == ActionType.REJECT_AUDIO_CONFIRMATION) {
                    rejectAudio(action.data as Song)
                }

                if (action.actionType == ActionType.EDIT_RATINGS) {
                    showRatingBar(action, "3Dots")
                }

                if (action.actionType == ActionType.AUDIO_REMOVE_FROM_TV) {
                    removeFromTV(action)
                }

                if (action.actionType == ActionType.AUDIO_ADD_TO_BLACKLIST) {
                    addToBlackList(action)
                }

            }
        }

        viewModel.selectedPrivacyGroups.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
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
                if (preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT) == "0") {
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

        viewModel.fromAction.observe(viewLifecycleOwner) {

            if (it != null && songAdapter != null) {

                if (it.first == "Update" && it.second != null) {
                    songAdapter!!.updateItem(it.second)
                } else if ((it.first == "Approve" || it.first == "Reject") && it.second != null) {
                    songAdapter!!.removeItem(it.second)
                }

            }
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
                    songsFromServer.addAll(SongProvider.getAllServerSongs(apiResponse.body.audioResponseList as ArrayList<AudioResponse>))

                    if (songAdapter != null) {
                        songAdapter!!.clearData()
                        songAdapter = null
                    }
                    setAdapter()

                } else {
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvAudioGames.visibility = View.GONE
                }

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
                if (fragmentContext != null) (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
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

            var selectedFilterService = ""
            when (preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)) {
                "0" -> {
                    selectedFilterService = "Pending"
                }
                "1" -> {
                    selectedFilterService = "ApprovedAndUnSchedule"
                }
                "2" -> {
                    selectedFilterService = "ApprovedAndScheduled"
                }
                "3" -> {
                    selectedFilterService = "ApprovedAndPublished"
                }
                "4" -> {
                    selectedFilterService = "BlackListed"
                }
            }
            getNextAudioShorts(
                selectedFilterService,
                Utilities.getTokenSeparatedString(audioIds, ",")
            )
        }
    }

    private fun getNextAudioShorts(service: String, audioIds: String) {
        when (service) {
            "ApprovedAndScheduled" -> {
                getNextAudioShortsApproved(audioIds)
            }
            "ApprovedAndPublished" -> {
                getNextAudioShortsApproved(audioIds)
            }
            else -> {
                viewModel.getNextAudioInfoForAdmin(
                    audioIds,
                    vocalUserId, selectedRadioButton
                ).observe(
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
                                songAdapter!!.addItems(
                                    SongProvider.getAllServerSongs(
                                        apiResponse.body.audioResponseList as ArrayList<AudioResponse>
                                    )
                                )
                                songsFromServer.addAll(
                                    SongProvider.getAllServerSongs(
                                        apiResponse.body.audioResponseList as ArrayList<AudioResponse>
                                    )
                                )
                                val modifiedSongsList: ArrayList<Song> = ArrayList()
                                songsFromServer.forEach {
                                    modifiedSongsList.add(it)
                                }
                                setMusicAgain(modifiedSongsList)
//                                    if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
//                                        val selectedSong = mPlayerAdapter!!.getCurrentSong()
//                                        mPlayerAdapter!!.instantRefreshSongsList(selectedSong!!,
//                                            songsFromServer)
//                                        binding.songTitle.visibility = View.VISIBLE
//                                        binding.songTitle.text = selectedSong.title
//                                        songsFromServer.forEach { item ->
//                                            item.isPlaying = item.audioId == selectedSong.audioId
//                                            item.isAudioPreparing =
//                                                item.audioId == selectedSong.audioId
//                                        }
//                                        songAdapter!!.notifyDataSetChanged()
//                                    }
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
    }

    private fun getNextAudioShortsApproved(audioIds: String) {

        viewModel.getNextApprovedAudioScheduleByAdmin(
            audioIds,
            vocalUserId, selectedRadioButton
        ).observe(
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
                        songAdapter!!.addItems(
                            SongProvider.getAllServerSongs(
                                apiResponse.body.audioResponseList as ArrayList<AudioResponse>
                            )
                        )
                        songsFromServer.addAll(
                            SongProvider.getAllServerSongs(
                                apiResponse.body.audioResponseList as ArrayList<AudioResponse>
                            )
                        )
                        val modifiedSongsList: ArrayList<Song> = ArrayList()
                        songsFromServer.forEach {
                            modifiedSongsList.add(it)
                        }
                        setMusicAgain(modifiedSongsList)
//                            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
//                                restorePlayerStatus()
//                                val selectedSong = mPlayerAdapter!!.getCurrentSong()
//                                mPlayerAdapter!!.instantRefreshSongsList(selectedSong!!,
//                                    songsFromServer)
//                                binding.songTitle.visibility = View.VISIBLE
//                                binding.songTitle.text = selectedSong.title
//                                songsFromServer.forEach { item ->
//                                    item.isPlaying = item.audioId == selectedSong.audioId
//                                    item.isAudioPreparing =
//                                        item.audioId == selectedSong.audioId
//                                }
//                                songAdapter!!.notifyDataSetChanged()
//                            }
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

    private fun getApprovedAudioScheduleByAdmin(service: String, date: String) {
        viewModel.getApprovedAudioScheduleByAdmin(service, date, vocalUserId, selectedRadioButton)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetAudioInfoResponse> ->
                if (apiResponse.loading) {
                    showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (service == "getAudioInfo") {
                            preferenceHelper.putValue(
                                Constants.KEY_AUDIO_LIMIT,
                                apiResponse.body.audioDuration
                            )
                            preferenceHelper.putValue(
                                Constants.KEY_AUDIO_VIEW,
                                apiResponse.body.audioViewDuration
                            )
                        }
                        handleResponse(apiResponse, false, service)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress();
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun rejectAudioConfirm(second: Song) {
        val titleText = "Do you want to reject this audio?"
        val actions = ArrayList<Action<Any>>()
        actions.add(
            Action(
                getString(R.string.label_confirm),
                R.color.color_black,
                ActionType.REJECT_AUDIO_CONFIRMATION,
                second
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )
    }

    private fun updateAudioVisibility(action: Action<Any?>?, gundu: String) {
        val audioResponse = action!!.data as Song
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

    private fun setFooterMessage() {
        isLoadingPage = false
        val footerMessage: String
        if (songAdapter != null) {
            footerMessage =
                if (songAdapter!!.itemCount > 0 /*&& !soloGamesAdapter.hasValidItems()*/) {
                    "These are the trending vocals. Want to play more? Check other filters"
                } else {
                    "No Vocals"
                }
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

    @SuppressLint("NotifyDataSetChanged")
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
        val song = action!!.data as Song
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
                        apiResponse.body.title?:"",
                        apiResponse.body.shortLink?:"",
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
                    if (fragmentContext != null) (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserLinkInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun share(
        title: String,
        shortLink: String,
        mediaUrl: String,
        mediaType: String,
    ) {
        if (!mediaUrl.equals("", ignoreCase = true)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMediaShare(
                    title,
                    shortLink,
                    mediaUrl,
                    mediaType
                )
            )
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
            val shareMessage = shortLink
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivityForResult(Intent.createChooser(shareIntent, title), 1212)
        }
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

        selectPrivacyViewModel.setSelected(oldSelected)

        selectPrivacyViewModel.getAllGroups("18").observe(
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
                        privacyGroups!!.listIterator()
                    while (iterator.hasNext()) {
                        val privacyGroup = iterator.next()
                        if (privacyGroup.groupName
                                .equals("Receivers Only", ignoreCase = true)
                        ) {
                            privacyGroup.groupName = "Private"
                            break
                        }
                    }
                    if (selectPrivacyViewModel.selectedPrivacyMutableLiveData
                            !!.value != null && !selectPrivacyViewModel.selectedPrivacyMutableLiveData
                            !!.value?.isEmpty()!!
                    ) {
                        val defaultSelected: MutableList<PrivacyGroup> =
                            ArrayList()
                        val oldSelectedAfterResponse: MutableList<PrivacyGroup> =
                            selectPrivacyViewModel.selectedPrivacyMutableLiveData!!.value as MutableList<PrivacyGroup>
                        for (selectedGroup in oldSelectedAfterResponse) {
                            for (group in privacyGroups) {
                                if (selectedGroup.groupId == group.groupId) {
                                    defaultSelected.add(group)
                                }
                            }
                        }
                        oldSelectedAfterResponse.clear()
                        oldSelectedAfterResponse.addAll(defaultSelected)
                        this@ScheduleVocalsFragment.privacyGroupsDefault.clear()
                        this@ScheduleVocalsFragment.privacyGroupsDefault.addAll(
                            oldSelectedAfterResponse
                        )
                        this@ScheduleVocalsFragment.privacyGroups.clear()
                        this@ScheduleVocalsFragment.privacyGroups.addAll(privacyGroups)
                        //viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    } else {
                        val defaultSelected: MutableList<PrivacyGroup> =
                            ArrayList()
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
                        this@ScheduleVocalsFragment.privacyGroupsDefault.clear()
                        this@ScheduleVocalsFragment.privacyGroupsDefault.addAll(defaultSelected)
                        this@ScheduleVocalsFragment.privacyGroups.clear()
                        this@ScheduleVocalsFragment.privacyGroups.addAll(privacyGroups)
                        //viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    }

                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavAudioPrivacy(
                            this@ScheduleVocalsFragment.privacyGroups.toTypedArray(),
                            this@ScheduleVocalsFragment.privacyGroupsDefault.toTypedArray(), song
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
                    if (fragmentContext != null) (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getAllGroups",
                        apiResponse.code.toString()
                    )
                }
            }
        }

    }


    private fun getAudioShorts(service: String) {

        when (service) {
            "ApprovedAndScheduled" -> {
                getApprovedAudioScheduleByAdmin("ApprovedAndScheduled", "All")
            }
            "ApprovedAndPublished" -> {
                getApprovedAudioScheduleByAdmin("ApprovedAndPublished", "All")
            }
            else -> {
                viewModel.getAudioInfoForAdmin(service, vocalUserId, selectedRadioButton)
                    .observe(
                        viewLifecycleOwner
                    ) { apiResponse: ApiResponse<GetAudioInfoResponse> ->
                        if (apiResponse.loading) {
                            showProgress();
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            binding.swipeRefreshLayout.isRefreshing = false
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                if (service == "getAudioInfo") {
                                    preferenceHelper.putValue(
                                        Constants.KEY_AUDIO_LIMIT,
                                        apiResponse.body.audioDuration
                                    )
                                    preferenceHelper.putValue(
                                        Constants.KEY_AUDIO_VIEW,
                                        apiResponse.body.audioViewDuration
                                    )
                                }
                                handleResponse(apiResponse, false, service)
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }
                        } else {
                            hideProgress();
                            showAlert(apiResponse.errorMessage)
                        }
                    }
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
        val audioResponse = action!!.data as Song
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
        val audioResponse = action!!.data as Song
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
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )
    }

    private fun reportInappropriateAudioTakeConfirmation(action: Action<Any?>?) {
        val audioResponse = action!!.data as Song
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
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle,
                "This will inform team Onourem to look into the content for appropriateness. If the content is found to be violating the Onourem’s content policy, it will be removed from the platform. This may take a few hours. \n\nContent will remain available till the process is completed."
            )
        )
    }

    private fun reportInappropriateAudio(action: Action<Any?>?) {
        val audioResponse = action!!.data as Song
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

    private fun audioStats(action: Action<Any?>?) {
        val audioResponse = action!!.data as Song
        navController.navigate(MobileNavigationDirections.actionGlobalNavAudioStats(audioResponse))
    }

    private fun showAudiosOfThisUser(action: Action<Any?>?) {
        val audioResponse = action!!.data as Song
        navController.navigate(
            (MobileNavigationDirections.actionGlobalNavOthersVocals(
                audioResponse.creatorId,
                audioResponse.userName, "", ""
            ))
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setMusic() {

        if (dpd != null) {
            dpd!!.dismiss()
        }

        if (tpd != null) {
            tpd!!.dismiss()
        }

        if (songsFromServer.size == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE

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
    }

    private fun scheduleAudioDatePicker(scheduleOrUpdate: Int) {
        this.scheduleOrUpdate = scheduleOrUpdate
        dpd = DatePickerDialog.newInstance(
            this@ScheduleVocalsFragment,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )
        dpd!!.version = DatePickerDialog.Version.VERSION_2
        dpd!!.show(childFragmentManager, "Datepickerdialog")
        dpd!!.isCancelable = true
        //dpd!!.minDate = now
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        val calendarTime = Calendar.getInstance()
        calendarTime[Calendar.HOUR_OF_DAY] = hourOfDay
        calendarTime[Calendar.MINUTE] = minute

        val timeFormat = "hh:mm:ss aa" // your own format
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        formattedDateTime = "$formattedDateTime ${sdf.format(calendarTime.time)}"

//        Toast.makeText(
//            fragmentContext, formattedDateTime.toUpperCase(Locale.getDefault()),
//            Toast.LENGTH_LONG
//        ).show()

        if (scheduleOrUpdate == Constants.CLICK_SCHEDULE) {
            scheduleAudioByAdmin()
        } else if (scheduleOrUpdate == Constants.CLICK_RE_SCHEDULE) {
            updateScheduleAudioTimeByAdmin()
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val tpd = TimePickerDialog.newInstance(
            this@ScheduleVocalsFragment,
            calendarDate[Calendar.HOUR_OF_DAY],
            calendarDate[Calendar.MINUTE],
            false
        )
        tpd.version = TimePickerDialog.Version.VERSION_2
        tpd.show(childFragmentManager, "Timepickerdialog")
        tpd.isCancelable = true
        tpd.enableSeconds(false)
        tpd.vibrate(true)
        tpd.version = TimePickerDialog.Version.VERSION_2


        val dateFormat = "dd/MM/yyyy" // your own format
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        formattedDateTime = sdf.format(calendarDate.time)
        Log.d("dd/MM/yyyy", formattedDateTime)

        val datePickerNow = sdf.format(now.time)
        if (formattedDateTime == datePickerNow) {
            tpd.setMinTime(
                calendarDate.get(Calendar.HOUR_OF_DAY),
                calendarDate.get(Calendar.MINUTE) + 5,
                calendarDate.get(Calendar.SECOND)
            )
        }
    }

    private fun scheduleAudioByAdmin() {
        viewModel.scheduleAudioByAdmin(
            scheduledItem.audioId,
            AppUtilities.getTimeZone(),
            formattedDateTime
        ).observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress();
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    showAlert(apiResponse.body.message) {
                        songAdapter!!.removeItem(scheduledItem)
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress();
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun updateScheduleAudioTimeByAdmin() {
        viewModel.updateScheduleAudioTimeByAdmin(
            scheduledItem.audioId,
            AppUtilities.getTimeZone(),
            formattedDateTime
        ).observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress();
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    showAlert(apiResponse.body.message) {
                        songAdapter!!.updateItem(scheduledItem)
                    }

                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress();
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun showBlackListedConfirmationAudios(action: Action<Any?>) {
        val audio = action.data as Song
        val titleText = getString(R.string.label_confirm_ui)
        val actions = ArrayList<Action<Any>>()

        actions.add(
            Action(
                getString(R.string.label_confirm),
                R.color.color_black,
                ActionType.AUDIO_ADD_TO_BLACKLIST,
                audio
            )
        )

        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText, bundle, ""
            )
        )
    }

    private fun removeFromTV(action: Action<Any?>) {
        val item = action.data as Song
        rejectAudio(item)
    }

    private fun addToBlackList(action: Action<Any?>) {
        val audio = action.data as Song
        viewModel.blackListAudio(audio.audioId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress();
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    songAdapter!!.removeItem(audio)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress();
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun showRatingBar(action: Action<Any?>, fromAction: String) {
        val audio = action.data as Song
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavRatingBar(
                audio,
                fromAction
            )
        )
    }

    private fun approveAudio(audio: Song) {
        viewModel.approveAudioRequest(audio.audioId, audio.audioRatings)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        //songAdapter!!.updateItem(audio)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress();
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun rejectAudio(audio: Song) {
        viewModel.rejectAudioRequest(audio.audioId, audio.audioRatings)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        songAdapter!!.removeItem(audio)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress();
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun showPendingAudios() {
        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songAdapter = null
        songsFromServer.clear()

        selectedFilter = ActionType.AUDIO_PENDING
        selectedFilterString = "Pending"
        selectedFilterInt = "0"
        layoutManager!!.smoothScrollToPosition(binding.rvAudioGames, null, 0);
        preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, selectedFilterInt)
        (fragmentContext as DashboardActivity).setTitle(selectedFilterString)
        getAudioShorts("Pending")
    }

    private fun showApprovedAndUnScheduledAudios() {
        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songAdapter = null
        songsFromServer.clear()

        selectedFilter = ActionType.AUDIO_APPROVED_UN_SCHEDULED
        selectedFilterString = "Approved & UnScheduled"
        selectedFilterInt = "1"
        layoutManager!!.smoothScrollToPosition(binding.rvAudioGames, null, 0);
        preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, selectedFilterInt)
        (fragmentContext as DashboardActivity).setTitle(selectedFilterString)
        getAudioShorts("ApprovedAndUnSchedule")
    }

    private fun showApprovedAndScheduledAudios() {
        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songAdapter = null
        songsFromServer.clear()

        selectedFilter = ActionType.AUDIO_APPROVED_SCHEDULED
        selectedFilterString = "Approved & Scheduled"
        selectedFilterInt = "2"
        layoutManager!!.smoothScrollToPosition(binding.rvAudioGames, null, 0);
        preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, selectedFilterInt)
        (fragmentContext as DashboardActivity).setTitle(selectedFilterString)
        getApprovedAudioScheduleByAdmin("ApprovedAndScheduled", "All")
    }

    private fun showApprovedAndPublishedAudios() {
        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songAdapter = null
        songsFromServer.clear()

        selectedFilter = ActionType.AUDIO_APPROVED_PUBLISHED
        selectedFilterString = "Approved & Published"
        selectedFilterInt = "3"
        layoutManager!!.smoothScrollToPosition(binding.rvAudioGames, null, 0);
        preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, selectedFilterInt)
        (fragmentContext as DashboardActivity).setTitle(selectedFilterString)
        getApprovedAudioScheduleByAdmin("ApprovedAndPublished", "All")
    }

    private fun showBlackListedAudios() {
        isLastPagePagination = false
        isLoadingPage = false
        if (songAdapter != null)
            songAdapter!!.removeFooter()
        if (songAdapter != null) {
            songAdapter!!.clearData()
            songAdapter = null
        }
        songAdapter = null
        songsFromServer.clear()

        selectedFilter = ActionType.AUDIO_BLACKLISTED
        selectedFilterString = "BlackListed"
        selectedFilterInt = "4"
        layoutManager!!.smoothScrollToPosition(binding.rvAudioGames, null, 0);
        preferenceHelper.putValue(Constants.KEY_SELECTED_FILTER_INT, selectedFilterInt)
        (fragmentContext as DashboardActivity).setTitle(selectedFilterString)
        getAudioShorts("BlackListed")
    }

    private fun addFilters() {

        val titleText = "Choose Filter"
        val actions = ArrayList<Action<Any?>>()
//        actions.add(
//            Action(
//                getString(R.string.action_label_pending),
//                R.color.color_black,
//                ActionType.AUDIO_PENDING,
//                null
//            )
//        )
//
//        actions.add(
//            Action(
//                getString(R.string.action_label_approved_un_scheduled),
//                R.color.color_black,
//                ActionType.AUDIO_APPROVED_UN_SCHEDULED,
//                null
//            )
//        )
//
//        actions.add(
//            Action(
//                getString(R.string.action_label_approved_scheduled),
//                R.color.color_black,
//                ActionType.AUDIO_APPROVED_SCHEDULED,
//                null
//            )
//        )

        actions.add(
            Action(
                getString(R.string.action_label_approved_published),
                R.color.color_black,
                ActionType.AUDIO_APPROVED_PUBLISHED,
                null
            )
        )

//        actions.add(
//            Action(
//                getString(R.string.action_label_blacklisted),
//                R.color.color_black,
//                ActionType.AUDIO_BLACKLISTED,
//                null
//            )
//        )

        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )

    }

}