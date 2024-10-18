package com.onourem.android.activity.ui.admin.main.other

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSettingsBinding
import com.onourem.android.activity.models.GetReportResponse
import com.onourem.android.activity.models.ReportItem
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel

class UserReportsFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentSettingsBinding>() {

    private lateinit var adapter: ReportsAdapter

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

        viewModel.getReportAbuseQueryInfoByAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetReportResponse> ->
            if (apiResponse.loading) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = true
                } else {
                    showProgress()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode == "000") {
                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    setAdapter(apiResponse.body.reportInappropriateResList)
                } else {
                    showAlert(apiResponse.errorMessage)
                }

            } else {
                hideProgress()
                showAlert(
                    getString(R.string.label_network_error), apiResponse.errorMessage
                )
            }
        }

//        val list = ArrayList<ReportItem>()
//        list.add(ReportItem("1759", "Onourem User", "XXX-reporting-ABCDEFGHIJKLMNOPQRSTUVWXYZ-abcdefghijklmnopqrstuvwxyz", "2023-11-30 12:46:11.0", "1200", "Kedar", "2355" , "Vocals", "Comment","098", ))
//        list.add(ReportItem("1759", "Onourem User", "XXX-reporting-ABCDEFGHIJKLMNOPQRSTUVWXYZ-abcdefghijklmnopqrstuvwxyz", "2023-11-30 12:46:11.0", "1200", "Kedar", "2355" , "Vocals", "Comment","098", ))
//        list.add(ReportItem("1759", "Onourem User", "XXX-reporting-ABCDEFGHIJKLMNOPQRSTUVWXYZ-abcdefghijklmnopqrstuvwxyz", "2023-11-30 12:46:11.0", "1200", "Kedar", "2355" , "Vocals", "Comment","098", ))
//        list.add(ReportItem("1759", "Onourem User", "XXX-reporting-ABCDEFGHIJKLMNOPQRSTUVWXYZ-abcdefghijklmnopqrstuvwxyz", "2023-11-30 12:46:11.0", "1200", "Kedar", "2355" , "Vocals", "Comment","098", ))
//        list.add(ReportItem("1759", "Onourem User", "XXX-reporting-ABCDEFGHIJKLMNOPQRSTUVWXYZ-abcdefghijklmnopqrstuvwxyz", "2023-11-30 12:46:11.0", "1200", "Kedar", "2355" , "Vocals", "Comment","098", ))
//        list.add(ReportItem("1759", "Onourem User", "XXX-reporting-ABCDEFGHIJKLMNOPQRSTUVWXYZ-abcdefghijklmnopqrstuvwxyz", "2023-11-30 12:46:11.0", "1200", "Kedar", "2355" , "Vocals", "Comment","098", ))
//        setAdapter(list)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(settingsItemList: ArrayList<ReportItem>) {

        adapter = ReportsAdapter(settingsItemList) {
            when (it.first) {
                ReportsAdapter.CLICK_COMMENTS -> {
//                    it.second.isOpen = !it.second.isOpen
//                    adapter.notifyDataSetChanged()
                }
            }
        }
        binding.rvSettings.adapter = adapter
    }
}