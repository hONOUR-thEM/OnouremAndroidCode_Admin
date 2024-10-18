package com.onourem.android.activity.ui.appreciation.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogAppreciateBinding
import com.onourem.android.activity.models.CategoryList
import com.onourem.android.activity.models.GetPostCategoryListNewResponse
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.appreciation.adapters.AppreciateThemeAdapter
import com.onourem.android.activity.ui.appreciation.viewmodel.AppreciationViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class AppreciateDialogFragment :
    AbstractBaseBindingDialogFragment<AppreciationViewModel, DialogAppreciateBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun viewModelType(): Class<AppreciationViewModel> {
        return AppreciationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_appreciate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        binding.tvTitle.text = getString(R.string.title_appreciate_dialog)
        binding.btnCancel.setOnClickListener(ViewClickListener { navController.popBackStack() })
        val loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
            LoginResponse::class.java
        )
        viewModel.getPostCategoryListNew(loginResponse.cityId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetPostCategoryListNewResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals(
                        "000",
                        ignoreCase = true
                    ) && apiResponse.body.categoryList != null && apiResponse.body.categoryList!!.isNotEmpty()
                ) {
                    setAdapter(apiResponse.body.categoryList!!)
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
                        AppUtilities.showLog("Network Error", "getPostCategoryListNew")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getPostCategoryListNew",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setAdapter(list: List<CategoryList>) {
        binding.rvTheme.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.rvTheme.adapter = AppreciateThemeAdapter(list) { item: CategoryList? ->
            viewModel.selectedCategory.observe(
                viewLifecycleOwner
            ) { categoryList: CategoryList? ->
                viewModel.selectedCategory.removeObservers(
                    viewLifecycleOwner
                )
                if (categoryList == null) {
                    viewModel.setSelectedCategory(item!!)
                    navController.navigate(
                        AppreciateDialogFragmentDirections.actionNavAppreciateDialogToNavComposeMessageDialog(
                            true, null
                        )
                    )
                } else {
                    viewModel.setSelectedCategory(item!!)
                    navController.popBackStack()
                }
            }
        }
    }
}