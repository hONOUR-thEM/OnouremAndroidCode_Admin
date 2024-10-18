package com.onourem.android.activity.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentDeleteAccountBinding
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class DeleteAccountFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentDeleteAccountBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_delete_account
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvMessage.text = getString(R.string.message_delete_account)
        binding.btnDeleteAccount.setOnClickListener(ViewClickListener { v: View? ->
            val loginResponse = Gson().fromJson(
                preferenceHelper!!.getString(Constants.KEY_USER_OBJECT), LoginResponse::class.java
            )
            TwoActionAlertDialog.showAlert(requireActivity(),
                null,
                "Do you really want to delete an account.",
                loginResponse,
                getString(R.string.label_cancel),
                getString(R.string.label_ok),
                OnItemClickListener { `object`: Pair<Int?, LoginResponse?> ->
                    if (TwoActionAlertDialog.ACTION_RIGHT == `object`.first) {
                        assert(`object`.second != null)
                        navController.navigate(DeleteAccountFragmentDirections.actionNavDeleteAccountToNavDeleteAccountReasons())
                    }
                })
        })
    }
}