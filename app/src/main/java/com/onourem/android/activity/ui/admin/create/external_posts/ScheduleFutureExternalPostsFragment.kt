package com.onourem.android.activity.ui.admin.create.external_posts

import android.annotation.SuppressLint
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
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentExternalPostsBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.admin.create.adapters.ExternalPostsAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*


class ScheduleFutureExternalPostsFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentExternalPostsBinding>(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var selectedExternalPost: ExternalActivityData? = null
    private var externalActivityIdList: ArrayList<String>? = null
    private var externalActivityDataList: ArrayList<ExternalActivityData>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var externalPostsAdapter: ExternalPostsAdapter? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var selectedRadioButton: String = "Unscheduled"
    private lateinit var now: Calendar


    private var formattedDateTime: String = ""

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_external_posts
    }

    companion object {

        fun create(): ScheduleFutureExternalPostsFragment {
            val fragment = ScheduleFutureExternalPostsFragment()
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

        getExternalListForAdmin(selectedRadioButton)

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (externalPostsAdapter != null) {
                externalPostsAdapter!!.clearData()
            }
            getExternalListForAdmin(selectedRadioButton)
        }

        binding.rdg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdbUnpublished -> {
                    selectedRadioButton = "Inactive"
                    refreshUI(selectedRadioButton)
                }
                R.id.rdbPublished -> {
                    selectedRadioButton = "Active"
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
        getExternalListForAdmin(selectedRadioButton)
    }

    private fun getExternalListForAdmin(selectedRadioButton: String) {

        viewModel.getExternalListForAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<ExternalPostResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        externalActivityDataList = ArrayList()
                        externalActivityDataList!!.addAll(apiResponse.body.externalActivityDataList)

                        if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
                        externalActivityIdList = ArrayList()
                        externalActivityIdList!!.addAll(apiResponse.body.externalActivityIdList)

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

    private fun deleteExternalActivityByAdmin(item: Triple<Int, ExternalActivityData, Int>) {

        viewModel.deleteExternalActivityByAdmin(item.second.id!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (externalPostsAdapter != null) {
                            externalPostsAdapter!!.removeItem(item.second)
                            Toast.makeText(fragmentContext, "Post has been deleted", Toast.LENGTH_LONG).show()
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

    private fun setAdapter(selectedRadioButton: String) {
        isLastPage = false

        val list: ArrayList<ExternalActivityData> = when (selectedRadioButton) {
            "Unscheduled" -> {
                externalActivityDataList!!.filter { !it.activityScheduled && it.status == "Y" } as ArrayList<ExternalActivityData>
            }
            "Triggered" -> {
                externalActivityDataList!!.filter { it.activityTriggered && it.status == "Y" } as ArrayList<ExternalActivityData>
            }
            "Scheduled" -> {
                externalActivityDataList!!.filter { it.activityScheduled && !it.activityTriggered && it.status == "Y" } as ArrayList<ExternalActivityData>
            }
            "Active" -> {
                externalActivityDataList!!.filter { it.status == "Y" } as ArrayList<ExternalActivityData>
            }
            else -> {
                externalActivityDataList!!.filter { it.status == "N" } as ArrayList<ExternalActivityData>
            }
        }

        externalPostsAdapter = ExternalPostsAdapter(
            list, selectedRadioButton
        ) {
            when (it.first) {
                ExternalPostsAdapter.CLICK_WHOLE -> {
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
                ExternalPostsAdapter.CLICK_MEDIA -> {
                    if (it.second!!.youtubeLink == "Y") {
                        val intent = Intent(context, YoutubeActivity::class.java)
                        intent.putExtra(
                            "youtubeId", it.second.videoId
                        )
                        fragmentContext.startActivity(intent)
                    } else {
                        if (!TextUtils.isEmpty(it.second.videoUrl)) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                    it.second.videoUrl!!
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
                ExternalPostsAdapter.CLICK_EDIT -> {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavUpdateExternalContent(
                            it.second
                        )
                    )
                }

                ExternalPostsAdapter.CLICK_NOTIFY -> {
                    Toast.makeText(fragmentContext, "Notify Api", Toast.LENGTH_LONG).show()
                }

                ExternalPostsAdapter.CLICK_DELETE -> {

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
                ExternalPostsAdapter.CLICK_UN_SCHEDULE -> {
                    now = Calendar.getInstance()
                    selectedExternalPost = it.second
                    val dpd = DatePickerDialog.newInstance(
                        this@ScheduleFutureExternalPostsFragment, now[Calendar.YEAR],  // Initial year selection
                        now[Calendar.MONTH],  // Initial month selection
                        now[Calendar.DAY_OF_MONTH] // Inital day selection
                    )
                    dpd.version = DatePickerDialog.Version.VERSION_2
                    dpd.show(childFragmentManager, "Datepickerdialog")
                    dpd.isCancelable = true
                    dpd.minDate = now
                    dpd.vibrate(true)                }

            }
        }

        binding.rvExternalPosts.adapter = externalPostsAdapter

        if (externalPostsAdapter != null && externalPostsAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvExternalPosts.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@ScheduleFutureExternalPostsFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@ScheduleFutureExternalPostsFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ScheduleFutureExternalPostsFragment.isLoading
            }
        })

    }

    // print twenty element from list from the start index specified
    private fun getDisplayElements(
        myList: List<String>, startIndex: Int
    ): List<String> {
        var sub: List<String> = java.util.ArrayList()
        sub = myList.subList(
            startIndex, Math.min(myList.size.toLong(), startIndex + 20L).toInt()
        )
        return sub
    }

    private fun loadMoreGames() {
        var start = 0
        var end = 0

        if (externalActivityIdList == null || externalActivityIdList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            return
        }
        start = externalPostsAdapter!!.itemCount
        end = externalActivityIdList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            return
        }
        val ids: MutableList<String> = ArrayList()
        val statusLists = getDisplayElements(
            externalActivityIdList!!, start
        )
        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
        var i = 0
        while (i < statusLists.size) {
            val item = statusLists[i]
            ids.add(item)
            i++
        }
        val idsChunk = Utilities.getTokenSeparatedString(ids, ",")

        viewModel.getNextExternalListForAdmin(idsChunk).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<ExternalPostResponse> ->
            if (apiResponse.loading) {
                if (externalPostsAdapter != null) {
                    externalPostsAdapter!!.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (externalPostsAdapter != null) {
                    externalPostsAdapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.externalActivityDataList.isEmpty()) {
                        // isLastPage = true;
                    } else {
                        if (externalPostsAdapter != null) {

                            val list: ArrayList<ExternalActivityData> = when (selectedRadioButton) {
                                "Unscheduled" -> {
                                    apiResponse.body.externalActivityDataList.filter { !it.activityTriggered } as ArrayList<ExternalActivityData>
                                }
                                "Triggered" -> {
                                    apiResponse.body.externalActivityDataList.filter { it.activityTriggered } as ArrayList<ExternalActivityData>
                                }
                                "Active" -> {
                                    apiResponse.body.externalActivityDataList.filter { it.status == "Y" } as ArrayList<ExternalActivityData>
                                }
                                else -> {
                                    apiResponse.body.externalActivityDataList.filter { it.status == "N" } as ArrayList<ExternalActivityData>
                                }
                            }

                            externalPostsAdapter!!.addItems(list as List<ExternalActivityData>)
                            externalActivityDataList!!.addAll(list as List<ExternalActivityData>)
                            //setDashboardDataToActivity()
                        }

                        if (apiResponse.body.externalActivityDataList.size < PaginationListener.PAGE_ITEM_SIZE) {
                            // isLastPage = true;
//                            setFooterMessage()
                        } else {
                            isLastPage = false
                        }
                    }
                } else {
                    // isLastPage = true;
//                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                isLoading = false
                if (externalPostsAdapter != null) {
                    externalPostsAdapter!!.removeLoading()
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

        if (selectedExternalPost != null) {
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
            this@ScheduleFutureExternalPostsFragment,
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