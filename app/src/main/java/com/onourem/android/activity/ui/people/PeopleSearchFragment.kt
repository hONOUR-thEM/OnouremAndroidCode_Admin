package com.onourem.android.activity.ui.people

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPeopleBinding
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.fragments.UserRelation
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class PeopleSearchFragment :
    AbstractBaseViewModelBindingFragment<UserListViewModel, FragmentPeopleBinding>(),
    View.OnClickListener {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var adapter: UserListAdapter? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var userId: String? = null
    private var isLastPage = false
    private var isLoading = false
    private var ids: List<Int>? = null
    private val blockedUserIds: List<UserList>? = null
    private var blockedUserCount = 0
    private var inputUserId: String? = null
    private var shouldShowFriends = false
    private var shouldShowSearchOptions = false
    private var currentVisibleTabId = -1
    private var dashboardViewModel: DashboardViewModel? = null

    override fun viewModelType(): Class<UserListViewModel> {
        return UserListViewModel::class.java
    }

    override fun onDestroyView() {
//        dashboardViewModel!!.updateTrendingData(true)
        super.onDestroyView()
    }


    override fun layoutResource(): Int {
        return R.layout.fragment_people
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
        //        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
        setHasOptionsMenu(true)
    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
////        val item = menu.findItem(R.id.search_nav)
////        if (item != null) item.isVisible = false
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {


            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
                override fun onPrepareMenu(menu: Menu) {
                    menu.findItem(R.id.search_nav)?.setVisible(false)
                    menu.findItem(R.id.profile_nav)?.setVisible(false)
                    menu.findItem(R.id.phone_nav)?.setVisible(false)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                    return false
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

            userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
                if (action != null && action.actionType != ActionType.DISMISS) {
                    userActionViewModel!!.actionConsumed()
                    if (action.actionType == ActionType.CANCEL_FRIEND_REQUEST) {
                        cancelFriendRequest(action)
                    } else if (action.actionType == ActionType.UN_FRIEND) {
                        removeFriend(action)
                    }
                }
            }
            shouldShowFriends = PeopleSearchFragmentArgs.fromBundle(
                requireArguments()
            ).shouldShowFriends
            shouldShowSearchOptions = PeopleSearchFragmentArgs.fromBundle(
                requireArguments()
            ).shouldShowSearchOptions
            userId = PeopleSearchFragmentArgs.fromBundle(
                requireArguments()
            ).userId
            inputUserId = PeopleSearchFragmentArgs.fromBundle(
                requireArguments()
            ).inputUserId
            binding.container.visibility = View.GONE
            blockedUserCount = 0
            //view.postDelayed({ init() }, 300)
            init()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.container.visibility = View.VISIBLE
        binding.btnMyFriends.setOnClickListener(this)
        binding.btnYouMayKnow.setOnClickListener(this)
        binding.btnSearchResults.setOnClickListener(this)
        binding.btnMyFriends.setBackgroundResource(R.drawable.selector_search_friend_tab_radio_button)
        binding.btnYouMayKnow.setBackgroundResource(R.drawable.selector_search_friend_tab_radio_button)
        binding.btnSearchResults.setBackgroundResource(R.drawable.selector_search_friend_tab_radio_button)
        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvUserList.addItemDecoration(VerticalSpaceItemDecoration(10))
        binding.rvUserList.layoutManager = linearLayoutManager
        binding.rvUserList.addOnScrollListener(object : PaginationListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@PeopleSearchFragment.isLoading = true
                loadMoreFriends()
            }

            override fun isLastPage(): Boolean {
                return this@PeopleSearchFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@PeopleSearchFragment.isLoading
            }
        })
        if (currentVisibleTabId == -1) {
            adapter = UserListAdapter(ArrayList()) { item: Pair<String, UserList> ->
                when (item.first) {
                    UserListAdapter.ACTION_ROW -> showUserProfile(item.second)
                    UserListAdapter.ACTION_BUTTON_IGNORE_FRIEND -> ignoreFriendRequest(item)
                    UserListAdapter.ACTION_BUTTON_IS_FRIEND -> {
                        val title = "What would you like to do?"
                        when (UserRelation.getStatus(item.second.status)) {
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
            if (shouldShowFriends) {
                binding.btnMyFriends.performClick()
            } else {
                if (shouldShowSearchOptions) {
                    binding.rgTabs.visibility = View.VISIBLE
                    binding.tilSearch.visibility = View.VISIBLE
                    binding.btnInvite.visibility = View.VISIBLE
                    binding.btnYouMayKnow.performClick()
                } else {
                    binding.rgTabs.visibility = View.GONE
                    binding.tilSearch.visibility = View.GONE
                    binding.btnInvite.visibility = View.GONE
                    binding.btnMyFriends.performClick()
                }
            }
        } else {
            if (adapter != null) {
                binding.rvUserList.adapter = adapter
            } else {
                if (currentVisibleTabId == binding.btnMyFriends.id) {
                    binding.btnMyFriends.performClick()
                } else if (currentVisibleTabId == binding.btnSearchResults.id) {
                    binding.btnSearchResults.performClick()
                } else {
                    binding.btnYouMayKnow.performClick()
                }
            }
        }
        binding.tilSearch.editText!!.setOnTouchListener { v: View?, event: MotionEvent? ->
            if (binding.rgTabs.checkedRadioButtonId != R.id.btnSearchResults) {
                binding.btnSearchResults.performClick()
            }
            false
        }
        binding.tilSearch.editText!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) searchUsers()
            false
        }
        binding.btnInvite.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavInviteFriends()
            )
        })
    }

    override fun onClick(view: View) {
        binding.tvMessage.visibility = View.GONE
        isLastPage = false
        isLoading = false
        if (adapter != null) adapter!!.removeFooter()
        blockedUserCount = 0
        currentVisibleTabId = view.id
        val loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        if (view.id == R.id.btnMyFriends) {
            adapter!!.clearData()
            Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
            if (loginUserId == userId) {
                viewModel.getMyFriendList(userId!!)
                    .observe(this) { apiResponse: ApiResponse<UserListResponse> ->
                        handleResponse(
                            apiResponse,
                            "getMyFriendList"
                        )
                    }
            } else {
                viewModel.getUserFriendList(userId!!)
                    .observe(this) { apiResponse: ApiResponse<UserListResponse> ->
                        handleResponse(
                            apiResponse,
                            "getUserFriendList"
                        )
                    }
            }
        } else if (view.id == R.id.btnYouMayKnow) {
            adapter!!.clearData()
            Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
            var id = inputUserId
            if (TextUtils.isEmpty(inputUserId) || loginUserId.equals(
                    inputUserId,
                    ignoreCase = true
                )
            ) {
                id = loginUserId
            }
            viewModel.getFriendSuggestionList(id!!)
                .observe(this) { apiResponse: ApiResponse<UserListResponse> ->
                    handleResponse(
                        apiResponse,
                        "getFriendSuggestionList"
                    )
                }
        } else if (view.id == R.id.btnSearchResults) {
            showSearchResults()
        }
    }

    private fun searchUsers() {
        adapter!!.clearData()
        val searchText =
            Objects.requireNonNull(binding.tilSearch.editText!!).text.toString()
                .trim { it <= ' ' }
        viewModel.getGlobalSearchResult(searchText)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserListResponse> ->
                handleResponse(
                    apiResponse,
                    "getGlobalSearchResult"
                )
            }
    }

    private fun loadMoreFriends() {
        if (ids == null || ids!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val start = adapter!!.itemCount + blockedUserCount
        val end =
            if (ids!!.size - start > PaginationListener.PAGE_ITEM_SIZE) start + PaginationListener.PAGE_ITEM_SIZE else ids!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val userIds: MutableList<Int> = ArrayList()
        for (i in start until end) {
            val id = ids!![i]
            if (id != null) userIds.add(id)
        }
        //Log.e("####", String.format("start: %d ; end: %d ; sent: %s ; total: %d", start, end, userIds, ids.size()));
        //Log.e("####", String.format("sent ids: %s", userIds));
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnMyFriends) {
//            userId = preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID);
            Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
            viewModel.getNextMyFriendList(Utilities.getTokenSeparatedString(userIds, ","))
                .observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<UserListResponse> ->
                    handleNextResponse(
                        apiResponse,
                        "getNextMyFriendList"
                    )
                }
        } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnYouMayKnow) {
            Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
            viewModel.getNextFriendSuggestionList(Utilities.getTokenSeparatedString(userIds, ","))
                .observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<UserListResponse> ->
                    handleNextResponse(
                        apiResponse,
                        "getNextFriendSuggestionList"
                    )
                }
        } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnSearchResults) {
            viewModel.getNextGlobalSearchList(Utilities.getTokenSeparatedString(userIds, ","))
                .observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<UserListResponse> ->
                    handleNextResponse(
                        apiResponse,
                        "getNextGlobalSearchList"
                    )
                }
        }
    }

    private fun handleNextResponse(
        apiResponse: ApiResponse<UserListResponse>,
        serviceName: String
    ) {
        if (apiResponse.loading) {
            if (adapter != null) {
                adapter!!.addLoading()
            }
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            if (adapter != null) {
                adapter!!.removeLoading()
            }
            isLoading = false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.userList == null || apiResponse.body.userList.isEmpty()) {
                    isLastPage = true
                    setFooterMessage()
                } else {
                    val users = apiResponse.body.userList
                    //blockedUserCount += BlockedUsersUtility.filterBlockedUsers(blockedUserIds, users);
                    adapter!!.addItems(users)
                    if (users.size + blockedUserCount >= ids!!.size) {
                        isLastPage = true
                        setFooterMessage()
                    }
                    //Log.e("####", String.format("server: %d", apiResponse.body.getUserList().size()));
                }
            } else {
                isLastPage = true
                setFooterMessage()
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            isLoading = false
            if (adapter != null) {
                adapter!!.removeLoading()
            }
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

    private fun setFooterMessage() {
        isLoading = false
        var footerMessage = ""
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnYouMayKnow && adapter!!.itemCount == 0) {
            footerMessage =
                "No friend suggestions yet. Make few friends to start seeing suggestions here."
        } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnSearchResults && adapter!!.itemCount == 0) {
            footerMessage = "No users found with this search. Please modify your search text."
        } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnMyFriends && adapter!!.itemCount == 0) {
            footerMessage =
                "You do not have any friends to enjoy Onourem with. \n Search  or invite your friends and family to join Onourem and earn points"
        }
        if (adapter != null && !TextUtils.isEmpty(footerMessage)) {
            val finalFooterMessage = footerMessage
            binding.rvUserList.postDelayed({ adapter!!.addFooter(finalFooterMessage) }, 200)
        }
    }

    private fun handleResponse(apiResponse: ApiResponse<UserListResponse>, serviceName: String) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.userList != null && !apiResponse.body.userList.isEmpty()) {
                    ids = apiResponse.body.userIdList
                    val users = apiResponse.body.userList
                    //blockedUserCount += BlockedUsersUtility.filterBlockedUsers(blockedUserIds, users);
                    setMyFriendsAdapter(users)
                } else {
                    val errorMessage: String
                    errorMessage = if (binding.rgTabs.checkedRadioButtonId == R.id.btnMyFriends) {
                        getString(R.string.message_no_friends_found)
                    } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnYouMayKnow) {
                        getString(R.string.message_no_friend_suggestions_found)
                    } else {
                        getString(R.string.message_no_users_found_with_this_search_please_modify_your_search_text)
                    }
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

    private fun showSearchResults() {
        adapter!!.clearData()
        val errorMessage =
            getString(R.string.message_no_users_found_with_this_search_please_modify_your_search_text)
        binding.tvMessage.text = errorMessage
        binding.tvMessage.visibility = View.VISIBLE
        binding.rvUserList.visibility = View.GONE
    }

    private fun setMyFriendsAdapter(userList: List<UserList>) {
        binding.tvMessage.visibility = View.GONE
        binding.rvUserList.visibility = View.VISIBLE
        adapter!!.resetData(userList)
        binding.rvUserList.adapter = adapter
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
        val userList = action?.data as UserList
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
        val userList = action?.data as UserList
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
        if (!userInfoList.userId.equals("4264", ignoreCase = true)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavProfile(
                    null,
                    userInfoList.userId
                )
            )
        } else {
            showAlert("You can't access profile of this admin user")
        }
    }
}