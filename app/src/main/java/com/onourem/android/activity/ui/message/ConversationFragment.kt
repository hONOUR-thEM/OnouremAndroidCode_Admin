package com.onourem.android.activity.ui.message

import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentConversationsBinding
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.GetConversationsResponse
import com.onourem.android.activity.models.Message
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.message.adapter.ConversationsAdapter
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Events.SERVICE_EVENT
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject

class ConversationFragment :
    AbstractBaseViewModelBindingFragment<ConversationsViewModel, FragmentConversationsBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginUserId: String? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val conversationIdsList: MutableList<Int>? = null
    private var isLastPage = false
    private var isLoading = false
    private var conversationsAdapter: ConversationsAdapter? = null
    private var conversationList: MutableList<Conversation>? = null
    private val isReadyToMove = true
    private var isLoadingData = true
    private var isLoadingDataFromEvent = false
    private var conversationFromDetailsScreen: Conversation? = null
    private var counter = 0
    private var mView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
////        val item = menu.findItem(R.id.search_nav)
////        if (item != null) item.isVisible = false
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }


    override fun layoutResource(): Int {
        return R.layout.fragment_conversations
    }

    override fun viewModelType(): Class<ConversationsViewModel> {
        return ConversationsViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.search_nav)?.setVisible(false)
                menu.findItem(R.id.profile_nav)?.setVisible(false)
                menu.findItem(R.id.phone_nav)?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        conversationList = ArrayList()
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        binding.swipeRefreshLayout.setOnRefreshListener {
            conversationIdsList?.clear()
            binding.swipeRefreshLayout.isRefreshing = true
            conversationsAdapter!!.clearData()
            loadData(false)
        }
        linearLayoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        binding.rvConversations.addItemDecoration(VerticalSpaceItemDecoration(15))
        binding.rvConversations.layoutManager = linearLayoutManager

