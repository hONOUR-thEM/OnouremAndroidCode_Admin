package com.onourem.android.activity.ui.dashboard.subscription

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentMySubscriptionBinding
import com.onourem.android.activity.models.GetUserCurrentSubscriptionResponse
import com.onourem.android.activity.models.PackageInfo
import com.onourem.android.activity.models.UserCurrentSubscriptionInfo
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel

class MySubscriptionFragment : AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentMySubscriptionBinding>() {

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_my_subscription
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener(ViewClickListener {
            navController.navigate(MobileNavigationDirections.actionGlobalNavSubscriptionPackage())
        })

        loadData()
    }

    private fun loadData() {

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvSubscriptions.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
        val packageInfoArrayList = ArrayList<UserCurrentSubscriptionInfo>()


        viewModel.getUserCurrentSubscriptions()
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserCurrentSubscriptionResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.loading) {
                        showProgress()
                    }
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        if (apiResponse.body.userCurrentSubscriptionInfo.isNotEmpty()) {
//                            packageInfoArrayList.add(apiResponse.body.userCurrentSubscriptionInfo!![0])
                            //packageInfoArrayList.add(apiResponse.body.subscriptionPackageResList!![1])
                            packageInfoArrayList.addAll(apiResponse.body.userCurrentSubscriptionInfo)

                            if (apiResponse.body.userCurrentSubscriptionInfo.isNotEmpty()) {


                                if (packageInfoArrayList.size == 1) {

                                   // val currentActiveSubscription = packageInfoArrayList.single { it.currentActiveSubscription == "Y" }

                                    if (packageInfoArrayList[0].daysRemainingInSubscription < 30) {
                                        binding.btnSubmit.visibility = View.VISIBLE
                                    } else {
                                        binding.btnSubmit.visibility = View.GONE
                                    }

                                }

                                binding.tvMessage.visibility = View.GONE
                                binding.rvSubscriptions.visibility = View.VISIBLE
                                binding.tvYour.visibility = View.VISIBLE


                            } else {
                                binding.tvMessage.visibility = View.VISIBLE
                                binding.rvSubscriptions.visibility = View.GONE
                                binding.btnSubmit.visibility = View.VISIBLE
                                binding.tvYour.visibility = View.GONE
                            }

                            setAdapter(packageInfoArrayList)

                            hideProgress()
                        }


                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
            }
    }

    private fun setAdapter(packageInfoArrayList: ArrayList<UserCurrentSubscriptionInfo>) {
        binding.rvSubscriptions.adapter = MySubscriptionAdapter(packageInfoArrayList) { item ->
            if (item.freeInviteNumber > 0) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavShareCode(item))
            }
        }
    }
}