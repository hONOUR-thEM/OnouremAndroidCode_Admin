package com.onourem.android.activity.ui.admin.oclub_auto_trigger.questions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAllActivitiesBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminActivityResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters.CommonActivityListAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener


class AllQuestionsFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAllActivitiesBinding>() {

    private lateinit var layoutManager: LinearLayoutManager
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var idList: ArrayList<String>? = null

    companion object {
        fun create(): AllQuestionsFragment {
            val fragment = AllQuestionsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var futureQuestionListAdapter: CommonActivityListAdapter? = null

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_all_activities
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAdminQuestionsByDate()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.rvActivities.adapter = null

            getAdminQuestionsByDate()
        }


        layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding.rvActivities.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.rvActivities.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.rvActivities.layoutManager = layoutManager

        getAdminQuestionsByDate()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshUI()
        }

    }

    private fun refreshUI() {
        getAdminQuestionsByDate()
    }

    private fun getAdminQuestionsByDate() {

        viewModel.getActivityCreatedByAdmin("", "1toM")
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
        isLastPage = false

        idList = ArrayList()
        if (getAdminActivityListResponse.adminActivityIdList != null) {
            idList?.addAll(getAdminActivityListResponse.adminActivityIdList)
        }
        val list: MutableList<AdminActivityResponse>? = getAdminActivityListResponse.adminActivityResponseList

        futureQuestionListAdapter =
            list?.let { adminActivityResponses ->
                CommonActivityListAdapter(
                    adminActivityResponses
                ) {
                    when {
                        it.first == CommonActivityListAdapter.CLICK_WHOLE -> {
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

                        it.first == CommonActivityListAdapter.CLICK_TRIGGER_IN_OCLUB -> {
                            navController.navigate(//nav_trigger_activities_in_oclub
                                MobileNavigationDirections.actionGlobalNavTriggerActivitiesInOclub(
                                    it.second.activityId!!,
                                    it.second.activityText!!,
                                    it.second.activityType!!,
                                )
                            )
                        }

                        it.first == CommonActivityListAdapter.CLICK_DELETE && !it.second.activityTriggered -> {

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
            }

        binding.rvActivities.adapter = futureQuestionListAdapter

        if (futureQuestionListAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvActivities.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@AllQuestionsFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AllQuestionsFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@AllQuestionsFragment.isLoading
            }
        })

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

        if (idList == null || idList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            return
        }
        start = futureQuestionListAdapter!!.itemCount
        end = idList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            return
        }
        val ids: MutableList<String> = java.util.ArrayList()
        val statusLists = getDisplayElements(
            idList!!, start
        )
        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
        var i = 0
        while (i < statusLists.size) {
            val item = statusLists[i]
            ids.add(item)
            i++
        }
        val idsChunk = Utilities.getTokenSeparatedString(ids, ",")

        viewModel.getNextActivityCreatedByAdmin(idsChunk, "1toM").observe(
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
                    if (apiResponse.body.adminActivityResponseList.isEmpty()) {
                        isLastPage = true;
                    } else {
                        if (futureQuestionListAdapter != null) {
                            futureQuestionListAdapter!!.addItems(apiResponse.body.adminActivityResponseList)
                        }

                        isLastPage = apiResponse.body.adminActivityResponseList.size < PaginationListener.PAGE_ITEM_SIZE

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