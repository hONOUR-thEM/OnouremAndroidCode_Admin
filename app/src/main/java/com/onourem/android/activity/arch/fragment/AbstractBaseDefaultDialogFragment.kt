package com.onourem.android.activity.arch.fragment

import android.app.Activity
import android.content.Context
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import androidx.annotation.LayoutRes
import android.os.Bundle
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.utils.listners.LogoutListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.NavController
import com.onourem.android.activity.ui.utils.listners.ProgressListener

abstract class AbstractBaseDefaultDialogFragment : BaseDialogFragment(), ProgressListener {
    protected lateinit var progressListener: ProgressListener
    protected lateinit var alertDialog: SimpleAlertDialog
    protected lateinit var fragmentContext: Context

    @LayoutRes
    protected abstract fun layoutResource(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        alertDialog = SimpleAlertDialog(context)
        alertDialog.setOnLogoutListener(context as LogoutListener)
        progressListener = context as ProgressListener
        fragmentContext = context
        //        setupBackPressListener();
    }

    //    private void setupBackPressListener() {
    //        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
    //            @Override
    //            public void handleOnBackPressed() {
    //                AbstractBaseDefaultDialogFragment.this.handleOnBackPressed();
    //            }
    //        });
    //    }
//    override fun onAttach(activity: Activity) {
//        super.onAttach(activity)
//        alertDialog = SimpleAlertDialog(activity)
//        alertDialog!!.setOnLogoutListener(activity as LogoutListener)
//        progressListener = activity as ProgressListener
//        fragmentContext = activity
//        //        setupBackPressListener();
//    }

//    override fun onDetach() {
//        super.onDetach()
//        alertDialog = null
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResource(), container, false)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
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
}