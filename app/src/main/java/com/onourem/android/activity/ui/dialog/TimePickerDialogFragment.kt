package com.onourem.android.activity.ui.dialog

import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.ui.settings.SettingsViewModel
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogTimePickerBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.DashboardActivity
import java.util.ArrayList

//This class is used for Time Picker
class TimePickerDialogFragment :
    AbstractBaseDialogBindingFragment<SettingsViewModel, DialogTimePickerBinding>() {
    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    public override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_time_picker
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        val hoursList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        // Set hours
        val adapterMonths = ArrayAdapter(
            requireActivity(),
            R.layout.simple_spinner_item, hoursList
        )
        binding.pickerHours.adapter = adapterMonths
        val amPm = arrayOf("AM", "PM")

        // Set hours
        val adapterAmPm = ArrayAdapter(
            requireActivity(),
            R.layout.simple_spinner_item, amPm
        )
        binding.pickerAmPm.adapter = adapterAmPm
        val minutes = ArrayList<String>()
        minutes.add("00")
        minutes.add("30")
        val adapterYear = ArrayAdapter(requireActivity(), R.layout.simple_spinner_item, minutes)
        binding.pickerMinute.adapter = adapterYear
        binding.btnNext.setOnClickListener(ViewClickListener { v: View? ->
            if (binding.pickerHours.checkedItemPosition == -1) {
                showAlert("Please select correct hour")
            } else if (binding.pickerMinute.checkedItemPosition == -1) {
                showAlert("Please select correct minutes")
            } else if (binding.pickerAmPm.checkedItemPosition == -1) {
                showAlert("Please select AM/PM")
            } else {
                val time = String.format(
                    "%s:%s:%s %s",
                    binding.pickerHours.adapter.getItem(binding.pickerHours.checkedItemPosition),
                    binding.pickerMinute.adapter.getItem(binding.pickerMinute.checkedItemPosition),
                    "00",
                    binding.pickerAmPm.adapter.getItem(binding.pickerAmPm.checkedItemPosition)
                )
                viewModel.updatePreferredNotificationTime(time).observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            viewModel.actionPerformed(
                                String.format(
                                    "%s:%s %s",
                                    binding.pickerHours.adapter.getItem(binding.pickerHours.checkedItemPosition),
                                    binding.pickerMinute.adapter.getItem(binding.pickerMinute.checkedItemPosition),
                                    binding.pickerAmPm.adapter.getItem(binding.pickerAmPm.checkedItemPosition)
                                )
                            )
                            navController.popBackStack()
                        } else {
                            showAlert(
                                getString(R.string.label_network_error),
                                apiResponse.body.errorMessage
                            )
                        }
                    } else {
                        hideProgress()
                        showAlert(
                            getString(R.string.label_network_error),
                            apiResponse.errorMessage
                        )
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog(
                                    "Network Error",
                                    "updatePreferredNotificationTime"
                                )
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "updatePreferredNotificationTime",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
            }
        })
        binding.btnCancel.setOnClickListener(ViewClickListener { v: View? -> navController.popBackStack() })
    }

    override fun isCancelable(): Boolean {
        return false
    }
}