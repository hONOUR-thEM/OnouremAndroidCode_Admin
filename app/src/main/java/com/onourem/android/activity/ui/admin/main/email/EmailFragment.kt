package com.onourem.android.activity.ui.admin.main.email

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentEmailBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class EmailFragment  //    private int counter = 0;
    : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentEmailBinding>() {
    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper


    override fun layoutResource(): Int {
        return R.layout.fragment_email
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLive.setOnClickListener(ViewClickListener { v: View? -> sendEmail("Live") })
        binding.btnTest.setOnClickListener(ViewClickListener { v: View? -> sendEmail("Test") })


    }

    private fun sendEmail(type: String) {

        if (!TextUtils.isEmpty(binding.edtSubject.text) && !TextUtils.isEmpty(binding.edtHTMLBody.text)){
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Email Send Alert")
            builder.setCancelable(true)
            builder.setMessage("Would you like to send $type emails?")

            builder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                notifyAllUserByAdmin(type, binding.edtSubject.text.toString(), binding.edtHTMLBody.text.toString())
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }else{
            showAlert("Make sure you have entered the Email Subject Line and HTML Content correctly")
        }

    }

    private fun notifyAllUserByAdmin(type: String, htmlSubject: String, htmlBodyContent: String) {
        viewModel.notifyAllUserByAdmin(type, htmlSubject, htmlBodyContent)
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<StandardResponse> ->
                if (apiResponse1.loading) {
                    showProgress();
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    hideProgress();
                    if (apiResponse1.body.errorCode.equals("000")) {
                        showAlert("Success")
                    }

                } else {
                    hideProgress();
                    showAlert(apiResponse1.errorMessage)
                    if (apiResponse1.errorMessage != null
                        && (apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "updateAndroidNotificationStatus")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "updateAndroidNotificationStatus",
                            apiResponse1.code.toString()
                        )
                    }
                }
            }
    }


}