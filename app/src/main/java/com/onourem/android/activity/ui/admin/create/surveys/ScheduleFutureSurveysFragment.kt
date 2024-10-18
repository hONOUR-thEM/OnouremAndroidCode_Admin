package com.onourem.android.activity.ui.admin.create.surveys

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentScheduleSurveyBinding
import com.onourem.android.activity.ui.admin.create.adapters.SurveysAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*


class ScheduleFutureSurveysFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentScheduleSurveyBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var selectedAdminSurveyResponse: AdminSurveyResponse? = null
    private var surveyActivityIdList: ArrayList<String>? = null
    private var surveyActivityDataList: ArrayList<AdminSurveyResponse>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var surveysAdapter: SurveysAdapter? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var selectedRadioButton: String = "Unscheduled"
    private lateinit var now: Calendar
    private var formattedDateTime: String = ""

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_schedule_survey
    }

    companion object {
        fun create(): ScheduleFutureSurveysFragment {
            val fragment = ScheduleFutureSurveysFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        binding.rvExternalPosts.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        getSurveyActivityListForAdmin(selectedRadioButton)

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (surveysAdapter != null) {
                surveysAdapter!!.clearData()
            }
            getSurveyActivityListForAdmin(selectedRadioButton)
        }

        binding.rdg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdbUnpublished -> {
                    selectedRadioButton = "Unpublished"
                    refreshUI(selectedRadioButton)
                }
                R.id.rdbPublished -> {
                    selectedRadioButton = "Published"
                    refreshUI(selectedRadioButton)
                }
                R.id.rdbUnscheduled -> {
                    selectedRadioButton = "Unscheduled"
                    refreshUI(selectedRadioButton)
                }
                R.id.rdbScheduled -> {
                    selectedRadioButton = "Scheduled"
                    refreshUI(selectedRadioButton)
                }

                R.id.rdbTriggered -> {
                    selectedRadioButton = "Triggered"
                    refreshUI(selectedRadioButton)
                }
            }
        }
    }

    private fun refreshUI(selectedRadioButton: String) {
        getSurveyActivityListForAdmin(selectedRadioButton)
    }

    private fun getSurveyActivityListForAdmin(selectedRadioButton: String) {

        viewModel.getSurveyActivityListForAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<SurveyActivityResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    surveyActivityDataList = ArrayList()
                    surveyActivityDataList!!.addAll(apiResponse.body.adminSurveyResponseList)

                    if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
                    surveyActivityIdList = ArrayList()
                    surveyActivityIdList!!.addAll(apiResponse.body.surveyActivityDataList)

                    setAdapter(selectedRadioButton)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun deleteExternalActivityByAdmin(item: Triple<Int, AdminSurveyResponse, Int>) {

//        viewModel.deleteExternalActivityByAdmin(item.second.id)
//            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse?> ->
//                if (apiResponse.loading) {
//                    showProgress()
//                } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                    hideProgress()
//                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        if (surveysAdapter != null) {
//                            surveysAdapter!!.removeItem(item.second)
//                            Toast.makeText(fragmentContext, "Post has been deleted", Toast.LENGTH_LONG).show()
//                        }
//                    } else {
//                        showAlert(apiResponse.body.errorMessage)
//                    }
//                } else {
//                    hideProgress()
//                    showAlert(apiResponse.errorMessage)
//                }
//            }
    }

    private fun setAdapter(selectedRadioButton: String) {
        isLastPage = false

        val list: ArrayList<AdminSurveyResponse> = when (selectedRadioButton) {
            "Unscheduled" -> {
                surveyActivityDataList!!.filter { !it.activityScheduled } as ArrayList<AdminSurveyResponse>
            }
            "Scheduled" -> {
                surveyActivityDataList!!.filter { it.activityScheduled && !it.activityTriggered } as ArrayList<AdminSurveyResponse>
            }
            "Triggered" -> {
                surveyActivityDataList!!.filter { it.activityTriggered } as ArrayList<AdminSurveyResponse>
            }
            "Published" -> {
                surveyActivityDataList!!
            }
            else -> {
                surveyActivityDataList!!
            }
        }

        surveysAdapter = SurveysAdapter(
            list, selectedRadioButton
        ) {
            when (it.first) {

                SurveysAdapter.CLICK_WHOLE, SurveysAdapter.CLICK_MEDIA -> {
                    if (!TextUtils.isEmpty(it.second!!.activityVideoUrl)) {
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

                SurveysAdapter.CLICK_NOTIFY -> {
                    Toast.makeText(fragmentContext, "Notify Api", Toast.LENGTH_LONG).show()
                }

                SurveysAdapter.CLICK_DELETE -> {

                    val builder = AlertDialog.Builder(fragmentContext)
                    builder.setMessage("Do you want to delete this Post?")
                    builder.setTitle("Delete Alert")
                    builder.setCancelable(true)

                    builder.setPositiveButton(
                        "Yes"
                    ) { dialog: DialogInterface?, id: Int ->
                        deleteExternalActivityByAdmin(it)
                    }

                    builder.setNegativeButton(
                        "No"
                    ) { dialog: DialogInterface?, id: Int ->
                        dialog!!.dismiss()
                    }

                    val alert = builder.create()
                    alert.show()
                }
                SurveysAdapter.CLICK_UN_SCHEDULE -> {

                    now = Calendar.getInstance()
                    selectedAdminSurveyResponse = it.second
                    val dpd = DatePickerDialog.newInstance(
                        this@ScheduleFutureSurveysFragment, now[Calendar.YEAR],  // Initial year selection
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

        binding.rvExternalPosts.adapter = surveysAdapter

        if (surveysAdapter != null && surveysAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = "You do not have any Survey in this section.\\n\\nPull down to refresh."
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvExternalPosts.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@ScheduleFutureSurveysFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@ScheduleFutureSurveysFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ScheduleFutureSurveysFragment.isLoading
            }
        })

    }

    // print twenty element from list from the start index specified
    private fun getDisplayElements(
        myList: List<String>, startIndex: Int
    ): List<String> {
        return myList.subList(
            startIndex, myList.size.toLong().coerceAtMost(startIndex + 15L).toInt()
        )
    }

    private fun loadMoreGames() {
        var start = 0
        var end = 0

        if (surveyActivityIdList == null || surveyActivityIdList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            return
        }
        start = surveysAdapter!!.itemCount
        end = surveyActivityIdList!!.size
//                AppUtilities.showLog("**activityStatusList:", activityStatusList!!.size.toString())
//                AppUtilities.showLog("**start:", soloGamesAdapter!!.itemCount.toString())
        if (start >= end) {
            isLastPage = true
            isLoading = false
            return
        }
        val ids: MutableList<String> = ArrayList()
        val statusLists = getDisplayElements(
            surveyActivityIdList!!, start
        )
        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
        var i = 0
        while (i < statusLists.size) {
            val item = statusLists[i]
            ids.add(item)
            i++
        }
        val idsChunk = Utilities.getTokenSeparatedString(ids, ",")

        viewModel.getNextSurveyActivityListForAdmin(idsChunk).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<SurveyActivityResponse> ->
            if (apiResponse.loading) {
                if (surveysAdapter != null) {
                    isLoading = true
                    surveysAdapter!!.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (surveysAdapter != null) {
                    surveysAdapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getNextUserActivityGroup" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                    if (apiResponse.body.adminSurveyResponseList.isEmpty()) {
                        isLastPage = true;
                    } else {
                        if (surveysAdapter != null) {

                            val list: ArrayList<AdminSurveyResponse> = when (selectedRadioButton) {
                                "Unscheduled" -> {
                                    apiResponse.body.adminSurveyResponseList.filter { !it.activityTriggered } as ArrayList<AdminSurveyResponse>
                                }
                                "Triggered" -> {
                                    apiResponse.body.adminSurveyResponseList.filter { it.activityTriggered } as ArrayList<AdminSurveyResponse>
                                }
                                "Published" -> {
                                    apiResponse.body.adminSurveyResponseList.filter { it.activityTriggered } as ArrayList<AdminSurveyResponse>
                                }
                                else -> {
                                    apiResponse.body.adminSurveyResponseList.filter { it.activityTriggered } as ArrayList<AdminSurveyResponse>
                                }
                            }

                            surveysAdapter!!.addItems(list as List<AdminSurveyResponse>)
                            //setDashboardDataToActivity()
                        }

                        isLastPage = apiResponse.body.adminSurveyResponseList.size < PaginationListener.PAGE_ITEM_SIZE
                    }
                } else {
                    isLastPage = true;
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                isLoading = false
                if (surveysAdapter != null) {
                    surveysAdapter!!.removeLoading()
                }
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

        if (selectedAdminSurveyResponse != null) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavScheduleQuestion(
                    formattedDateTime.uppercase(Locale.getDefault()),
                    selectedAdminSurveyResponse?.activityType ?: "",
                    selectedAdminSurveyResponse?.activityId ?: ""
                )
            )
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val tpd = TimePickerDialog.newInstance(
            this@ScheduleFutureSurveysFragment,
            calendarDate[Calendar.HOUR_OF_DAY],
            calendarDate[Calendar.MINUTE],
            false
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
                calendarDate.get(Calendar.HOUR_OF_DAY),
                calendarDate.get(Calendar.MINUTE) + 5,
                calendarDate.get(Calendar.SECOND)
            )
        }
    }
}