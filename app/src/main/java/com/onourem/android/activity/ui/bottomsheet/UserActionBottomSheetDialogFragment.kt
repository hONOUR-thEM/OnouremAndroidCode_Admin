package com.onourem.android.activity.ui.bottomsheet

import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.R
import android.os.Bundle
import android.view.View
import com.onourem.android.activity.ui.bottomsheet.UserActionBottomSheetDialogFragmentArgs
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.ui.bottomsheet.ActionBottomSheetDialogAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.util.*

class UserActionBottomSheetDialogFragment :
    AbstractBaseBottomSheetDialogFragment<UserActionViewModel>(), OnItemClickListener<Action<Any?>> {
    override fun layoutResource(): Int {
        return R.layout.dialog_bottom_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        var actionBottomDialogFragmentArgs: UserActionBottomSheetDialogFragmentArgs? = null
        if (arguments != null) {
            actionBottomDialogFragmentArgs =
                UserActionBottomSheetDialogFragmentArgs.fromBundle(requireArguments())
        }
        val appCompatTextView = requireView().findViewById<AppCompatTextView>(R.id.tvTitle)
        val tvSubTitle = requireView().findViewById<AppCompatTextView>(R.id.tvSubTitle)
        val rvActions = requireView().findViewById<RecyclerView>(R.id.rvActions)
        rvActions.layoutManager = LinearLayoutManager(requireActivity())
        rvActions.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )
        if (actionBottomDialogFragmentArgs != null) {
            val actions: List<Action<*>> =
                actionBottomDialogFragmentArgs.actions.getParcelableArrayList(
                    Constants.KEY_BOTTOM_SHEET_ACTIONS
                )!!
            //actions.add(actions.size(), new Action<>(getString(R.string.label_dismiss), R.color.color_black, ActionType.DISMISS, null));
            rvActions.adapter = ActionBottomSheetDialogAdapter(actions as MutableList<Action<Any?>>, this)
        }
        if (actionBottomDialogFragmentArgs != null) {
            appCompatTextView.text = actionBottomDialogFragmentArgs.title
            if (!actionBottomDialogFragmentArgs.subTitle.equals("", ignoreCase = true)) {
                tvSubTitle.visibility = View.VISIBLE
                tvSubTitle.text = actionBottomDialogFragmentArgs.subTitle
            } else {
                tvSubTitle.visibility = View.GONE
            }
        }
    }

    override fun viewModelType(): Class<UserActionViewModel> {
        return UserActionViewModel::class.java
    }

    override fun onItemClick(action: Action<Any?>) {
        dismissAndRefresh(action)
    }

    private fun dismissAndRefresh(action: Action<Any?>) {
        if (action.actionType !== ActionType.DISMISS) {
            if (action.from == null) {
                viewModel.actionPerformed(action)
            } else {
                if (action.from.equals("MyVocals", ignoreCase = true)) {
                    viewModel.actionPerformedMyVocals(action)
                } else if (action.from.equals("Trending", ignoreCase = true)) {
                    viewModel.actionPerformedTrending(action)
                } else if (action.from.equals("Favorite", ignoreCase = true)) {
                    viewModel.actionPerformedFavorite(action)
                } else if (action.from.equals("Friends", ignoreCase = true)) {
                    viewModel.actionPerformedFriends(action)
                } else if (action.from.equals("Counselling", ignoreCase = true)) {
                    viewModel.actionPerformedCounselling(action)
                }
            }
        }
        navController.popBackStack()
    }
}