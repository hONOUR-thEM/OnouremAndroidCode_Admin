package com.onourem.android.activity.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentBlockListBinding
import com.onourem.android.activity.models.BlockListResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.BlockUserListAdapter
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.people.UserListViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import javax.inject.Inject

class BlockListFragment :
    AbstractBaseViewModelBindingFragment<UserListViewModel, FragmentBlockListBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var blockUserListAdapter: BlockUserListAdapter? = null
    private var blockUserList: List<UserList>? = null
    override fun viewModelType(): Class<UserListViewModel> {
        return UserListViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_block_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvMembers.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            loadBlockUsersList(false)
        }
        loadBlockUsersList(true)
    }

    private fun loadBlockUsersList(showProgress: Boolean) {
//        viewModel.getBlockUserList();
        viewModel.blockedUserListFromServer.observe(viewLifecycleOwner) { apiResponse: ApiResponse<BlockListResponse> ->
            if (apiResponse.loading) {
                if (showProgress) showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null && apiResponse.body.errorCode.equals(
                    "000",
                    ignoreCase = true
                )
            ) {
                hideProgress()
                if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                    false
                blockUserList = apiResponse.body.blockUserList
                if (blockUserList != null && blockUserList!!.size > 0) {
//                    preferenceHelper.putValue(KEY_BLOCKED_USERS_LIST_OBJECT, new Gson().toJson(blockUserList));
                    binding.rvMembers.visibility = View.VISIBLE
                    binding.tvMessage.visibility = View.GONE
                    setAdapter()
                } else {
                    binding.rvMembers.visibility = View.GONE
                    binding.tvMessage.visibility = View.VISIBLE
                }
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                    false
                hideProgress()
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getBlockedUserListFromServer")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getBlockedUserListFromServer",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setAdapter() {
        blockUserListAdapter = BlockUserListAdapter(blockUserList as MutableList<UserList>) { item: UserList ->
            TwoActionAlertDialog.showAlert(
                requireActivity(),
                null,
                String.format(
                    "Are you sure you want to un-block %s %s",
                    item.firstName,
                    item.lastName
                ),
                item,
                getString(R.string.label_cancel),
                getString(R.string.label_yes),
                OnItemClickListener { `object`: Pair<Int?, UserList?> ->
                    if (TwoActionAlertDialog.ACTION_RIGHT == `object`.first) {
                        assert(`object`.second != null)
                        unblockUser(`object`.second)
                    }
                })
        }
        binding.rvMembers.adapter = blockUserListAdapter
    }

    private fun unblockUser(item: UserList?) {
        viewModel.unBlockUser(item!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        blockUserListAdapter!!.removeItem(item)
                        //                    viewModel.notifyUserBlocked(item);
//                    viewModel.removeLocalBlockedUser(item);
//                    preferenceHelper.putValue(KEY_BLOCKED_USERS_LIST_OBJECT, new Gson().toJson(blockUserList.toString()));
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                    if (blockUserListAdapter!!.itemCount == 0) {
                        binding.rvMembers.visibility = View.GONE
                        binding.tvMessage.visibility = View.VISIBLE
                    } else {
                        binding.rvMembers.visibility = View.VISIBLE
                        binding.tvMessage.visibility = View.GONE
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "unBlockUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "unBlockUser",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }
}