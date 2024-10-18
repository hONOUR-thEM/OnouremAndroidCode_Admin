package com.onourem.android.activity.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentSettingsBinding
import com.onourem.android.activity.models.SettingsItem
import com.onourem.android.activity.models.WebContent
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.settings.adapters.SettingsAdapter

class SettingsFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentSettingsBinding>() {

    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        val itemDecoration =
            DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                fragmentContext, R.drawable.divider
            )!!
        )
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvSettings.layoutManager = layoutManager
        binding.rvSettings.addItemDecoration(itemDecoration)
        layoutManager.orientation = RecyclerView.VERTICAL
        val settingsItemArrayList = ArrayList<SettingsItem>()
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_1),
                R.drawable.ic_profile
            )
        )
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_7),
                R.drawable.ic_settings
            )
        )
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_2),
                R.drawable.ic_block_black_24dp
            )
        )
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_3),
                R.drawable.ic_terms
            )
        )
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_4),
                R.drawable.ic_shield_new
            )
        )
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_5),
                R.drawable.ic_groups
            )
        )
        settingsItemArrayList.add(
            SettingsItem(
                getString(R.string.label_settings_row_6),
                R.drawable.ic_delete_1
            )
        )
        setAdapter(settingsItemArrayList)
    }

    private fun setAdapter(settingsItemList: ArrayList<SettingsItem>) {
        binding.rvSettings.adapter = SettingsAdapter(settingsItemList) { item: SettingsItem ->
            if (item.title.equals(getString(R.string.label_settings_row_1), ignoreCase = true)) {
                navController.navigate(SettingsFragmentDirections.actionNavSettingsToNavProfileSettings())
            } else if (item.title.equals(
                    getString(R.string.label_settings_row_7),
                    ignoreCase = true
                )
            ) {
                navController.navigate(SettingsFragmentDirections.actionNavSettingsToNavPushNotificationsSettings())
            } else if (item.title.equals(
                    getString(R.string.label_settings_row_2),
                    ignoreCase = true
                )
            ) {
                navController.navigate(SettingsFragmentDirections.actionNavSettingsToNavBlockUsersList())
            } else if (item.title.equals(
                    getString(R.string.label_settings_row_3),
                    ignoreCase = true
                )
            ) {
                val webContent = WebContent()
                webContent.property = ""
                webContent.screenId = "38"
                webContent.tnc = "Y"
                webContent.title = getString(R.string.label_settings_row_3)
                navController.navigate(
                    SettingsFragmentDirections.actionNavSettingsToNavWebContent(
                        webContent
                    )
                )
            } else if (item.title.equals(
                    getString(R.string.label_settings_row_4),
                    ignoreCase = true
                )
            ) {
                val webContent = WebContent()
                webContent.property = ""
                webContent.screenId = "40"
                webContent.tnc = "N"
                webContent.title = getString(R.string.label_settings_row_4)
                navController.navigate(
                    SettingsFragmentDirections.actionNavSettingsToNavWebContent(
                        webContent
                    )
                )
            } else if (item.title.equals(
                    getString(R.string.label_settings_row_5),
                    ignoreCase = true
                )
            ) {
                val webContent = WebContent()
                webContent.property = "communityGuidelines"
                webContent.screenId = "40"
                webContent.tnc = ""
                webContent.title = getString(R.string.label_settings_row_5)
                navController.navigate(
                    SettingsFragmentDirections.actionNavSettingsToNavWebContent(
                        webContent
                    )
                )
            } else if (item.title.equals(
                    getString(R.string.label_settings_row_6),
                    ignoreCase = true
                )
            ) {
                navController.navigate(SettingsFragmentDirections.actionNavSettingsToNavDeleteAccount())
            }
        }
    }
}