package com.onourem.android.activity.arch.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.utils.listners.LogoutListener
import com.onourem.android.activity.ui.utils.listners.ProgressListener
import dagger.android.support.DaggerFragment

abstract class AbstractBaseDefaultFragment : DaggerFragment(), ProgressListener {

    protected lateinit var navController: NavController
    protected lateinit var progressListener: ProgressListener
    protected lateinit var alertDialog: SimpleAlertDialog
    protected lateinit var logoutListener: LogoutListener
    protected lateinit var fragmentContext: Context

    @LayoutRes
    protected abstract fun layoutResource(): Int
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResource(), container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        alertDialog = SimpleAlertDialog(context)
        logoutListener = context as LogoutListener
        alertDialog.setOnLogoutListener(logoutListener)
        progressListener = context as ProgressListener
        fragmentContext = context
    }

//    override fun onAttach(activity: Activity) {
//        super.onAttach(activity)
//        alertDialog = SimpleAlertDialog(activity)
//        alertDialog.setOnLogoutListener(activity as LogoutListener)
//        progressListener = activity as ProgressListener
//        fragmentContext = activity
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
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

    protected fun showAlert(message: String?) {
        alertDialog.setOnClickListener(null)
        alertDialog.setButtonText(null)
        alertDialog.showAlert(message)
    }

    protected fun showAlert(title: String?, message: String?) {
        alertDialog.setOnClickListener(null)
        alertDialog.setButtonText(null)
        alertDialog.showAlert(title, message)
    }

    protected fun showAlert(title: String?, message: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(null)
        alertDialog.showAlert(title, message)
    }

    protected fun showAlert(message: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(null)
        alertDialog.showAlert(message)
    }

    protected fun showAlert(title: String?, message: String?, buttonText: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(buttonText)
        alertDialog.showAlert(title, message)
    }
}