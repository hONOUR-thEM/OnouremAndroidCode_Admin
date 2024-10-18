package com.onourem.android.activity.ui.games.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.util.Pair
import com.bumptech.glide.Glide
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.DialogDynamicBannerBinding
import com.onourem.android.activity.models.CustomScreenPopup
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

object DynamicBannerAlertDialog {
    var ACTION_LEFT = 0
    var ACTION_RIGHT = 1

    //    private static CountDownTimer countDownTimer;
    private var alertDialog: AlertDialog? = null
    fun <A> showAlert(
        context: Context?,
        customScreenPopup: CustomScreenPopup,
        data: A,
        onItemClickListener: OnItemClickListener<Pair<Int, AlertDialog?>?>
    ): Dialog? {
        val binding = DialogDynamicBannerBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
            .setView(binding.root)
            .setCancelable(false)
        if (!TextUtils.isEmpty(customScreenPopup.title)) {
            binding.tvTitle.text = customScreenPopup.title
            binding.tvTitle.visibility = View.VISIBLE
        } else {
            binding.tvTitle.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(customScreenPopup.txtMessage)) {
            binding.tvMessage.text = customScreenPopup.txtMessage
        } else {
            binding.tvMessage.visibility = View.GONE
        }
        Glide.with(context!!)
            .load(customScreenPopup.drawableImage)
            .placeholder(R.drawable.ic_app_logo)
            .into(binding.ivDynamicImage)
        if (customScreenPopup.btnOneText != null) {
            binding.btn1.text = customScreenPopup.btnOneText
            binding.btn1.setOnClickListener(ViewClickListener { v: View? ->
                // countDownTimer.cancel();
                onItemClickListener.onItemClick(Pair.create(ACTION_LEFT, alertDialog))
            })
        } else {
            binding.btn1.visibility = View.GONE
        }
        if (customScreenPopup.btnTwoText != null) {
            binding.btn2.text = customScreenPopup.btnTwoText
            binding.btn2.setOnClickListener(ViewClickListener { v: View? ->
                // countDownTimer.cancel();
                onItemClickListener.onItemClick(Pair.create(ACTION_RIGHT, alertDialog))
            })
        } else {
            binding.btn2.visibility = View.GONE
        }
        binding.cvClose.setOnClickListener(ViewClickListener { v: View? ->
            // countDownTimer.cancel();
            alertDialog!!.dismiss()
        })

//        countDownTimer = new CountDownTimer(15000, 1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                binding.tvCounter.setText(String.format("This banner will be auto closed in %s seconds", millisUntilFinished / 1000));
//            }
//
//            @Override
//            public void onFinish() {
//                if (alertDialog.isShowing())
//                    alertDialog.dismiss();
//            }
//        }.start();
        alertDialog = builder.show()
        return alertDialog
    }
}