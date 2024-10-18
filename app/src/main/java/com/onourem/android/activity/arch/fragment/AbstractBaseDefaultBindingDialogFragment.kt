package com.onourem.android.activity.arch.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.LogoutListener
import com.onourem.android.activity.ui.utils.listners.ProgressListener

abstract class AbstractBaseDefaultBindingDialogFragment<B : ViewDataBinding> : BaseDialogFragment(), ProgressListener {

    protected lateinit var navController: NavController
    protected lateinit var progressListener: ProgressListener
    protected lateinit var alertDialog: SimpleAlertDialog
    protected lateinit var logoutListener: LogoutListener
    protected lateinit var fragmentContext: Context
    protected lateinit var binding: B


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
    }

    //    @Override
    //    public void onAttach(@NonNull Activity activity) {
    //        super.onAttach(activity);
    //        alertDialog = new SimpleAlertDialog(activity);
    //        alertDialog.setOnLogoutListener((LogoutListener) activity);
    //        progressListener = (ProgressListener) activity;
    //        this.mContext = activity;
    //    }
//    override fun onDetach() {
//        super.onDetach()
//        alertDialog = null
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController(requireActivity(), R.id.fragmentContainerView)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResource(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val windowParams = dialog!!.window!!.attributes
            windowParams.dimAmount = Constants.DIM_AMOUNT
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dialog!!.window!!.attributes = windowParams
        }
    }

    protected fun showAlert(message: String?) {
        alertDialog.setButtonText(null)
        alertDialog.setOnClickListener(null)
        if (message != null) {
            alertDialog.showAlert(message)
        }
    }

    protected fun showAlert(message: String?, onClickListener: View.OnClickListener?, buttonText: String?) {
        alertDialog.setButtonText(buttonText)
        alertDialog.setOnClickListener(onClickListener)
        if (message != null) {
            alertDialog.showAlert(message)
        }
    }

    protected fun showAlert(message: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setButtonText(null)
        alertDialog.setOnClickListener(onClickListener)
        if (message != null) {
            alertDialog.showAlert(message)
        }
    }

    protected fun showAlert(title: String?, message: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(null)
        alertDialog.showAlert(title, message)
    }

    protected fun showAlert(title: String?, message: String?) {
        alertDialog.setButtonText(null)
        alertDialog.setOnClickListener(null)
        alertDialog.showAlert(title, message)
    }

    protected fun showAlert(title: String?, message: String?, buttonText: String?, onClickListener: View.OnClickListener?) {
        alertDialog.setOnClickListener(onClickListener)
        alertDialog.setButtonText(buttonText)
        alertDialog.showAlert(title, message)
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

//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
}