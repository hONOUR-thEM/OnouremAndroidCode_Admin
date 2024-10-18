package com.onourem.android.activity.ui.games.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogEditPrivacyBinding
import com.onourem.android.activity.models.GetVisibilityResponse
import com.onourem.android.activity.models.UpdateVisibilityResponse
import com.onourem.android.activity.models.VisibilityGroupList
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.EditPrivacyAdapter
import com.onourem.android.activity.ui.games.adapters.FooterAdapter
import com.onourem.android.activity.ui.games.viewmodel.OneToManyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class EditPrivacyDialogFragment :
    AbstractBaseBindingDialogFragment<OneToManyViewModel, DialogEditPrivacyBinding>() {
    private var adapter: EditPrivacyAdapter? = null
    private var postId: String? = null
    private var gameId: String? = null
    private var showPrivateOption = false
    override fun layoutResource(): Int {
        return R.layout.dialog_edit_privacy
    }

    override fun viewModelType(): Class<OneToManyViewModel> {
        return OneToManyViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = EditPrivacyDialogFragmentArgs.fromBundle(requireArguments())
        postId = args.postId
        gameId = args.gameId
        showPrivateOption = args.showPrivateOption
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDone.setOnClickListener(ViewClickListener { v: View? ->
            viewModel.updateVisibility(
                postId!!, gameId!!, Utilities.getTokenSeparatedString(
                    adapter!!.selected, ","
                ), "N"
            ).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<UpdateVisibilityResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        navController.popBackStack()
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else if (!apiResponse.loading) {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "updateVisibility")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "updateVisibility",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        })
        binding.btnCancel.setOnClickListener(ViewClickListener { v: View? -> navController.popBackStack() })
        viewModel.getVisibility(postId!!, gameId!!).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetVisibilityResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.visibilityGroupList == null || apiResponse.body.visibilityGroupList!!.isEmpty()) {
                        showAlert("Looks like content is not available anymore, please try refreshing your screen.") { v: View? -> navController.popBackStack() }
                    } else {
                        val privacyGroups = apiResponse.body.visibilityGroupList
                        setAdapter(privacyGroups as MutableList<VisibilityGroupList>)
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else if (!apiResponse.loading) {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getGlobalSearchResult")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getGlobalSearchResult",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setAdapter(privacyGroups: MutableList<VisibilityGroupList>) {
        val defaultSelected: MutableList<VisibilityGroupList> = ArrayList()
        val iterator = privacyGroups.listIterator()
        while (iterator.hasNext()) {
            val privacyGroup = iterator.next()
            if (privacyGroup.status!!) {
                defaultSelected.add(privacyGroup)
            }
            if (privacyGroup.groupName.equals("Receivers Only", ignoreCase = true)) {
                if (!showPrivateOption) iterator.remove()
                privacyGroup.groupName = "Private"
                break
            }
        }
        binding.rvPrivacies.layoutManager =
            LinearLayoutManager(requireActivity())
        adapter = EditPrivacyAdapter(privacyGroups, defaultSelected)
        val footerAdapter =
            FooterAdapter("You can create custom privacy groups from 'Privacy Groups' section in the Menu")
        val concatAdapter = ConcatAdapter(adapter, footerAdapter)
        binding.rvPrivacies.adapter = concatAdapter
    }
}