package com.onourem.android.activity.ui.admin.main.subscriptions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogAddPocBinding
import com.onourem.android.activity.databinding.FragmentAddInstituteBinding
import com.onourem.android.activity.models.Institute
import com.onourem.android.activity.models.POCUser
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.main.subscriptions.adapters.AddPOCAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener


class AddInstitutionFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAddInstituteBinding>() {

    private var onlineCounsellingSelection: String = ""
    private var offlineCounsellingSelection: String = ""
    private var onouremOnlineSelection: String = ""
    private lateinit var pocUpdatedList: java.util.ArrayList<POCUser>
    private lateinit var pocAdapter: AddPOCAdapter
    private var textInputLayoutArrayList: ArrayList<TextInputLayout>? = null
    private val listOfYesNo = ArrayList<String>()
    private lateinit var adapterOnlineDropdown: ArrayAdapter<String>
    private lateinit var adapterOfflineDropdown: ArrayAdapter<String>
    private lateinit var adapterOnouremOnlineDropdown: ArrayAdapter<String>


    override fun layoutResource(): Int {
        return R.layout.fragment_add_institute
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOfYesNo.add("Y")
        listOfYesNo.add("N")

        adapterOnlineDropdown = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_menu_popup_item,
            listOfYesNo
        )

        binding.tilATOnlineCounselling.setAdapter(adapterOnlineDropdown)
        binding.tilATOnlineCounselling.setOnItemClickListener { parent, _, pos, _ ->
            onlineCounsellingSelection = parent.getItemAtPosition(pos) as String

            if (onlineCounsellingSelection == "Y") {
                binding.tilOnlinePartnerLink.visibility = View.VISIBLE
                binding.tilSpinnerCounsellingOnlinePartnerName.visibility = View.VISIBLE
                binding.tilOnlinePartnerEmail.visibility = View.VISIBLE
                binding.tilOnlinePartnerImage.visibility = View.VISIBLE
                binding.llOnlinePartnerPhone.visibility = View.VISIBLE

            } else {
                onlineCounsellingSelection = "N"
                binding.tilOnlinePartnerLink.visibility = View.GONE
                binding.tilSpinnerCounsellingOnlinePartnerName.visibility = View.GONE
                binding.tilOnlinePartnerEmail.visibility = View.GONE
                binding.tilOnlinePartnerImage.visibility = View.GONE
                binding.llOnlinePartnerPhone.visibility = View.GONE
            }
        }

