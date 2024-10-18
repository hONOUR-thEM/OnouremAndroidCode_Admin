package com.onourem.android.activity.ui.admin.oclub_auto_trigger.task_message

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAllActivitiesBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminActivityResponse
import com.onourem.android.activity.ui.admin.create.question_schedule.GetAdminActivityListResponse
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters.CommonActivityListAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import java.util.*
import javax.inject.Inject

class AllTaskFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAllActivitiesBinding>() {

    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var adminTaskMessageIdList: java.util.ArrayList<String>? = null

    private var formattedDateTime: String = ""

    private var adapter: CommonActivityListAdapter? = null


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.fragment_all_activities
    }

    companion object {
        fun create(): AllTaskFragment {
            val fragment = AllTaskFragment()
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
        val animator = binding.rvActivities.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.rvActivities.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.rvActivities.layoutManager = linearLayoutManager

        getTaskMessageCreatedByAdmin()

        binding.swipeRefreshLayout.setOnRefreshListener {
            getTaskMessageCreatedByAdmin()
        }

    }

    private fun getTaskMessageCreatedByAdmin() {

        viewModel.getTaskMessageCreatedByAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAdminActivityListResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    setAdapter(apiResponse.body)
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

    private fun setAdapter(getAdminActivityListResponse: GetAdminActivityListResponse) {
        isLastPage = false
        val layoutManager = LinearLayoutManager(activity)
        binding.rvActivities.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        adminTaskMessageIdList = ArrayList()
        adminTaskMessageIdList!!.addAll(getAdminActivityListResponse.adminTaskMessageIdList)
        val list: ArrayList<AdminActivityResponse> =
            getAdminActivityListResponse.adminTaskMessageResponseList as ArrayList<AdminActivityResponse>

        adapter = CommonActivityListAdapter(
            list
        ) {
            when {
                it.first == CommonActivityListAdapter.CLICK_WHOLE -> {
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
                    builder.setMessage("Do you want to delete this Triggered Task?")
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

        binding.rvActivities.adapter = adapter

        if (adapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvActivities.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@AllTaskFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AllTaskFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@AllTaskFragment.isLoading
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
//                    adapter.removeItem(position)
//                } else {
//                    showAlert(apiResponse.body.errorMessage)
//                }
//            } else {
//                hideProgress()
//                showAlert(apiResponse.errorMessage)
//            }
//        }
//    }

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
        start = adapter!!.itemCount
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
                if (adapter != null) {
                    isLoading = true
                    adapter!!.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (adapter != null) {
                    adapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.adminTaskMessageResponseList.isEmpty()) {
                        isLastPage = true;
                    } else {
                        if (adapter != null) {

                            val list: ArrayList<AdminActivityResponse> =
                                apiResponse.body.adminTaskMessageResponseList as ArrayList<AdminActivityResponse>

                            adapter!!.addItems(list as List<AdminActivityResponse>)
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
                if (adapter != null) {
                    adapter!!.removeLoading()
                }
                showAlert(apiResponse.errorMessage)
            }
        }

    }


}