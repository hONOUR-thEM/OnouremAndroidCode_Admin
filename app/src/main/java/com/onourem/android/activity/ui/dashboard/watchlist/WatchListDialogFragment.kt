package com.onourem.android.activity.ui.dashboard.watchlist

import android.content.DialogInterface
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogWatchListBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class WatchListDialogFragment :
    AbstractBaseDialogBindingFragment<DashboardViewModel, DialogWatchListBinding>(),
    OnWatchListItemClickListener<Pair<Int, UserWatchList>> {
    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    private var watchListAdapter: WatchListAdapter? = null

    //    private DashboardViewModel dashboardViewModel;
    private var from = ""
    override fun layoutResource(): Int {
        return R.layout.dialog_watch_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val watchListDialogFragmentArgs = WatchListDialogFragmentArgs.fromBundle(
            requireArguments()
        )
        from = watchListDialogFragmentArgs.from
        binding.tvDialogTitle.text = view.context.resources.getString(R.string.watch_list)
        binding.clParent.setOnClickListener(ViewClickListener { v: View? -> navController.popBackStack() })
        binding.rvWatchlist.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        val getWatchListResponse = watchListDialogFragmentArgs.watchList
        val userWatchLists = ArrayList<UserWatchList>()
        if (getWatchListResponse.userWatchList != null) {
            if (getWatchListResponse.userWatchList!!.size == 0) {
                val userWatchList = UserWatchList()
                userWatchList.centerText =
                    requireActivity().getString(R.string.add_to_watch_list)
                userWatchLists.add(userWatchList)
                binding.tvDialogSubtext.text =
                    getWatchListResponse.descriptionTextForWatchListWhenZeroWatching
            } else {
                for (userWatchList in getWatchListResponse.userWatchList!!) {
                    userWatchList.centerText = ""
                    userWatchLists.add(userWatchList)
                }
                val userWatchList = UserWatchList()
                userWatchList.centerText = requireActivity().getString(R.string.edit_watch_list)
                userWatchLists.add(userWatchList)
                binding.tvDialogSubtext.text =
                    getWatchListResponse.descriptionTextForWatchList
            }
        }
        watchListAdapter = WatchListAdapter(requireActivity(), userWatchLists)
        binding.rvWatchlist.adapter = watchListAdapter
        if (watchListAdapter != null) watchListAdapter!!.setOnItemClickListener(this)
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun isCancelable(): Boolean {
        return true
    }

    override fun onItemClick(item: Pair<Int, UserWatchList>, position: Int) {

        /*if (item.first == ADD_TO_WATCH_LIST) {
            viewModel.addUserToWatchList(item.second.getUserId()).observe(getViewLifecycleOwner(), standardResponseApiResponse -> {
                updateAdapter(position);
            });

        } else*/
        if (item.first == WatchListActions.WHOLE_ITEM) {
            if (item.second.status.equals("Watching", ignoreCase = true)) {
                val shareMessage = String.format(
                    "Hi %s, I just saw you felt" + " " + item.second.expressionName!!.lowercase(
                        Locale.getDefault()
                    ) + ". What made you feel that way?", item.second.firstName
                )
                val conversation = Conversation()
                conversation.id = "EMPTY"
                conversation.userName = item.second.firstName + " " + item.second.lastName
                conversation.userOne =
                    sharedPreferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                conversation.userTwo = item.second.userId
                conversation.profilePicture = item.second.profilePictureUrl
                conversation.userTypeId = item.second.userTypeId
                conversation.userMessageFromWatchlist = shareMessage
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavConversations(
                        conversation
                    )
                )
                //                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem");
//                String shareMessage = String.format("Hi %s, I just saw you felt" + " " + item.second.getExpressionName().toLowerCase() + ". What made you feel that way?" , item.second.getFirstName());
//                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                startActivity(Intent.createChooser(shareIntent, "Onourem"));
            }
        } else if (item.first == WatchListActions.ACCEPT_INVITATION) {
            item.second.status = "Watching"
            if (watchListAdapter != null) watchListAdapter!!.notifyItemChanged(position)
            viewModel.acceptPendingWatchRequest(item.second.userId!!)
                .observe(viewLifecycleOwner) { standardResponseApiResponse: ApiResponse<AcceptPendingWatchResponse>? -> }
        } else if (item.first == WatchListActions.REJECT_INVITATION) {
            removeItem(position)
            viewModel.cancelWatchListRequest(item.second.userId!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse>? -> }
        } else if (item.first == WatchListActions.CANCEL_INVITATION) {
            removeItem(position)
            viewModel.cancelWatchListPendingRequest(item.second.userId!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse>? -> }
        } /* else if (item.first == WATCH_LIST_TILE) {

        } */ else if (item.first == WatchListActions.WATCH_LIST_EDIT) {
            navController.navigate(
                WatchListDialogFragmentDirections.actionNavWatchListToNavEditAddWatchList(
                    false
                )
            )
            //            navController.popBackStack();
        } else if (item.first == WatchListActions.WATCH_LIST_ADD) {
            navController.navigate(
                WatchListDialogFragmentDirections.actionNavWatchListToNavEditAddWatchList(
                    true
                )
            )
            //            navController.popBackStack();
        } else {
            throw IllegalStateException("Unexpected value: " + item.first)
        }
    }

    private fun removeItem(position: Int) {
        if (watchListAdapter != null) {
            if (watchListAdapter!!.itemCount == 2) {
                val userWatchList = UserWatchList()
                userWatchList.centerText = requireActivity().getString(R.string.add_to_watch_list)
                watchListAdapter!!.modifyItem(1, userWatchList)
            }
            watchListAdapter!!.removeItem(position)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.setCallShowBadges(true)
        if (from.equals("NotificationsFragment", ignoreCase = true)) {
            viewModel.setNotificationInfoListMutableLiveData(from)
        }
    }
}