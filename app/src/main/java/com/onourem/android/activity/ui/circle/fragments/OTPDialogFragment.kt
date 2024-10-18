package com.onourem.android.activity.ui.circle.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.util.Pair
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogCountryCodesBinding
import com.onourem.android.activity.databinding.DialogOtpBinding
import com.onourem.android.activity.models.SendOtpResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.SingleChoiceCountryAdapter
import com.onourem.android.activity.ui.games.dialogs.WhatsAppAlertDialog
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.Common


class OTPDialogFragment :
    AbstractBaseDialogBindingFragment<FriendCircleGameViewModel, DialogOtpBinding>() {

    private lateinit var singleChoiceAdapter: SingleChoiceCountryAdapter
    private var stopTimer: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private lateinit var dialog1: AlertDialog

    override fun layoutResource(): Int {
        return R.layout.dialog_otp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.search_nav)?.setVisible(false)
                menu.findItem(R.id.profile_nav)?.setVisible(false)
                menu.findItem(R.id.phone_nav)?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        countryAlertInit()

        binding.clCountry.setOnClickListener(ViewClickListener {
            dialog1.show()
        })

        binding.tvResendOtp.setOnClickListener(ViewClickListener {
            if (binding.tvResendOtp.text == "Resend OTP") {
                if (binding.edtMobile.text!!.length < 10) {
                    Common.showCenterToast(fragmentContext, "Enter Valid Mobile Number")
                    return@ViewClickListener
                } else {
                    verifyPhoneNumber(
                        binding.edtMobile.text.toString(),
                        singleChoiceAdapter.selected.isdCode
                    )
                }
            }
        })

        binding.tvOtpNotSent.setOnClickListener(ViewClickListener {
            openWhatsappDialog()
        })

        binding.btnVerify.setOnClickListener(ViewClickListener {

            if (binding.btnVerify.text == "Send Otp On WhatsApp") {
                if (binding.edtMobile.text!!.length < 10) {
                    Common.showCenterToast(fragmentContext, "Enter Valid Mobile Number")
                    return@ViewClickListener
                } else {
                    binding.tvOtpNotSent.visibility = View.VISIBLE
                    binding.btnVerify.text = "Verify"
                    binding.clOtp.visibility = View.VISIBLE
                    binding.tvResendOtp.visibility = View.INVISIBLE
                    binding.tvOtpEnter.visibility = View.VISIBLE

                    verifyPhoneNumber(
                        binding.edtMobile.text.toString(),
                        singleChoiceAdapter.selected.isdCode
                    )
                }

            } else {
                if (binding.edtOtp.text!!.length < 6) {
                    Common.showCenterToast(fragmentContext, "Enter Valid OTP")
                    return@ViewClickListener
                }

                if (binding.edtOtp.text!!.length == 6) {
                    verifyOTP(
                        binding.edtMobile.text.toString(),
                        singleChoiceAdapter.selected.isdCode,
                        binding.edtOtp.text.toString()
                    )
                }
            }


        })

        binding.btnCancel.setOnClickListener(ViewClickListener {
            stopTimer = true
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
            dismiss()
        })

        binding.edtMobile.doAfterTextChanged {
            if (it != null && it.isEmpty() || it!!.length >= 10) {
                if (countDownTimer != null) {
                    countDownTimer!!.cancel()
                    binding.tvResendOtp.visibility = View.GONE
                }
                binding.btnVerify.text = "Send Otp On WhatsApp"
                binding.tvResendOtp.text = "Resend OTP"
            }
        }

    }

    private fun openWhatsappDialog() {
        WhatsAppAlertDialog.showAlert(
            fragmentContext,
            "",
            "Our WhatsApp may be facing some issues to deliver your OTP. Send us a message on WhatsApp to fix this issue immediately.",
            null,
            "Cancel",
            "Open WhatsApp"
        ) { item1: Pair<Int, Any?>? ->
            if (WhatsAppAlertDialog.ACTION_RIGHT == item1!!.first) {
                binding.tvOtpNotSent.visibility = View.INVISIBLE
                binding.tvResendOtp.visibility = View.VISIBLE
                val url = "https://wa.me/916350673748?text=OTP%20not%20received"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)

                try {
                    val pm = context!!.packageManager
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                } catch (e: PackageManager.NameNotFoundException) {
                    Toast.makeText(
                        fragmentContext,
                        "Whatsapp app not installed in your phone",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }


    private fun countryAlertInit() {

        val builder = AlertDialog.Builder(requireActivity())
        val dialogViewBinding =
            DialogCountryCodesBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogViewBinding.root)
        builder.setCancelable(true)
        dialog1 = builder.create()
        dialog1.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog1.window!!
            .setBackgroundDrawableResource(android.R.color.transparent)


        singleChoiceAdapter = SingleChoiceCountryAdapter(
            requireActivity(), countryCodesList()
        ) { item: ISDCodesModel ->
            binding.tvCountry.text = item.codeName
            binding.tvCode.text = "+" + item.isdCode
            singleChoiceAdapter.updateItem(item.isdCode)
            dialog1.hide()
        }

        dialogViewBinding.rvCategory.layoutManager = LinearLayoutManager(requireActivity())
        dialogViewBinding.rvCategory.adapter = singleChoiceAdapter
        singleChoiceAdapter.updateItem("91")

    }

    private fun verifyPhoneNumber(phoneNumber: String, countryCode: String) {
        viewModel.verifyPhoneNumber(phoneNumber, countryCode).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<SendOtpResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                stopTimer = false
                countDownTimer = object : CountDownTimer(120000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (!stopTimer) {
                            binding.tvResendOtp.text = String.format(
                                "You can resend OTP after %s seconds",
                                millisUntilFinished / 1000
                            )
                        }
                    }

                    override fun onFinish() {
                        if (!stopTimer) {
                            binding.tvOtpNotSent.visibility = View.INVISIBLE
                            binding.tvResendOtp.visibility = View.VISIBLE
                            stopTimer = true
                            binding.tvResendOtp.text = "Resend OTP"
                            countDownTimer = null
                        }
                    }
                }.start()

                showAlert("An OTP has been sent to your WhatsApp Number\n$phoneNumber")

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.message != "") {
                        showAlert(apiResponse.body.message)
                    } else {
                        //binding.edtOtp.setText(apiResponse.body.otp)
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

    private fun verifyOTP(phoneNumber: String, countryCode: String, otp: String) {
        viewModel.verifyOTP(phoneNumber, countryCode, otp).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.message != "") {
                        showAlert(apiResponse.body.message)
                    } else {
                        stopTimer = true
                        viewModel.setUpdatedNumber(binding.edtMobile.text.toString())
                        dismiss()
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

    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }


    private fun countryCodesList(): ArrayList<ISDCodesModel> {

        val countryCodes = ArrayList<ISDCodesModel>()

        countryCodes.add(ISDCodesModel("Argentina", "54", "ARG"))
        countryCodes.add(ISDCodesModel("Australia", "61", "AUS"))
        countryCodes.add(ISDCodesModel("Austria", "43", "AUT"))
        countryCodes.add(ISDCodesModel("Bangladesh", "880", "BGD"))
        countryCodes.add(ISDCodesModel("Belgium", "32", "BEL"))
        countryCodes.add(ISDCodesModel("Bhutan", "975", "BTN"))
        countryCodes.add(ISDCodesModel("Brazil", "55", "BRA"))
        countryCodes.add(ISDCodesModel("Canada", "1", "CAN"))
        countryCodes.add(ISDCodesModel("Denmark", "45", "DNK"))
        countryCodes.add(ISDCodesModel("Finland", "358", "FIN"))
        countryCodes.add(ISDCodesModel("France", "33", "FRA"))
        countryCodes.add(ISDCodesModel("Georgia", "995", "GEO"))
        countryCodes.add(ISDCodesModel("Germany", "49", "DEU"))
        countryCodes.add(ISDCodesModel("India", "91", "IND"))
        countryCodes.add(ISDCodesModel("Indonesia", "62", "IDN"))
        countryCodes.add(ISDCodesModel("Iran", "98", "IRN"))
        countryCodes.add(ISDCodesModel("Ireland", "353", "IRL"))
        countryCodes.add(ISDCodesModel("Israel", "972", "ISR"))
        countryCodes.add(ISDCodesModel("Italy", "39", "ITA"))
        countryCodes.add(ISDCodesModel("Japan", "81", "JPN"))
        countryCodes.add(ISDCodesModel("Kenya", "254", "KEN"))
        countryCodes.add(ISDCodesModel("Malaysia", "60", "MYS"))
        countryCodes.add(ISDCodesModel("Myanmar", "95", "MMR"))
        countryCodes.add(ISDCodesModel("Nepal", "977", "NPL"))
        countryCodes.add(ISDCodesModel("Netherlands", "31", "NLD"))
        countryCodes.add(ISDCodesModel("New Zealand", "64", "NZL"))
        countryCodes.add(ISDCodesModel("Norway", "47", "NOR"))
        countryCodes.add(ISDCodesModel("Pakistan", "92", "PAK"))
        countryCodes.add(ISDCodesModel("Philippines", "63", "PHL"))
        countryCodes.add(ISDCodesModel("Poland", "48", "POL"))
        countryCodes.add(ISDCodesModel("Portugal", "351", "PRT"))
        countryCodes.add(ISDCodesModel("Qatar", "974", "QAT"))
        countryCodes.add(ISDCodesModel("Russia", "7", "RUS"))
        countryCodes.add(ISDCodesModel("Saudi Arabia", "966", "SAU"))
        countryCodes.add(ISDCodesModel("Singapore", "65", "SGP"))
        countryCodes.add(ISDCodesModel("South Africa", "27", "ZAF"))
        countryCodes.add(ISDCodesModel("South Korea", "82", "KOR"))
        countryCodes.add(ISDCodesModel("Spain", "34", "ESP"))
        countryCodes.add(ISDCodesModel("Sri Lanka", "94", "LKA"))
        countryCodes.add(ISDCodesModel("Sweden", "46", "SWE"))
        countryCodes.add(ISDCodesModel("Switzerland", "41", "CHE"))
        countryCodes.add(ISDCodesModel("Turkey", "90", "TUR"))
        countryCodes.add(ISDCodesModel("United Arab Emirates", "971", "ARE"))
        countryCodes.add(ISDCodesModel("United Kingdom", "44", "GBR"))
        countryCodes.add(ISDCodesModel("United States", "1", "USA"))
        countryCodes.add(ISDCodesModel("Vietnam", "84", "VNM"))
        countryCodes.add(ISDCodesModel("Zimbabwe", "263", "ZWE"))

        return countryCodes

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialog1.dismiss()
    }
}

data class ISDCodesModel(val countryName: String, val isdCode: String, val codeName: String)