package com.onourem.android.activity.ui.games.fragments

import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPrivacyGroupsBinding
import com.onourem.android.activity.models.GetAllGroupsResponse
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.PrivacyGroupAdapter
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class PrivacyGroupsFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, FragmentPrivacyGroupsBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var questionGamesAdapter: PrivacyGroupAdapter? = null
    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_privacy_groups
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getActionMutableLiveData().observe(this) { action ->
            if (action == null) return@observe
            val actionType: ActionType? = action.actionType
            if (actionType == null || actionType == ActionType.NONE) return@observe
            viewModel.actionConsumed()
            if (actionType == ActionType.ADD_REMOVE_PRIVACY_GROUP_MEMBERS) {
                (action.data as PrivacyGroup?)?.let { viewModel.setPrivacyGroup(it) }
                navController.navigate(PrivacyGroupsFragmentDirections.actionNavPrivacyGroupsToNavPrivacyGroupMember())
            } else if (actionType == ActionType.DELETE_PRIVACY_GROUP) {
                initView(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (questionGamesAdapter != null) binding.rvPrivacyGroup.adapter = questionGamesAdapter
        binding.rvPrivacyGroup.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPrivacyGroup.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) binding.fab.extend() else if (dy > 0) binding.fab.shrink()
            }
        })
        binding.fab.setOnClickListener(ViewClickListener {
            navController.navigate(
                PrivacyGroupsFragmentDirections.actionNavPrivacyGroupsToNavPrivacyGroupMember()
            )
        })
        initView(questionGamesAdapter == null)
    }

    private fun initView(showProgress: Boolean) {
        viewModel.getAllGroups("23").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetAllGroupsResponse> ->
            if (apiResponse.loading) {
                if (showProgress) showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    setAdapter(apiResponse.body)
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        apiResponse.body.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            }
        }
    }

    private fun setAdapter(playGroupsResponse: GetAllGroupsResponse?) {
        val privacyGroupArrayList: ArrayList<PrivacyGroup> = ArrayList()
        if (playGroupsResponse!!.privacyGroups != null) {
            for (privacyGroup: PrivacyGroup in playGroupsResponse.privacyGroups!!) {
                if (privacyGroup.groupId > 4) {
                    privacyGroupArrayList.add(privacyGroup)
                }
            }
        }
        if (privacyGroupArrayList.isEmpty()) {
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.rvPrivacyGroup.visibility = View.GONE
        } else {
            binding.rvPrivacyGroup.visibility = View.VISIBLE
            binding.tvErrorMessage.visibility = View.GONE
            if (questionGamesAdapter == null) {
                questionGamesAdapter = PrivacyGroupAdapter(
                    privacyGroupArrayList
                ) { item: Pair<String, PrivacyGroup> ->
                    if (item.first.equals(PrivacyGroupAdapter.ACTION_ROW, ignoreCase = true)) {
                        viewModel.setPrivacyGroup(item.second)
                        navController.navigate(PrivacyGroupsFragmentDirections.actionNavPrivacyGroupsToNavPrivacyGroupMember())
                    } else if (item.first.equals(
                            PrivacyGroupAdapter.ACTION_MORE,
                            ignoreCase = true
                        )
                    ) {
                        val bundle: Bundle = Bundle()
                        val actions: ArrayList<Action<*>> = ArrayList()
                        val privacyGroup: PrivacyGroup = item.second
                        actions.add(
                            Action(
                                getString(R.string.action_label_add_remove_members),
                                R.color.color_black,
                                ActionType.ADD_REMOVE_PRIVACY_GROUP_MEMBERS,
                                privacyGroup
                            )
                        )
                        actions.add(
                            Action(
                                getString(R.string.action_label_delete_privacy_group),
                                R.color.color_red,
                                ActionType.DELETE_PRIVACY_GROUP,
                                privacyGroup
                            )
                        )
                        bundle.putParcelableArrayList(
                            Constants.KEY_BOTTOM_SHEET_ACTIONS,
                            actions
                        )
                        navController.navigate(
                            PrivacyGroupsFragmentDirections
                                .actionNavPrivacyGroupsToNavBootomSheet(
                                    privacyGroup.groupName!!,
                                    bundle
                                )
                        )
                    }
                }
                binding.rvPrivacyGroup.adapter = questionGamesAdapter
            } else {
                questionGamesAdapter!!.resetData(privacyGroupArrayList)
            }
        }
    }
}