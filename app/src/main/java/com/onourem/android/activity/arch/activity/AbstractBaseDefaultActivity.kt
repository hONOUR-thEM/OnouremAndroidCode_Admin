package com.onourem.android.activity.arch.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.utils.listners.LogoutListener
import com.onourem.android.activity.ui.utils.listners.ProgressListener
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import dagger.android.support.DaggerAppCompatActivity

abstract class AbstractBaseDefaultActivity : DaggerAppCompatActivity(), ProgressListener,
    LogoutListener {
    //    private ProgressDialogFragment progressDialogFragment;
    var progressDialog: OnouremProgressDialog? = null
    private var alertDialog: SimpleAlertDialog? = null

    @LayoutRes
    protected abstract fun layoutResource(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResource())
        alertDialog = SimpleAlertDialog(this)
        alertDialog?.setOnLogoutListener(this)
        progressDialog = OnouremProgressDialog(this)

//        progressDialogFragment = new ProgressDialogFragment();
    }

    override fun showProgress() {
        progressDialog?.showDialog()
        //        if (progressDialogFragment != null && progressDialogFragment.isVisible())
//            progressDialogFragment.dismissAllowingStateLoss();
//        if (progressDialogFragment != null)
//            progressDialogFragment.show(getSupportFragmentManager(), "PROGRESS");
    }

    override fun showProgressWithText(text: String, showLineProgress: Boolean) {
        progressDialog?.showDialogWithText(text, showLineProgress)
    }

    override fun hideProgress() {
        progressDialog?.hideDialog()
        //        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
//            progressDialogFragment.dismissAllowingStateLoss();
////            progressDialogFragment = null;
//        }
    }

    protected fun showAlert(message: String?) {
        alertDialog?.setButtonText(null)
        alertDialog?.setOnClickListener(null)
        alertDialog?.showAlert(message)
    }

    protected fun showAlert(
        title: String?,
        message: String?,
        onClickListener: View.OnClickListener?
    ) {
        alertDialog?.setButtonText(null)
        alertDialog?.setOnClickListener(onClickListener)
        alertDialog?.showAlert(title!!, message)
    }

    //    protected void showAlert(String message, View.OnClickListener onClickListener) {
    //        alertDialog.setButtonText(null);
    //        alertDialog.setOnClickListener(onClickListener);
    //        alertDialog.showAlert(message);
    //    }
    protected fun showAlert(title: String?, message: String?) {
        alertDialog?.setButtonText(null)
        alertDialog?.setOnClickListener(null)
        alertDialog?.showAlert(title!!, message)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (alertDialog != null){
            alertDialog?.hideDialog()
        }
    }
}