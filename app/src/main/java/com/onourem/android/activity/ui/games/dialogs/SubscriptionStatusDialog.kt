package com.onourem.android.activity.ui.games.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.util.Pair
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.DialogSubscriptionStatusBinding
import com.onourem.android.activity.databinding.DialogWhastappAlertDialogBinding
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

object SubscriptionStatusDialog {
    var ACTION_LEFT = 0
    var ACTION_RIGHT = 1
    fun <A> showAlert(
        context: Context?, title: String?, message: String, data: A, buttonLeftLabel: String,
        buttonRightLabel: String, onItemClickListener: OnItemClickListener<Pair<Int, A>?>
    ): Dialog {
        val binding = DialogSubscriptionStatusBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
            .setView(binding.root)
            .setCancelable(false)
        if (!TextUtils.isEmpty(title)) {
            binding.tvTitle.text = title
            binding.tvTitle.visibility = View.VISIBLE
        } else {
            binding.tvTitle.visibility = View.GONE
        }
        binding.tvDialogMessage.text = message
        binding.btnLeft.text = buttonLeftLabel
        binding.btnRight.text = buttonRightLabel
        val alertDialog = builder.show()
        binding.btnLeft.setOnClickListener(ViewClickListener { view1: View? ->
            onItemClickListener.onItemClick(Pair.create(ACTION_LEFT, data))
            alertDialog.dismiss()
        })
        binding.btnRight.setOnClickListener(ViewClickListener { view1: View? ->
            onItemClickListener.onItemClick(Pair.create(ACTION_RIGHT, data))
            alertDialog.dismiss()
        })
        return alertDialog
    }
}