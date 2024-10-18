package com.onourem.android.activity.ui.admin.create

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentCreateActivitiesMainBinding
import com.onourem.android.activity.models.FutureQuestionDetails
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.create.card.ScheduleFutureCardsFragment
import com.onourem.android.activity.ui.admin.create.external_posts.ScheduleFutureExternalPostsFragment
import com.onourem.android.activity.ui.admin.create.question_schedule.ScheduleFutureQuestionsFragment
import com.onourem.android.activity.ui.admin.create.surveys.ScheduleFutureSurveysFragment
import com.onourem.android.activity.ui.admin.create.task.ScheduleFutureTaskFragment
import com.onourem.android.activity.ui.admin.vocals.ScheduleVocalsFragment
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants.SELECTED_INDEX_ADMIN_MENU
import com.onourem.android.activity.viewmodel.DashboardViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class CreateActivitiesFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentCreateActivitiesMainBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

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

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.fragment_create_activities_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_nav).isVisible = false
        menu.findItem(R.id.profile_nav).isVisible = false
        menu.findItem(R.id.phone_nav).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tabs.visibility = View.VISIBLE

        renderViewPager()

        renderTabLayout()

        binding.fab.setOnClickListener {

            when (binding.fab.text) {
                "Create System Question" -> {

//                    navController.navigate(
//                        MobileNavigationDirections.actionGlobalNavScheduleQuestion(
//                            "", null, null, null, null
//                        )
//                    )

                    val questionTo = "1toM"
                    val futureQuestionDetails =
                        FutureQuestionDetails(AppUtilities.getTimeZone(), "", "", questionTo, "", "All")

                    navController.navigate(
                        CreateActivitiesFragmentDirections.actionNavCreateActivitiesMainToNavAdminCreateOwnQuestion(
                            PlayGroup(), futureQuestionDetails
                        )
                    )



//                    now = Calendar.getInstance()
//
//                    val dpd = DatePickerDialog.newInstance(
//                        this@CreateActivitiesFragment,
//                        now[Calendar.YEAR],  // Initial year selection
//                        now[Calendar.MONTH],  // Initial month selection
//                        now[Calendar.DAY_OF_MONTH] // Inital day selection
//                    )
//                    dpd.version = DatePickerDialog.Version.VERSION_2
//                    dpd.show(childFragmentManager, "Datepickerdialog")
//                    dpd.isCancelable = true
//                    dpd.minDate = now
                }

                "Create System Card" -> {

                    val questionTo = "Card"
                    val futureQuestionDetails =
                        FutureQuestionDetails(AppUtilities.getTimeZone(), "", "", questionTo, "", "All")

                    navController.navigate(
                        CreateActivitiesFragmentDirections.actionNavCreateActivitiesMainToNavAdminCreateOwnQuestion(
                            PlayGroup(), futureQuestionDetails
                        )
                    )

                    //                    now = Calendar.getInstance()

//                    val dpd = DatePickerDialog.newInstance(
//                        this@CreateActivitiesFragment,
//                        now[Calendar.YEAR],  // Initial year selection
//                        now[Calendar.MONTH],  // Initial month selection
//                        now[Calendar.DAY_OF_MONTH] // Inital day selection
//                    )
//                    dpd.version = DatePickerDialog.Version.VERSION_2
//                    dpd.show(childFragmentManager, "Datepickerdialog")
//                    dpd.isCancelable = true
//                    dpd.minDate = now
                }

                "Create Task / Message" -> {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavAdminAppreciateDialog(
                            formattedDateTime.uppercase(
                                Locale.getDefault()
                            )
                        )
                    )
                }

                "Create External Post" -> {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavExternalContent(formattedDateTime.uppercase(Locale.getDefault()))
                    )
                }

                "Create Survey" -> {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavAddSurvey(formattedDateTime.uppercase(Locale.getDefault()))
                    )
                }
            }
        }

        val dateFormat = "dd-MM-yyyy" // your own format
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    private fun renderViewPager() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {

                return when (position) {
                    0 -> {
                        ScheduleFutureQuestionsFragment.create()
                    }
                    1 -> {
                        ScheduleFutureCardsFragment.create()
                    }
                    2 -> {
                        ScheduleFutureTaskFragment.create()
                    }
                    3 -> {
                        ScheduleFutureExternalPostsFragment.create()
                    }
                    4 -> {
                        ScheduleFutureSurveysFragment.create()
                    }
                    5 -> {
                        ScheduleVocalsFragment.create()
                    }

                    else -> {
                        ScheduleFutureQuestionsFragment.create()
                    }
                }
            }

            override fun getItemCount(): Int {
                return tabList.size
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setFabButtonText(position)
                if (position == 5){
                    binding.fab.visibility = View.GONE
                }else{
                    binding.fab.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun setFabButtonText(position: Int) {
        preferenceHelper.putValue(SELECTED_INDEX_ADMIN_MENU, position)
        when (position) {
            0 -> {
                binding.fab.text = "Create System Question"
            }
            1 -> {
                binding.fab.text = "Create System Card"
            }
            2 -> {
                binding.fab.text = "Create Task / Message"
            }
            3 -> {
                binding.fab.text = "Create External Post"
            }
            4 -> {
                binding.fab.text = "Create Survey"
            }
        }
    }

    fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
        tab.view.children.find { it is TextView }?.let { tv ->
            (tv as TextView).post {
                tv.setTypeface(tv.typeface, style)
            }
        }
    }

    private fun renderTabLayout() {

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.NORMAL)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
            }

        })

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = tabList[position]

            for (i in 0 until (binding.tabs.getChildAt(0) as ViewGroup).childCount) {
                val tabView = (binding.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tabView.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(0, 0, 20, 0)
                tabView.requestLayout()
            }
        }.attach()

        binding.viewpager.postDelayed({
            binding.viewpager.currentItem = if (preferenceHelper.getInt(SELECTED_INDEX_ADMIN_MENU) == 0) {
                CreateActivitiesFragmentArgs.fromBundle(requireArguments()).position.toInt()
            } else {
                preferenceHelper.getInt(SELECTED_INDEX_ADMIN_MENU)
            }
        }, 400)

//        binding.viewpager.postDelayed({
//            binding.viewpager.currentItem = CreateActivitiesFragmentArgs.fromBundle(requireArguments()).position.toInt()
//        }, 400)

        binding.viewpager.isUserInputEnabled = false

    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        val calendarTime = Calendar.getInstance()
        calendarTime[Calendar.HOUR_OF_DAY] = hourOfDay
        calendarTime[Calendar.MINUTE] = minute

        val timeFormat = "hh:mm:ss aa" // your own format
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        formattedDateTime = "$formattedDateTime ${sdf.format(calendarTime.time)}"


        when (binding.fab.text) {
            "Create System Question" -> {

            }

            "Create System Card" -> {

            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val tpd = TimePickerDialog.newInstance(
            this@CreateActivitiesFragment,
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

    companion object {
        val tabList = listOf(
            "Questions",
            "Cards",
            "Task / Message",
            "External Posts",
            "Surveys",
            "Vocals",
        )
    }

}
