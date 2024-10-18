package com.onourem.android.activity.ui.admin.main.subscriptions

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAddSubscriptionBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*


class AddSubscriptionFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAddSubscriptionBinding>(),
    DatePickerDialog.OnDateSetListener {

    private var oClubRequiredEditTextSelection = ""
    private var packageCategory: PackageCategory? = null
    private var oClubCategory: OClubCategory? = null
    private var packageNameId: PackageNameId? = null
    private var packageId: PackageNameId? = null
    private var instituteNameId: InstituteNameId? = null
    private lateinit var now: Calendar
    private var formattedDateTime: String = ""


    //    private lateinit var adapterPackageCode: ArrayAdapter<String>
    private lateinit var adapterPackageName: ArrayAdapter<PackageNameId>
    private lateinit var adapterInstituteCode: ArrayAdapter<InstituteNameId>
    private lateinit var adapterCategoryCode: ArrayAdapter<PackageCategory>
    private lateinit var adapterOClub: ArrayAdapter<String>
    private lateinit var adapterComment: ArrayAdapter<String>
    private lateinit var adapterInviteLink: ArrayAdapter<String>
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
        return R.layout.fragment_add_subscription
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        now = Calendar.getInstance()

        getPackageAndInstitutionCode()

        binding.txtAddSubscription.setOnClickListener(ViewClickListener {
            if (binding.parentAddSubscription.visibility == View.VISIBLE) {
                binding.parentAddSubscription.visibility = View.GONE
            } else {
                binding.parentAddSubscription.visibility = View.VISIBLE
            }
        })

        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                val subscription = Subscription()
                subscription.packageName = binding.tilPackageName.editText!!.text.toString().trim()
                subscription.cost = binding.tilCost.editText!!.text.toString().trim()
                subscription.currency = binding.tilSpinnerCurrency.editText!!.text.toString().trim()
                subscription.discountedCost = binding.tilDiscountedCost.editText!!.text.toString().trim()

                subscription.freeInviteNumber = binding.tilFreeInviteNumber.editText!!.text.toString().trim()
                subscription.freeInvitePackageID = packageNameId!!.id.toString().trim()
                subscription.descriptionText = binding.tilDescriptionText.editText!!.text.toString().trim()
                subscription.packageCategoryId = packageCategory!!.id.toString().trim()
                subscription.isOclub = if (binding.tilSpinnerOClub.editText!!.text.toString() == "Yes") {
                    "Y"
                } else {
                    "N"
                }

                if (oClubRequiredEditTextSelection.isNotEmpty() && oClubRequiredEditTextSelection == "Yes") {
                    subscription.oClubCategoryId = oClubCategory!!.id.toString().trim()
                    subscription.oclubName = binding.tilOClubNameText.editText!!.text.toString().trim()
                    subscription.oClubCommentsEnable = if (binding.tilSpinneroClubCommentsEnable.editText!!.text.toString() == "Yes") {
                        "Y"
                    } else {
                        "N"
                    }

                    subscription.oClubInviteLinkEnable = if (binding.tilSpinneroClubInviteLinkEnable.editText!!.text.toString() == "Yes") {
                        "Y"
                    } else {
                        "N"
                    }
                } else {
                    subscription.oClubCategoryId = ""
                    subscription.oclubName = ""
                    subscription.oClubCommentsEnable = ""
                    subscription.oClubInviteLinkEnable = ""
                }

                if (isUpdatedImage) {
                    subscription.image = AppUtilities.getBase64String(bitmap, 500)
                }

                if (binding.tilDurationMonths.editText!!.text.toString().trim().isNotEmpty()
                    && binding.tilDurationDays.editText!!.text.toString().trim().isNotEmpty()
                    || binding.tilDurationMonths.editText!!.text.toString().trim().isEmpty()
                    && binding.tilDurationDays.editText!!.text.toString().trim().isEmpty()
                ) {
                    showAlert("Please fill either Months or Days") {
                        binding.tilDurationMonths.editText!!.setText("")
                        binding.tilDurationMonths.editText!!.requestFocus()
                        binding.tilDurationDays.editText!!.setText("")
                    }

                    return@ViewClickListener
                }

                subscription.durationMonths = binding.tilDurationMonths.editText!!.text.toString().trim()
                subscription.durationDays = binding.tilDurationDays.editText!!.text.toString().trim()

                viewModel.createSubscriptionPackageByAdmin(subscription)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()

                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                getPackageAndInstitutionCode()
                                binding.parentAddSubscription.visibility = View.GONE

                                textInputLayoutArrayList!!.forEach {
                                    it.editText!!.setText("")
                                    isUpdatedImage = false
                                    binding.btnSubscriptionImage.text = "Image"
                                    binding.ivSourceImage.setImageDrawable(null)
                                }

                                binding.tilFreeInvitePackageID.editText!!.setText("")
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

        binding.btnSubscriptionImage.setOnClickListener(ViewClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(
                    false
                )
            )
        })

        binding.tilSpinnerCategory.setOnItemClickListener { parent, _, pos, _ ->
            packageCategory = parent.getItemAtPosition(pos) as PackageCategory
        }

        binding.tilSpinnerOClubCategoryEditText.setOnItemClickListener { parent, _, pos, _ ->
            oClubCategory = parent.getItemAtPosition(pos) as OClubCategory
        }

        binding.tilSpinnerFreeInvitePackageID.setOnItemClickListener { parent, _, pos, _ ->
            packageNameId = parent.getItemAtPosition(pos) as PackageNameId
        }

        binding.tilSpinnerOClubEditText.setOnItemClickListener { parent, _, pos, _ ->
            oClubRequiredEditTextSelection = parent.getItemAtPosition(pos) as String

            if (oClubRequiredEditTextSelection == "Yes") {
                binding.tilOClubNameText.visibility = View.VISIBLE
                binding.tilSpinnerOClubCategory.visibility = View.VISIBLE
                binding.tilSpinneroClubCommentsEnable.visibility = View.VISIBLE
                binding.tilSpinneroClubInviteLinkEnable.visibility = View.VISIBLE
            } else {
                oClubRequiredEditTextSelection = "No"
                binding.tilOClubNameText.visibility = View.GONE
                binding.tilSpinnerOClubCategory.visibility = View.GONE
                binding.tilSpinneroClubCommentsEnable.visibility = View.GONE
                binding.tilSpinneroClubInviteLinkEnable.visibility = View.GONE
            }
        }

        observeOnActivityResult()

    }

    private fun observeOnActivityResult() {
        pickerViewModel!!.intentMutableLiveData?.observe(
            viewLifecycleOwner
        ) { intentIntegerPair: Pair<Intent, Int?>? ->
            if (intentIntegerPair == null) return@observe
            when (intentIntegerPair.second) {
                111, 222 -> {
                    val data111 = intentIntegerPair.first
                    val uri = data111.data
                    if (uri != null) {
                        uriImagePath = uri.toString()
                        Glide.with(requireActivity()).asBitmap().load(uriImagePath)
                            .addListener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    model: Any,
                                    target: Target<Bitmap>,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    requireActivity().runOnUiThread {
                                        isUpdatedImage = true
                                        bitmap = resource
                                        binding.btnSubscriptionImage.text = "Image Selected"
                                        Glide.with(fragmentContext)
                                            .load(bitmap)
                                            .apply(options)
                                            .into(binding.ivSourceImage)
                                    }
                                    return true
                                }
                            }).submit()
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel!!.setIntentMutableLiveData(null)
        pickerViewModel = null

    }


    private val isValidData: Boolean
        get() {
            var result = true
            textInputLayoutArrayList = ArrayList()
            textInputLayoutArrayList!!.add(binding.tilPackageName)
            textInputLayoutArrayList!!.add(binding.tilCost)
            textInputLayoutArrayList!!.add(binding.tilDiscountedCost)
//            textInputLayoutArrayList!!.add(binding.tilDurationMonths)
//            textInputLayoutArrayList!!.add(binding.tilDurationDays)
            textInputLayoutArrayList!!.add(binding.tilFreeInviteNumber)
//            textInputLayoutArrayList!!.add(binding.tilFreeInvitePackageID)
            textInputLayoutArrayList!!.add(binding.tilDescriptionText)
            textInputLayoutArrayList!!.add(binding.tilSpinnerCategoryCode)
            textInputLayoutArrayList!!.add(binding.tilSpinnerOClub)
            textInputLayoutArrayList!!.add(binding.tilSpinnerCurrency)

            textInputLayoutArrayList!!.forEach {
                if (it.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
                    Toast.makeText(fragmentContext, "Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    result = false
                    return false
                }
            }

//            if (binding.tilFreeInviteNumber.editText.toString().isNotEmpty() && binding.tilFreeInviteNumber.editText.toString().toInt() > 0) {
//                if (binding.tilFreeInvitePackageID.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
//                    Toast.makeText(fragmentContext, "Please select free package id", Toast.LENGTH_SHORT).show()
//                    result = false
//                    return false
//                }
//            }

            if (binding.tilSpinnerOClub.editText!!.text.isNotEmpty() && binding.tilSpinnerOClub.editText!!.text.toString() == "Yes") {
                if (binding.tilOClubNameText.editText!!.text.toString() == "") {
                    Toast.makeText(fragmentContext, "Oclub Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return false
                } else if (binding.tilSpinnerOClubCategory.editText!!.text.toString() == "") {
                    Toast.makeText(fragmentContext, "OClub Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return false
                } else if (binding.tilSpinneroClubCommentsEnable.editText!!.text.toString() == "") {
                    Toast.makeText(fragmentContext, "Oclub Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return false
                } else if (binding.tilSpinneroClubInviteLinkEnable.editText!!.text.toString() == "") {
                    Toast.makeText(fragmentContext, "OClub Fields Can Not Be Empty...", Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            if (TextUtils.isEmpty(uriImagePath) || bitmap == null) {
                showAlert("Please attach image")
                return false
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


                    adapterCategoryCode = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        apiResponse.body.packageCategoryList
                    )
                    binding.tilSpinnerCategory.setAdapter(adapterCategoryCode)

                    adapterPackageName = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        apiResponse.body.packageIdAndNameList
                    )

                    binding.tilSpinnerFreeInvitePackageID.setAdapter(adapterPackageName)

                    adapterOClub = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        resources.getStringArray(R.array.yesNo)
                    )
                    binding.tilSpinnerOClubEditText.setAdapter(adapterOClub)

                    adapterComment = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        resources.getStringArray(R.array.yesNo)
                    )
                    binding.tilSpinnerOClubCommentsEnableEditText.setAdapter(adapterComment)

                    adapterInviteLink = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        resources.getStringArray(R.array.yesNo)
                    )
                    binding.tilSpinneroClubInviteLinkEnableEditText.setAdapter(adapterInviteLink)

                    val currency = ArrayList<String>()
                    currency.add("INR")
                    currency.add("USD")
                    adapterCurrency = ArrayAdapter(
                        requireActivity(),
                        R.layout.dropdown_menu_popup_item,
                        currency
                    )
                    binding.tilSpinnerCurrencyEditText.setAdapter(adapterCurrency)
                    binding.tilSpinnerCurrencyEditText.setSelection(0)


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
                    binding.tilSpinnerOClubCategoryEditText.setAdapter(adapterOClubCategory)

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

        Log.d("dd/MM/yyyy", formattedDateTime)

    }
}