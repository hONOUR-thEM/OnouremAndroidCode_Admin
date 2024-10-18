package com.onourem.android.activity.ui.dashboard

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentDashboardNewBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import com.onourem.android.activity.ui.dashboard.watchlist.WatchListAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class DashboardNewFragment :
    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentDashboardNewBinding>(),
    OnWatchListItemClickListener<Pair<Int, UserWatchList>> {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.ic_app_logo_24)
        .error(R.drawable.ic_app_logo_24)

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var watchListAdapter: WatchListAdapter? = null
    private var ivTileIcon: ImageView? = null
    private var header: TextView? = null
    private var getGetWatchListResponse: GetWatchListResponse? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_dashboard_new
    }

    public override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateWatchList.observe(this) { show: Boolean ->
            if (show) {
                binding.tile1.root.visibility = View.GONE
                userWatchList
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    //                    viewClicksEnabled(false);
    //navController.navigate(DashboardFragmentDirections.actionNavHomeToWatchListDialogFragment(apiResponse.body, "DashboardFragment"));
    private val userWatchList: Unit
        get() {
            viewModel.getUserWatchList("")
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetWatchListResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            preferenceHelper!!.putValue(
                                Constants.KEY_GAME_POINTS,
                                apiResponse.body.gamePoint
                            )
                            setAdapter(apiResponse.body)
                            requireView().invalidate()
                            binding.tile1.root.visibility = View.VISIBLE
                            //                    viewClicksEnabled(false);
                            //navController.navigate(DashboardFragmentDirections.actionNavHomeToWatchListDialogFragment(apiResponse.body, "DashboardFragment"));
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                    }
                }
        }

    private fun init() {
        val dashboardNewFragmentArgs = DashboardNewFragmentArgs.fromBundle(requireArguments())
        getGetWatchListResponse = dashboardNewFragmentArgs.getWatchListResponse
        if (getGetWatchListResponse != null && getGetWatchListResponse!!.userWatchList != null) {
            setAdapter(getGetWatchListResponse)
        } else {
            binding.tile1.root.visibility = View.GONE
            userWatchList
        }
        ivTileIcon = binding.tile1.root.findViewById(R.id.ivTileIcon)
        header = binding.tile1.root.findViewById(R.id.tvTileSubHeader)
        val value = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION)
        val expressionList = Gson().fromJson(value, UserExpressionList::class.java)
        expressionList?.let { setMood(it) }
        ivTileIcon!!.setOnClickListener(ViewClickListener { v: View? ->
            MoodsDialogFragment.instance
                .show(requireActivity().supportFragmentManager, "MoodsDialogFragment")
        })
        header!!.setOnClickListener(ViewClickListener { v: View? ->
            MoodsDialogFragment.instance
                .show(requireActivity().supportFragmentManager, "MoodsDialogFragment")
        })
        binding.tile1.btnMoodHistory.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMoodHistory()
            )
        })
        viewModel.selectedExpression.observe(viewLifecycleOwner) { userExpressionList: UserExpressionList ->
            setMood(
                userExpressionList
            )
        }
    }

    private fun setMood(userExpressionList: UserExpressionList) {
        header!!.text = "My Mood"
//        ivTileIcon!!.setImageResource(userExpressionList.moodImage)
        Glide.with(requireActivity())
            .load(userExpressionList.moodImage)
            .apply(options)
            .into(ivTileIcon!!)
    }

    private fun setAdapter(getWatchListResponse: GetWatchListResponse?) {
        val tileRight = binding.tile1.root.findViewById<ConstraintLayout>(R.id.tile1Right)
        val tvTileHeader = tileRight.findViewById<TextView>(R.id.tvTileHeader)
        val tvTileSubHeader = tileRight.findViewById<TextView>(R.id.tvTileSubHeader)

//        tvTileHeader.setOnClickListener(new ViewClickListener(v -> navController.navigate(MobileNavigationDirections.actionGlobalNavInviteFriends())));
//        tvTileSubHeader.setOnClickListener(new ViewClickListener(v -> navController.navigate(MobileNavigationDirections.actionGlobalNavInviteFriends())));
        tvTileSubHeader.text = getString(R.string.label_points)
        tvTileSubHeader.letterSpacing = 0.1f
        tvTileSubHeader.setTextColor(Color.BLACK)
        tvTileHeader.text = getWatchListResponse!!.gamePoint
        binding.rvWatchlist.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.tvDialogTitle.text =
            binding.root.context.resources.getString(R.string.watch_list)
        val userWatchLists = ArrayList<UserWatchList>()
        if (getWatchListResponse.userWatchList != null) {
            if (getWatchListResponse.userWatchList!!.size == 0) {
                val userWatchList = UserWatchList()
                userWatchList.centerText = requireActivity().getString(R.string.add_to_watch_list)
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
                binding.tvDialogSubtext.text = getWatchListResponse.descriptionTextForWatchList
            }
        }
        watchListAdapter = WatchListAdapter(binding.root.context, userWatchLists)
        binding.rvWatchlist.adapter = watchListAdapter
        if (watchListAdapter != null) watchListAdapter!!.setOnItemClickListener(this)
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
                conversation.userOne = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
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
//                String shareMessage = String.format("Hi %s, how are you doing? Let's catch up over a phone call sometime?", item.second.getFirstName());
//                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                startActivity(Intent.createChooser(shareIntent, "Onourem"));
            }
        } else if (item.first == WatchListActions.ACCEPT_INVITATION) {
            item.second.status = "Watching"
            if (watchListAdapter != null) watchListAdapter!!.notifyItemChanged(position)
            viewModel.acceptPendingWatchRequest(item.second.userId?:"")
                .observe(viewLifecycleOwner) { standardResponseApiResponse: ApiResponse<AcceptPendingWatchResponse>? -> }
        } else if (item.first == WatchListActions.REJECT_INVITATION) {
            removeItem(position)
            viewModel.cancelWatchListRequest(item.second.userId?:"")
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse>? -> }
        } else if (item.first == WatchListActions.CANCEL_INVITATION) {
            removeItem(position)
            viewModel.cancelWatchListPendingRequest(item.second.userId?:"")
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse>? -> }
        } /* else if (item.first == WATCH_LIST_TILE) {

        } */ else if (item.first == WatchListActions.WATCH_LIST_EDIT) {
            navController.navigate(
                DashboardNewFragmentDirections.actionNavDashboardNewToNavEditAddWatchList(
                    false
                )
            )
            //            navController.popBackStack();
        } else if (item.first == WatchListActions.WATCH_LIST_ADD) {
            navController.navigate(
                DashboardNewFragmentDirections.actionNavDashboardNewToNavEditAddWatchList(
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
    }
}