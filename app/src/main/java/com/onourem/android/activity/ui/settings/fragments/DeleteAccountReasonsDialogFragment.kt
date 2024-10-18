package com.onourem.android.activity.ui.settings.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DeleteAccountReasonsBottomSheetBinding
import com.onourem.android.activity.models.GetDeleteAccReasonsResponse
import com.onourem.android.activity.models.ReasonList
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.settings.adapters.SingleChoiceReasonAdapter
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class DeleteAccountReasonsDialogFragment :
    AbstractBaseBottomSheetBindingDialogFragment<SettingsViewModel, DeleteAccountReasonsBottomSheetBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var singleChoiceReasonAdapter: SingleChoiceReasonAdapter? = null
    private var reason: String? = null
    public override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.delete_account_reasons_bottom_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupView() {
        userAcctDeletedReasonList
        binding.cvClose.setOnClickListener(ViewClickListener { v: View? -> navController.popBackStack() })
        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (singleChoiceReasonAdapter!!.selected != null) {
                reason = singleChoiceReasonAdapter!!.selected!!.reason
                if (reason.equals("Other", ignoreCase = true)) {
                    reason = singleChoiceReasonAdapter!!.selected!!.otherReason
                    if (!TextUtils.isEmpty(reason)) {
                        deleteAccount(reason)
                    } else {
                        AppUtilities.showSnackbar(
                            binding.root.parent as ViewGroup,
                            "Please enter the 'Other' text first.",
                            View.GONE,
                            Snackbar.LENGTH_LONG,
                            "",
                            ViewClickListener { v1: View? -> })
                    }
                } else {
                    deleteAccount(reason)
                }
            } else {
                AppUtilities.showSnackbar(
                    binding.root.parent as ViewGroup,
                    "Please select any reason first.",
                    View.GONE,
                    Snackbar.LENGTH_LONG,
                    "",
                    ViewClickListener { v1: View? -> })
            }
        })
    }

    private val userAcctDeletedReasonList: Unit
        get() {
            viewModel.userAcctDeletedReasonList().observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetDeleteAccReasonsResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        binding.tvDialogTitle.text = apiResponse.body.title
                        binding.tvDialogSubTitle.text = apiResponse.body.subtitle
                        setAdapter(apiResponse.body.reasonsList!!)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog(
                                "Network Error",
                                "getUserAcctDeletedReasonList"
                            )
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserAcctDeletedReasonList",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        }

    private fun deleteAccount(reason: String?) {
        //Toast.makeText(getFragmentContext(), "Deleted with " + reason, Toast.LENGTH_SHORT).show();
        viewModel.deleteAccount(
            preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
            reason!!
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    logoutListener.onLogout()
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "deleteAccount")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "deleteAccount",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setAdapter(list: List<ReasonList?>) {
        singleChoiceReasonAdapter = SingleChoiceReasonAdapter(
            fragmentContext,
            list as ArrayList<ReasonList?>
        ) { item: ReasonList? -> }
        binding.rvResult.layoutManager =
            LinearLayoutManager(fragmentContext, RecyclerView.VERTICAL, false)
        binding.rvResult.adapter = singleChoiceReasonAdapter
    }
}