package com.onourem.android.activity.ui.dashboard.guest.audio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentVocalsBinding
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.models.AudioResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio.playback.SongProvider
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.data.OtherVocalsData
import com.onourem.android.activity.ui.data.VocalsData
import com.onourem.android.activity.ui.games.viewmodel.*
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class GuestTrendingVocalsFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentVocalsBinding>() {

    private var positionIndex: Int = -1
    private var topView: Int = -1
    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var isValidLink: Boolean = false
    private var vocalsData: VocalsData? = null
    private var otherVocalsData: OtherVocalsData? = null

    private lateinit var commentsViewModel: CommentsViewModel

    private var isLastPagePagination: Boolean = false
    private var isLoadingPage: Boolean = false
    private var displayNumberOfAudios: Long = 20L
    private var layoutManager: LinearLayoutManager? = null

    private lateinit var dashboardViewModel: DashboardViewModel
    private var editPrivacyAction: Action<Any?>? = null
    private var privacyIds: String = ""
    private lateinit var selectPrivacyViewModel: SelectPrivacyViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    private var songAdapter: GuestTrendingVocalsAdapter? = null
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
        fun create(): GuestTrendingVocalsFragment {
            return GuestTrendingVocalsFragment()
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

        binding.swipeRefreshLayout.setOnRefreshListener {
            isDataLoading = false

            refreshUI()

            if (vocalsData != null || otherVocalsData != null) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        if ((fragmentContext as DashboardActivity).getPrevAudioList().size > 0) {
            (fragmentContext as DashboardActivity).setMusicPlayerVisibility(true)
        } else {
            (fragmentContext as DashboardActivity).setMusicPlayerVisibility(false)
        }

        if (songAdapter == null) {
            loadData()
        } else {
            songsFromServer.forEach {
                it.isPlaying = false
                it.isAudioPreparing = false
            }
            setAdapter()
        }
        //viewModel.reloadUI(true)

    }

    override fun layoutResource(): Int {
        return R.layout.fragment_vocals
    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshUI() {
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

        getAudioShorts(Constants.GUEST_VOCALS)

        // loadFriendsList()

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

        if (songAdapter == null) {
            songAdapter =
                GuestTrendingVocalsAdapter(songsFromServer, preferenceHelper) { item ->

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
                        Constants.CLICK_LIKE -> {
                            (fragmentContext as DashboardActivity).showGuestPopup(
                                "Like Vocal",
                                "You can mark Vocals as favorite after login."
                            )
                        }
                        Constants.CLICK_COMMENT -> {
                            (fragmentContext as DashboardActivity).showGuestPopup(
                                "Write Comment",
                                "You can write comments only after login."
                            )
                        }
                        Constants.CLICK_COMMENT_LIST -> {
                            (fragmentContext as DashboardActivity).showGuestPopup(
                                "Comments",
                                "You can read comments only after login."
                            )
                        }

                    }
                }

        }

        layoutManager = LinearLayoutManager(fragmentContext)
        binding.rvAudioGames.layoutManager = layoutManager
        binding.rvAudioGames.setHasFixedSize(true)
        binding.rvAudioGames.adapter = songAdapter
        songAdapter!!.notifyDataSetChanged()

        setMusic()

        if (positionIndex != -1) {
            layoutManager!!.scrollToPositionWithOffset(positionIndex, topView)
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
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName,
                    apiResponse.code.toString()
                )
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


    private fun getAudioShorts(service: String) {
        isLastPagePagination = false
        isDataLoading = true
        viewModel.getAudioInfo(service, "", "")
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
                                Constants.KEY_AUDIO_LIMIT,
                                apiResponse.body.audioDuration
                            )
                            preferenceHelper.putValue(
                                Constants.KEY_AUDIO_VIEW,
                                apiResponse.body.audioViewDuration
                            )
                        }
                        val gson = Gson()
                        val json = gson.toJson(apiResponse.body)
                        preferenceHelper.putValue(Constants.VOCALS, json)

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


    @SuppressLint("NotifyDataSetChanged")
    private fun setMusic() {

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

}