package com.onourem.android.activity.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogDatePickerBinding
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import java.util.*
import javax.inject.Inject

class DatePickerDialogFragment :
    AbstractBaseDialogBindingFragment<OnboardingViewModel, DialogDatePickerBinding>() {
    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    var dashboardViewModel: DashboardViewModel? = null
    public override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_date_picker
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DashboardViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        val monthsList = arrayOf(
            "Month", "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"
        )

        // Set months
        val adapterMonths = ArrayAdapter(
            requireActivity(),
            R.layout.simple_spinner_item, monthsList
        )
        binding.pickerMonth.adapter = adapterMonths
        binding.pickerMonth.setItemChecked(0, true)
        val years = ArrayList<String>()
        var thisYear = Calendar.getInstance()[Calendar.YEAR]
        thisYear = thisYear - 13
        years.add("Year")
        for (i in thisYear downTo 1940) {
            years.add(Integer.toString(i))
        }
        val adapterYear = ArrayAdapter(requireActivity(), R.layout.simple_spinner_item, years)
        binding.pickerYear.adapter = adapterYear
        binding.pickerYear.setItemChecked(0, true)
        binding.btnNext.setOnClickListener(ViewClickListener { v: View? ->
            if (binding.pickerMonth.checkedItemPosition == 0) {
                showAlert("Please select correct month")
            } else if (binding.pickerYear.checkedItemPosition == 0) {
                showAlert("Please select correct year")
            } else {
                val date =
                    "01-" + getMonth(binding.pickerMonth) + "-" + binding.pickerYear.adapter.getItem(
                        binding.pickerYear.checkedItemPosition
                    )
                viewModel.updateDateOfBirth(date).observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        val gson = Gson()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            val loginResponse = gson.fromJson(
                                sharedPreferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                                LoginResponse::class.java
                            )
                            loginResponse.dateOfBirth = date
                            sharedPreferenceHelper!!.putValue(
                                Constants.KEY_USER_OBJECT,
                                gson.toJson(loginResponse)
                            )
                            sharedPreferenceHelper!!.putValue(Constants.KEY_NEW_USER, "Y")
                            startActivity(
                                Intent(
                                    requireActivity(),
                                    DashboardActivity::class.java
                                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
                            requireActivity().finish()
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

    private fun getMonth(monthPicker: ListView): String {
        var month = ""
        when (monthPicker.adapter.getItem(monthPicker.checkedItemPosition).toString()) {
            "January" -> month = "01"
            "February" -> month = "02"
            "March" -> month = "03"
            "April" -> month = "04"
            "May" -> month = "05"
            "June" -> month = "06"
            "July" -> month = "07"
            "August" -> month = "08"
            "September" -> month = "09"
            "October" -> month = "10"
            "November" -> month = "11"
            "December" -> month = "12"
        }
        return month
    }

    override fun isCancelable(): Boolean {
        return false
    }
}