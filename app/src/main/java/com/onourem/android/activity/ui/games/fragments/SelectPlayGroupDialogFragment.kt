package com.onourem.android.activity.ui.games.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDefaultBindingDialogFragment
import com.onourem.android.activity.databinding.DialogSelectPrivacyBinding
import com.onourem.android.activity.models.ActivityGroupList
import com.onourem.android.activity.ui.games.adapters.SelectPlayGroupAdapter
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SelectPlayGroupDialogFragment :
    AbstractBaseDefaultBindingDialogFragment<DialogSelectPrivacyBinding>() {
    private var itemClickListener: OnItemClickListener<ActivityGroupList>? = null
    private var playGroupList: List<ActivityGroupList?>? = null
    override fun layoutResource(): Int {
        return R.layout.dialog_select_privacy
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bubble.root.visibility = View.GONE
        binding.tvTitle.text = "Played Multiple Times.\nSelect One"
        binding.btnDone.text = getString(R.string.label_cancel)
        binding.btnDone.setOnClickListener(ViewClickListener { v: View? -> dismiss() })
        setAdapter()
    }

    private fun setAdapter() {
        binding.rvPrivacies.layoutManager = LinearLayoutManager(requireActivity())
        val selectPlayGroupAdapter =
            SelectPlayGroupAdapter(playGroupList) { item: ActivityGroupList ->
                itemClickListener!!.onItemClick(item)
                dismiss()
            }
        binding.rvPrivacies.adapter = selectPlayGroupAdapter
    }

    companion object {
        fun getInstance(
            playGroupList: List<ActivityGroupList?>?,
            itemClickListener: OnItemClickListener<ActivityGroupList>?
        ): SelectPlayGroupDialogFragment {
            val dialogFragment: SelectPlayGroupDialogFragment = SelectPlayGroupDialogFragment()
            dialogFragment.playGroupList = playGroupList
            dialogFragment.itemClickListener = itemClickListener
            return dialogFragment
        }
    }
}