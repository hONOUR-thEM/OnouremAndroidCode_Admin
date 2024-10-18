package com.onourem.android.activity.ui.admin.main.portal

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSignUpPortalBinding
import com.onourem.android.activity.models.GetPackageAndInstitutionCodeResponse
import com.onourem.android.activity.models.InstituteNameId
import com.onourem.android.activity.models.OClubCategory
import com.onourem.android.activity.models.PortalSignUpRequest
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.AppValidate
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class PortalSignUpFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentSignUpPortalBinding>() {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var deviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private lateinit var adapterInstitute: ArrayAdapter<InstituteNameId>
    private var institute: InstituteNameId? = null


    override fun layoutResource(): Int {
        return R.layout.fragment_sign_up_portal
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
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

                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPackageAndInstitutionCode()

        binding.tilSpinner.setOnItemClickListener { parent, _, pos, _ ->
            institute = parent.getItemAtPosition(pos) as InstituteNameId
        }

        AppUtilities.disableEmojis(Objects.requireNonNull(binding.tilFirstName.editText))
//        AppUtilities.disableEmojis(Objects.requireNonNull(binding.tilPhoneNumber.editText))
        binding.btnSignUp.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                val signUpRequest = PortalSignUpRequest()
                signUpRequest.contactPerson =
                    binding.tilFirstName.editText!!.text.toString().trim { it <= ' ' }
                signUpRequest.contactNumber =
                    binding.tilPhoneNumber.editText!!.text.toString().trim { it <= ' ' }
                signUpRequest.emailAddress =
                    binding.tilEmail.editText!!.text.toString()

                val text = binding.tilPassword.editText!!.text.toString()
                signUpRequest.password = Base64Utility.encodeToString(text.trim { it <= ' ' })

                signUpRequest.institutionId = institute?.id!!

                viewModel.createPortalUserByAdmin(signUpRequest)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                showAlert("Alert", "Portal User Added") {
                                    navController.popBackStack()
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
        })
    }

    private val isValidData: Boolean
        get() {
            val fName = binding.tilFirstName.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(fName) || AppUtilities.hasSpecialChars(fName)) {
                showAlert("Please enter valid Contact Person Name.")
                return false
            } else {
                if (binding.tilFirstName.editText!!.text.isEmpty() || binding.tilFirstName.editText!!.text.length > 30) {
                    showAlert("Please enter valid Name of minimum 1 and Max 30 Characters")
                    return false
                }
            }
            val lName = binding.tilPhoneNumber.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(lName)) {
                showAlert("Please enter valid Phone Number.")
                return false
            }

            if (binding.tilSpinner.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlert("Please select Institute.")
                return false
            }
//            if (binding.tilSpinnerProf.text.toString().trim { it <= ' ' }.isEmpty()) {
//                showAlert("Please select profession.")
//                return false
//            }
            if (TextUtils.isEmpty(
                    binding.tilEmail.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter email Id.")
                return false
            } else if (!AppValidate.isValidEmail(
                    binding.tilEmail.editText!!.text.toString().lowercase(
                        Locale.getDefault()
                    ).trim { it <= ' ' })
            ) {
                showAlert("Please enter valid email Id.")
                return false
            }
            if (TextUtils.isEmpty(
                    binding.tilPassword.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter password.")
                return false
            }
            if (binding.tilPassword.editText!!.text.toString()
                    .trim { it <= ' ' } != binding.tilRePassword.editText!!.text.toString()
                    .trim { it <= ' ' }
            ) {
                showAlert("Re-Password does not match with Password.")
                return false
            }
            return true
        }

}