        adapterOfflineDropdown = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_menu_popup_item,
            listOfYesNo
        )

        binding.tilATOffline.setOnItemClickListener { parent, _, pos, _ ->
            offlineCounsellingSelection = parent.getItemAtPosition(pos) as String

            if (offlineCounsellingSelection == "Y") {
                binding.llOffline.visibility = View.VISIBLE
                binding.rvPOCUsers.visibility = View.VISIBLE
                binding.llCounsellingContactNumber.visibility = View.VISIBLE
                binding.tilAddress.visibility = View.VISIBLE
                binding.tilOpenHours.visibility = View.VISIBLE
            } else {
                offlineCounsellingSelection = "N"
                binding.llOffline.visibility = View.GONE
                binding.rvPOCUsers.visibility = View.GONE
                binding.llCounsellingContactNumber.visibility = View.GONE
                binding.tilAddress.visibility = View.GONE
                binding.tilOpenHours.visibility = View.GONE

            }
        }


        binding.tilATOffline.setAdapter(adapterOfflineDropdown)

        adapterOnouremOnlineDropdown = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_menu_popup_item,
            listOfYesNo
        )

        binding.tilATOnouremOnline.setAdapter(adapterOnouremOnlineDropdown)

        binding.tilATOnouremOnline.setOnItemClickListener { parent, _, pos, _ ->
            onouremOnlineSelection = parent.getItemAtPosition(pos) as String

            if (onouremOnlineSelection == "Y") {
//                binding.tilOnouremOnlinePartnerLink.visibility = View.VISIBLE
//                binding.tilOnouremOnlinePartnerName.visibility = View.VISIBLE
//                binding.tilOnouremOnlinePartnerEmail.visibility = View.VISIBLE
//                binding.tilOnouremOnlinePartnerImage.visibility = View.VISIBLE
//                binding.tilOnouremOnlinePartnerPhone.visibility = View.VISIBLE
            } else {
                onouremOnlineSelection = "N"
//                binding.tilOnouremOnlinePartnerLink.visibility = View.GONE
//                binding.tilOnouremOnlinePartnerName.visibility = View.GONE
//                binding.tilOnouremOnlinePartnerEmail.visibility = View.GONE
//                binding.tilOnouremOnlinePartnerImage.visibility = View.GONE
//                binding.tilOnouremOnlinePartnerPhone.visibility = View.GONE
            }
        }

        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {

                if (!TextUtils.isEmpty(onlineCounsellingSelection)) {
                    if (onlineCounsellingSelection == "Y" && binding.tilSpinnerCounsellingOnlinePartnerName.editText?.text.toString().trim() == "") {
                        Toast.makeText(fragmentContext, "Online PartnerName Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                        return@ViewClickListener
                    } else if (onlineCounsellingSelection == "Y" && binding.tilOnlinePartnerLink.editText?.text.toString().trim() == "") {
                        Toast.makeText(fragmentContext, "Online PartnerLink Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                        return@ViewClickListener
                    } else if (binding.tilOnlinePartnerPhone.editText?.text.toString().trim() != "") {
                        if (binding.tilOnlinePartnerPhoneCountryCode.editText?.text.toString().trim() == "") {
                            Toast.makeText(fragmentContext, "Country Code Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                            return@ViewClickListener
                        }
                    }
                } else {
                    Toast.makeText(fragmentContext, "Online Counselling Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return@ViewClickListener
                }

                if (!TextUtils.isEmpty(onouremOnlineSelection)) {
//                    if (onouremOnlineSelection == "Y" && binding.tilOnouremOnlinePartnerName.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "OnouremOnlinePartner Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                        return@ViewClickListener
//                    } else if (onouremOnlineSelection == "Y" && binding.tilOnouremOnlinePartnerPhone.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "OnouremOnlinePartner Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                        return@ViewClickListener
//                    } else if (onouremOnlineSelection == "Y" && binding.tilOnouremOnlinePartnerEmail.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "OnouremOnlinePartner Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                        return@ViewClickListener
//                    } else if (onouremOnlineSelection == "Y" && binding.tilOnouremOnlinePartnerLink.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "OnouremOnlinePartner Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                        return@ViewClickListener
//                    } else if (onouremOnlineSelection == "Y" && binding.tilOnouremOnlinePartnerImage.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "OnouremOnlinePartner Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                        return@ViewClickListener
//                    }
                } else {
                    Toast.makeText(fragmentContext, "Onourem Online Counselling Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return@ViewClickListener
                }

                if (!TextUtils.isEmpty(offlineCounsellingSelection)) {
                    if (offlineCounsellingSelection == "Y" && pocUpdatedList.isEmpty()) {
                        Toast.makeText(fragmentContext, "Add Offline POC...", Toast.LENGTH_SHORT).show()
                        return@ViewClickListener
                    }

                    if (binding.tilCounsellingContactNumber.editText?.text.toString().trim() != "") {
                        if (binding.tilCounsellingContactNumberCountryCode.editText?.text.toString().trim() == "") {
                            Toast.makeText(fragmentContext, "Country Code Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                            return@ViewClickListener
                        }
                    }
//                    else if (offlineCounsellingSelection == "Y" && binding.tilCounsellingContactNumber.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "Counselling Contact Number Can Not Be Empty...", Toast.LENGTH_SHORT)
//                            .show()
//                        return@ViewClickListener
//                    } else if (offlineCounsellingSelection == "Y" && binding.tilOpenHours.editText?.text.toString().trim() == "") {
//                        Toast.makeText(fragmentContext, "Open Hours Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                        return@ViewClickListener
//                    }
                } else {
                    Toast.makeText(fragmentContext, "Offline Counselling Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return@ViewClickListener
                }

                val institute = Institute()
                institute.name = binding.tilInstitutionName.editText!!.text.toString().trim()
                institute.city = binding.tilCity.editText!!.text.toString().trim()
                institute.state = binding.tilState.editText!!.text.toString().trim()
                institute.country = binding.tilCountry.editText!!.text.toString().trim()

                institute.address = binding.tilAddress.editText!!.text.toString().trim()
                institute.officialHelpLink = binding.tilOfficialHelpLink.editText!!.text.toString().trim()
                institute.logoLink = binding.tilOfficialLogoLink.editText!!.text.toString().trim()
                institute.appointmentLink = binding.tilAppointmentLink.editText!!.text.toString().trim()
                institute.teamPage = binding.tilTeamPageLink.editText!!.text.toString().trim()
                institute.instituteOnline = binding.tilSpinnerCounsellingOnlineStatus.editText!!.text.toString().trim()
                institute.instituteOffline = binding.tilSpinnerOfflineStatus.editText!!.text.toString().trim()
                institute.instituteOnlinePartnerLink = binding.tilOnlinePartnerLink.editText!!.text.toString().trim()
                institute.instituteOnlinePartnerName =
                    binding.tilSpinnerCounsellingOnlinePartnerName.editText!!.text.toString().trim()
                institute.onouremOnline = binding.tilSpinnerOnouremOnlineStatus.editText!!.text.toString().trim()

                institute.instituteOnlinePartnerPhone =  maskPhoneNumber(
                    binding.tilOnlinePartnerPhoneCountryCode.editText?.text.toString().trim(),
                    binding.tilOnlinePartnerPhone.editText?.text.toString().trim()
                )

                institute.instituteOnlinePartnerEmail = binding.tilOnlinePartnerEmail.editText!!.text.toString().trim()
                institute.instituteOnlinePartnerImage = binding.tilOnlinePartnerImage.editText!!.text.toString().trim()
                institute.timings = binding.tilOpenHours.editText!!.text.toString().trim()

                institute.counsellingContactNumber = maskPhoneNumber(
                    binding.tilCounsellingContactNumberCountryCode.editText?.text.toString().trim(),
                    binding.tilCounsellingContactNumber.editText?.text.toString().trim()
                )

//                institute.onouremOnlinePartnerName = binding.tilOnouremOnlinePartnerName.editText!!.text.toString().trim()
//                institute.onouremOnlinePartnerLink = binding.tilOnouremOnlinePartnerLink.editText!!.text.toString().trim()
//                institute.onouremOnlinePartnerPhone = binding.tilOnouremOnlinePartnerPhone.editText!!.text.toString().trim()
//                institute.onouremOnlinePartnerEmail = binding.tilOnouremOnlinePartnerEmail.editText!!.text.toString().trim()
//                institute.onouremOnlinePartnerImage = binding.tilOnouremOnlinePartnerImage.editText!!.text.toString().trim()

                val gson = Gson()
                val jsonArrayString = gson.toJson(pocUpdatedList)
                institute.pocUsers = jsonArrayString

                viewModel.createInstitutionByAdmin(institute)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                navController.popBackStack()
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


        val layoutManager = LinearLayoutManager(fragmentContext, RecyclerView.HORIZONTAL, false)

        binding.rvPOCUsers.layoutManager = layoutManager
        pocUpdatedList = ArrayList()
        pocAdapter = AddPOCAdapter(pocUpdatedList) {

        }

        binding.rvPOCUsers.adapter = pocAdapter

        binding.btnAddPOC.setOnClickListener(ViewClickListener {
            showPOCDialog()
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showPOCDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
        val dialogViewBinding: DialogAddPocBinding =
            DialogAddPocBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogViewBinding.root)
        builder.setCancelable(false)
        val alertDialog: AlertDialog = builder.create()

        dialogViewBinding.btnDialogCancel.setOnClickListener(
            ViewClickListener { view1: View? ->
                alertDialog.dismiss()
            }
        )

        dialogViewBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->

            if (isValidDataPOC(dialogViewBinding)) {
                val pocName = dialogViewBinding.tilPOCName.editText?.text.toString().trim();
                val pocDesignation = dialogViewBinding.tilPOCDesignation.editText?.text.toString().trim();
                val pocEmail = dialogViewBinding.tilPOCEmail.editText?.text.toString().trim()

                if (dialogViewBinding.tilPOCContactNumberPersonal.editText?.text.toString().trim() != "") {
                    if (dialogViewBinding.tilPOCContactNumberPersonalCountryCode.editText?.text.toString().trim() == "") {
                        Toast.makeText(fragmentContext, "Country Code Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                        return@ViewClickListener
                    }
                }

                if (dialogViewBinding.tilPOCContactNumberOffice.editText?.text.toString().trim() != "") {
                    if (dialogViewBinding.tilPOCContactNumberOfficeCountryCode.editText?.text.toString().trim() == "") {
                        Toast.makeText(fragmentContext, "Country Code Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                        return@ViewClickListener
                    }
                }

                val pocContactNumber1 = maskPhoneNumber(
                    dialogViewBinding.tilPOCContactNumberPersonalCountryCode.editText?.text.toString().trim(),
                    dialogViewBinding.tilPOCContactNumberPersonal.editText?.text.toString().trim()
                )
                val pocContactNumber2 = maskPhoneNumber(
                    dialogViewBinding.tilPOCContactNumberOfficeCountryCode.editText?.text.toString().trim(),
                    dialogViewBinding.tilPOCContactNumberOffice.editText?.text.toString().trim()
                )
                val pocTimings = dialogViewBinding.tilPOCTimings.editText?.text.toString().trim()
                val pocEmergencyTime = dialogViewBinding.tilPOCEmergencyTime.editText?.text.toString().trim()
                val pocImage = dialogViewBinding.tilImageUrl.editText?.text.toString().trim()

                pocUpdatedList.add(
                    POCUser(
                        pocName,
                        pocDesignation,
                        pocEmail,
                        pocContactNumber1,
                        pocContactNumber2,
                        pocTimings,
                        pocEmergencyTime,
                        pocImage
                    )
                )
                pocAdapter.notifyDataSetChanged()
                alertDialog.dismiss()
            }

        })

        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun maskPhoneNumber(countryCode: String, phone: String): String {
        return "+$countryCode-${phone.replace("\\s".toRegex(), "")}"
    }

    private val isValidData: Boolean
        get() {
            var result = true
//            textInputLayoutArrayList = ArrayList()
//            textInputLayoutArrayList!!.add(binding.tilInstitutionName)
//            textInputLayoutArrayList!!.add(binding.tilCity)
//            textInputLayoutArrayList!!.add(binding.tilState)
//            textInputLayoutArrayList!!.add(binding.tilCountry)
//            textInputLayoutArrayList!!.add(binding.tilAddress)
//            textInputLayoutArrayList!!.add(binding.tilOfficialHelpLink)
//            textInputLayoutArrayList!!.add(binding.tilOfficialLogoLink)
//            textInputLayoutArrayList!!.add(binding.tilAppointmentLink)
//            textInputLayoutArrayList!!.add(binding.tilTeamPageLink)
//
//            textInputLayoutArrayList!!.forEach {
//                if (it.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
//                    Toast.makeText(fragmentContext, "Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                    result = false
//                    return false
//                }
//            }
            return result
        }

    private fun isValidDataPOC(dialogViewBinding: DialogAddPocBinding): Boolean {
//        val textInputLayoutArrayList = ArrayList<TextInputLayout>()
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCName)
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCDesignation)
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCEmail)
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCContactNumberPersonal)
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCContactNumberOffice)
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCTimings)
//        textInputLayoutArrayList.add(dialogViewBinding.tilPOCEmergencyTime)
//        textInputLayoutArrayList.add(dialogViewBinding.tilImageUrl)
//
//        textInputLayoutArrayList.forEach {
//            if (it.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
//                Toast.makeText(fragmentContext, "Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
//                return false
//            }
//        }
        return true
    }
}