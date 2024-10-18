package com.onourem.android.activity.ui.admin.oclub_auto_trigger

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
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentAllActivitiesMainBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.cards.AllCardsFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.external.AllExternalPostsFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.questions.AllQuestionsFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.surveys.AllAdminSurveysFragment
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.task_message.AllTaskFragment
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.Constants.SELECTED_INDEX_ADMIN_MENU
import com.onourem.android.activity.viewmodel.DashboardViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AllActivitiesFragment : AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentAllActivitiesMainBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var now: Calendar

    private var formattedDateTime: String = ""

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.fragment_all_activities_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

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
                        AllQuestionsFragment.create()
                    }

                    1 -> {
                        AllCardsFragment.create()
                    }

                    2 -> {
                        AllTaskFragment.create()
                    }

                    3 -> {
                        AllExternalPostsFragment.create()
                    }

                    4 -> {
                        AllAdminSurveysFragment.create()
                    }

                    else -> {
                        AllQuestionsFragment.create()
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
            }
        })

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

//        binding.viewpager.postDelayed({
//            binding.viewpager.currentItem = if (preferenceHelper.getInt(SELECTED_INDEX_ADMIN_MENU) == 0) {
//                AllActivitiesFragmentArgs.fromBundle(requireArguments()).position.toInt()
//            } else {
//                preferenceHelper.getInt(SELECTED_INDEX_ADMIN_MENU)
//            }
//        }, 400)

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

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.set(year, monthOfYear, dayOfMonth);

        val tpd = TimePickerDialog.newInstance(
            this@AllActivitiesFragment, calendarDate[Calendar.HOUR_OF_DAY], calendarDate[Calendar.MINUTE], false
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

    companion object {
        val tabList = listOf(
            "Questions",
            "Cards",
            "Task / Message",
            "External Posts",
            "Surveys",
        )
    }

}
