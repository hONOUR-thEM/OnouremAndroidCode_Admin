package com.onourem.android.activity.ui.games.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAddGroupMemberBinding
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.FriendsSearchListAdapter
import com.onourem.android.activity.ui.games.adapters.SelectedUserListAdapter
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class AddGroupMemberFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, FragmentAddGroupMemberBinding>() {
    private val friendList: MutableList<UserList> = ArrayList()
    private val selectedFriendList = ArrayList<UserList?>()
    private var friendsSearchListAdapter: FriendsSearchListAdapter? = null

    //    private List<UserList> blockedUserIds;
    private var groupMembers: ArrayList<String>? = null
    private var selectedUserListAdapter: SelectedUserListAdapter? = null
    private var playgroupUserLimit = 0
    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_add_group_member
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AddGroupMemberFragmentArgs.fromBundle(requireArguments())
        groupMembers = args.groupMemberIds as ArrayList<String>
        val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.rvMembers.layoutManager = layoutManager
        binding.tvMessage.visibility = View.GONE
        binding.rvMembers.visibility = View.VISIBLE
        val layoutManager1 = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.rvSelectedMembers.layoutManager = layoutManager1
        selectedUserListAdapter = SelectedUserListAdapter(selectedFriendList) { item: UserList ->
            for (user in friendList) {
                if (item.userId.equals(user.userId, ignoreCase = true)) {
                    user.isAlreadyGroupMember = false
                    user.isSelected = false
                    friendsSearchListAdapter!!.notifyDataSetChanged()
                    selectedFriendList.remove(item)
                    selectedUserListAdapter!!.notifyDataSetChanged()
                    if (selectedFriendList.size > 0) {
                        binding.tvIsFriend.visibility = View.VISIBLE
                    } else {
                        binding.tvIsFriend.visibility = View.GONE
                    }
                    break
                }
            }
        }
        binding.rvSelectedMembers.adapter = selectedUserListAdapter

        // if (friendsSearchListAdapter == null) {
        viewModel.friendList.observe(viewLifecycleOwner) { friendsListResponseApiResponse: ApiResponse<UserListResponse> ->
            if (friendsListResponseApiResponse.isSuccess && friendsListResponseApiResponse.body != null) {
                if (friendsListResponseApiResponse.body.errorCode.equals(
                        "000",
                        ignoreCase = true
                    )
                ) {
                    val list = friendsListResponseApiResponse.body.userList
                    playgroupUserLimit = friendsListResponseApiResponse.body.playgroupUserLimit!!
                    //BlockedUsersUtility.filterBlockedUsers(blockedUserIds, list);
                    if (list.isEmpty()) {
                        binding.tvMessage.visibility = View.VISIBLE
                        binding.rvMembers.visibility = View.INVISIBLE
                    } else {
                        friendList.addAll(list)
                        setAdapter()
                    }
                } else {
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvMembers.visibility = View.INVISIBLE
                }
            } else if (!friendsListResponseApiResponse.loading) {
                hideProgress()
                showAlert(friendsListResponseApiResponse.errorMessage)
                if (friendsListResponseApiResponse.errorMessage != null
                    && (friendsListResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || friendsListResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getFriendList")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getFriendList",
                        friendsListResponseApiResponse.code.toString()
                    )
                }
            }
        }
        //    }
//        else {
//            binding.rvMembers.setAdapter(friendsSearchListAdapter);
//        }
        binding.etAddFriends.onFocusChangeListener =
            OnFocusChangeListener { v: View?, hasFocus: Boolean ->
                if (hasFocus) {
                    setAdapter()
                }
            }
        Objects.requireNonNull(binding.etAddFriends).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (friendsSearchListAdapter != null && friendsSearchListAdapter!!.filter != null) {
                    friendsSearchListAdapter!!.filter.filter(s)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.tvIsFriend.setOnClickListener(ViewClickListener { v: View? ->
            if (selectedFriendList.size > 0) {
                viewModel.addNewSelectedFriendInGroup(selectedFriendList)
                navController.popBackStack()
            }
        })
        if (navController.currentDestination != null) navController.currentDestination!!.label =
            AddGroupMemberFragmentArgs.fromBundle(
                requireArguments()
            ).screenTitle
    }

    private fun setAdapter() {
        if (groupMembers != null && !groupMembers!!.isEmpty()) {
            for (userId in groupMembers!!) {
                for (user in friendList) {
                    if (userId.equals(user.userId, ignoreCase = true)) {
                        user.isAlreadyGroupMember = true
                        user.isSelected = true
                        break
                    }
                }
            }
        }
        friendsSearchListAdapter =
            FriendsSearchListAdapter(friendList, playgroupUserLimit) { item: UserList ->
                var skipAdd = false
                if (groupMembers != null && !groupMembers!!.isEmpty()) {
                    for (userId in groupMembers!!) {
                        if (userId.equals(item.userId, ignoreCase = true)) {
                            skipAdd = true
                            break
                        }
                    }
                }
                if (!skipAdd) {
                    viewModel.addNewFriendInGroup(item)
                    navController.popBackStack()
                }
            }
        binding.rvMembers.adapter = friendsSearchListAdapter
    }
}