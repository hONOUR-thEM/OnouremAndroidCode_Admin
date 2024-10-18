package com.onourem.android.activity.ui.circle.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentFriendsThoughtsBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.FriendsThoughtsAdapter
import com.onourem.android.activity.ui.circle.adapters.RecyclerViewHeaderAdapter
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.Common
import javax.inject.Inject


class FriendsThoughtsFragment :
    AbstractBaseViewModelBindingFragment<FriendCircleGameViewModel, FragmentFriendsThoughtsBinding>() {

    private lateinit var userActionViewModel: UserActionViewModel
    private lateinit var arrayList: ArrayList<UserQualityInfo>
    private lateinit var adapter: FriendsThoughtsAdapter

    private var userPhoneNumbersResponse: GetVerifiedPhoneNumbersResponse? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friends_thoughts
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getQualityQuestions()

        getVerifiedPhoneNumbers()

        getTaggedUserQualityInfo()

        binding.btnPrev.text = "Play Again"

        val clear = FriendsThoughtsFragmentArgs.fromBundle(requireArguments()).clearContactList
        if (clear) {
            clearGameData()
        }

        binding.btnPrev.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).getContactList() != null && (fragmentContext as DashboardActivity).getContactList()!!.size > 0) {
                val actions = java.util.ArrayList<Action<*>>()

                actions.add(
                    Action(
                        "Play With The Same Friends",
                        R.color.color_black,
                        ActionType.PLAY_WITH_SAME_FRIENDS,
                        ""
                    )
                )
                actions.add(
                    Action(
                        "Select New Friends",
                        R.color.color_black,
                        ActionType.PLAY_WITH_NEW_FRIENDS,
                        ""
                    )
                )
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    Constants.KEY_BOTTOM_SHEET_ACTIONS,
                    actions
                )
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                        "What would you like to do?",
                        bundle, ""
                    )
                )
            } else {
                viewModel.reset()
                viewModel.setSelectedContacts("0")

                if (userPhoneNumbersResponse != null && userPhoneNumbersResponse!!.verifiedNumberList.isNotEmpty()) {
                    navController.navigate(
                        FriendsThoughtsFragmentDirections.actionNavFriendsThoughtsToNavSelectContacts(
                            true, false
                        ), NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false).build()
                    )
                } else {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavFriendsCircleAddNumber(true),
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false).build()
                    )
                }
            }

        })

        binding.btnClose.setOnClickListener(ViewClickListener {
            navController.popBackStack(R.id.nav_home, false)
        })

        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType == ActionType.PLAY_WITH_SAME_FRIENDS) {

                    val listOfContacts = viewModel.getSelectedFinalContactsForQuestions(true)
                    if (listOfContacts.size > 0) {
                        listOfContacts.forEach {
                            val item = it
                            item.selected = false
                            viewModel.update(item)
                        }
                    }

                    navController.navigate(
                        FriendsThoughtsFragmentDirections.actionNavFriendsThoughtsToNavSelectContacts(
                            true, false
                        ), NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false).build()
                    )
                }
                if (action.actionType == ActionType.PLAY_WITH_NEW_FRIENDS) {
//                    val savedContactList =
//                        (fragmentContext as DashboardActivity).getContactList()
//                    if (savedContactList != null && savedContactList.size > 0) {
//                        savedContactList.forEach {
//                            it.selected = false
//                            it.isRowCreated = false
//                        }
//                        (fragmentContext as DashboardActivity).setContactList(savedContactList)
//                    }

                    clearGameData()

                    navController.navigate(
                        FriendsThoughtsFragmentDirections.actionNavFriendsThoughtsToNavSelectContacts(
                            true, false
                        ),
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false).build()
                    )
                }
            }
        }

    }

    private fun clearGameData() {
        viewModel.reset()
        viewModel.setSelectedContacts("0")
        (fragmentContext as DashboardActivity).setContactList(null)
    }

    private fun getTaggedUserQualityInfo() {

        viewModel.getTaggedUserQualityInfo().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserQualityResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                    updateQualitySeenStatus()

                    setAdapter(apiResponse.body)

                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun getVerifiedPhoneNumbers() {

        viewModel.getVerifiedPhoneNumbers().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetVerifiedPhoneNumbersResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    userPhoneNumbersResponse = apiResponse.body
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    private fun setAdapter(body: UserQualityResponse) {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvThoughts.layoutManager = layoutManager
        arrayList = ArrayList()

        val firstItem = UserQualityInfo("", 0, 0, "", 0, "Dummy", "", "", "")
        val lastItem = UserQualityInfo("", 0, 0, "", 0, "Dummy", "", "", "")
        arrayList.add(firstItem)
        arrayList.addAll(body.userQualityInfoList)
        arrayList.add(lastItem)

        val colorsWaveList = ArrayList<String>()
        val colorsBackgroundList = ArrayList<String>()

        body.userQualityInfoList.forEach {
            colorsWaveList.add(Common.addHash(it.waveColor))
            colorsBackgroundList.add(Common.addHash(it.backgroundColor))
        }

        adapter = FriendsThoughtsAdapter(colorsWaveList, colorsBackgroundList, arrayList) {
            if (it.friendCount > 0) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavTaggedFriends(
                        it
                    )
                )
            } else {
                showAlert(
                    "You Aren't Tagged",
                    "No friend has tagged you on this quality yet. Play this game with as many friends as possible to receive feedback from them."
                )
            }
        }

        val headerAdapter = RecyclerViewHeaderAdapter(
            "Qualities your friends have highlighted about you", null
        )
        binding.rvThoughts.adapter = ConcatAdapter(headerAdapter, adapter)

//        ViewCompat.setNestedScrollingEnabled(binding.rvThoughts, true)

//        binding.rvThoughts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0 && binding.btnPrev.visibility == View.VISIBLE) {
//                    binding.btnPrev.visibility = View.GONE
//                } else if (dy < 0 && binding.btnClose.visibility == View.VISIBLE) {
//                    binding.btnPrev.visibility = View.VISIBLE
//                }
//            }
//        })

    }


    private fun getQualityQuestions() {

        viewModel.getQualityQuestions().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetQualityQuestionsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.qualityQuestionList!!.isNotEmpty()) {
                        binding.btnPrev.visibility = View.VISIBLE
                    } else {
                        binding.btnPrev.visibility = View.INVISIBLE
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun updateQualitySeenStatus() {

        viewModel.updateQualitySeenStatus().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                // showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

}