//        enableSwipeToDeleteAndUndo();
        binding.rvConversations.addOnScrollListener(object :
            PaginationListener(linearLayoutManager!!) {
            override fun loadMoreItems() {
                this@ConversationFragment.isLoading = true
                loadMoreConversations()
            }

            override fun isLastPage(): Boolean {
                return this@ConversationFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ConversationFragment.isLoading
            }
        })
        binding.fab.shrink()
        binding.rvConversations.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) binding.fab.visibility =
                    View.GONE else if (dy > 0) binding.fab.visibility = View.VISIBLE
            }
        })
        binding.fab.setOnClickListener(ViewClickListener { v: View? ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavFriendPicker())
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })
        val conversation = ConversationFragmentArgs.fromBundle(requireArguments()).conversation
        if (conversation.id != null && conversation.id.equals("EMPTY", ignoreCase = true)) {
            if (counter == 0) {
                counter++
                navController.navigate(
                    ConversationFragmentDirections.actionNavConversationsToNavConversationDetails(
                        conversation
                    )
                )
            } else {
                counter++
                if (!isLoadingDataFromEvent) {
                    if (conversationFromDetailsScreen != null) {
                        updateUserMessageReadStatus(conversationFromDetailsScreen!!, "onDestroy")
                    } else {
                        loadData(true)
                    }
                }
            }
        } else {
            if (mView != null) {
                SERVICE_EVENT.observe(viewLifecycleOwner) { message: Message ->
                    if (!isLoadingData) {
                        isLoadingDataFromEvent = true
                        if (message.blockedUser != null && message.blockedUser.equals(
                                "true",
                                ignoreCase = true
                            )
                        ) {
                            loadData(false)
                        } else {
                            view.postDelayed({
                                if (conversationFromDetailsScreen != null) {
                                    updateUserMessageReadStatus(
                                        conversationFromDetailsScreen!!,
                                        "onDestroy"
                                    )
                                } else {
                                    loadData(false)
                                }
                            }, 500)
                        }
                    }
                }
                if (!isLoadingDataFromEvent) {
                    if (conversationFromDetailsScreen != null) {
                        updateUserMessageReadStatus(conversationFromDetailsScreen!!, "onDestroy")
                    } else {
                        loadData(true)
                    }
                }

                viewModel.conversationReadStatus.observe(viewLifecycleOwner) { item: Conversation? ->
                    if (item != null) {
                        viewModel.updateConversationReadStatusConsumed()
                        conversationFromDetailsScreen = item
                        updateUserMessageReadStatus(item, "onDestroy");
                    }
                }
            }

        }
    }

    private fun loadMoreConversations() {}

    //    private void delete(Conversation item) {
    //
    //        viewModel.deleteConversation(String.valueOf(item.getId())).observe(this, apiResponse -> {
    //
    //            if (apiResponse.loading) {
    ////                showProgress();
    //            } else if (apiResponse.isSuccess() && apiResponse.body != null) {
    ////                hideProgress();
    //                if (apiResponse.body.getErrorCode().equalsIgnoreCase("000")) {
    //                    isReadyToMove = true;
    //                    if (conversationsAdapter != null && isVisible())
    //                        conversationsAdapter.notifyDataSetChanged();
    //                } else {
    ////                    showAlert(getString(R.string.label_network_error), apiResponse.body.getErrorMessage());
    //                }
    //            } else {
    ////                hideProgress();
    ////                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage);
    //                if (apiResponse.errorMessage != null
    //                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
    //                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))) {
    //                    if (BuildConfig.DEBUG) {
    //                        AppUtilities.showLog("Network Error", "deleteNotification");
    //                    }
    //                    if (getFragmentContext() != null)
    //                        ((DashboardActivity) getFragmentContext()).addNetworkErrorUserInfo("deleteNotification", String.valueOf(apiResponse.code));
    //                }
    //            }
    //        });
    //    }
    private fun loadData(showProgress: Boolean) {
        if (mView != null) {
            viewModel.conversations()
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetConversationsResponse> ->
                    if (apiResponse.loading) {
                        if (showProgress) {
                            showProgress()
                        }
                        isLoadingData = true
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        isLoadingData = false
                        if (!showProgress) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        binding.tvMessage.visibility = View.GONE
                        isLoading = false
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            conversationList!!.clear()
                            conversationList!!.addAll(apiResponse.body.conversationList!!)
                            setAdapter()
                            updateUserChatNotificationReadStatus()
                        } else {
                            binding.tvMessage.visibility = View.VISIBLE
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        isLoadingData = false
                        hideProgress()
                        if (!showProgress) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null
                            && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getConversations")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "getConversations",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
        }
    }

    private fun updateUserMessageReadStatus(conversation: Conversation, isFrom: String) {
        if (mView != null) {
            if (conversation.id != null && !conversation.id.equals(
                    "",
                    ignoreCase = true
                ) && !conversation.id.equals("EMPTY", ignoreCase = true)
            ) {
                viewModel.updateUserMessageReadStatus(conversation.id!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.loading) {
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (isFrom.equals("Adapter", ignoreCase = true)) {
                                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_conversations) {
                                    navController.navigate(
                                        ConversationFragmentDirections.actionNavConversationsToNavConversationDetails(
                                            conversation
                                        )
                                    )
                                }

                            } else if (isFrom.equals("onDestroy", ignoreCase = true)) {
                                conversationFromDetailsScreen = null
                                loadData(false)
                            }
                        } else {
                            hideProgress()
                        }
                    }
            }
        }
    }

    private fun updateUserChatNotificationReadStatus() {
        if (mView != null) {
            viewModel.updateUserChatNotificationReadStatus()
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                    } else {
                        hideProgress()
                    }
                }
        }
    }

    private fun setAdapter() {
        if (conversationList == null || conversationList!!.isEmpty()) {
            binding.tvMessage.visibility = View.VISIBLE
            isLastPage = true
        } else {
            binding.tvMessage.visibility = View.GONE
        }
        conversationsAdapter =
            ConversationsAdapter(conversationList!!) { item: Pair<Int, Conversation> ->
                if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                    if (Objects.requireNonNull(item.second.readStatus).equals("1", ignoreCase = true)) {
                        updateUserMessageReadStatus(item.second, "Adapter")
                    } else {
                        navController.navigate(
                            ConversationFragmentDirections.actionNavConversationsToNavConversationDetails(
                                item.second
                            )
                        )
                    }
                } else {
                    (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
                }
            }
        binding.rvConversations.adapter = conversationsAdapter
    }
}