package com.onourem.android.activity.ui.admin.main.portal

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPortalUsersBinding
import com.onourem.android.activity.models.GetPackageAndInstitutionCodeResponse
import com.onourem.android.activity.models.InstituteNameId
import com.onourem.android.activity.models.PortalUsersResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

@Suppress("UNCHECKED_CAST")
class PortalUsersFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentPortalUsersBinding>() {

    private lateinit var adapterInstitute: ArrayAdapter<InstituteNameId>
    private var institute: InstituteNameId? = null



    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_portal_users
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPackageAndInstitutionCode()

        binding.tilSpinner.setOnItemClickListener { parent, _, pos, _ ->
            institute = parent.getItemAtPosition(pos) as InstituteNameId
            loadData(institute!!)
        }

        binding.fab.setOnClickListener(ViewClickListener{
            navController.navigate(MobileNavigationDirections.actionGlobalNavPortalUserCreate())
        })

    }

    private fun getPackageAndInstitutionCode() {

        viewModel.getPackageAndInstitutionCode().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetPackageAndInstitutionCodeResponse> ->
            if (apiResponse.loading) {
                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    hideProgress()

                    adapterInstitute = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        apiResponse.body.institutionCodeList
                    )

                    binding.tilSpinner.setAdapter(adapterInstitute)

                    binding.tilSpinner.setText(adapterInstitute.getItem(0).toString(), false)

                    loadData(apiResponse.body.institutionCodeList[0])


                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }


    private fun loadData(institute: InstituteNameId) {

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvSettings.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        viewModel.getPortalUserListByAdmin(institute.id)?.observe(viewLifecycleOwner) { apiResponse: ApiResponse<PortalUsersResponse> ->
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
                setAdapter(apiResponse.body.portalUserList as ArrayList<String>)
            } else {
                hideProgress()
                showAlert(
                    getString(R.string.label_network_error), apiResponse.errorMessage
                )
            }
        }
    }

    private fun setAdapter(settingsItemList: ArrayList<String>) {
        binding.rvSettings.adapter = PortalUsersAdapter(settingsItemList) {

        }
    }
}