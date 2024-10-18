package com.onourem.android.activity.ui.admin.main.mood

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentMoodInfoListBinding
import com.onourem.android.activity.models.MoodInfoData
import com.onourem.android.activity.models.MoodInfoResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.admin.create.adapters.ExternalPostsAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.LocalMoods
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*


class MoodInfoListFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentMoodInfoListBinding>() {

    private var selectedMoodInfo: MoodInfoData? = null
    private var moodInfoIdList: ArrayList<String>? = null
    private var moodInfoDataList: ArrayList<MoodInfoData>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var adapter: MoodInfoAdapter? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var selectedRadioButton: String = "Active"
    private lateinit var now: Calendar
    private var localMoodsMap: HashMap<String, UserExpressionList>? = null


    private var formattedDateTime: String = ""

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_mood_info_list
    }

    companion object {

        fun create(): MoodInfoListFragment {
            val fragment = MoodInfoListFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localMoodsMap = LocalMoods.getAllMoodsMap()

        layoutManager = LinearLayoutManager(activity)
        binding.rvExternalPosts.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        getMoodResponseCounsellingDataByAdmin(selectedRadioButton)

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (adapter != null) {
                adapter!!.clearData()
            }
            getMoodResponseCounsellingDataByAdmin(selectedRadioButton)
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
            }
        }

        binding.fab.setOnClickListener(ViewClickListener {
            navController.navigate(MobileNavigationDirections.actionGlobalNavUpdateMoodInfo(null))
        })

    }

    private fun refreshUI(selectedRadioButton: String) {
        getMoodResponseCounsellingDataByAdmin(selectedRadioButton)
    }

    private fun getMoodResponseCounsellingDataByAdmin(selectedRadioButton: String) {

        viewModel.getMoodResponseCounsellingDataByAdmin().observe(viewLifecycleOwner) { apiResponse: ApiResponse<MoodInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    moodInfoDataList = ArrayList()
                    moodInfoDataList!!.addAll(apiResponse.body.moodInfoDataList)

                    if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
//                        moodInfoIdList = ArrayList()
//                        moodInfoIdList!!.addAll(apiResponse.body.moodInfoDataList)

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

    private fun deleteExternalActivityByAdmin(item: Triple<Int, MoodInfoData, Int>) {

//        viewModel.deleteExternalActivityByAdmin(item.second.id!!)
//            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
//                if (apiResponse.loading) {
//                    showProgress()
//                } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                    hideProgress()
//                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        if (externalPostsAdapter != null) {
//                            externalPostsAdapter!!.removeItem(item.second)
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

    private fun setAdapter(selectedRadioButton: String) {
        isLastPage = false

        val list: ArrayList<MoodInfoData> = when (selectedRadioButton) {

            "Active" -> {
                moodInfoDataList!!.filter { it.status == "0" } as ArrayList<MoodInfoData>
            }

            else -> {
                moodInfoDataList!!.filter { it.status == "1" } as ArrayList<MoodInfoData>
            }
        }

        adapter = MoodInfoAdapter(
            list, selectedRadioButton, localMoodsMap
        ) {
            when (it.first) {
                MoodInfoAdapter.CLICK_WHOLE -> {
                    if (it.second!!.youtubeLink == "Y") {
                        val intent = Intent(context, YoutubeActivity::class.java)
                        intent.putExtra(
                            "youtubeId", it.second!!.videoId!!
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

                MoodInfoAdapter.CLICK_MEDIA -> {
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

                MoodInfoAdapter.CLICK_EDIT -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavUpdateMoodInfo(it.second))
                }

//                ExternalPostsAdapter.CLICK_NOTIFY -> {
//                    Toast.makeText(fragmentContext, "Notify Api", Toast.LENGTH_LONG).show()
//                }
//
                MoodInfoAdapter.CLICK_DELETE -> {

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
//                ExternalPostsAdapter.CLICK_UN_SCHEDULE -> {
//                    now = Calendar.getInstance()
//                    selectedMoodInfo = it.second
//                    val dpd = DatePickerDialog.newInstance(
//                        this@MoodInfoListFragment, now[Calendar.YEAR],  // Initial year selection
//                        now[Calendar.MONTH],  // Initial month selection
//                        now[Calendar.DAY_OF_MONTH] // Inital day selection
//                    )
//                    dpd.version = DatePickerDialog.Version.VERSION_2
//                    dpd.show(childFragmentManager, "Datepickerdialog")
//                    dpd.isCancelable = true
//                    dpd.minDate = now
//                    dpd.vibrate(true)                }

            }
        }

        binding.rvExternalPosts.adapter = adapter

        if (adapter != null && adapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvExternalPosts.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@MoodInfoListFragment.isLoading = true
//                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@MoodInfoListFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@MoodInfoListFragment.isLoading
            }
        })

    }

//    // print twenty element from list from the start index specified
//    private fun getDisplayElements(
//        myList: List<String>, startIndex: Int
//    ): List<String> {
//        var sub: List<String> = java.util.ArrayList()
//        sub = myList.subList(
//            startIndex, Math.min(myList.size.toLong(), startIndex + 20L).toInt()
//        )
//        return sub
//    }
//
//    private fun loadMoreGames() {
//        var start = 0
//        var end = 0
//
//        if (moodInfoIdList == null || moodInfoIdList!!.isEmpty()) {
//            isLastPage = true
//            isLoading = false
//            return
//        }
//        start = adapter!!.itemCount
//        end = moodInfoIdList!!.size
//        if (start >= end) {
//            isLastPage = true
//            isLoading = false
//            return
//        }
//        val ids: MutableList<String> = ArrayList()
//        val statusLists = getDisplayElements(
//            moodInfoIdList!!, start
//        )
//        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
//        var i = 0
//        while (i < statusLists.size) {
//            val item = statusLists[i]
//            ids.add(item)
//            i++
//        }
//        val idsChunk = Utilities.getTokenSeparatedString(ids, ",")
//
//        viewModel.getNextExternalListForAdmin(idsChunk).observe(
//            viewLifecycleOwner
//        ) { apiResponse: ApiResponse<ExternalPostResponse> ->
//            if (apiResponse.loading) {
//                if (adapter != null) {
//                    adapter!!.addLoading()
//                }
//            } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                if (adapter != null) {
//                    adapter!!.removeLoading()
//                }
//                isLoading = false
//                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                    if (apiResponse.body.externalActivityDataList.isEmpty()) {
//                        // isLastPage = true;
//                    } else {
//                        if (adapter != null) {
//
//                            val list: ArrayList<ExternalActivityData> = when (selectedRadioButton) {
//                                "Unscheduled" -> {
//                                    apiResponse.body.externalActivityDataList.filter { !it.activityTriggered } as ArrayList<ExternalActivityData>
//                                }
//                                "Triggered" -> {
//                                    apiResponse.body.externalActivityDataList.filter { it.activityTriggered } as ArrayList<ExternalActivityData>
//                                }
//                                "Active" -> {
//                                    apiResponse.body.externalActivityDataList.filter { it.status == "Y" } as ArrayList<ExternalActivityData>
//                                }
//                                else -> {
//                                    apiResponse.body.externalActivityDataList.filter { it.status == "N" } as ArrayList<ExternalActivityData>
//                                }
//                            }
//
//                            adapter!!.addItems(list as List<ExternalActivityData>)
//                            moodInfoDataList!!.addAll(list as List<ExternalActivityData>)
//                            //setDashboardDataToActivity()
//                        }
//
//                        if (apiResponse.body.externalActivityDataList.size < PaginationListener.PAGE_ITEM_SIZE) {
//                            // isLastPage = true;
////                            setFooterMessage()
//                        } else {
//                            isLastPage = false
//                        }
//                    }
//                } else {
//                    // isLastPage = true;
////                    setFooterMessage()
//                    showAlert(apiResponse.body.errorMessage)
//                }
//            } else {
//                isLoading = false
//                if (adapter != null) {
//                    adapter!!.removeLoading()
//                }
//                showAlert(apiResponse.errorMessage)
//            }
//        }
//
//    }

}