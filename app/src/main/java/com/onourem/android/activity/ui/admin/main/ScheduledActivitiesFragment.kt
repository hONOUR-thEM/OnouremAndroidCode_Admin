package com.onourem.android.activity.ui.admin.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.Size
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.CalendarDayLayoutBinding
import com.onourem.android.activity.databinding.FragmentScheduledActivitiesBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.create.adapters.ActivityListAdapter
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminActivityResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.calender.getColorCompat
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class ScheduledActivitiesFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentScheduledActivitiesBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var datePickForHorizontalCalender: Boolean = false
    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private var selectedActivity: AdminActivityResponse? = null
//    private var selectedRadioButton: String = "Unscheduled"
//    private var isLastPage: Boolean = false
//    private var isLoading: Boolean = false
//    private var idList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]
    }

    companion object {
        fun create(): ScheduledActivitiesFragment {
            val fragment = ScheduledActivitiesFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var now: Calendar
    private var futureQuestionListAdapter: ActivityListAdapter? = null

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

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_scheduled_activities
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        now = Calendar.getInstance()
        val dm = DisplayMetrics()
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        binding.calendarView.apply {
            val dayWidth = dm.widthPixels / 5
            val dayHeight = (dayWidth * 1.25).toInt()
            daySize = Size(dayWidth, dayHeight)
        }


        layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding.rvQuestionsGames.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.rvQuestionsGames.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.rvQuestionsGames.layoutManager = layoutManager

        @SuppressLint("NewApi")
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = CalendarDayLayoutBinding.bind(view)
            lateinit var day: CalendarDay

            init {

                view.setOnClickListener {

                    val firstDay = binding.calendarView.findFirstVisibleDay()
                    val lastDay = binding.calendarView.findLastVisibleDay()
                    if (firstDay == day) {
                        // If the first date on screen was clicked, we scroll to the date to ensure
                        // it is fully visible if it was partially off the screen when clicked.
                        binding.calendarView.smoothScrollToDate(day.date)
                    } else if (lastDay == day) {
                        // If the last date was clicked, we scroll to 4 days ago, this forces the
                        // clicked date to be fully visible if it was partially off the screen.
                        // We scroll to 4 days ago because we show max of five days on the screen
                        // so scrolling to 4 days ago brings the clicked date into full visibility
                        // at the end of the calendar view.

                        binding.calendarView.smoothScrollToDate(day.date.minusDays(4))
                    }

                    // Example: If you want the clicked date to always be centered on the screen,
                    // you would use: exSevenCalendar.smoothScrollToDate(day.date.minusDays(2))

                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.calendarView.notifyDateChanged(day.date)
                        oldDate?.let { binding.calendarView.notifyDateChanged(it) }
                    }

                    val defaultZoneId: ZoneId = ZoneId.systemDefault()

                    val date = Date.from(selectedDate.atStartOfDay(defaultZoneId).toInstant())
                    val dateFormat = "yyyy-MM-dd" // your own format
                    val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

                    binding.rvQuestionsGames.adapter = null

                    searchAdminActivityByDate(sdf.format(date))

                }
            }

            fun bind(day: CalendarDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = yearFormatter.format(day.date)
                bind.exSevenMonthText.text = monthFormatter.format(day.date)

                bind.exSevenDateText.setTextColor(view.context.getColorCompat(if (day.date == selectedDate) R.color.colorAccent else R.color.gray))
                bind.exSevenDayText.setTextColor(view.context.getColorCompat(if (day.date == selectedDate) R.color.colorAccent else R.color.gray))
                bind.exSevenMonthText.setTextColor(view.context.getColorCompat(if (day.date == selectedDate) R.color.colorAccent else R.color.gray))
                bind.exSevenSelectedView.isVisible = day.date == selectedDate


            }
        }

        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind(day)
        }

        val currentMonth = YearMonth.now()
        // Value for firstDayOfWeek does not matter since inDates and outDates are not generated.
        binding.calendarView.setup(
            currentMonth.minusMonths(6),
            currentMonth.plusMonths(6),
            DayOfWeek.values().random()
        )
        binding.calendarView.scrollToDate(LocalDate.now())

        val dateFormat = "yyyy-MM-dd" // your own format
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

        searchAdminActivityByDate(sdf.format(Date()))

        binding.swipeRefreshLayout.setOnRefreshListener {
            val defaultZoneId: ZoneId = ZoneId.systemDefault()

            val date = Date.from(selectedDate.atStartOfDay(defaultZoneId).toInstant())

            binding.rvQuestionsGames.adapter = null

            searchAdminActivityByDate(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))
        }

        binding.btnSelectDate.setOnClickListener {
            now = Calendar.getInstance()

            val minDateCalender = Calendar.getInstance()
            minDateCalender.add(Calendar.MONTH, -6)

            val maxDateCalender = Calendar.getInstance()
            maxDateCalender.add(Calendar.MONTH, 6)

            datePickForHorizontalCalender = true
            val dpd = DatePickerDialog.newInstance(
                this@ScheduledActivitiesFragment,
                now[Calendar.YEAR],  // Initial year selection
                now[Calendar.MONTH],  // Initial month selection
                now[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.version = DatePickerDialog.Version.VERSION_2
            dpd.show(childFragmentManager, "Datepickerdialog")
            dpd.isCancelable = true
            dpd.minDate = minDateCalender
            dpd.maxDate = maxDateCalender
            dpd.vibrate(true)
        }

    }

    private fun searchAdminActivityByDate(searchDate: String) {

        Toast.makeText(
            fragmentContext,
            searchDate,
            Toast.LENGTH_LONG
        ).show()

        viewModel.searchAdminActivityByDate(searchDate)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAdminActivityListResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        setAdapter(apiResponse.body)
                        if (binding.swipeRefreshLayout.isRefreshing)
                            binding.swipeRefreshLayout.isRefreshing = false
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun setAdapter(getAdminActivityListResponse: GetAdminActivityListResponse) {
//        isLastPage = false

//        idList = ArrayList()
//        if (getAdminActivityListResponse.adminActivityIdList !=null){
//            idList?.addAll(getAdminActivityListResponse.adminActivityIdList)
//        }
//        val list: ArrayList<AdminActivityResponse> = if (selectedRadioButton == "Unscheduled"){
//            getAdminActivityListResponse.adminActivityResponseList.filter { !it.isActivityTriggered && it.activityType == "1toM"} as ArrayList<AdminActivityResponse>
//        }else{
//            getAdminActivityListResponse.adminActivityResponseList.filter { it.isActivityTriggered && it.activityType == "1toM"} as ArrayList<AdminActivityResponse>
//        }

        futureQuestionListAdapter =
            ActivityListAdapter(
                getAdminActivityListResponse.adminActivityResponseList
            ) {
                when {
                    it.first == ActivityListAdapter.CLICK_WHOLE -> {
                        if (!TextUtils.isEmpty(it.second.activityVideoUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavMediaView(
                                    2,
                                    it.second.activityVideoUrl!!
                                )
                            )
                        } else if (!TextUtils.isEmpty(it.second.activityImageUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavMediaView(
                                    1,
                                    it.second.activityImageUrl!!
                                )
                            )
                        }

                    }

                    it.first == ActivityListAdapter.CLICK_UN_SCHEDULE -> {
                        now = Calendar.getInstance()
                        selectedActivity = it.second
                        val dpd = DatePickerDialog.newInstance(
                            this@ScheduledActivitiesFragment,
                            now[Calendar.YEAR],  // Initial year selection
                            now[Calendar.MONTH],  // Initial month selection
                            now[Calendar.DAY_OF_MONTH] // Inital day selection
                        )
                        dpd.version = DatePickerDialog.Version.VERSION_2
                        dpd.show(childFragmentManager, "Datepickerdialog")
                        dpd.isCancelable = true
                        dpd.minDate = now
                        dpd.vibrate(true)
                    }

                    it.first == ActivityListAdapter.CLICK_INFO -> {
                        navController.navigate(MobileNavigationDirections.actionGlobalNavActivityUpdates())
                    }

                    it.first == ActivityListAdapter.CLICK_DELETE && !it.second.activityTriggered -> {

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

//        binding.rvQuestionsGames.addOnScrollListener(object : PaginationListener(layoutManager) {
//            override fun loadMoreItems() {
//                this@ScheduledActivitiesFragment.isLoading = true
//                loadMoreGames()
//            }
//
//            override fun isLastPage(): Boolean {
//                return this@ScheduledActivitiesFragment.isLastPage
//            }
//
//            override fun isLoading(): Boolean {
//                return this@ScheduledActivitiesFragment.isLoading
//            }
//        })

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
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        if (datePickForHorizontalCalender) {

            selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            binding.calendarView.smoothScrollToDate(selectedDate)
            val defaultZoneId: ZoneId = ZoneId.systemDefault()
            val date = Date.from(selectedDate.atStartOfDay(defaultZoneId).toInstant())
            binding.rvQuestionsGames.adapter = null
            searchAdminActivityByDate(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))


        } else {

            val calendarDate = Calendar.getInstance()
            calendarDate.set(year, monthOfYear, dayOfMonth);

            val tpd = TimePickerDialog.newInstance(
                this@ScheduledActivitiesFragment,
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

    private fun getDisplayElements(
        myList: List<String>, startIndex: Int
    ): List<String> {
        return myList.subList(
            startIndex, myList.size.toLong().coerceAtMost(startIndex + 15L).toInt()
        )
    }

}