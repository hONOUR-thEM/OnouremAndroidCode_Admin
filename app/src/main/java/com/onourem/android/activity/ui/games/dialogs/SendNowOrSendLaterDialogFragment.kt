package com.onourem.android.activity.ui.games.dialogs

import android.os.Bundle
import android.util.Pair
import android.view.View
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDefaultBindingDialogFragment
import com.onourem.android.activity.databinding.DialogSendNowSendLaterBinding
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SendNowOrSendLaterDialogFragment :
    AbstractBaseDefaultBindingDialogFragment<DialogSendNowSendLaterBinding>() {
    private var onItemClickListener: OnItemClickListener<Pair<LoginDayActivityInfoList?, Int>>? =
        null
    private var activityInfoList: LoginDayActivityInfoList? = null
    private var message: String? = null
    override fun layoutResource(): Int {
        return R.layout.dialog_send_now_send_later
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDialogMessage.text = message
        val clickListener: View.OnClickListener = ViewClickListener { view1: View ->
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        activityInfoList,
                        if (view1.id == binding.btnDialogSendNow.id) 1 else 0
                    )
                )
            }
            dismissAllowingStateLoss()
        }
        binding.btnDialogSendNow.setOnClickListener(clickListener)
        binding.btnDialogSendLater.setOnClickListener(clickListener)
    }

    override fun isCancelable(): Boolean {
        return false
    }

    companion object {
        @JvmStatic
        fun getInstance(
            message: String?,
            activityInfoList: LoginDayActivityInfoList?,
            onItemClickListener: OnItemClickListener<Pair<LoginDayActivityInfoList?, Int>>?
        ): SendNowOrSendLaterDialogFragment {
            val sendNowOrSendLaterDialogFragment = SendNowOrSendLaterDialogFragment()
            sendNowOrSendLaterDialogFragment.onItemClickListener = onItemClickListener
            sendNowOrSendLaterDialogFragment.message = message
            sendNowOrSendLaterDialogFragment.activityInfoList = activityInfoList
            return sendNowOrSendLaterDialogFragment
        }
    }
}