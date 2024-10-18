package com.onourem.android.activity.ui.admin.vocals.playback

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSearchVocalBinding
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.people.UserListViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import java.util.*
import javax.inject.Inject

class AdminSearchVocalFragment : AbstractBaseBottomSheetBindingDialogFragment<UserListViewModel, FragmentSearchVocalBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var adapter: VocalsAdapter? = null
    private val userId: String? = null
    private var isLastPage = false
    private var isLoading = false
    private var ids: List<Int>? = null
    private var blockedUserCount = 0
    override fun viewModelType(): Class<UserListViewModel> {
        return UserListViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_search_vocal
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blockedUserCount = 0
        view.postDelayed({ init() }, 300)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        val linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.edtSearchQuery.requestFocus()
        val methodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        methodManager.showSoftInput(binding.edtSearchQuery, InputMethodManager.SHOW_IMPLICIT)
        binding.rvUserList.addItemDecoration(VerticalSpaceItemDecoration(10))
        binding.rvUserList.layoutManager = linearLayoutManager
        //        binding.rvUserList.addOnScrollListener(new PaginationListener(linearLayoutManager) {
//            @Override
//            protected void loadMoreItems() {
//                isLoading = true;
//                loadMoreFriends();
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return isLastPage;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return isLoading;
//            }
//        });
        adapter = VocalsAdapter(ArrayList()) { item: Pair<String?, UserList> ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavAdminVocals(
                    item.second.userId,
                    item.second.firstName + " " + item.second.lastName,
                    "",
                    ""
                )
            )
        }
        binding.rvUserList.adapter = adapter
        showSearchResults()

//        binding.tilSearch.getEditText().setOnTouchListener((v, event) -> {
//            showSearchResults();
//            return false;
//        });
        binding.edtSearchQuery.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) searchUsers()
            false
        }
    }

    private fun searchUsers() {
        adapter!!.clearData()
        val searchText = Objects.requireNonNull(binding.edtSearchQuery.text).toString().trim { it <= ' ' }
        viewModel.getGlobalSearchResult(searchText).observe(
            this
        ) { apiResponse: ApiResponse<UserListResponse> -> handleResponse(apiResponse, "getGlobalSearchResult") }
    }

    private fun loadMoreFriends() {
        if (ids == null || ids!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val start = adapter!!.itemCount + blockedUserCount
        val end = if (ids!!.size - start > PaginationListener.PAGE_ITEM_SIZE) start + PaginationListener.PAGE_ITEM_SIZE else ids!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val userIds: MutableList<Int> = ArrayList()
        for (i in start until end) {
            val id = ids!![i]
            userIds.add(id)
        }
        Log.e("####", String.format("start: %d ; end: %d ; sent: %s ; total: %d", start, end, userIds, ids!!.size))
        Log.e("####", String.format("sent ids: %s", userIds))
        viewModel.getNextGlobalSearchList(Utilities.getTokenSeparatedString(userIds, ",")).observe(
            viewLifecycleOwner,
            Observer<ApiResponse<UserListResponse>> { apiResponse: ApiResponse<UserListResponse> ->
                handleNextResponse(
                    apiResponse,
                    "getNextGlobalSearchList"
                )
            })
    }

    private fun handleNextResponse(apiResponse: ApiResponse<UserListResponse>, serviceName: String) {
        if (apiResponse.loading) {
            if (adapter != null) {
                adapter!!.addLoading()
            }
        }
        else if (apiResponse.isSuccess && apiResponse.body != null) {
            if (adapter != null) {
                adapter!!.removeLoading()
            }
            isLoading = false
            if (apiResponse.body.errorCode.equals("000")) {
                if (apiResponse.body.userList == null || apiResponse.body.userList.isEmpty()) {
                    isLastPage = true
                    setFooterMessage()
                }
                else {
                    val users = apiResponse.body.userList
                    //blockedUserCount += BlockedUsersUtility.filterBlockedUsers(blockedUserIds, users);
                    adapter!!.addItems(users)
                    if (users.size + blockedUserCount >= ids!!.size) {
                        isLastPage = true
                        setFooterMessage()
                    }
                    Log.e("####", String.format("server: %d", apiResponse.body.userList.size))
                }
            }
            else {
                isLastPage = true
                setFooterMessage()
                showAlert(apiResponse.body.errorMessage)
            }
        }
        else {
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
                if (fragmentContext != null) (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(serviceName, apiResponse.code.toString())
            }
        }
    }

    private fun setFooterMessage() {
        isLoading = false
        //        String footerMessage = "";
//        footerMessage = "Please modify your search text.";
//
//        if (adapter != null && !TextUtils.isEmpty(footerMessage)) {
//            String finalFooterMessage = footerMessage;
//            binding.rvUserList.postDelayed(() -> adapter.addFooter(finalFooterMessage), 200);
//        }
    }

    private fun handleResponse(apiResponse: ApiResponse<UserListResponse>, serviceName: String) {
        if (apiResponse.loading) {
            showProgress()
        }
        else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000")) {
                if (apiResponse.body.userList.isNotEmpty()) {
                    ids = apiResponse.body.userIdList
                    val users = apiResponse.body.userList
                    //blockedUserCount += BlockedUsersUtility.filterBlockedUsers(blockedUserIds, users);
                    val userLists = ArrayList<UserList>()
                    for (item in users) {
                        if (!item.userId.equals(preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID), ignoreCase = true)) {
                            userLists.add(item)
                        }
                    }
                    setMyFriendsAdapter(userLists)
                }
                else {
                    val errorMessage = "Please modify your search text"
                    binding.tvMessage.text = errorMessage
                    binding.tvMessage.visibility = View.VISIBLE
                }
            }
            else {
                showAlert(apiResponse.body.errorMessage)
            }
        }
        else {
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
                if (fragmentContext != null) (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(serviceName, apiResponse.code.toString())
            }
        }
    }

    private fun showSearchResults() {
        adapter!!.clearData()
        val errorMessage = "Please modify your search text"
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

    private fun notifyDataSetChanged(data: UserList) {
        if (adapter != null) {
            adapter!!.notifyDataUpdated(data)
        }
    }

    private fun showUserProfile(userInfoList: UserList) {
        if (!userInfoList.userId.equals("4264", ignoreCase = true)) {
            navController.navigate(MobileNavigationDirections.actionGlobalNavProfile(null, userInfoList.userId))
        }
        else {
            showAlert("You can't access profile of this admin user")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}