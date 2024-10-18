package com.onourem.android.activity.ui.games.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogPrivacyGroupNameBinding
import com.onourem.android.activity.databinding.FragmentPrivacyGroupMemberBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.MembersListAdapter
import com.onourem.android.activity.ui.games.adapters.PrivacyMembersListAdapter
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject

class PrivacyGroupsMemberFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, FragmentPrivacyGroupMemberBinding>() {
    private val groupMembers: MutableList<GroupMember> = ArrayList()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    //    private List<UserList> blockedUserIds;
    private var membersListAdapter: PrivacyMembersListAdapter? = null
    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_privacy_group_member
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
        viewModel.getSelectedFriendForGroup().observe(this) { friend ->
            if (friend != null) {
                if (viewModel.getPrivacyGroup().isNew) {
                    createNewGroup(friend)
                } else {
                    addNewFriendInGroup(friend)
                }
                viewModel.resetAddNewFriendInGroup()
            }
        }
        viewModel.getActionMutableLiveData().observe(this) { action ->
            if (action == null) return@observe
            val actionType: ActionType? = action.actionType
            if (actionType == null || actionType == ActionType.NONE) return@observe
            viewModel.actionConsumed()
            if (actionType == ActionType.DELETE_PRIVACY_GROUP) {
                navController.popBackStack(R.id.nav_question_games, false)
            } else if (actionType == ActionType.REMOVE_FROM_PRIVACY_GROUP) {
                val item: Pair<PlayGroup, GroupMember> =
                    action.data as Pair<PlayGroup, GroupMember>
                val iterator: MutableIterator<GroupMember> = groupMembers.iterator()
                while (iterator.hasNext()) {
                    val user: GroupMember = iterator.next()
                    if ((user.userId == item.second.userId)) {
                        iterator.remove()
                        break
                    }
                }
                if (groupMembers.size > 0) {
                    membersListAdapter!!.notifyDataSetChanged()
                } else {
                    navController.popBackStack()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvGroupNameHint: AppCompatTextView = view.findViewById(R.id.tvGroupNameHint)
        tvGroupNameHint.text = getString(R.string.action_label_privacy_group_name)
        val switchCompat: SwitchCompat = view.findViewById(R.id.switchCompat)
        switchCompat.visibility = View.GONE
        binding.clContainer.setOnClickListener(ViewClickListener { v: View? ->
            val extras: FragmentNavigator.Extras = FragmentNavigator.Extras.Builder()
                .addSharedElement(binding.clContainer, "clContainer")
                .addSharedElement(binding.rvMembers, "rvMembers")
                .build()
            val groupMemberIds: ArrayList<String> = ArrayList()
            for (item: GroupMember in groupMembers) {
                groupMemberIds.add(item.userId!!)
            }
            navController.navigate(
                PrivacyGroupsMemberFragmentDirections.actionNavPrivacyGroupMemberToNavAddGroupMember(
                    "Privacy Group Members",
                    groupMemberIds
                ), extras
            )
        })
        val layoutManager: LinearLayoutManager = LinearLayoutManager(requireActivity())
        binding.rvMembers.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.rvMembers.isNestedScrollingEnabled = false
        init()
    }

    private fun init() {
        val privacyGroup: PrivacyGroup = viewModel.getPrivacyGroup()
        if (!privacyGroup.isNew) {
            if (membersListAdapter == null) {
                viewModel.getAllGroupUsers(
                    privacyGroup.groupId.toString(),
                    privacyGroup.groupName
                ).observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<GetAllGroupsResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess) {
                        hideProgress()
                        if (apiResponse.body != null) {
                            val list: List<GroupMember>? = apiResponse.body.groupMember
                            if (list != null && list.size > 0) {
                                groupMembers.clear()
                                groupMembers.addAll(list)
                                //                                if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
//                                    for (UserList blockedUser : blockedUserIds) {
//                                        Iterator<GroupMember> iterator = groupMembers.iterator();
//                                        while (iterator.hasNext()) {
//                                            GroupMember user = iterator.next();
//                                            if (user.getUserId().equals(blockedUser.getUserId())) {
//                                                iterator.remove();
//                                            }
//                                        }
//                                    }
//                                }
                                setUserListAdapter(groupMembers)
                            } else {
                                navController.popBackStack()
                            }
                        }
                    } else {
                        hideProgress()
                        showAlert(
                            requireActivity().getString(R.string.label_network_error),
                            apiResponse.errorMessage
                        )
                        if (apiResponse.errorMessage != null && ((apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getAllGroupUsers")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "getAllGroupUsers",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
            } else {
                binding.rvMembers.adapter = membersListAdapter
            }
        } else if (TextUtils.isEmpty(privacyGroup.groupName)) {
            showGroupNameDialog("")
        }
        binding.tvGroupName.text = privacyGroup.groupName
        binding.ivEditGroupName.setOnClickListener(ViewClickListener { v: View? ->
            showGroupNameDialog(
                privacyGroup.groupName!!
            )
        })
    }

    private fun setUserListAdapter(groupMembers: List<GroupMember>) {
        membersListAdapter = PrivacyMembersListAdapter(
            groupMembers, { item: Pair<String, GroupMember> ->
                when (item.first) {
                    MembersListAdapter.ACTION_MORE -> showMoreActions(item.second)
                    MembersListAdapter.ACTION_ROW -> showUserProfile(item.second.userId!!)
                }
            }, preferenceHelper!!.getString(
                Constants.KEY_LOGGED_IN_USER_ID
            )
        )
        binding.rvMembers.adapter = membersListAdapter
    }

    private fun showMoreActions(groupMember: GroupMember) {
        val title: String = groupMember.firstName + " " + groupMember.lastName
        val actions: ArrayList<Action<*>> = ArrayList()
        actions.add(
            Action(
                getString(R.string.action_label_remove_from_group),
                R.color.color_red,
                ActionType.REMOVE_FROM_PRIVACY_GROUP,
                Pair(
                    viewModel.getPrivacyGroup(), groupMember
                )
            )
        )
        val bundle: Bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            PrivacyGroupsMemberFragmentDirections
                .actionNavPrivacyGroupMemberToNavBootomSheet(title, bundle)
        )
    }

    private fun showUserProfile(userId: String) {
        navController.navigate(MobileNavigationDirections.actionGlobalNavProfile(null, userId))
    }

    private fun addNewFriendInGroup(item: UserList) {
        viewModel.addPrivacyGroupUser(
            viewModel.getPrivacyGroup().groupId.toString(),
            item.userId
        ).observe(
            viewLifecycleOwner,
            Observer({ standardResponseApiResponse: ApiResponse<StandardResponse> ->
                if (standardResponseApiResponse.loading) {
                    showProgress()
                } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                    hideProgress()
                    val response: StandardResponse? = standardResponseApiResponse.body
                    if (response!!.errorCode.equals("000", ignoreCase = true)) {
                        val newMember: GroupMember = GroupMember()
                        newMember.userId = item.userId
                        newMember.firstName = item.firstName
                        newMember.lastName = item.lastName
                        newMember.profilePicture = item.profilePicture
                        groupMembers.add(0, newMember)
                        if (membersListAdapter != null) membersListAdapter!!.notifyDataSetChanged()
                    } else {
                        showAlert(response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(
                        getString(R.string.label_network_error),
                        standardResponseApiResponse.errorMessage
                    )
                }
            })
        )
    }

    private fun createNewGroup(item: UserList) {
        viewModel.createCustomGroup(binding.tvGroupName.text.toString(), item.userId)
            .observe(
                viewLifecycleOwner,
                Observer { apiResponse: ApiResponse<GetAllGroupsResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    }
                    else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        val response: GetAllGroupsResponse? = apiResponse.body
                        if (response!!.errorCode.equals("000", ignoreCase = true)) {
                            viewModel.getPrivacyGroup().isNew = false
                            viewModel.getPrivacyGroup().groupId = response.groupId!!.toInt()
                            viewModel.getPrivacyGroup().groupName = binding.tvGroupName.text.toString()
                            init()
                        }
                        else {
                            showAlert(response.errorMessage)
                        }
                    }
                    else {
                        hideProgress()
                        showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    }
                }
            )
    }

    private fun showGroupNameDialog(groupName: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val dialogBinding: DialogPrivacyGroupNameBinding =
            DialogPrivacyGroupNameBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogBinding.root)
        builder.setCancelable(false)
        val alertDialog: AlertDialog = builder.create()
        dialogBinding.tvDialogTitle.text = getString(R.string.action_label_set_group_name)
        dialogBinding.edtGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (dialogBinding.edtGroupName.text
                        .toString().length == 1 && (dialogBinding.edtGroupName.tag
                        .toString() == "true")
                ) {
                    dialogBinding.edtGroupName.tag = "false"
                    dialogBinding.edtGroupName.setText(
                        dialogBinding.edtGroupName.text.toString().uppercase(
                            Locale.getDefault()
                        )
                    )
                    dialogBinding.edtGroupName.setSelection(
                        dialogBinding.edtGroupName.text.toString().length
                    )
                }
                if (dialogBinding.edtGroupName.text.toString().length == 0) {
                    dialogBinding.edtGroupName.tag = "true"
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        if (!TextUtils.isEmpty(groupName)) {
            dialogBinding.edtGroupName.setText(groupName)
        }
        dialogBinding.btnDialogCancel.setOnClickListener(ViewClickListener(View.OnClickListener({ view1: View? ->
            alertDialog.dismiss()
            if (TextUtils.isEmpty(
                    viewModel.getPrivacyGroup().groupName
                )
            ) navController.popBackStack()
        })))
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener(View.OnClickListener({ view12: View? ->
            val name: String = dialogBinding.edtGroupName.text.toString().trim({ it <= ' ' })
            if (!TextUtils.isEmpty(name) && !AppUtilities.hasSpecialChars(name)) {
                binding.tvGroupName.text = name
                val privacyGroup: PrivacyGroup = viewModel.getPrivacyGroup()
                if (!privacyGroup.isNew) {
                    viewModel.updatePrivacyGroupName(privacyGroup.groupId.toString(), name)
                        .observe(
                            viewLifecycleOwner,
                            Observer({ response: ApiResponse<StandardResponse> ->
                                if (response.loading) {
                                    showProgress()
                                } else if (!response.isSuccess || (response.body == null) || !response.body.errorCode
                                        .equals("000", ignoreCase = true)
                                ) {
                                    hideProgress()
                                    showAlert(
                                        requireActivity().getString(R.string.label_network_error),
                                        response.errorMessage
                                    )
                                } else {
                                    hideProgress()
                                }
                            })
                        )
                }
                privacyGroup.groupName = name
                alertDialog.dismiss()
            } else {
                showAlert("Please enter a valid Privacy Group name")
            }
        })))
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setPrivacyGroup(null)
    }
}