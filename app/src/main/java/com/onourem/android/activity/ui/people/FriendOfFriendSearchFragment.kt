package com.onourem.android.activity.ui.people

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentFriendOfFriendSearchBinding
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.fragments.UserRelation
import com.onourem.android.activity.ui.games.fragments.UserRelation.Companion.getStatus
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import java.util.*
import javax.inject.Inject

class FriendOfFriendSearchFragment :
    AbstractBaseViewModelBindingFragment<UserListViewModel, FragmentFriendOfFriendSearchBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var adapter: UserListFOFAdapter? = null
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (adapter != null) {
                adapter!!.filter.filter(s)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
    private var userActionViewModel: UserActionViewModel? = null
    private var inputUserId: String? = null
    private var userList: MutableList<UserList>? = null
    override fun viewModelType(): Class<UserListViewModel> {
        return UserListViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friend_of_friend_search
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
        //        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
        userActionViewModel!!.getActionMutableLiveData().observe(this) { action ->
            if (action != null && action.actionType !== ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType === ActionType.CANCEL_FRIEND_REQUEST) {
                    cancelFriendRequest(action)
                } else if (action.actionType === ActionType.UN_FRIEND) {
                    removeFriend(action)
                }
            }
        }
        inputUserId = FriendOfFriendSearchFragmentArgs.fromBundle(requireArguments()).inputUserId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.container.visibility = View.VISIBLE
        userList = ArrayList()
        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvUserList.addItemDecoration(VerticalSpaceItemDecoration(10))
        binding.rvUserList.layoutManager = linearLayoutManager
        adapter = UserListFOFAdapter(userList!!) { item: Pair<String, UserList> ->
            when (item.first) {
                UserListAdapter.ACTION_ROW -> showUserProfile(item.second)
                UserListAdapter.ACTION_BUTTON_IGNORE_FRIEND -> ignoreFriendRequest(item)
                UserListAdapter.ACTION_BUTTON_IS_FRIEND -> {
                    val title = "What would you like to do?"
                    when (getStatus(item.second.status)) {
                        UserRelation.FRIENDS -> {
                            val actions = ArrayList<Action<*>>()
                            actions.add(
                                Action(
                                    getString(R.string.action_label_un_friend),
                                    R.color.color_black,
                                    ActionType.UN_FRIEND,
                                    item.second
                                )
                            )
                            val bundle = Bundle()
                            bundle.putParcelableArrayList(
                                Constants.KEY_BOTTOM_SHEET_ACTIONS,
                                actions
                            )
                            navController.navigate(
                                MobileNavigationDirections
                                    .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                            )
                        }
                        UserRelation.ADD_FRIEND -> sendFriendRequest(item)
                        UserRelation.ACCEPT_REQUEST -> acceptFriendRequest(item)
                        UserRelation.CANCEL_REQUEST -> {
                            val cancelActions = ArrayList<Action<*>>()
                            cancelActions.add(
                                Action(
                                    getString(R.string.action_cancel_friend_request),
                                    R.color.color_black,
                                    ActionType.CANCEL_FRIEND_REQUEST,
                                    item.second
                                )
                            )
                            val bundle = Bundle()
                            bundle.putParcelableArrayList(
                                Constants.KEY_BOTTOM_SHEET_ACTIONS,
                                cancelActions
                            )
                            navController.navigate(
                                MobileNavigationDirections
                                    .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                            )
                        }
                        UserRelation.NONE -> {}
                    }
                }
            }
        }
        binding.rvUserList.adapter = adapter
        viewModel.getUserFriendList(inputUserId!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserListResponse> ->
                handleResponse(
                    apiResponse,
                    "getUserFriendList"
                )
            }
        Objects.requireNonNull(binding.tilSearch.editText!!).addTextChangedListener(textWatcher)
    }

    private fun handleResponse(apiResponse: ApiResponse<UserListResponse>, serviceName: String) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.userList != null && !apiResponse.body.userList.isEmpty()) {
                    val users = apiResponse.body.userList
                    //blockedUserCount += BlockedUsersUtility.filterBlockedUsers(blockedUserIds, users);
                    setMyFriendsAdapter(users)
                } else {
                    val errorMessage: String
                    //                    if (binding.rgTabs.getCheckedRadioButtonId() == R.id.btnMyFriends) {
//                        errorMessage = getString(R.string.message_no_friends_found);
//                    } else if (binding.rgTabs.getCheckedRadioButtonId() == R.id.btnYouMayKnow) {
//                        errorMessage = getString(R.string.message_no_friend_suggestions_found);
//                    } else {
                    errorMessage =
                        getString(R.string.message_no_users_found_with_this_search_please_modify_your_search_text)
                    //                    }
                    binding.tvMessage.text = errorMessage
                    binding.tvMessage.visibility = View.VISIBLE
                }
            } else {
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            hideProgress()
            showAlert(apiResponse.errorMessage)
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

    private fun setMyFriendsAdapter(users: List<UserList>) {
        userList!!.clear()
        userList!!.addAll(users)
        binding.tvMessage.visibility = View.GONE
        binding.rvUserList.visibility = View.VISIBLE
        binding.rvUserList.adapter = adapter
        binding.tilSearch.clearFocus()
        Objects.requireNonNull(binding.tilSearch.editText!!).setText(
            String.format(
                "%s", binding.tilSearch.editText!!
                    .text
            )
        )
    }

    private fun sendFriendRequest(item: Pair<String, UserList>) {
        viewModel.sendFriendRequest(item.second.userId, "24")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        item.second.status = "Friend Request Sent"
                        notifyDataSetChanged(item.second)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                }
            }
    }

    private fun removeFriend(action: Action<Any?>?) {
        val userList = action!!.data as UserList
        viewModel.removeFriend(userList.userId, "24")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        userList.status = "Add as Friend"
                        notifyDataSetChanged(userList)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                }
            }
    }

    private fun notifyDataSetChanged(data: UserList) {
        if (adapter != null) {
            adapter!!.notifyDataUpdated(data)
        }
    }

    private fun cancelFriendRequest(action: Action<Any?>?) {
        val userList = action!!.data as UserList
        viewModel.cancelSentRequest(userList.userId, "24")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        userList.status = "Add as Friend"
                        notifyDataSetChanged(userList)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                }
            }
    }

    private fun ignoreFriendRequest(item: Pair<String, UserList>) {
        viewModel.cancelPendingRequest(item.second.userId, "24")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        item.second.status = "Add as Friend"
                        notifyDataSetChanged(item.second)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                }
            }
    }

    private fun acceptFriendRequest(item: Pair<String, UserList>) {
        viewModel.acceptPendingRequest(item.second.userId, "24")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        item.second.status = "Friends"
                        notifyDataSetChanged(item.second)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                }
            }
    }

    private fun showUserProfile(userInfoList: UserList) {
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavProfile(
                null,
                userInfoList.userId
            )
        )
    }
}