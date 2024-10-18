package com.onourem.android.activity.ui.circle.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentFriendsCircleAddNumberBinding
import com.onourem.android.activity.models.GetVerifiedPhoneNumbersResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.MobileNumberAdapter
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject


class FriendsCircleAddNumberFragment :
    AbstractBaseViewModelBindingFragment<FriendCircleGameViewModel, FragmentFriendsCircleAddNumberBinding>() {

    private lateinit var arrayList: ArrayList<String>
    private lateinit var adapter: MobileNumberAdapter
    private var userPhoneNumbersResponse: GetVerifiedPhoneNumbersResponse? = null


    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friends_circle_add_number
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.isEnabled = false
        binding.btnNext.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.gray_color_light
            )
        )

        binding.btnAddNumber.setOnClickListener(ViewClickListener {
            //Common.showCenterToast(fragmentContext, "Adding Number...")
            if (arrayList.size < 3) {
                navController.navigate(FriendsCircleAddNumberFragmentDirections.actionNavFriendsCircleAddNumberToNavOtp())
            } else {
                showAlert(
                    "Limit Reached",
                    "You can add upto 3 numbers. Remove one or more existing numbers to add another one."
                )
            }
        })

        binding.btnNext.setOnClickListener(ViewClickListener {
            if (arrayList.size > 0) {
                navController.navigate(
                    FriendsCircleAddNumberFragmentDirections.actionNavFriendsCircleAddNumberToNavSelectContacts(
                        false, false
                    )
                )
            }
            //Common.showCenterToast(fragmentContext, "Next")
        })

        binding.btnSkip.setOnClickListener(ViewClickListener {
            navController.popBackStack(R.id.nav_home, false)
        })

        viewModel.getUpdatedNumber().observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.setUpdateMobileConsumed()
                arrayList.add(it)
                adapter.notifyDataSetChanged()
                if (!binding.btnNext.isEnabled) {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setBackgroundColor(
                        ContextCompat.getColor(
                            fragmentContext,
                            R.color.good_red
                        )
                    )
                }
            }
        }

        if (userPhoneNumbersResponse == null) {
            getVerifiedPhoneNumbers()
        } else {
            setAdapter(userPhoneNumbersResponse!!.verifiedNumberList as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */)
        }

    }

    private fun getVerifiedPhoneNumbers() {

        viewModel.getVerifiedPhoneNumbers().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetVerifiedPhoneNumbersResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    userPhoneNumbersResponse = apiResponse.body
                    setAdapter(apiResponse.body.verifiedNumberList as ArrayList<String>)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun deleteVerifiedPhoneNumber(phoneNumber: String) {

        viewModel.deleteVerifiedPhoneNumber(phoneNumber).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    this.arrayList.remove(phoneNumber)
                    adapter.notifyDataSetChanged()
                    if (this.arrayList.size == 0) {
                        binding.btnNext.isEnabled = false
                        binding.btnNext.setBackgroundColor(
                            ContextCompat.getColor(
                                fragmentContext,
                                R.color.gray_color_light
                            )
                        )
                    }

                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun setAdapter(arrayList: ArrayList<String>) {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvMobileNumbers.layoutManager = layoutManager
        this.arrayList = ArrayList<String>()
        this.arrayList.addAll(arrayList)
        adapter = MobileNumberAdapter(this.arrayList) {
            deleteVerifiedPhoneNumber(it)
        }

        if (this.arrayList.size > 0) {
            if (!binding.btnNext.isEnabled) {
                binding.btnNext.isEnabled = true
                binding.btnNext.setBackgroundColor(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.good_red
                    )
                )
            }
        }

        binding.rvMobileNumbers.adapter = adapter

    }


}