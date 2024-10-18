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
import com.onourem.android.activity.models.GetPackageAndInstitutionCodeResponse
import com.onourem.android.activity.models.InstituteNameId
import com.onourem.android.activity.models.OClubSettingsItem
import com.onourem.android.activity.models.PackageNameId
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.main.subscriptions.adapters.InstituteAdapter
import com.onourem.android.activity.ui.admin.main.subscriptions.adapters.PackageAdapter
import com.onourem.android.activity.ui.admin.main.subscriptions.adapters.PlayGroupsAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog

class InstituteFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentSettingsBinding>() {

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

        viewModel.getPackageAndInstitutionCode().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetPackageAndInstitutionCodeResponse> ->
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
                setAdapter(apiResponse.body.institutionCodeList as ArrayList<InstituteNameId>)
            } else {
                hideProgress()
                showAlert(
                    getString(R.string.label_network_error), apiResponse.errorMessage
                )
            }
        }
    }

    private fun setAdapter(settingsItemList: ArrayList<InstituteNameId>) {
        binding.rvSettings.adapter = InstituteAdapter(settingsItemList) {

        }
    }
}