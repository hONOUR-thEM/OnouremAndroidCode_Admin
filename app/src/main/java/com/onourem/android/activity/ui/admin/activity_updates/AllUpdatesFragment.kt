package com.onourem.android.activity.ui.admin.activity_updates

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentAllUpdatesBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.activity_updates.adapters.ActivityInfoAdapter
import com.onourem.android.activity.ui.admin.activity_updates.models.ActivityInfoResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.*
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import javax.inject.Inject

class AllUpdatesFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAllUpdatesBinding>() {

    private var layoutManager: LinearLayoutManager? = null
    private var activityInfoAdapter: ActivityInfoAdapter? = null


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    companion object {
        fun create(): AllUpdatesFragment {
            return AllUpdatesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener {

        }

        loadData()

    }

    override fun layoutResource(): Int {
        return R.layout.fragment_all_updates
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    private fun loadData() {
        setAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() {

        layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding.rvUpdates.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.rvUpdates.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.rvUpdates.layoutManager = layoutManager

        val list = ArrayList<ActivityInfoResponse>()

        list.add(
            ActivityInfoResponse(
                "",
                false,
                false,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "20",
                "All",
                "Solo",
                "Y"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                false,
                false,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "20",
                "All",
                "Solo",
                "Y"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                true,
                false,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "60",
                "Fun",
                "O-Club",
                "Y"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                true,
                true,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "30",
                "Family",
                "O-Club",
                "N"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                false,
                false,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "40",
                "Classmate",
                "O-Club",
                "Y"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                false,
                true,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "50",
                "All",
                "O-Club",
                "N"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                true,
                true,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "20",
                "All",
                "Solo",
                "Y"
            )
        )
        list.add(
            ActivityInfoResponse(
                "",
                false,
                false,
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "Fri, 15 Sep 2023",
                "20",
                "All",
                "Solo",
                "Y"
            )
        )

        activityInfoAdapter = ActivityInfoAdapter(list) {

        }

        binding.rvUpdates.adapter = activityInfoAdapter


    }
}