package com.onourem.android.activity.ui.admin.main.subscriptions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAddSubscriptionDiscountBinding
import com.onourem.android.activity.models.CodeNameId
import com.onourem.android.activity.models.GetPackageAndInstitutionCodeResponse
import com.onourem.android.activity.models.InstituteNameId
import com.onourem.android.activity.models.OClubCategory
import com.onourem.android.activity.models.PackageCategory
import com.onourem.android.activity.models.PackageNameId
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.SubscriptionDiscount
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddSubscriptionDiscountFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAddSubscriptionDiscountBinding>(),
    DatePickerDialog.OnDateSetListener {

    private var packageId: PackageNameId? = null
    private var instituteNameId: InstituteNameId? = null
    private var codeNameId: CodeNameId? = null
    private lateinit var now: Calendar
    private var formattedDateTime: String = ""


    //    private lateinit var adapterPackageCode: ArrayAdapter<String>
    private lateinit var adapterPackageName: ArrayAdapter<PackageNameId>
    private lateinit var adapterInstituteCode: ArrayAdapter<InstituteNameId>
    private lateinit var adapterCodeType: ArrayAdapter<CodeNameId>
    private lateinit var adapterCategoryCode: ArrayAdapter<PackageCategory>
    private lateinit var adapterOClub: ArrayAdapter<String>
    private lateinit var adapterCurrency: ArrayAdapter<String>
    private lateinit var adapterOClubCategory: ArrayAdapter<OClubCategory>
    var textInputLayoutArrayList: ArrayList<TextInputLayout>? = null
    var textInputLayoutArrayListDiscount: ArrayList<TextInputLayout>? = null

    private var bitmap: Bitmap? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var uriImagePath = ""
    private var isUpdatedImage = false
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)

    override fun layoutResource(): Int {
        return R.layout.fragment_add_subscription_discount
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        now = Calendar.getInstance()

        getPackageAndInstitutionCode()

        binding.txtAddSubscriptionDiscount.setOnClickListener(ViewClickListener {
            if (binding.parentAddSubscriptionDiscount.visibility == View.VISIBLE) {
                binding.parentAddSubscriptionDiscount.visibility = View.GONE
            } else {
                binding.parentAddSubscriptionDiscount.visibility = View.VISIBLE
            }
        })

        binding.tilCodeValidTill.editText!!.setOnClickListener(ViewClickListener {
            val dpd = DatePickerDialog.newInstance(
                this@AddSubscriptionDiscountFragment,
                now[Calendar.YEAR],  // Initial year selection
                now[Calendar.MONTH],  // Initial month selection
                now[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.version = DatePickerDialog.Version.VERSION_2
            dpd.show(childFragmentManager, "Datepickerdialog")
            dpd.isCancelable = true
            dpd.minDate = now
            dpd.vibrate(true)
        })

        binding.btnSubmitDiscount.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidDiscountData) {
                val subscription = SubscriptionDiscount()
                subscription.codeType = codeNameId?.id.toString().trim()
                subscription.packageCode = packageId?.id.toString().trim()
                subscription.institutionCode = instituteNameId?.id.toString().trim()

                if (codeNameId?.name == "Single") {
                    subscription.numberOfCode = binding.tilNoOfCodes.editText?.text.toString().trim()
                    subscription.maxUsers = "1"
                } else {
                    subscription.numberOfCode = "1"
                    subscription.maxUsers = binding.tilMaxUsers.editText?.text.toString().trim()
                }

                subscription.codeValidTill = binding.tilCodeValidTill.editText?.text.toString().trim()

                viewModel.createSubscriptionDiscountByAdmin(subscription)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()

                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                binding.parentAddSubscriptionDiscount.visibility = View.GONE

                                textInputLayoutArrayListDiscount!!.forEach {
                                    it.editText!!.setText("")
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

        binding.tilSpinnerPC.setOnItemClickListener { parent, _, pos, _ ->
            packageId = parent.getItemAtPosition(pos) as PackageNameId
        }

        binding.tilSpinnerIC.setOnItemClickListener { parent, _, pos, _ ->
            instituteNameId = parent.getItemAtPosition(pos) as InstituteNameId
        }

        binding.tilSpinnerCT.setOnItemClickListener { parent, _, pos, _ ->
            codeNameId = parent.getItemAtPosition(pos) as CodeNameId

            binding.tilNoOfCodes.isEnabled = codeNameId?.name == "Single"
            binding.tilMaxUsers.visibility = if (codeNameId?.name == "Multiple") {
                View.VISIBLE
            } else {
                View.GONE
            }

            if (codeNameId?.name == "Multiple") {
                binding.tilNoOfCodes.editText?.setText("1")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel!!.setIntentMutableLiveData(null)
        pickerViewModel = null

    }

    private val isValidDiscountData: Boolean
        get() {
            var result = true
            textInputLayoutArrayListDiscount = ArrayList()
            textInputLayoutArrayListDiscount?.add(binding.tilSpinnerCodeType)
            if (binding.tilSpinnerCodeType.editText?.text.toString() == "Multiple") {
                textInputLayoutArrayListDiscount?.add(binding.tilMaxUsers)
            }
            textInputLayoutArrayListDiscount?.add(binding.tilSpinnerPackageCode)
            textInputLayoutArrayListDiscount?.add(binding.tilSpinnerInstitutionCode)
            textInputLayoutArrayListDiscount?.add(binding.tilNoOfCodes)
            textInputLayoutArrayListDiscount?.add(binding.tilCodeValidTill)

            textInputLayoutArrayListDiscount?.forEach {
                if (it.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
                    Toast.makeText(fragmentContext, "Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    result = false
                    return false
                }
            }

            val value = binding.tilNoOfCodes.editText?.text.toString()
            val codeType = binding.tilSpinnerCodeType.editText?.text.toString()
            if (codeType == "Single" && value.toInt() > 3000) {
                showAlert("Max code generation would be 3000")
                return false
            }

            if (codeType == "Multiple") {
                val maxUsers = binding.tilMaxUsers.editText?.text.toString()
                if (maxUsers.toInt() < 1) {
                    showAlert("Max User Count should be greater than 1")
                    return false
                }
            }


            return result
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
//                    adapterPackageCode = ArrayAdapter(
//                        requireActivity(),
//                        R.layout.dropdown_menu_popup_item,
//                        apiResponse.body.packageCodeList
//                    )
//                    binding.tilSpinnerPC.setAdapter(adapterPackageCode)

                    adapterInstituteCode = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        apiResponse.body.institutionCodeList
                    )
                    binding.tilSpinnerIC.setAdapter(adapterInstituteCode)

                    val listOfCodeType = ArrayList<CodeNameId>()

                    listOfCodeType.add(CodeNameId("0", "Single"))
                    listOfCodeType.add(CodeNameId("1", "Multiple"))

                    adapterCodeType = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        listOfCodeType
                    )
                    binding.tilSpinnerCT.setAdapter(adapterCodeType)

                    adapterCategoryCode = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        apiResponse.body.packageCategoryList
                    )

                    adapterPackageName = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        apiResponse.body.packageIdAndNameList
                    )

                    binding.tilSpinnerPC.setAdapter(adapterPackageName)

                    val list = ArrayList<String>()
                    list.add("Yes")
                    list.add("No")
                    adapterOClub = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        list
                    )

                    val currency = ArrayList<String>()
                    currency.add("INR")
                    currency.add("USD")
                    adapterCurrency = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        currency
                    )


                    val catoClubList = ArrayList<OClubCategory>()
                    val oc1 = OClubCategory()
                    oc1.id = "1"
                    oc1.category = "Family"
                    oc1.status = "Y"
                    catoClubList.add(oc1)

                    val oc2 = OClubCategory()
                    oc2.id = "2"
                    oc2.category = "Close Friends"
                    oc2.status = "Y"
                    catoClubList.add(oc2)

                    val oc3 = OClubCategory()
                    oc3.id = "3"
                    oc3.category = "Friends"
                    oc3.status = "Y"
                    catoClubList.add(oc3)

                    val oc4 = OClubCategory()
                    oc4.id = "4"
                    oc4.category = "Colleagues"
                    oc4.status = "Y"
                    catoClubList.add(oc4)

                    val oc5 = OClubCategory()
                    oc5.id = "5"
                    oc5.category = "Classmates"
                    oc5.status = "Y"
                    catoClubList.add(oc5)


                    val oc6 = OClubCategory()
                    oc6.id = "4"
                    oc6.category = "Hobby Group"
                    oc6.status = "Y"
                    catoClubList.add(oc6)

                    val oc7 = OClubCategory()
                    oc7.id = "5"
                    oc7.category = "Partner-Spouse"
                    oc7.status = "Y"
                    catoClubList.add(oc7)

                    adapterOClubCategory = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        catoClubList
                    )

                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val dateFormat = "dd/MM/yyyy" // your own format
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        formattedDateTime = sdf.format(calendarDate.time)

        binding.tilCodeValidTill.editText!!.setText(formattedDateTime)
        Log.d("dd/MM/yyyy", formattedDateTime)

    }
}