package com.onourem.android.activity.ui.survey.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAllSurveysBinding
import com.onourem.android.activity.models.FilterItem
import com.onourem.android.activity.models.SurveyCOList
import com.onourem.android.activity.models.UserProfileSurveyRequest
import com.onourem.android.activity.models.UserProfileSurveyResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.QuestionGamesFilterAdapter
import com.onourem.android.activity.ui.survey.adapters.AllSurveyAdapter
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import javax.inject.Inject


class AllSurveysFragment :
    AbstractBaseViewModelBindingFragment<SurveyViewModel, FragmentAllSurveysBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    var prevListSize = 0
    private var isLoading = false
    private var isLastPage = false
    private var surveyIdList: MutableList<Int>? = null
    private var surveyCOList: MutableList<SurveyCOList?>? = null
    private var allSurveyAdapter: AllSurveyAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var filterItems: ArrayList<FilterItem>? = null
    private var quesGamesFilterAdapter: QuestionGamesFilterAdapter? = null
    private var selectedFilterIndex = 0
    private var isFromAnswered = "Y"
    override fun viewModelType(): Class<SurveyViewModel> {
        return SurveyViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_all_surveys
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isLastPage = false
        isLoading = false
        binding.rvQuesGamesFilter.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.rvQuesGamesFilter.visibility = View.GONE
//        selectedFilterIndex = 1
//        filterItems = ArrayList()
//        filterItems!!.add(FilterItem("Un-Answered", "Un-Answered", false, 0))
//        filterItems!!.add(FilterItem("Answered", "Answered", false, 1))
//        quesGamesFilterAdapter = QuestionGamesFilterAdapter(filterItems!!) { item: FilterItem ->
//            //Toast.makeText(requireActivity(), "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
//            item.isVisible = false
//            quesGamesFilterAdapter!!.modifyItem(item.pos, item)
//            when (item.id) {
//                "Answered" -> {
//                    selectedFilterIndex = 1
//                    isFromAnswered = "Y"
//                    loadData()
//                    binding.rvAllSurveys.scrollToPosition(0)
//                }
//                "Un-Answered" -> {
//                    isFromAnswered = "N"
//                    selectedFilterIndex = 0
//                    loadData()
//                    binding.rvAllSurveys.scrollToPosition(0)
//                }
//            }
//        }
//        quesGamesFilterAdapter!!.selectedPos = selectedFilterIndex
//        binding.rvQuesGamesFilter.adapter = quesGamesFilterAdapter
        loadData()
        viewModel.getActionMutableLiveData()
            .observe(viewLifecycleOwner, Observer { item: SurveyCOList? ->
                if (item != null) {
                    var index = 0
                    for (surveyCOList in surveyCOList!!) {
                        index++
                        if (item.id == surveyCOList!!.id) {
                            surveyCOList.userAnserForSurvey = "Y"
                            if (allSurveyAdapter != null) {
                                allSurveyAdapter!!.notifyItemChanged(index)
                            }
                        }
                    }
                }
            })
    }

    private fun loadData() {
        surveyIdList = ArrayList()
        surveyCOList = ArrayList()
        val userProfileSurveyRequest = UserProfileSurveyRequest()
        userProfileSurveyRequest.screenId = "47"
        userProfileSurveyRequest.isFromAnswered = isFromAnswered
        userProfileSurveyRequest.serviceName = "getUserProfileSurey"
        viewModel.getUserProfileSurvey(userProfileSurveyRequest).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserProfileSurveyResponse> ->
            if (apiResponse.loading) {
                showProgress()
            }
            else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    surveyCOList!!.clear()
                    surveyIdList!!.clear()
                    surveyIdList!!.addAll(apiResponse.body.surveyIdList!!)
                    surveyCOList!!.addAll(apiResponse.body.surveyData!!)
                    prevListSize = surveyCOList!!.size
                    setAdapter(surveyCOList!!)
                }
                else {
                    showAlert(apiResponse.body.errorMessage)
                }
            }
            else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getUserProfileSurvey")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserProfileSurvey",
                        apiResponse.code.toString()
                    )
                }
            }
        }

