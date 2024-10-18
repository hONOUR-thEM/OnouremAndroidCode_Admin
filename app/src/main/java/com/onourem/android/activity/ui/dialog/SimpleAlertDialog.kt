package com.onourem.android.activity.ui.dialog

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.DialogSimpleBinding
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.LogoutListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SimpleAlertDialog(private val context: Context) {
    private var dialog: AlertDialog? = null
    private var onClickListener: View.OnClickListener? = null
    private var buttonText: String? = null
    private var onLogoutListener: LogoutListener? = null
    private var binding: DialogSimpleBinding? = null
    private fun init() {
        val dialogBuilder = AlertDialog.Builder(
            context
        )
        binding = DialogSimpleBinding.inflate(
            LayoutInflater.from(
                context
            )
        )
        dialogBuilder.setView(binding!!.root)
        dialog = dialogBuilder.create()
        dialog?.setCancelable(false)
        binding?.tvDialogTitle?.visibility = View.GONE
    }

    fun showAlert(title: String?, message: String?) {
        if (!TextUtils.isEmpty(message)) {
            show(title?:"", message)
        }
    }

    fun showAlert(message: String?) {
        if (!TextUtils.isEmpty(message)) {
            show("", message)
        }
    }

    private fun show(title: String?, text: String?) {
        var message = text
        if (!TextUtils.isEmpty(title)) {
            binding!!.tvDialogTitle.text = title
            binding!!.tvDialogTitle.visibility = View.VISIBLE
        } else {
            binding!!.tvDialogTitle.visibility = View.GONE
        }
        
        if (!TextUtils.isEmpty(buttonText)) {
            binding!!.btnDialogOk.text = buttonText
        } else {
            binding!!.btnDialogOk.setText(R.string.label_ok)
        }
        if (message == null) {
            message = "Server Error"
        }
        val finalMessage: String = message
        binding!!.btnDialogOk.setOnClickListener(ViewClickListener(View.OnClickListener { v: View? ->
            if ("You are already login with other device. Please login again".equals(
                    finalMessage,
                    ignoreCase = true
                ) && onLogoutListener != null
            ) {
                onLogoutListener?.onLogout()
                dialog?.dismiss()
                return@OnClickListener
            }
            if (onClickListener != null) {
                onClickListener?.onClick(v)
            }
            dialog?.dismiss()
        }))
        binding!!.tvDialogMessage.text = message
        if (message.contains(context.getString(R.string.unable_to_connect_host_message))) {
            binding!!.tvDialogTitle.text = context.getString(R.string.no_internet)
            binding!!.tvDialogMessage.text = context.getString(R.string.no_internet_message)
            binding!!.tvDialogTitle.visibility = View.VISIBLE
        } else if (message.contains(context.getString(R.string.unable_to_connect_host_message1))
            || message.contains(context.getString(R.string.unable_to_connect_host_message3))
        ) {
            binding!!.tvDialogTitle.text = context.getString(R.string.network_error_title)
            binding!!.tvDialogMessage.text = context.getString(R.string.network_error_text_1)
            binding!!.tvDialogTitle.visibility = View.VISIBLE
        }
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog?.show()

        val windowParams = dialog?.window?.attributes
        windowParams?.dimAmount = Constants.DIM_AMOUNT
        windowParams?.flags = windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        //        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog?.window?.attributes = windowParams
    }

    fun setButtonText(buttonText: String?) {
        this.buttonText = buttonText
    }

    fun setOnClickListener(onClickListener: View.OnClickListener?) {
        this.onClickListener = onClickListener
    }

    fun setOnLogoutListener(onLogoutListener: LogoutListener?) {
        this.onLogoutListener = onLogoutListener
    }

    init {
        init()
    }

    fun hideDialog() {
        if (dialog != null && dialog!!.isShowing) dialog!!.cancel()
    }
}