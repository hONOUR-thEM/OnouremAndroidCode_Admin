package com.onourem.android.activity.ui.admin.details

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogAutoTriggerInOclubBinding
import com.onourem.android.activity.databinding.FragmentActivityDetailsBinding
import com.onourem.android.activity.models.GetPlayGroupCategories
import com.onourem.android.activity.models.PlayGroupCategoryList
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.admin.create.adapters.*
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalActivityData
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminActivityResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.DialogUtils.showToast
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ActivityDetailsFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentActivityDetailsBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private var categoriesResponse: GetPlayGroupCategories? = null
    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var adapter: ArrayAdapter<String>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var futureQuestionListAdapter: ActivityDetailsAdapter? = null
    private var selectedExternalPost: ExternalActivityData? = null
    private var selectedActivity: AdminActivityResponse? = null
    private var externalPostsAdapter: ActivityExternalDetailsAdapter? = null


    private lateinit var now: Calendar


    @SuppressLint("NewApi")
    private var selectedDate = LocalDate.now()

    @SuppressLint("NewApi")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    @SuppressLint("NewApi")
    private val dayFormatter = DateTimeFormatter.ofPattern("EEE")

    @SuppressLint("NewApi")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM")

    @SuppressLint("NewApi")
    private val yearFormatter = DateTimeFormatter.ofPattern("YYYY")

    private var formattedDateTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_activity_details
    }

    @SuppressLint("CheckResult", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (adapter == null) {
            adapter = ArrayAdapter(
                requireActivity(), R.layout.dropdown_menu_popup_item, resources.getStringArray(R.array.activityType)
            )
        }

        binding.tilSpinner.setAdapter(adapter)
        binding.tilSpinner.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, l ->
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            AppUtilities.hideKeyboard(requireActivity())
            binding.ibClear.visibility = View.GONE
            binding.rvQuestionsGames.adapter = null
            adapter = null
        }

        layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding.rvQuestionsGames.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.rvQuestionsGames.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.rvQuestionsGames.layoutManager = layoutManager

        binding.btnDetails.setOnClickListener(ViewClickListener {
            val activityId = binding.edtSearchQuery.text.toString()
            val activityType = binding.tilSpinnerInput.editText?.text.toString()
            if (!TextUtils.isEmpty(activityId) && !TextUtils.isEmpty(activityType)) {
                searchAdminActivityById(activityId, activityType)
            } else {
                showAlert("Please Check Activity Id and Activity Type First")
            }
        })

        binding.btnSubmit.setOnClickListener(ViewClickListener {

        })

        binding.ibClear.setOnClickListener(ViewClickListener { v: View? ->
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            AppUtilities.hideKeyboard(requireActivity())
            binding.ibClear.visibility = View.GONE
            binding.rvQuestionsGames.adapter = null
            adapter = null
            binding.tilActivityType.editText?.setText("")

        })

        binding.edtSearchQuery.setOnTouchListener { view1: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // 100 is a fix value for the moment but you can change it
                // according to your view
                binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.ibClear.visibility = View.VISIBLE
            }
            false
        }

    }


    private fun searchAdminActivityById(activityId: String, activityType: String) {

        viewModel.searchAdminActivityById(activityId, activityType)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAdminActivityListResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (activityType == "External") {
                            setAdapter(apiResponse.body.externalActivityDataList)
                        } else {
                            setAdapter(apiResponse.body)
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

    private fun setAdapter(externalActivityDataList: MutableList<ExternalActivityData>) {

//        val list: ArrayList<ExternalActivityData> = when (selectedRadioButton) {
//            "Unscheduled" -> {
//                externalActivityDataList!!.filter { !it.activityTriggered && it.status == "Y" } as ArrayList<ExternalActivityData>
//            }
//            "Triggered" -> {
//                externalActivityDataList!!.filter { it.activityTriggered && it.status == "Y" } as ArrayList<ExternalActivityData>
//            }
//            "Active" -> {
//                externalActivityDataList!!.filter { it.status == "Y" } as ArrayList<ExternalActivityData>
//            }
//            else -> {
//                externalActivityDataList!!.filter { it.status == "N" } as ArrayList<ExternalActivityData>
//            }
//        }

        externalPostsAdapter = ActivityExternalDetailsAdapter(
            externalActivityDataList
        ) {
            when (it.first) {
                ActivityExternalDetailsAdapter.CLICK_WHOLE -> {
                    if (it.second!!.youtubeLink == "Y") {
                        val intent = Intent(context, YoutubeActivity::class.java)
                        intent.putExtra(
                            "youtubeId", it.second?.videoId
                        )
                        fragmentContext.startActivity(intent)
                    } else {
                        if (!TextUtils.isEmpty(it.second!!.videoUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                    it.second!!.videoUrl!!
                                )
                            )
                        } else if (!TextUtils.isEmpty(it.second.imageUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavMediaView(
                                    1, it.second.imageUrl!!
                                )
                            )
                        }

                    }

                }

                ActivityExternalDetailsAdapter.CLICK_MEDIA -> {
                    if (it.second!!.youtubeLink == "Y") {
                        val intent = Intent(context, YoutubeActivity::class.java)
                        intent.putExtra(
                            "youtubeId", it.second?.videoId
                        )
                        fragmentContext.startActivity(intent)
                    } else {
                        if (!TextUtils.isEmpty(it.second!!.videoUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                    it.second!!.videoUrl!!
                                )
                            )
                        } else if (!TextUtils.isEmpty(it.second.imageUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavMediaView(
                                    1, it.second.imageUrl!!
                                )
                            )
                        }

                    }
                }

                ActivityExternalDetailsAdapter.CLICK_EDIT -> {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavUpdateExternalContent(
                            it.second
                        )
                    )
                }

                ActivityExternalDetailsAdapter.CLICK_NOTIFY -> {
                    Toast.makeText(fragmentContext, "Notify Api", Toast.LENGTH_LONG).show()
                }

                ActivityExternalDetailsAdapter.CLICK_AUTO_TRIGGER_OCLUB -> {
                    navController.navigate(//nav_trigger_activities_in_oclub
                        MobileNavigationDirections.actionGlobalNavTriggerActivitiesInOclub(
                            it.second.id!!,
                            it.second.summary!!,
                            it.second.activityType!!,
                        )
                    )
                }

                ActivityExternalDetailsAdapter.CLICK_IN_ACTIVE -> {
                    navController.navigate(//nav_inactive_activity
                        MobileNavigationDirections.actionGlobalNavInactiveActivity(
                            it.second.id!!,
                            it.second.summary!!,
                            it.second.activityType!!,
                        )
                    )
                }

                ActivityExternalDetailsAdapter.CLICK_DELETE -> {

//                    val builder = AlertDialog.Builder(fragmentContext)
//                    builder.setMessage("Do you want to delete this Post?")
//                    builder.setTitle("Delete Alert")
//                    builder.setCancelable(true)
//
//                    builder.setPositiveButton(
//                        "Yes"
//                    ) { dialog: DialogInterface?, id: Int ->
//                        deleteExternalActivityByAdmin(it)
//                    }
//
//                    builder.setNegativeButton(
//                        "No"
//                    ) { dialog: DialogInterface?, id: Int ->
//                        dialog!!.dismiss()
//                    }
//
//                    val alert = builder.create()
//                    alert.show()
                }

                ActivityExternalDetailsAdapter.CLICK_UN_SCHEDULE -> {
                    now = Calendar.getInstance()

                    when (it.second.activityType) {
                        "External" -> {
                            selectedExternalPost = it.second

                        }
                    }

                    val dpd = DatePickerDialog.newInstance(
                        this@ActivityDetailsFragment, now[Calendar.YEAR],  // Initial year selection
                        now[Calendar.MONTH],  // Initial month selection
                        now[Calendar.DAY_OF_MONTH] // Inital day selection
                    )
                    dpd.version = DatePickerDialog.Version.VERSION_2
                    dpd.show(childFragmentManager, "Datepickerdialog")
                    dpd.isCancelable = true
                    dpd.minDate = now
                    dpd.vibrate(true)
                }

            }
        }

        binding.rvQuestionsGames.adapter = externalPostsAdapter

        if (externalPostsAdapter != null && externalPostsAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

    }

    private fun setAdapter(getAdminActivityListResponse: GetAdminActivityListResponse) {


        futureQuestionListAdapter = ActivityDetailsAdapter(
            getAdminActivityListResponse.adminActivityResponseList, ""
        ) {
            when {
                it.first == ActivityDetailsAdapter.CLICK_WHOLE -> {
                    if (!TextUtils.isEmpty(it.second.activityVideoUrl)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavMediaView(
                                2, it.second.activityVideoUrl!!
                            )
                        )
                    } else if (!TextUtils.isEmpty(it.second.activityImageUrl)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavMediaView(
                                1, it.second.activityImageUrl!!
                            )
                        )
                    }

                }

                it.first == ActivityDetailsAdapter.CLICK_UN_SCHEDULE -> {
                    now = Calendar.getInstance()
                    selectedActivity = it.second

                    val dpd = DatePickerDialog.newInstance(
                        this@ActivityDetailsFragment, now[Calendar.YEAR],  // Initial year selection
                        now[Calendar.MONTH],  // Initial month selection
                        now[Calendar.DAY_OF_MONTH] // Inital day selection
                    )
                    dpd.version = DatePickerDialog.Version.VERSION_2
                    dpd.show(childFragmentManager, "Datepickerdialog")
                    dpd.isCancelable = true
                    dpd.minDate = now
                    dpd.vibrate(true)
                }

                it.first == ActivityDetailsAdapter.CLICK_NOTIFY -> {
                    if (it.second.isEditEnabled) {
                        binding.parent.visibility = View.VISIBLE
                    } else {
                        binding.parent.visibility = View.GONE
                    }
                }

                it.first == ActivityDetailsAdapter.CLICK_AUTO_TRIGGER_OCLUB -> {
                    navController.navigate(//nav_trigger_activities_in_oclub
                        MobileNavigationDirections.actionGlobalNavTriggerActivitiesInOclub(
                            it.second.activityId!!,
                            it.second.activityText!!,
                            it.second.activityType!!,
                        )
                    )
                }

                it.first == ActivityDetailsAdapter.CLICK_IN_ACTIVE -> {
                    navController.navigate(//nav_inactive_activity
                        MobileNavigationDirections.actionGlobalNavInactiveActivity(
                            it.second.activityId!!,
                            it.second.activityText!!,
                            it.second.activityType!!,
                        )
                    )
                }

                it.first == ActivityDetailsAdapter.CLICK_DELETE && !it.second.activityTriggered -> {

                    val builder = AlertDialog.Builder(fragmentContext)
                    builder.setMessage("Do you want to delete this scheduled question?")
                    builder.setTitle("Delete Alert")
                    builder.setCancelable(true)

                    builder.setPositiveButton(
                        "Yes"
                    ) { dialog: DialogInterface?, id: Int ->
                        deleteActivity(it.second.activityId, it.third)
                    }

                    builder.setNegativeButton(
                        "No"
                    ) { dialog: DialogInterface?, id: Int ->
                        dialog!!.dismiss()
                    }

                    val alert = builder.create()
                    alert.show()
                }
            }
        }

        binding.rvQuestionsGames.adapter = futureQuestionListAdapter

        if (futureQuestionListAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

    }

    private fun getActivityInfoByAdmin(id: String) {

//        viewModel.getOrderInfoByAdmin(id)
//            .observe(this) { apiResponse: ApiResponse<CheckOrderInfoResponse> ->
//                if (apiResponse.isSuccess && apiResponse.body != null) {
//                    if (apiResponse.loading) {
//                        showProgress();
//                    } else if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        setData(apiResponse.body.orderInfo)
//                    } else {
//                        showAlert(apiResponse.body.errorMessage)
//                        hideProgress()
//                    }
//                }
//            }

    }

    private fun setData(info: AdminActivityResponse?) {

        if (info != null) {
            setEditTextData(binding.tilActivityId, info.activityId.toString())
            setEditTextData(binding.tilActivityType, info.activityType)
            setEditTextData(binding.tilActivityText, info.activityText)
            setEditTextData(binding.tilActivityImageUrl, info.activityImageUrl)
            setEditTextData(binding.tilActivityVideoUrl, info.activityVideoUrl)
            setEditTextData(binding.tilActivityTriggered, info.activityTriggered.toString())
            setEditTextData(binding.tilCreatedTime, info.createdTime)
            setEditTextData(binding.tilActivityCategory, info.activityCategory)
            setEditTextData(binding.tilYoutubeLink, info.youtubeLink)
        } else {
            setEditTextData(binding.tilActivityId, "")
            setEditTextData(binding.tilActivityType, "")
            setEditTextData(binding.tilActivityText, "")
            setEditTextData(binding.tilActivityImageUrl, "")
            setEditTextData(binding.tilActivityVideoUrl, "")
            setEditTextData(binding.tilActivityTriggered, "")
            setEditTextData(binding.tilCreatedTime, "")
            setEditTextData(binding.tilActivityCategory, "")
            setEditTextData(binding.tilYoutubeLink, "")
        }

        hideProgress()
    }


    private fun updateActivityDetails(info: AdminActivityResponse) {
        info.activityId = getEditTextData(binding.tilActivityId)
        info.activityType = getEditTextData(binding.tilActivityType)
        info.activityText = getEditTextData(binding.tilActivityText)
        info.activityImageUrl = getEditTextData(binding.tilActivityImageUrl)
        info.activityVideoUrl = getEditTextData(binding.tilActivityVideoUrl)
        info.activityTriggered = getEditTextData(binding.tilActivityTriggered).toBoolean()
        info.createdTime = getEditTextData(binding.tilCreatedTime)
        info.activityCategory = getEditTextData(binding.tilActivityCategory)
        info.youtubeLink = getEditTextData(binding.tilYoutubeLink)


//        viewModel.updateOrderInfoByAdmin(info)
//            .observe(this) { apiResponse: ApiResponse<StandardResponse> ->
//                if (apiResponse.isSuccess && apiResponse.body != null) {
//                    if (apiResponse.loading) {
//                        showProgress();
//                    } else if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        Toast.makeText(fragmentContext, "Payment Details Updated successfully", Toast.LENGTH_LONG).show()
//                        setData(null)
//                    } else {
//                        showAlert(apiResponse.body.errorMessage)
//                        hideProgress()
//                    }
//                }
//            }
    }

    private fun getEditTextData(til: TextInputLayout): String {
        return if (til.editText!!.text != null) {
            til.editText!!.text.toString()
        } else {
            ""
        }
    }

    private fun setEditTextData(til: TextInputLayout, value: String?) {
        if (value != null && value != "") {
            til.editText!!.setText(value)
            til.editText!!.isEnabled = false
            //til.editText!!.setTextColor(ColorStateList.valueOf())
        } else {
            til.editText!!.isEnabled = true
        }
    }

    private fun deleteActivity(activityId: String?, position: Int) {
        viewModel.deleteActivityCreatedByAdmin(activityId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    futureQuestionListAdapter!!.removeItem(position)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        val calendarTime = Calendar.getInstance()
        calendarTime[Calendar.HOUR_OF_DAY] = hourOfDay
        calendarTime[Calendar.MINUTE] = minute

        val timeFormat = "hh:mm:ss aa" // your own format
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        formattedDateTime = "$formattedDateTime ${sdf.format(calendarTime.time)}"

        if (selectedActivity != null) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavScheduleQuestion(
                    formattedDateTime.uppercase(Locale.getDefault()),
                    selectedActivity?.activityType ?: "",
                    selectedActivity?.activityId ?: ""
                )
            )
        } else if (selectedExternalPost != null) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavScheduleQuestion(
                    formattedDateTime.uppercase(Locale.getDefault()),
                    selectedExternalPost?.activityType ?: "",
                    selectedExternalPost?.id ?: ""
                )
            )
        }

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val tpd = TimePickerDialog.newInstance(
            this@ActivityDetailsFragment, calendarDate[Calendar.HOUR_OF_DAY], calendarDate[Calendar.MINUTE], false
        )
        tpd.version = TimePickerDialog.Version.VERSION_2
        tpd.show(childFragmentManager, "Timepickerdialog")
        tpd.isCancelable = true
        tpd.enableSeconds(false)
        tpd.vibrate(true)
        tpd.version = TimePickerDialog.Version.VERSION_2


        val dateFormat = "dd/MM/yyyy" // your own format
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        formattedDateTime = sdf.format(calendarDate.time)
        Log.d("dd/MM/yyyy", formattedDateTime)

        val datePickerNow = sdf.format(now.time)
        if (formattedDateTime == datePickerNow) {
            tpd.setMinTime(
                calendarDate.get(Calendar.HOUR_OF_DAY), calendarDate.get(Calendar.MINUTE) + 5, calendarDate.get(Calendar.SECOND)
            )
        }
    }

}