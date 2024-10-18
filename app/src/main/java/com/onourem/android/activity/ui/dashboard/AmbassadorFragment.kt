package com.onourem.android.activity.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentAmbassadorBinding
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.AppValidate
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import java.util.*
import javax.inject.Inject

class AmbassadorFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentAmbassadorBinding>() {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private var adapter: ArrayAdapter<String>? = null

    override fun layoutResource(): Int {
        return R.layout.fragment_ambassador
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inIt()

        AppUtilities.disableEmojis(Objects.requireNonNull(binding.tilFirstName.editText))
        binding.btnDone.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                showAlert("Webservice Call")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun inIt() {


        val loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
            LoginResponse::class.java
        )
        if (loginResponse != null) {

            binding.edtEmail.setText(loginResponse.emailAddress)
            binding.edtEmail.isEnabled = false
            binding.tilFirstName.editText!!.setText(loginResponse.firstName + " " + loginResponse.lastName)

            if (adapter == null) {
                adapter = ArrayAdapter(
                    requireActivity(),
                    R.layout.dropdown_menu_popup_item,
                    resources.getStringArray(R.array.occupation)
                )
            }
            binding.tilSpinner.setAdapter(adapter)
        }

    }

    private val isValidData: Boolean
        get() {
            val fName = binding.tilFirstName.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(fName) || AppUtilities.hasSpecialChars(fName)) {
                showAlert("Please enter valid First Name.")
                return false
            }
            else {
                if (binding.tilFirstName.editText!!
                        .text.isEmpty() || binding.tilFirstName.editText!!.text.length > 30
                ) {
                    showAlert("Please enter valid Name of minimum 1 and Max 30 Characters")
                    return false
                }
            }

            if (binding.tilSpinner.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlert("Please select occupation.")
                return false
            }

            if (binding.tilPhone.editText!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlert("Please enter valid phone number.")
                return false
            }
            if (TextUtils.isEmpty(
                    binding.tilEmail.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter email Id.")
                return false
            }
            else if (!AppValidate.isValidEmail(
                    binding.tilEmail.editText!!.text.toString().lowercase(
                        Locale.getDefault()
                    ).trim { it <= ' ' })
            ) {
                showAlert("Please enter valid email Id.")
                return false
            }

            return true
        }
}