package com.onourem.android.activity.ui.admin.oclub_auto_trigger.surveys

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAllActivitiesBinding
import com.onourem.android.activity.ui.admin.create.adapters.SurveysAdapter
import com.onourem.android.activity.ui.admin.create.surveys.AdminSurveyResponse
import com.onourem.android.activity.ui.admin.create.surveys.SurveyActivityResponse
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters.AllSurveyListAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import java.util.Calendar


class AllAdminSurveysFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAllActivitiesBinding>() {

    private var surveyActivityIdList: ArrayList<String>? = null
    private var surveyActivityDataList: ArrayList<AdminSurveyResponse>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var surveysAdapter: AllSurveyListAdapter? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_all_activities
    }

    companion object {
        fun create(): AllAdminSurveysFragment {
            val fragment = AllAdminSurveysFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        binding.rvActivities.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        getSurveyActivityListForAdmin()

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (surveysAdapter != null) {
                surveysAdapter!!.clearData()
            }
            getSurveyActivityListForAdmin()
        }

    }

    private fun getSurveyActivityListForAdmin() {

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

                    setAdapter()
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

    private fun setAdapter() {
        isLastPage = false

        surveysAdapter = AllSurveyListAdapter(
            surveyActivityDataList!!,
        ) {
            when (it.first) {

                AllSurveyListAdapter.CLICK_WHOLE, AllSurveyListAdapter.CLICK_MEDIA -> {
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

                AllSurveyListAdapter.CLICK_DELETE -> {

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

                AllSurveyListAdapter.CLICK_TRIGGER_IN_OCLUB -> {
                    navController.navigate(//nav_trigger_activities_in_oclub
                        MobileNavigationDirections.actionGlobalNavTriggerActivitiesInOclub(
                            it.second.activityId!!,
                            it.second.activityText!!,
                            it.second.activityType!!,
                        )
                    )
                }

            }
        }

        binding.rvActivities.adapter = surveysAdapter

        if (surveysAdapter != null && surveysAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = "You do not have any Survey in this section.\\n\\nPull down to refresh."
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvActivities.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@AllAdminSurveysFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AllAdminSurveysFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@AllAdminSurveysFragment.isLoading
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

                            surveysAdapter!!.addItems(apiResponse.body.adminSurveyResponseList as List<AdminSurveyResponse>)
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


}