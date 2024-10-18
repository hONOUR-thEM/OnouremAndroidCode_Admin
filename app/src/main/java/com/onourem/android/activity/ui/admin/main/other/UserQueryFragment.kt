package com.onourem.android.activity.ui.admin.main.other

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSettingsBinding
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.GetUserQueryResponse
import com.onourem.android.activity.models.UserQueryItem
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.Constants
import javax.inject.Inject

class UserQueryFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentSettingsBinding>() {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private lateinit var adapter: UserQueryAdapter

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

        viewModel.getUserQueryByAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserQueryResponse> ->
            if (apiResponse.loading) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = true
                } else {
                    showProgress()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                setAdapter(apiResponse.body.userQueryList as ArrayList<UserQueryItem>)
            } else {
                hideProgress()
                showAlert(
                    getString(R.string.label_network_error), apiResponse.errorMessage
                )
            }
        }

//        val list = ArrayList<UserQueryItem>()
//        list.add(UserQueryItem("1759", "Jonny John", "Chhatrapati is a royal title from Sanskrit used to denote a monarch or imperial head of state. The word \"Chhatrapati\" is a Sanskrit language compound word of chhatra and pati (master/lord/ruler). This title was used by the House of Bhonsle, between 1674 and 1818", "2023-11-30 12:46:11.0", "Contact Us"))
//        list.add(UserQueryItem("1759", "Kenny Ken", "How to use onourem?", "2023-11-30 12:46:11.0", "Report Us"))
//        list.add(UserQueryItem("1759", "Bobby Bob", "How to make friends on onourem?", "2023-11-30 12:46:11.0", "Report Us"))
//        setAdapter(list)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(settingsItemList: ArrayList<UserQueryItem>) {
        val loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)

        adapter = UserQueryAdapter(settingsItemList) {

            var userId = ""

            when (BuildConfig.FLAVOR) {
                "AdminDev" -> userId = "2023"
                "AdminProd" -> userId = "4264"
            }

            if (userId != it.second.userId) {
                when (it.first) {
                    UserQueryAdapter.CLICK_COMMENTS -> {
                        val conversation = Conversation()
                        conversation.id = "EMPTY"
                        conversation.userName = it.second.userName
                        conversation.userTwo = it.second.userId
                        conversation.userOne = loginUserId
                        conversation.profilePicture = ""
                        conversation.userTypeId = "1"
                        conversation.query = it.second.description

                        navController.navigate(UserQueryFragmentDirections.actionGlobalNavConversationDetails(conversation))
                    }
                }
            } else {
                Toast.makeText(fragmentContext, "Admin Cant Send Reply To Himself", Toast.LENGTH_LONG).show()
            }


        }
        binding.rvSettings.adapter = adapter
    }
}