package com.onourem.android.activity.ui.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentFilterListBinding
import com.onourem.android.activity.databinding.FragmentSettingsBinding
import com.onourem.android.activity.models.SettingsItem
import com.onourem.android.activity.models.WebContent
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.settings.adapters.SettingsAdapter

class FilterListFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentFilterListBinding>() {

    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_filter_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFilter.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

        setAdapter()
    }

    private fun setAdapter() {

        when(FilterListFragmentArgs.fromBundle(requireArguments()).filterId){
            "1"->{

            }
            "2"->{

            }
            "3"->{

            }
            "4"->{

            }
        }
//        binding.rvFilter.adapter = SettingsAdapter(settingsItemList) { item: SettingsItem ->
//
//
//        }
    }
}