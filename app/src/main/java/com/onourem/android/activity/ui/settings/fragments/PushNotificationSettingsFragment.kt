package com.onourem.android.activity.ui.settings.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPushNotificationSettingsBinding
import com.onourem.android.activity.models.GetAllNotificationAlertSettingsResponse
import com.onourem.android.activity.models.PushSettingsItem
import com.onourem.android.activity.models.UpdateNotificationAlertSettingsResponse
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.settings.adapters.PushSettingsAdapter
import com.onourem.android.activity.ui.utils.AppUtilities

class PushNotificationSettingsFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentPushNotificationSettingsBinding>() {
    private var pushSettingsTimeItemArrayList: ArrayList<PushSettingsItem>? = null
    private var pushSettingsAdapter: PushSettingsAdapter? = null
    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_push_notification_settings
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        pushSettingsTimeItemArrayList = ArrayList()
        viewModel.getActionMutableLiveData()
            .observe(viewLifecycleOwner) { time: String? ->
                if (time != null) {
                    viewModel.actionConsumed()
                    if (pushSettingsTimeItemArrayList != null) {
                        pushSettingsTimeItemArrayList!!.clear()
                        pushSettingsTimeItemArrayList!!.add(
                            PushSettingsItem(
                                "For all the push notifications that you turn off in the above section, you will receive only one summary push notification at this time.",
                                "time",
                                time,
                                false,
                                false
                            )
                        )
                        pushSettingsAdapter!!.notifyDataSetChanged()
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        viewModel.allNotificationAlertSettings().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetAllNotificationAlertSettingsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    val itemDecoration =
                        DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
                    itemDecoration.setDrawable(
                        AppCompatResources.getDrawable(
                            fragmentContext, R.drawable.divider
                        )!!
                    )
                    val layoutManager = LinearLayoutManager(requireActivity())
                    layoutManager.orientation = RecyclerView.VERTICAL
                    binding.includePushSwitch.includeRecyclerView.layoutManager =
                        layoutManager
                    binding.includePushSwitch.includeRecyclerView.addItemDecoration(
                        itemDecoration
                    )
                    val layoutManager2 = LinearLayoutManager(requireActivity())
                    layoutManager2.orientation = RecyclerView.VERTICAL
                    binding.includeTime.includeRecyclerView.layoutManager = layoutManager2
                    binding.includePushSwitch.materialTextView.visibility = View.VISIBLE
                    binding.includePushSwitch.materialTextView.text =
                        "Get Immediate Notifications When :"
                    binding.includeTime.materialTextView.visibility = View.VISIBLE
                    binding.includeTime.materialTextView.text =
                        "A Daily Summary Notification :"
                    setAdapterPushSwitch(apiResponse.body)
                    setAdapterPushTime(apiResponse.body)
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        apiResponse.body.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getAllNotificationAlertSettings")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getAllNotificationAlertSettings",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setAdapterPushTime(body: GetAllNotificationAlertSettingsResponse?) {
        pushSettingsTimeItemArrayList!!.add(
            PushSettingsItem(
                "For all the push notifications that you turn off in the above section, you will receive only one summary push notification at this time.",
                "time",
                body!!.notficationPreferredTime!!!!,
                false,
                false
            )
        )
        pushSettingsAdapter =
            PushSettingsAdapter(pushSettingsTimeItemArrayList!!) { item: PushSettingsItem ->
                if ("time" == item.typeOfAlert) {
                    setPreferredTime()
                }
            }
        binding.includeTime.includeRecyclerView.adapter = pushSettingsAdapter
    }

    private fun isSwitchOnYorN(switchOnOff: String?): Boolean {
        var switchStatus = false
        if (!TextUtils.isEmpty(switchOnOff)) {
            switchStatus = switchOnOff.equals("Y", ignoreCase = true)
        }
        return switchStatus
    }

    private fun getSwitchOnYorN(switchOnOff: Boolean): String {
        var status = "N"
        status = if (switchOnOff) {
            "Y"
        } else {
            "N"
        }
        return status
    }

    private fun setAdapterPushSwitch(body: GetAllNotificationAlertSettingsResponse?) {
        val settingsItemArrayList = ArrayList<PushSettingsItem>()
        settingsItemArrayList.add(
            PushSettingsItem(
                "You receive friend request",
                "newFriendRequest",
                body!!.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.newFriendRequest)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "A question is asked to you",
                "questionAsked",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.questionAsked)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "A friend answers a question you have played",
                "questionAnswered",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.questionAnswered)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "A friend answers a question you have not played",
                "toAllFriendsNotAnsweredInSolo",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.toAllFriendsNotAnsweredInSolo)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "Non-friend O-Club member answers a question in O-Club",
                "nonFriendAnswerInOClub",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.nonFriendAnswerInOClub)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "Someone comments on my content",
                "friendAndStrangerCommOnMycontent",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.friendAndStrangerCommOnMycontent)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "Friend comments on a friend content",
                "friendCommentingOnFriendAnswer",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.friendCommentingOnFriendAnswer)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "Friend comments on content I have commented on",
                "friendCommentingOnAnswerIhaveCommented", body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.friendCommentingOnAnswerIhaveCommented)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "Friend comments on content of people I don't know",
                "friendCommentingOnStrangerAnswer",
                body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.friendCommentingOnStrangerAnswer)
            )
        )
        settingsItemArrayList.add(
            PushSettingsItem(
                "Non-friend O-Club member comments on a Friend's content",
                "nonFriendCommentingOnFriendanswerInOClub", body.notficationPreferredTime!!,
                true,
                isSwitchOnYorN(body.getAllNotificationAlertSettings!!.nonfriendCommentingOnFriendanswerInOclub)
            )
        )
        binding.includePushSwitch.includeRecyclerView.adapter =
            PushSettingsAdapter(settingsItemArrayList) { item: PushSettingsItem ->
                when (item.typeOfAlert) {
                    "newFriendRequest", "questionAsked", "questionAnswered", "toAllFriendsNotAnsweredInSolo", "nonFriendAnswerInOClub", "friendAndStrangerCommOnMycontent", "friendCommentingOnFriendAnswer", "friendCommentingOnAnswerIhaveCommented", "friendCommentingOnStrangerAnswer", "nonFriendCommentingOnFriendanswerInOClub" -> setValueForSwitch(
                        item
                    )
                }
            }
    }

    private fun setPreferredTime() {
        navController.navigate(PushNotificationSettingsFragmentDirections.actionNavPushNotificationsSettingsToNavTimePicker())
    }

    private fun setValueForSwitch(item: PushSettingsItem) {
        viewModel.updateNotificationAlertSettings(
            item.typeOfAlert,
            getSwitchOnYorN(item.isValueOfAlert)
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UpdateNotificationAlertSettingsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                when {
                    apiResponse.body.errorCode.equals("000", ignoreCase = true) -> {
                    }
                    else -> {
                        showAlert(
                            getString(R.string.label_network_error),
                            apiResponse.body.errorMessage
                        )
                    }
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "updateNotificationAlertSettings")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "updateNotificationAlertSettings",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }
}