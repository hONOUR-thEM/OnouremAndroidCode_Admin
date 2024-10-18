package com.onourem.android.activity.arch.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.LogoutListener
import com.onourem.android.activity.ui.utils.listners.ProgressListener

abstract class AbstractBaseDefaultBottomSheetDialogFragment : BaseBottomSheetDialogFragment(), ProgressListener {

    protected lateinit var navController: NavController
    protected lateinit var progressListener: ProgressListener
    protected lateinit var alertDialog: SimpleAlertDialog
    protected lateinit var logoutListener: LogoutListener
    protected lateinit var fragmentContext: Context

    protected lateinit var dialog: BottomSheetDialog
    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        progressListener = context as ProgressListener
        alertDialog = SimpleAlertDialog(context)
        logoutListener = context as LogoutListener
        alertDialog.setOnLogoutListener(logoutListener)
        fragmentContext = context
    }

    @LayoutRes
    protected abstract fun layoutResource(): Int
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResource(), container, false)
    }

    override fun showProgress() {
        progressListener.showProgress()
    }

    override fun showProgressWithText(text: String, showLineProgress: Boolean) {
        progressListener.showProgressWithText(text, showLineProgress)
    }

    override fun hideProgress() {
        progressListener.hideProgress()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
    }

    protected fun showAlert(message: String?) {
        if (message != null) {
            alertDialog.showAlert(message)
        }
    }

    // --Commented out by Inspection START (25-05-2020 11:29 PM):
    //    protected void showAlert(String title, String message) {
    //        alertDialog.showAlert(title, message);
    //    }
    // --Commented out by Inspection STOP (25-05-2020 11:29 PM)
    protected fun showAlert(message: String?, onClickListener: View.OnClickListener?) {
        alertDialog.showAlert(message)
        alertDialog.setOnClickListener(onClickListener)
    }

    // --Commented out by Inspection START (25-05-2020 11:29 PM):
    protected fun showAlert(title: String?, message: String?) {
        alertDialog.showAlert(title, message)
    }

    protected fun showAlert(message: String?,onClickListener: View.OnClickListener?, buttonText : String?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(buttonText)
        alertDialog.showAlert(message)
    }

    protected fun showAlert(title: String?, message: String?, buttonText: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(buttonText)
        alertDialog.showAlert(title, message)
    }

    override fun onStart() {
        super.onStart()
        if (getDialog() != null && getDialog()?.window != null) {
            getDialog()?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            getDialog()?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val windowParams = getDialog()?.window?.attributes
            windowParams?.dimAmount = Constants.DIM_AMOUNT
            windowParams?.flags = windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getDialog()?.window?.attributes = windowParams
        }
    }
}