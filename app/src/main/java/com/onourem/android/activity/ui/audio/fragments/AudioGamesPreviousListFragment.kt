package com.onourem.android.activity.ui.audio.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAudioGamesBinding
import com.onourem.android.activity.models.GetAllGroupsResponse
import com.onourem.android.activity.models.GetUserLinkInfoResponse
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.playback.AudioGamesAdapter
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.SelectPrivacyViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants.*
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject


class AudioGamesPreviousListFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentAudioGamesBinding>() {

    //  private lateinit var mPlayerAdapter: PlayerAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var currentPlayList: Array<Song>? = null
    private var selectedFilterInt: String = ""
    private lateinit var dashboardViewModel: DashboardViewModel
    private var editPrivacyAction: Action<Any?>? = null
    private var privacyIds: String = ""
    private lateinit var selectPrivacyViewModel: SelectPrivacyViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    private var songAdapter: AudioGamesAdapter? = null
    private var songsFromServer = mutableListOf<Song>()
    private var songsFromPreviousList = mutableListOf<Song>()

    var privacyGroupsDefault: ArrayList<PrivacyGroup> = ArrayList()
    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList()

    val ffmpegQueryExtension = FFmpegQueryExtension()

    var audioPlaybackHistoryList = ArrayList<AudioPlaybackHistory>()

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )

        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DashboardViewModel::class.java
        )

        selectPrivacyViewModel = ViewModelProvider(this, viewModelFactory).get(
            SelectPrivacyViewModel::class.java
        )

    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_games
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //mPlayerAdapter = (fragmentContext as DashboardActivity).getPlayerAdapter()!!
        preferenceHelper.putValue(KEY_SELECTED_FILTER_INT, "5")
        currentPlayList =
            AudioGamesPreviousListFragmentArgs.fromBundle(requireArguments()).currentPlayingList
        if (currentPlayList != null) {
            viewModel.reloadUI(false)
            songsFromPreviousList.addAll(currentPlayList!!.toList())
            if (songsFromPreviousList.size > 0) {
                setViewsForPreviousList()
                setMusicFromPreviousList(songsFromPreviousList as ArrayList<Song>)
            }
        }

        dashboardViewModel!!.updateUI.observe(viewLifecycleOwner) {
            if (it != null) {
//                val selectedSong = it
//                selectedSong.isPlaying = true
//                selectedSong.isAudioPreparing = false
                binding.rvAudioGames.smoothScrollToPosition(songAdapter!!.getPlayingItemPosition(it))
                songAdapter!!.updatePlayingItem(it)
                songAdapter!!.updateAnimatePlayingItem(null)
            }
        }

        checkReadStoragePermissions()
        checkRecordPermissions()
    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
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
                    Manifest.permission.CAMERA,
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
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,

                    ), 1
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setViewsForPreviousList() {

        songAdapter =
            AudioGamesAdapter(songsFromPreviousList, preferenceHelper) { item ->

                when (item.first) {

                    CLICK_WHOLE_ITEM -> {
                        (fragmentContext as DashboardActivity).setMusicPlayerVisibility(true)
                        // onSongSelected(item.second, songsFromServer)
                        (fragmentContext as DashboardActivity).setMusicPlayerList(
                            songAdapter!!.getPlayingItemPosition(item.second), item.second,
                            songsFromServer as ArrayList<Song>
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
                    CLICK_MORE -> {
                        val titleText = "Choose Options"
                        val actions = ArrayList<Action<Any?>?>()

                        if (item.second.creatorId == preferenceHelper.getString(
                                KEY_LOGGED_IN_USER_ID
                            )
                        ) {
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
                            actions.add(
                                Action(
                                    getString(R.string.action_label_report_inappropriate),
                                    R.color.color_black,
                                    ActionType.REPORT_INAPPROPRIATE,
                                    item.second
                                )
                            )
//                            actions.add(Action(getString(R.string.action_label_show_audios),
//                                R.color.color_black,
//                                ActionType.SHOW_AUDIOS_OF_THIS_USERS,
//                                item.second))
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

        binding.fab.setOnClickListener(ViewClickListener {
            // mPlayerAdapter!!.pause()
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavRecorder(
                    preferenceHelper.getInt(KEY_AUDIO_LIMIT).toLong()
                )
            )
        })
        binding.edtSearchQuery.visibility = View.GONE
        binding.ibFilter.visibility = View.GONE
        (fragmentContext as DashboardActivity).setTitle("Current Vocals")

        userActionViewModel!!.actionMutableLiveData.observe(
            viewLifecycleOwner
        ) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                when (action.actionType) {
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
                    ActionType.AUDIO_UNFOLLOW -> {
                        unfollowForCreatorOfThisAudio(action)
                    }
                    ActionType.AUDIO_FOLLOW -> {
                        followForCreatorOfThisAudio(action)
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
    }


    private fun shareAudioLink(action: Action<Any?>) {

        val song = action.data as Song
        dashboardViewModel!!.getUserLinkInfo(
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

    private fun share(title: String?, linkMsg: String?, mediaUrl: String?, mediaType: String?) {
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavShareContent(
                title ?: "",
                linkMsg ?: "",
                mediaUrl ?: "",
                mediaType ?: ""
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
                        this@AudioGamesPreviousListFragment.privacyGroupsDefault.clear()
                        this@AudioGamesPreviousListFragment.privacyGroupsDefault.addAll(
                            oldSelectedAfterResponse
                        )
                        this@AudioGamesPreviousListFragment.privacyGroups.clear()
                        if (privacyGroups != null) {
                            this@AudioGamesPreviousListFragment.privacyGroups.addAll(privacyGroups)
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
                        this@AudioGamesPreviousListFragment.privacyGroupsDefault.clear()
                        this@AudioGamesPreviousListFragment.privacyGroupsDefault.addAll(
                            defaultSelected
                        )
                        this@AudioGamesPreviousListFragment.privacyGroups.clear()
                        if (privacyGroups != null) {
                            this@AudioGamesPreviousListFragment.privacyGroups.addAll(privacyGroups)
                        }
                        //viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    }

                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavAudioPrivacy(
                            this@AudioGamesPreviousListFragment.privacyGroups.toTypedArray(),
                            this@AudioGamesPreviousListFragment.privacyGroupsDefault.toTypedArray(),
                            song
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
        bundle.putParcelableArrayList(KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )
    }

    private fun reportInappropriateAudioTakeConfirmation(action: Action<Any?>?) {
        val audioResponse = action!!.data as Song
        val titleText = "Do you want to report this audio?"
        val actions = ArrayList<Action<Any>>()
        actions.add(
            Action(
                getString(R.string.label_confirm),
                R.color.color_black,
                ActionType.REPORT_INAPPROPRIATE_CONFIRM,
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
    private fun setMusicFromPreviousList(songs: ArrayList<Song>) {

        songsFromServer.clear()
        songAdapter!!.notifyDataSetChanged()
        songsFromServer.addAll(songs)
        songAdapter?.clearData()
        songAdapter?.updateItems(songsFromServer)

        if (songsFromServer.size == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE

//            if (mPlayerAdapter.isPlaying()) {
//                //restorePlayerStatus()
//                val selectedSong = mPlayerAdapter.getCurrentSong()
//                selectedSong!!.isPlaying = true
//                selectedSong.isAudioPreparing = false
//                songAdapter!!.updatePlayingItem(selectedSong)
//            }
        }

    }

}
