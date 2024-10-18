package com.onourem.android.activity.ui.admin.main.delete

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavOptions
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogTwoActionAlertDialogBinding
import com.onourem.android.activity.databinding.DialogTwoActionAlertDialogSmallBinding
import com.onourem.android.activity.databinding.DialogUpdateTitleBinding
import com.onourem.android.activity.databinding.FragmentDeleteUserAccountBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class DeleteUserAccountFragment  //    private int counter = 0;
    : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentDeleteUserAccountBinding>() {

    override fun layoutResource(): Int {
        return R.layout.fragment_delete_user_account
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener(ViewClickListener {
            if (isValidData) {
                deleteAccountByAdminConfirmation()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun deleteAccountByAdminConfirmation() {
        val builder = AlertDialog.Builder(fragmentContext)
        val dialogViewBinding: DialogTwoActionAlertDialogSmallBinding =
            DialogTwoActionAlertDialogSmallBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogViewBinding.root)
        builder.setCancelable(false)
        val alertDialog = builder.create()
        dialogViewBinding.tvTitle.text = "Alert"
        dialogViewBinding.tvTitle.visibility = View.VISIBLE
        dialogViewBinding.tvDialogMessage.text = "Would you like to delete user '${binding.tilSource.editText!!.text}'?"
        dialogViewBinding.btnLeft.text = "Cancel"
        dialogViewBinding.btnRight.text = "Confirm"
        dialogViewBinding.btnLeft.setOnClickListener(ViewClickListener { view1: View? ->
            alertDialog.dismiss()
        })

        dialogViewBinding.btnRight.setOnClickListener(ViewClickListener { view12: View? ->
            alertDialog.dismiss()
            deleteAccountByAdmin()
        })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

    }

    private val isValidData: Boolean
        get() {
            if (TextUtils.isEmpty(
                    binding!!.tilSource.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter valid email id.")
                return false
            }

            return true
        }


    private fun deleteAccountByAdmin() {

        viewModel!!.deleteAccountByAdmin(binding.tilSource.editText!!.text.toString())
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000")) {

                        if (apiResponse.body.message != null && apiResponse.body.message != "") {
                            showAlert(apiResponse.body.message) {
                                navController.popBackStack()
                            }
                        }

                    }
                    else {
                        showAlert(apiResponse.errorMessage)
                    }

                }
                else {
                    showAlert(apiResponse.errorMessage)
                    hideProgress()
                }
            }
    }

}