//        if (allSurveyAdapter == null) {
//
//        } else {
//            refreshUI();
//        }
    }

    private fun refreshUI() {
        isLastPage = false
        isLoading = false
        if (allSurveyAdapter != null) allSurveyAdapter!!.removeFooter()

//        setAdapter(surveyCOList);
    }

    @SuppressLint("SetTextI18n")
    private fun setAdapter(surveyDataArrayList: List<SurveyCOList?>) {
        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvAllSurveys.layoutManager = layoutManager
        allSurveyAdapter =
            AllSurveyAdapter(requireActivity(), surveyDataArrayList) { item ->

                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_all_surveys) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavAnonymousSurvey(
                            item.first.id!!,
                        )
                    )
                }

            }
        binding.rvAllSurveys.adapter = allSurveyAdapter
        isLastPage = false
        isLoading = false
        if (allSurveyAdapter != null) allSurveyAdapter!!.removeFooter()
        if (allSurveyAdapter != null && allSurveyAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
            if (isFromAnswered.equals("N", ignoreCase = true)) {
                binding.tvMessage.text = "You have answered all the surveys."
            }
            else if (isFromAnswered.equals("Y", ignoreCase = true)) {
                binding.tvMessage.text = "You have not answered any survey so far."
            }
        }
        else {
            binding.tvMessage.visibility = View.GONE
        }
        binding.rvAllSurveys.addOnScrollListener(object : PaginationListener(layoutManager!!) {
            override fun loadMoreItems() {
                var totalItemCount = 0
                if (layoutManager != null) {
                    totalItemCount = layoutManager!!.itemCount
                    val lastVisible = layoutManager!!.findLastVisibleItemPosition()
                    val endHasBeenReached = lastVisible + 2 >= totalItemCount
                    if (totalItemCount > 0 && endHasBeenReached) {
                        //you have reached to the bottom of your recycler view
                        this@AllSurveysFragment.isLoading = true
                        doApiCall()
                    }
                }
            }

            override fun isLastPage(): Boolean {
                return this@AllSurveysFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@AllSurveysFragment.isLoading
            }
        })
    }

    private fun doApiCall() {
        if (surveyIdList == null || surveyIdList!!.isEmpty()) {
            this@AllSurveysFragment.isLastPage = true
            this@AllSurveysFragment.isLoading = false
            setFooterMessage()
            return
        }
        val start = allSurveyAdapter!!.itemCount
        val end =
            if (surveyIdList!!.size - start > PaginationListener.PAGE_ITEM_SIZE) start + PaginationListener.PAGE_ITEM_SIZE else surveyIdList!!.size
        if (start >= end) {
            this@AllSurveysFragment.isLastPage = true
            this@AllSurveysFragment.isLoading = false
            setFooterMessage()
            return
        }
        val stringBuilder = StringBuilder()
        for (i in start until end) {
            val id = surveyIdList!![i]
            if (id != null) stringBuilder.append(id).append(",")
        }
        val ids = stringBuilder.toString()
        val userProfileSurveyRequest = UserProfileSurveyRequest()
        userProfileSurveyRequest.screenId = "47"
        userProfileSurveyRequest.surveyIds = ids
        userProfileSurveyRequest.serviceName = "getNextUserSurveyInfo"
        //Log.e("####", String.format("start: %d ; end: %d ; sent: %s ; total: %d", start, end, ids, surveyIdList.size()));
        //Log.e("####", String.format("sent ids: %s", ids));
        viewModel.getNextUserProfileSurvey(userProfileSurveyRequest).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserProfileSurveyResponse> ->
            if (apiResponse.loading) {
                if (allSurveyAdapter != null) {
                    allSurveyAdapter!!.addLoading()
                }
            }
            else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (allSurveyAdapter != null) {
                    allSurveyAdapter!!.removeLoading()
                }
                this@AllSurveysFragment.isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.surveyData == null || apiResponse.body.surveyData!!.isEmpty()) {
                        this@AllSurveysFragment.isLastPage = true
                        setFooterMessage()
                    }
                    else {
                        allSurveyAdapter!!.addItems(apiResponse.body.surveyData)
                        if (apiResponse.body.surveyData!!.size < PaginationListener.PAGE_ITEM_SIZE) {
                            this@AllSurveysFragment.isLastPage = true
                            setFooterMessage()
                        }
                        //Log.e("####", String.format("server: %d", apiResponse.body.getSurveyData().size()));
                    }
                }
                else {
                    this@AllSurveysFragment.isLastPage = true
                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            }
            else {
                this@AllSurveysFragment.isLoading = false
                if (allSurveyAdapter != null) {
                    allSurveyAdapter!!.removeLoading()
                }
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getNextUserProfileSurvey")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getNextUserProfileSurvey",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setFooterMessage() {
        this@AllSurveysFragment.isLoading = false
        if (allSurveyAdapter != null) {
            val footerMessage: String = if (allSurveyAdapter!!.itemCount == 0) {
                "System is facing some problem, please check back tomorrow."
            }
            else {
                "That's all for now but we add new surveys everyday. Catch us again tomorrow."
            }
            binding.rvAllSurveys.postDelayed({ allSurveyAdapter!!.addFooter(footerMessage) }, 200)
        }
    }

}