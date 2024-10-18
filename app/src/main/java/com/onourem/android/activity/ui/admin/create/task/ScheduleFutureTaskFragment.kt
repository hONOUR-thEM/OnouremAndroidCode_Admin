package com.onourem.android.activity.ui.admin.create.task

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentTaskListBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.admin.create.adapters.FutureTaskListAdapter
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminActivityResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ScheduleFutureTaskFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentTaskListBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var now: Calendar
    private var selectedTaskOrMessage: AdminActivityResponse? = null
    private var selectedRadioButton: String = "Unscheduled"
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var adminTaskMessageIdList: java.util.ArrayList<String>? = null

    private var formattedDateTime: String = ""

    private var futureQuestionListAdapter: FutureTaskListAdapter? = null


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.fragment_task_list
    }

    companion object {
        fun create(): ScheduleFutureTaskFragment {
            val fragment = ScheduleFutureTaskFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding.recyclerView.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.recyclerView.layoutManager = linearLayoutManager

        getTaskMessageCreatedByAdmin(selectedRadioButton)

        binding.swipeRefreshLayout.setOnRefreshListener {
            getTaskMessageCreatedByAdmin(selectedRadioButton)
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
        getTaskMessageCreatedByAdmin(selectedRadioButton)
    }

    private fun getTaskMessageCreatedByAdmin(selectedRadioButton: String) {

        viewModel.getTaskMessageCreatedByAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAdminActivityListResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    setAdapter(apiResponse.body, selectedRadioButton)
                    if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
                } else {
                    showAlert(apiResponse.body.errorMessage)
                    if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setAdapter(getAdminActivityListResponse: GetAdminActivityListResponse, selectedRadioButton: String) {
        isLastPage = false
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        adminTaskMessageIdList = ArrayList()
        adminTaskMessageIdList!!.addAll(getAdminActivityListResponse.adminTaskMessageIdList)
        val list: ArrayList<AdminActivityResponse> = if (selectedRadioButton == "Unscheduled") {
            getAdminActivityListResponse.adminTaskMessageResponseList.filter { !it.activityScheduled } as ArrayList<AdminActivityResponse>
        } else if (selectedRadioButton == "Scheduled") {
            getAdminActivityListResponse.adminTaskMessageResponseList.filter { it.activityScheduled && !it.activityTriggered } as ArrayList<AdminActivityResponse>
        } else {
            getAdminActivityListResponse.adminTaskMessageResponseList.filter { it.activityTriggered } as ArrayList<AdminActivityResponse>
        }

        futureQuestionListAdapter = FutureTaskListAdapter(
            list, selectedRadioButton
        ) {
            when {
                it.first == FutureTaskListAdapter.CLICK_WHOLE -> {
                    if (!TextUtils.isEmpty(it.second.youtubeLink)) {
                        val intent = Intent(context, YoutubeActivity::class.java)
                        intent.putExtra(
                            "youtubeId", it.second!!.youtubeLink!!
                        )
                        fragmentContext.startActivity(intent)
                    } else {
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

                }
                it.first == FutureTaskListAdapter.CLICK_UN_SCHEDULE -> {
                    now = Calendar.getInstance()
                    selectedTaskOrMessage = it.second
                    val dpd = DatePickerDialog.newInstance(
                        this@ScheduleFutureTaskFragment, now[Calendar.YEAR],  // Initial year selection
                        now[Calendar.MONTH],  // Initial month selection
                        now[Calendar.DAY_OF_MONTH] // Inital day selection
                    )
                    dpd.version = DatePickerDialog.Version.VERSION_2
                    dpd.show(childFragmentManager, "Datepickerdialog")
                    dpd.isCancelable = true
                    dpd.minDate = now
                    dpd.vibrate(true)
                }
                it.first == FutureTaskListAdapter.CLICK_NOTIFY -> {
                    Toast.makeText(fragmentContext, "Notify Api", Toast.LENGTH_LONG).show()
                }
                it.first == FutureTaskListAdapter.CLICK_DELETE && !it.second.activityTriggered -> {

                    val builder = AlertDialog.Builder(fragmentContext)
                    builder.setMessage("Do you want to delete this Triggered question?")
                    builder.setTitle("Delete Alert")
                    builder.setCancelable(true)

                    builder.setPositiveButton(
                        "Yes"
                    ) { dialog: DialogInterface?, id: Int ->
                        //deleteActivity(it.second.activityId, it.third)
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

        binding.recyclerView.adapter = futureQuestionListAdapter

        if (futureQuestionListAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.recyclerView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@ScheduleFutureTaskFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@ScheduleFutureTaskFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ScheduleFutureTaskFragment.isLoading
            }
        })

    }

//    private fun deleteActivity(activityId: String?, position: Int) {
//        viewModel.deleteActivityCreatedByAdmin(activityId).observe(
//            viewLifecycleOwner
//        ) { apiResponse: ApiResponse<StandardResponse> ->
//            if (apiResponse.loading) {
//                showProgress()
//            } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                hideProgress()
//                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                    futureQuestionListAdapter.removeItem(position)
//                } else {
//                    showAlert(apiResponse.body.errorMessage)
//                }
//            } else {
//                hideProgress()
//                showAlert(apiResponse.errorMessage)
//            }
//        }
//    }


    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        val calendarTime = Calendar.getInstance()
        calendarTime[Calendar.HOUR_OF_DAY] = hourOfDay
        calendarTime[Calendar.MINUTE] = minute

        val timeFormat = "hh:mm:ss aa" // your own format
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        formattedDateTime = "$formattedDateTime ${sdf.format(calendarTime.time)}"

        if (selectedTaskOrMessage != null)
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavScheduleQuestion(
                    formattedDateTime.uppercase(Locale.getDefault()),
                    selectedTaskOrMessage?.activityType ?: "",
                    selectedTaskOrMessage?.activityId ?: ""
                )
            )
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val tpd = TimePickerDialog.newInstance(
            this@ScheduleFutureTaskFragment, calendarDate[Calendar.HOUR_OF_DAY], calendarDate[Calendar.MINUTE], false
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

        if (adminTaskMessageIdList == null || adminTaskMessageIdList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            return
        }
        start = futureQuestionListAdapter!!.itemCount
        end = adminTaskMessageIdList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            return
        }
        val ids: MutableList<String> = java.util.ArrayList()
        val statusLists = getDisplayElements(
            adminTaskMessageIdList!!, start
        )
        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
        var i = 0
        while (i < statusLists.size) {
            val item = statusLists[i]
            ids.add(item)
            i++
        }
        val idsChunk = Utilities.getTokenSeparatedString(ids, ",")

        viewModel.getNextTaskMessageCreatedByAdmin(idsChunk).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetAdminActivityListResponse> ->
            if (apiResponse.loading) {
                if (futureQuestionListAdapter != null) {
                    isLoading = true
                    futureQuestionListAdapter!!.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (futureQuestionListAdapter != null) {
                    futureQuestionListAdapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.adminTaskMessageResponseList.isEmpty()) {
                        isLastPage = true;
                    } else {
                        if (futureQuestionListAdapter != null) {

                            val list: ArrayList<AdminActivityResponse> = if (selectedRadioButton == "Unscheduled") {
                                apiResponse.body.adminTaskMessageResponseList.filter { !it.activityTriggered } as ArrayList<AdminActivityResponse>
                            } else {
                                apiResponse.body.adminTaskMessageResponseList.filter { it.activityTriggered } as ArrayList<AdminActivityResponse>
                            }

                            futureQuestionListAdapter!!.addItems(list as List<AdminActivityResponse>)
                        }

                        isLastPage = apiResponse.body.adminTaskMessageResponseList.size < PaginationListener.PAGE_ITEM_SIZE

                    }
                } else {
                    isLastPage = true;
//                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                isLoading = false
                if (futureQuestionListAdapter != null) {
                    futureQuestionListAdapter!!.removeLoading()
                }
                showAlert(apiResponse.errorMessage)
            }
        }

    }


}