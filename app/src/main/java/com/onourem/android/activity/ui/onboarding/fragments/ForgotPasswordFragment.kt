package com.onourem.android.activity.ui.onboarding.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentForgotPasswordBinding
import com.onourem.android.activity.models.ForgotPasswordResponse
import com.onourem.android.activity.ui.utils.AppValidate
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import java.util.*

class ForgotPasswordFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentForgotPasswordBinding>() {
    private var strEmail = ""
    override fun layoutResource(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        binding.tvBackToLogin.setOnClickListener(ViewClickListener { v: View? ->
            //NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack()
        })
        binding.btnResetPassword.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                viewModel.forgotPassword(strEmail)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<ForgotPasswordResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                //NavController navController = Navigation.findNavController(requireView());
                                navController.popBackStack()
                                showAlert(apiResponse.body.errorMessage)
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }
                        } else {
                            hideProgress()
                            showAlert(apiResponse.errorMessage)
                        }
                    }
            }
        })
    }

    private val isValidData: Boolean
        get() {
            strEmail =
                binding.tilEmail.editText!!.text.toString().lowercase(
                    Locale.getDefault()
                ).trim { it <= ' ' }
            if (TextUtils.isEmpty(binding.tilEmail.editText!!.text) && !AppValidate.isValidEmail(
                    strEmail
                )
            ) {
                showAlert("Please enter valid email Id.")
                return false
            }
            return true
        }
}