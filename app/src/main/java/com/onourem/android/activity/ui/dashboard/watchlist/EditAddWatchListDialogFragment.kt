package com.onourem.android.activity.ui.dashboard.watchlist

import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogEditWatchListBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class EditAddWatchListDialogFragment :
    AbstractBaseDialogBindingFragment<DashboardViewModel, DialogEditWatchListBinding>(),
    OnWatchListItemClickListener<Pair<Int, UserWatchList>> {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var editAddWatchListAdapter: EditAddWatchListAdapter? = null
    private var isFromAddWatchList = false
    private var userWatchLists: ArrayList<UserWatchList>? = null
    private var questionViewModel: QuestionGamesViewModel? = null
    private var counter = 0
    override fun layoutResource(): Int {
        return R.layout.dialog_edit_watch_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()

        questionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<*>? ->
            if (action == null || action.actionType == ActionType.NONE || viewModel == null || (navController.currentDestination == null
                        || navController.currentDestination!!.id != R.id.nav_edit_add_watch_list)
            ) return@observe
            questionViewModel!!.actionConsumed()
            if (action.actionType == ActionType.REMOVE_FROM_WATCHLIST) {
                val userWatchList = action.data as UserWatchList
                if (userWatchList != null && editAddWatchListAdapter != null) {
                    userWatchList.status = "AddToWatchList"
                    editAddWatchListAdapter!!.notifyItemChanged(userWatchList)
                    val watchListResponse =
                        (fragmentContext as DashboardActivity).getWatchListData()

                    if (watchListResponse != null) {
                        val userWatchLists = ArrayList<UserWatchList>()
                        watchListResponse.userWatchList!!.forEach {
                            if (it.userId != null && userWatchList.userId != null && it.userId != userWatchList.userId) {
                                userWatchLists.add(it)
                            }
                        }
                        watchListResponse.userWatchList = userWatchLists
                        (fragmentContext as DashboardActivity).setWatchListData(
                            watchListResponse
                        )
                        viewModel.setUpdateWatchlistResponse(watchListResponse)
                    }
                }
            }
        }

        binding.tvDialogTitle.text = "Add/Remove friends to/from Watch List"
        val editAddWatchListDialogFragmentArgs = EditAddWatchListDialogFragmentArgs.fromBundle(
            requireArguments()
        )
        isFromAddWatchList = editAddWatchListDialogFragmentArgs.isFromAddWatchList
        binding.btnDialogOk.visibility = View.VISIBLE
        binding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> dismiss() })
        binding.rvWatchlist.layoutManager = GridLayoutManager(requireActivity(), 2)
        init()
    }

    private fun init() {
        userWatchLists = ArrayList()
        viewModel.watchFriendList().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetWatchFriendListResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                val getWatchFriendListResponse = apiResponse.body
                if (isFromAddWatchList) {
                    for (userWatchList in getWatchFriendListResponse.userWatchFriendList!!) {
                        if (userWatchList.status.equals("AddToWatchList", ignoreCase = true)) {
                            userWatchList.centerText = ""
                            userWatchLists!!.add(userWatchList)
                        }
                    }
                } else {
                    for (userWatchList in getWatchFriendListResponse.userWatchFriendList!!) {
                        userWatchList.centerText = ""
                        userWatchLists!!.add(userWatchList)
                    }
                }
                val userWatchList = UserWatchList()
                userWatchList.centerText =
                    requireActivity().getString(R.string.find_more_friends)
                userWatchLists!!.add(userWatchList)
                editAddWatchListAdapter =
                    EditAddWatchListAdapter(requireActivity(), userWatchLists!!)
                binding.rvWatchlist.adapter = editAddWatchListAdapter
                editAddWatchListAdapter!!.setOnItemClickListener(this)
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getWatchFriendList")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getWatchFriendList",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }


    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun isCancelable(): Boolean {
        return true
    }

    override fun onItemClick(item: Pair<Int, UserWatchList>, position: Int) {
        if (item.first == WatchListActions.ADD_TO_WATCH_LIST) {
            viewModel.addUserToWatchList(item.second.userId!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AcceptPendingWatchResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            counter++
                            item.second.status = "CancelInvitation"
                            if (editAddWatchListAdapter != null) editAddWatchListAdapter!!.notifyItemChanged(
                                position
                            )

                            val watchListResponse =
                                (fragmentContext as DashboardActivity).getWatchListData()
                            if (watchListResponse != null) {
                                watchListResponse.userWatchList!!.add(item.second)
                                (fragmentContext as DashboardActivity).setWatchListData(
                                    watchListResponse
                                )
                                viewModel.setUpdateWatchlistResponse(watchListResponse)
                            }


                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "addUserToWatchList")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "addUserToWatchList",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
        } else if (item.first == WatchListActions.ACCEPT_INVITATION) {
            viewModel.acceptPendingWatchRequest(item.second.userId!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AcceptPendingWatchResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            counter++
                            item.second.status = "Watching"
                            if (editAddWatchListAdapter != null) editAddWatchListAdapter!!.notifyItemChanged(
                                position
                            )
                            val watchListResponse =
                                (fragmentContext as DashboardActivity).getWatchListData()
                            if (watchListResponse != null) {
                                watchListResponse.userWatchList!!.forEach {
                                    if (it.userId == item.second.userId) {
                                        it.status = "Watching"
                                    }
                                }
                                (fragmentContext as DashboardActivity).setWatchListData(
                                    watchListResponse
                                )
                                viewModel.setUpdateWatchlistResponse(watchListResponse)
                            }

                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "acceptPendingWatchRequest")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "acceptPendingWatchRequest",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
        } else if (item.first == WatchListActions.REJECT_INVITATION) {
            viewModel.cancelWatchListRequest(item.second.userId!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            counter++
                            item.second.status = "AddToWatchList"
                            if (editAddWatchListAdapter != null) editAddWatchListAdapter!!.notifyItemChanged(
                                position
                            )

                            val watchListResponse =
                                (fragmentContext as DashboardActivity).getWatchListData()

                            if (watchListResponse != null) {
                                val userWatchLists = ArrayList<UserWatchList>()
                                watchListResponse.userWatchList!!.forEach {
                                    if (it.userId != null && item.second.userId != null && it.userId != item.second.userId) {
                                        userWatchLists.add(it)
                                    }
                                }
                                watchListResponse.userWatchList = userWatchLists
                                (fragmentContext as DashboardActivity).setWatchListData(
                                    watchListResponse
                                )
                                viewModel.setUpdateWatchlistResponse(watchListResponse)
                            }
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "cancelWatchListRequest")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "cancelWatchListRequest",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
        } else if (item.first == WatchListActions.CANCEL_INVITATION) {
            viewModel.cancelWatchListPendingRequest(item.second.userId!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            counter++
                            item.second.status = "AddToWatchList"
                            if (editAddWatchListAdapter != null) editAddWatchListAdapter!!.notifyItemChanged(
                                position
                            )

                            val watchListResponse =
                                (fragmentContext as DashboardActivity).getWatchListData()

                            if (watchListResponse != null) {
                                val userWatchLists = ArrayList<UserWatchList>()
                                watchListResponse.userWatchList!!.forEach {
                                    if (it.userId != null && item.second.userId != null && it.userId != item.second.userId) {
                                        userWatchLists.add(it)
                                    }
                                }
                                watchListResponse.userWatchList = userWatchLists
                                (fragmentContext as DashboardActivity).setWatchListData(
                                    watchListResponse
                                )
                                viewModel.setUpdateWatchlistResponse(watchListResponse)
                            }

                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog(
                                    "Network Error",
                                    "cancelWatchListPendingRequest"
                                )
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "cancelWatchListPendingRequest",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
        } else if (item.first == WatchListActions.WATCHING) {
            counter++
            val arrayList = ArrayList<Action<*>>()
            arrayList.add(
                Action(
                    getString(R.string.action_label_remove_from_watch_list),
                    R.color.color_black,
                    ActionType.REMOVE_FROM_WATCHLIST,
                    item.second
                )
            )
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, arrayList)
            if (navController.currentDestination!!.id == R.id.nav_edit_add_watch_list) {
                navController
                    .navigate(
                        EditAddWatchListDialogFragmentDirections.actionNavEditAddWatchListToNavBootomSheet(
                            String.format("%s %s", item.second.firstName, item.second.lastName),
                            bundle
                        )
                    )
            }
        } else if (item.first == WatchListActions.WATCH_LIST_FIND_MORE_FRIENDS) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavSearch(
                    false, preferenceHelper!!.getString(
                        Constants.KEY_LOGGED_IN_USER_ID
                    ), true, item.second.firstName
                )
            )
        } else {
            throw IllegalStateException("Unexpected value: " + item.first)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (counter > 0) {
            viewModel.setUpdateWatchList(true)
        }
    }
}