package com.onourem.android.activity.ui.admin.random

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentGenerateRandomNumbersBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class GenerateRandomNumbersFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentGenerateRandomNumbersBinding>() {

    var textInputLayoutArrayList: ArrayList<TextInputLayout>? = null

    override fun layoutResource(): Int {
        return R.layout.fragment_generate_random_numbers
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {

                viewModel.generateRandomCode(binding.tilNumbers.editText!!.text.toString())
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                showAlert(apiResponse.body.errorMessage, apiResponse.body.message) {
                                    navController.popBackStack()
                                }
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }
                        } else {
                            hideProgress()
                            showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                        }
                    }
            }
        })
    }

    private val isValidData: Boolean
        get() {
            var result = true
            textInputLayoutArrayList = ArrayList()
            textInputLayoutArrayList!!.add(binding.tilNumbers)

            textInputLayoutArrayList!!.forEach {
                if (it.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
                    Toast.makeText(fragmentContext, "Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    result = false
                    return false
                }
            }
            return result
        }
}