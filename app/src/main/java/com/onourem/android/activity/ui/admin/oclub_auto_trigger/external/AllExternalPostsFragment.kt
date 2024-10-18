package com.onourem.android.activity.ui.admin.oclub_auto_trigger.external

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
import com.onourem.android.activity.databinding.FragmentAllActivitiesBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalActivityData
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalPostResponse
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters.ExternalPostListAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*


class AllExternalPostsFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAllActivitiesBinding>() {

    private var selectedExternalPost: ExternalActivityData? = null
    private var externalActivityIdList: ArrayList<String>? = null
    private var externalActivityDataList: ArrayList<ExternalActivityData>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var externalPostsAdapter: ExternalPostListAdapter? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var selectedRadioButton: String = "Unscheduled"
    private lateinit var now: Calendar


    private var formattedDateTime: String = ""

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_all_activities
    }

    companion object {

        fun create(): AllExternalPostsFragment {
            val fragment = AllExternalPostsFragment()
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

        getExternalListForAdmin()

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (externalPostsAdapter != null) {
                externalPostsAdapter!!.clearData()
            }
            getExternalListForAdmin()
        }

    }

    private fun getExternalListForAdmin() {

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

    private fun setAdapter() {
        isLastPage = false

        externalPostsAdapter = ExternalPostListAdapter(
            externalActivityDataList!!
        ) {
            when (it.first) {
                ExternalPostListAdapter.CLICK_WHOLE -> {
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

                ExternalPostListAdapter.CLICK_MEDIA -> {
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
//                ExternalPostListAdapter.CLICK_EDIT -> {
//                    navController.navigate(
//                        MobileNavigationDirections.actionGlobalNavUpdateExternalContent(
//                            it.second
//                        )
//                    )
//                }

//                ExternalPostListAdapter.CLICK_NOTIFY -> {
//                    Toast.makeText(fragmentContext, "Notify Api", Toast.LENGTH_LONG).show()
//                }

                ExternalPostListAdapter.CLICK_DELETE -> {

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

                ExternalPostListAdapter.CLICK_TRIGGER_IN_OCLUB -> {
                    navController.navigate(//nav_trigger_activities_in_oclub
                        MobileNavigationDirections.actionGlobalNavTriggerActivitiesInOclub(
                            it.second.id!!,
                            it.second.summary!!,
                            it.second.activityType!!,
                        )
                    )
                }

            }
        }

        binding.rvActivities.adapter = externalPostsAdapter

        if (externalPostsAdapter != null && externalPostsAdapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvActivities.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@AllExternalPostsFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AllExternalPostsFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@AllExternalPostsFragment.isLoading
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

                            externalPostsAdapter!!.addItems(apiResponse.body.externalActivityDataList)
//                            externalActivityDataList!!.addAll(list as List<ExternalActivityData>)
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
}