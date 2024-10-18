package com.onourem.android.activity.ui.admin.main.subscriptions

import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSettingsBinding
import com.onourem.android.activity.models.GetOClubSettingsResponse
import com.onourem.android.activity.models.OClubSettingsItem
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.main.subscriptions.adapters.PlayGroupsAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog

class OClubSettingsFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentSettingsBinding>() {

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            loadData()
        }
    }

    private fun loadData() {

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvSettings.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        viewModel.getPlaygroupCreatedByAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetOClubSettingsResponse> ->
            if (apiResponse.loading) {
                if (binding.swipeRefreshLayout.isRefreshing){
                    binding.swipeRefreshLayout.isRefreshing = true
                }else{
                    showProgress()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                setAdapter(apiResponse.body.playGroupList as ArrayList<OClubSettingsItem>)
            } else {
                hideProgress()
                showAlert(
                    getString(R.string.label_network_error), apiResponse.errorMessage
                )
            }
        }
    }

    private fun blockLinkSharingOnPlaygroupByAdmin(linkEnabled: String, playGroupId: String) {
        viewModel.blockLinkSharingOnPlaygroupByAdmin(linkEnabled, playGroupId)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    loadData()
                } else {
                    hideProgress()
                    showAlert(
                        getString(R.string.label_network_error), apiResponse.errorMessage
                    )
                }
            }
    }

    private fun blockCommentOnPlaygroupByAdmin(commentEnabled: String, playGroupId: String) {
        viewModel.blockCommentOnPlaygroupByAdmin(commentEnabled, playGroupId)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    loadData()
                } else {
                    hideProgress()
                    showAlert(
                        getString(R.string.label_network_error), apiResponse.errorMessage
                    )
                }
            }
    }

    private fun setAdapter(settingsItemList: ArrayList<OClubSettingsItem>) {
        binding.rvSettings.adapter = PlayGroupsAdapter(settingsItemList) {
            when (it.first) {
                PlayGroupsAdapter.CLICK_LINKS -> {
                    TwoActionAlertDialog.showAlert(
                        fragmentContext,
                        "Invite Links are ${
                            if (it.second!!.inviteLinkEnabled == "Y") {
                                "Enabled"
                            } else {
                                "Disabled"
                            }
                        }",
                        "Would you like to change ${it.second!!.playGroupId} - ${it.second!!.playGroupName}'s Invite Link Enable Status?",
                        null,
                        "Cancel",
                        "Yes"
                    ) { item1: Pair<Int?, Song?>? ->
                        if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                            if (it.second!!.inviteLinkEnabled == "Y") {
                                blockLinkSharingOnPlaygroupByAdmin("N", it.second!!.playGroupId)
                            } else if (it.second!!.inviteLinkEnabled == "N"){
                                blockLinkSharingOnPlaygroupByAdmin("Y", it.second!!.playGroupId)
                            }
                        }
                    }
                }
                PlayGroupsAdapter.CLICK_COMMENTS -> {
                    TwoActionAlertDialog.showAlert(
                        fragmentContext,
                        "Comments are ${
                            if (it.second!!.commentsEnabled == "Y") {
                                "Enabled"
                            } else {
                                "Disabled"
                            }
                        }",
                        "Would you like to change ${it.second!!.playGroupId} - ${it.second!!.playGroupName}'s Comments Enable Status?",
                        null,
                        "Cancel",
                        "Yes"
                    ) { item1: Pair<Int?, Song?>? ->
                        if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                            if (it.second!!.commentsEnabled == "Y") {
                                blockCommentOnPlaygroupByAdmin("N", it.second!!.playGroupId)
                            } else if (it.second!!.commentsEnabled == "N"){
                                blockCommentOnPlaygroupByAdmin("Y", it.second!!.playGroupId)
                            }
                        }
                    }
                }
            }
        }
    }
}