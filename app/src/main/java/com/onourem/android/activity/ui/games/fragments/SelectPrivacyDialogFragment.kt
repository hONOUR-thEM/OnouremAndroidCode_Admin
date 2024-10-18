package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogSelectPrivacyBinding
import com.onourem.android.activity.models.GetAllGroupsResponse
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.FooterAdapter
import com.onourem.android.activity.ui.games.adapters.SelectPrivacyAdapter
import com.onourem.android.activity.ui.games.viewmodel.SelectPrivacyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SelectPrivacyDialogFragment :
    AbstractBaseBindingDialogFragment<SelectPrivacyViewModel, DialogSelectPrivacyBinding>() {
    private var adapter: SelectPrivacyAdapter? = null
    private var groupPointsMap: HashMap<String, String>? = null
    private var showPrivateOption: Boolean = false
    override fun layoutResource(): Int {
        return R.layout.dialog_select_privacy
    }

    override fun viewModelType(): Class<SelectPrivacyViewModel> {
        return SelectPrivacyViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupPointsMap =
            SelectPrivacyDialogFragmentArgs.fromBundle(requireArguments()).groupPointsMap as HashMap<String, String>?
        showPrivateOption =
            SelectPrivacyDialogFragmentArgs.fromBundle(requireArguments()).showPrivateOption
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDone.setOnClickListener(ViewClickListener(View.OnClickListener { v: View? ->
            if (adapter != null) {
                if (adapter!!.selected.isNotEmpty()) {
                    viewModel.setSelected(adapter!!.selected)
                    navController.popBackStack()
                } else {
                    showAlert("Please select privacy")
                }
            }
        }))
        viewModel.getAllGroups("18").observe(
            viewLifecycleOwner,
            Observer { apiResponse: ApiResponse<GetAllGroupsResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        val privacyGroups: MutableList<PrivacyGroup> =
                            apiResponse.body.privacyGroups as MutableList<PrivacyGroup>
                        val iterator: MutableListIterator<PrivacyGroup> =
                            privacyGroups.listIterator()
                        while (iterator.hasNext()) {
                            val privacyGroup: PrivacyGroup = iterator.next()
                            if (privacyGroup.groupName
                                    .equals("Receivers Only", ignoreCase = true)
                            ) {
                                if (!showPrivateOption) iterator.remove()
                                privacyGroup.groupName = "Private"
                                break
                            }
                        }
                        if (viewModel.selectedPrivacyMutableLiveData!!.value != null && viewModel.selectedPrivacyMutableLiveData!!.value!!.isNotEmpty()
                        ) {
                            val defaultSelected: MutableList<PrivacyGroup> = ArrayList()
                            val oldSelected: MutableList<PrivacyGroup> =
                                viewModel.selectedPrivacyMutableLiveData!!.value as MutableList<PrivacyGroup>
                            for (selectedGroup: PrivacyGroup? in oldSelected) {
                                for (group: PrivacyGroup in privacyGroups) {
                                    if (selectedGroup!!.groupId == group.groupId) {
                                        defaultSelected.add(group)
                                    }
                                }
                            }
                            oldSelected.clear()
                            oldSelected.addAll(defaultSelected)
                            setAdapter(privacyGroups, oldSelected)
                        } else {
                            val defaultSelected: MutableList<PrivacyGroup> = ArrayList()
                            for (i in privacyGroups.indices) {
                                val privacyGroup: PrivacyGroup = privacyGroups.get(i)
                                if (privacyGroup.groupId == 1) {
                                    defaultSelected.add(privacyGroup)
                                } else if (privacyGroup.groupName
                                        .equals("Receivers Only", ignoreCase = true)
                                ) {
                                    privacyGroup.groupName = "Private"
                                }
                            }
                            setAdapter(privacyGroups, defaultSelected)
                        }
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else if (!apiResponse.loading) {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if ((apiResponse.errorMessage != null
                                && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getAllGroups")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getAllGroups",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        )
    }

    private fun setAdapter(
        privacyGroups: List<PrivacyGroup>,
        defaultSelected: List<PrivacyGroup>
    ) {
        if ((groupPointsMap != null) && groupPointsMap!!.isNotEmpty() && defaultSelected.isNotEmpty()) setPrivacyBubbleCount(
            groupPointsMap!!.get(defaultSelected.get(0).groupId.toString()), getTintColor(
                defaultSelected.get(0).groupId
            )
        )
        binding.rvPrivacies.layoutManager = LinearLayoutManager(requireActivity())
        adapter = SelectPrivacyAdapter(
            privacyGroups,
            defaultSelected,
            OnItemClickListener { item: List<PrivacyGroup> ->
                var tintColor: Int = R.color.color_transparent
                if (groupPointsMap != null && item.size == 1) {
                    val groupId: Int = item.get(0).groupId
                    when (groupId) {
                        1 -> tintColor = R.color.color_blue
                        2 -> tintColor = R.color.color_pink
                        4 -> tintColor = R.color.color_green
                    }
                    setPrivacyBubbleCount(
                        groupPointsMap!!.get(item.get(0).groupId.toString()),
                        tintColor
                    )
                } else setPrivacyBubbleCount("", tintColor)
            }
        )
        val footerAdapter: FooterAdapter =
            FooterAdapter("You can create custom privacy groups from 'Privacy Groups' section in the Menu")
        val concatAdapter: ConcatAdapter = ConcatAdapter(adapter, footerAdapter)
        binding.rvPrivacies.adapter = concatAdapter
    }

    private fun getTintColor(groupId: Int): Int {
        var tintColor: Int = R.color.color_transparent
        when (groupId) {
            1 -> tintColor = R.color.color_blue
            2 -> tintColor = R.color.color_pink
            4 -> tintColor = R.color.color_green
        }
        return tintColor
    }

    @SuppressLint("RestrictedApi")
    private fun setPrivacyBubbleCount(points: String?, tintColor: Int) {
        if (TextUtils.isEmpty(points) || points.equals("0", ignoreCase = true)) {
            binding.bubble.tvQuestionPoints.visibility = View.GONE
        } else {
            val builder: SpannableStringBuilder = SpannableStringBuilder().append(points)
                .append("\n")
                .append("Points")
            builder.setSpan(
                RelativeSizeSpan(1.2f),
                0, points!!.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setSpan(
                RelativeSizeSpan(0.7f),
                points.length, builder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.bubble.tvQuestionPoints.text = builder
            binding.bubble.tvQuestionPoints.visibility = View.VISIBLE
            binding.bubble.tvQuestionPoints.supportBackgroundTintList = AppCompatResources.getColorStateList(
                requireActivity(),
                tintColor
            )
        }
    }
}