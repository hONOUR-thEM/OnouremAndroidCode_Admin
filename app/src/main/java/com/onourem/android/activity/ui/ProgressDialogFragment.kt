package com.onourem.android.activity.ui

import android.os.Bundle
import com.onourem.android.activity.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerDialogFragment

class ProgressDialogFragment : DaggerDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_progress, container, false)
    }

    //    @Override
    //    public void onStart() {
    //        super.onStart();
    //        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    //        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    //            getDialog().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    //        }
    //    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun isCancelable(): Boolean {
        return false
    }
}