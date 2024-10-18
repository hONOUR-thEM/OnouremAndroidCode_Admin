package com.onourem.android.activity.ui.message

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.util.Pair
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentConversationDetailsBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.message.adapter.MessageAdapter
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.Events.SERVICE_EVENT
import com.onourem.android.activity.ui.utils.listners.MessagePaginationListener
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.swipe_refresh.SwipyRefreshLayoutDirection
import java.util.*
import javax.inject.Inject

class ConversationDetailsFragment :
    AbstractBaseViewModelBindingFragment<ConversationsViewModel, FragmentConversationDetailsBinding>() {
    private var counter: Int = 0

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginUserId: String? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val conversationIdsList: List<Int>? = null
    private var isLastPage = false
    private var isLoading = false
    private var messageList: MutableList<Message>? = null
    private var messageAdapter: MessageAdapter? = null
    private var mConversation: Conversation? = null
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var messageIdList: MutableList<Int>? = null
    private var displayNumberOfMessages = 20
    private var isReadyToMove = true
    private var userActionViewModel: UserActionViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]
        setHasOptionsMenu(true)
    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
////        val item = menu.findItem(R.id.search_nav)
////        if (item != null) item.isVisible = false
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }

    override fun layoutResource(): Int {
        return R.layout.fragment_conversation_details
    }

    override fun viewModelType(): Class<ConversationsViewModel> {
        return ConversationsViewModel::class.java
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        messageIdList = ArrayList()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mConversation = ConversationDetailsFragmentArgs.fromBundle(requireArguments()).conversation

        if (mConversation?.query != null && mConversation?.query!!.isNotEmpty()){
            binding.tvQuery.visibility = View.VISIBLE
            binding.tvQuery.text = mConversation?.query

            binding.etMessage.setText("This is your query : \n\n\"${mConversation?.query}\"")

        }else{
            binding.tvQuery.visibility = View.GONE
        }

        messageList = ArrayList()
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        if (mConversation?.userMessageFromWatchlist != null && !mConversation?.userMessageFromWatchlist.equals(
                "",
                ignoreCase = true
            )
        ) {
            binding.etMessage.setText(mConversation?.userMessageFromWatchlist)
        }
        SERVICE_EVENT.observe(viewLifecycleOwner) { message: Message? ->
            if (message != null) {
                if (mConversation?.id != null && !mConversation?.id.equals(
                        "EMPTY",
                        ignoreCase = true
                    )
                ) {
                    if (message.conversationId.equals(mConversation?.id, ignoreCase = true)) {
                        if (message.blockedUser != null && message.blockedUser.equals(
                                "true",
                                ignoreCase = true
                            )
                        ) {
                            navController.popBackStack()
                        }
                        else {
                            if (message.conversationId.equals(
                                    mConversation?.id,
                                    ignoreCase = true
                                )
                            ) {
                                addItem(message, "SilentMessage")
                            }
                        }
                    }
                }
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener { direction: SwipyRefreshLayoutDirection? ->
            if (messageIdList != null) messageIdList!!.clear()
            binding.swipeRefreshLayout.isRefreshing = true
            messageAdapter!!.clearData()
            loadData(false)
        }
        linearLayoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        //        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager!!.reverseLayout = true
        binding.rvMessages.addItemDecoration(VerticalSpaceItemDecoration(20))
        binding.rvMessages.layoutManager = linearLayoutManager
        binding.rvMessages.addOnScrollListener(object :
            MessagePaginationListener(linearLayoutManager!!) {
            override fun loadMoreItems() {
                this@ConversationDetailsFragment.isLoading = true
                loadMoreMessages()
            }

            override fun isLastPage(): Boolean {
                return this@ConversationDetailsFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ConversationDetailsFragment.isLoading
            }
        })
        loadData(true)
        binding.btnSend.visibility = View.VISIBLE
        binding.btnSend.setOnClickListener(ViewClickListener { v: View? ->
            var text: String? = Objects.requireNonNull(
                binding.etMessage.text
            ).toString().trim { it <= ' ' }
            binding.etMessage.setText("")
            binding.circularProgressView.visibility = View.VISIBLE
            binding.btnSend.visibility = View.GONE
            if (!TextUtils.isEmpty(text)) {
                text = Base64Utility.encodeToString(text)
                sendMessage(text)
            }
            else {
                binding.circularProgressView.visibility = View.GONE
                binding.btnSend.visibility = View.VISIBLE
            }
        })
        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                when (action.actionType) {
                    ActionType.MESSAGE_COPY   -> copyMessageText(action)
                    ActionType.MESSAGE_DELETE -> deleteUserMessage(action)
                    else -> {}
                }
            }
        }
        binding.etMessage.filters = arrayOf<InputFilter>(LengthFilter(2000))
    }

    private fun copyMessageText(action: Action<Any?>?) {
        val item = action!!.data as Message
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", Base64Utility.decode(item.messageText))
        clipboard.setPrimaryClip(clip)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addItem(message: Message, from: String) {
        //messageList.add(0, message);
        val isMessageAvailable = false
        if (from.equals("SendMessage", ignoreCase = true)) {
            binding.circularProgressView.visibility = View.GONE
            binding.btnSend.visibility = View.VISIBLE
            hideProgress()
            if (messageAdapter != null) {
                messageAdapter!!.addItem(message)
            }
        }
        if (from.equals("SilentMessage", ignoreCase = true)) {
            if (messageAdapter != null) {
                messageAdapter!!.addItem(message)
            }
        }
        if (messageIdList != null && message.id != null) {
            messageIdList?.add(0, Integer.valueOf(message.id!!))
        }

        counter += 1
    }

    private fun sendMessage(text: String?) {
        var otherUserId: String? = ""
        if (!Objects.requireNonNull(mConversation?.userTwo)
                .equals(loginUserId, ignoreCase = true)
        ) {
            otherUserId = mConversation?.userTwo
        }
        else if (!Objects.requireNonNull(mConversation?.userOne)
                .equals(loginUserId, ignoreCase = true)
        ) {
            otherUserId = mConversation?.userOne
        }

        if (mConversation?.id != null){
            viewModel.postMessage(mConversation?.id!!, otherUserId!!, text!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<SendMessageResponse> ->
                    if (apiResponse.isSuccess && apiResponse.body != null) {
                        binding.tvMessage.visibility = View.GONE
                        isLoading = false
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            val cId = apiResponse.body.message.conversationId.toString()
                            if (!TextUtils.isEmpty(cId)) {
                                mConversation?.id = cId
                            }
                            addItem(apiResponse.body.message, "SendMessage")
                            //updateUserMessageReadStatus();
                        }
                        else {
                            binding.circularProgressView.visibility = View.GONE
                            binding.btnSend.visibility = View.VISIBLE
                            showAlert(apiResponse.body.errorMessage)
                            hideProgress()
                        }
                    }
                    else {
                        binding.circularProgressView.visibility = View.GONE
                        binding.btnSend.visibility = View.VISIBLE
                        hideProgress()
                    }
                }
        }

    }

    private fun updateUserMessageReadStatus() {
        if (mConversation?.id != null && !mConversation?.id.equals(
                "",
                ignoreCase = true
            ) && !mConversation?.id.equals("EMPTY", ignoreCase = true)
        ) {
            viewModel.updateUserMessageReadStatus(mConversation?.id!!)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                    }
                    else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                    }
                    else {
                        hideProgress()
                    }
                }
        }
    }

    // print twenty element from list from the start index specified
    fun getDisplayGameIdListElements(myList: List<Int>, startIndex: Int): List<Int> {
        return myList.subList(startIndex, Math.min(myList.size, startIndex + displayNumberOfMessages))
    }

    private fun loadMoreMessages() {

//        if (messageList.size() > displayNumberOfMessages) {
        if (messageIdList == null || messageIdList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val activityIds: List<Int>
        val start = messageAdapter!!.itemCount
        val end = messageIdList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val gameIdList2 = getDisplayGameIdListElements(
            messageIdList!!, start
        )
        AppUtilities.showLog("**gameIdList2:", gameIdList2.size.toString())
        activityIds = ArrayList(gameIdList2)
        viewModel.getNextMessages(Utilities.getTokenSeparatedString(activityIds, ",")).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetMessagesResponse> ->
            if (apiResponse.loading) {
                if (messageAdapter != null) {
                    messageAdapter!!.addLoading()
                }
            }
            else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (messageAdapter != null) {
                    messageAdapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getZZZNextUserAnsweredActivityInfo" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                    if (apiResponse.body.messageList.isEmpty()) {
                        // isLastPage = true;
                        setFooterMessage()
                    }
                    else {
                        messageAdapter!!.addItems(apiResponse.body.messageList)
                        messageList!!.addAll(apiResponse.body.messageList)
                        if (apiResponse.body.messageList.size < PaginationListener.PAGE_ITEM_SIZE) {
                            // isLastPage = true;
                            setFooterMessage()
                        }
                        else {
                            isLastPage = false
                        }
                        //Log.e("####", String.format("server: %d", apiResponse.body.getMessageList().size()));
                    }
                }
                else {
                    // isLastPage = true;
                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            }
            else {
                isLoading = false
                if (messageAdapter != null) {
                    messageAdapter!!.removeLoading()
                }
                showAlert(apiResponse.errorMessage)
            }
        }

//        }
    }

    private fun setFooterMessage() {}

    //    private void enableSwipeToDeleteAndUndo() {
    //
    //        SwipeHelper swipeHelper = new SwipeHelper(requireActivity()) {
    //            @Override
    //            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
    //
    //                if (viewHolder instanceof MessageAdapter.MessageRightViewHolder) {
    //
    //                    underlayButtons.add(new SwipeHelper.UnderlayButton(
    //                            "Delete",
    //                            0,
    //                            ContextCompat.getColor(getFragmentContext(), R.color.colorAccent),
    //                            pos -> {
    //                                isReadyToMove = false;
    //                                final Message item = messageAdapter.getData().get(pos);
    //                                if (item.getId() != null && !item.getId().equalsIgnoreCase("0")) {
    //                                    deleteUserMessage(item, pos);
    //                                }
    //                            }
    //                    ));
    //                }
    //
    //            }
    //        };
    //
    //        swipeHelper.attachToRecyclerView(binding.rvMessages);
    //    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteUserMessage(action: Action<Any?>?) {
        val item = action!!.data as Message
        viewModel.deleteUserMessage(item.id.toString())
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<DeletedMessageResponse> ->
                if (apiResponse.loading) {
//                showProgress();
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
//                hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        isReadyToMove = true
                        item.messageText = apiResponse.body.deletedMessage
                        item.status = "2"
                        messageAdapter!!.updateItem(item)
                        if (messageAdapter != null && isVisible) {
                            messageAdapter!!.notifyDataSetChanged()
                        }
                        val snackbar =
                            Snackbar.make(binding.root, "Message Deleted", Snackbar.LENGTH_LONG)
                        snackbar.setTextColor(Color.WHITE)
                        snackbar.show()
                    }
                    else {
                        showAlert(
                            getString(R.string.label_network_error),
                            apiResponse.body.errorMessage
                        )
                    }
                }
                else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "deleteUserMessage")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "deleteUserMessage",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(showProgress: Boolean) {
        isLastPage = false
        viewModel.getUserMessages(mConversation?.id!!, mConversation?.userTwo!!).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetMessagesResponse> ->
            if (apiResponse.loading) {
                if (showProgress) {
                    showProgress()
                }
            }
            else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (!showProgress) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                binding.tvMessage.visibility = View.GONE
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.conversationStatus.equals("1", ignoreCase = true)) {
                        updateUserMessageReadStatus()
                    }
                    mConversation?.id = apiResponse.body.conversationId
                    messageList!!.addAll(apiResponse.body.messageList)
                    displayNumberOfMessages = apiResponse.body.displayNumberOfMessages
                    messageIdList!!.addAll(apiResponse.body.messageIdList)
                    setAdapter()
                }
                else {
                    binding.tvMessage.visibility = View.VISIBLE
                    if (mConversation != null && mConversation?.id != null && mConversation?.id.equals(
                            "EMPTY",
                            ignoreCase = true
                        )
                    ) {
                        binding.tvMessage.text =
                            """Start a private conversation with ${mConversation?.userName} 
by sending a message."""
                    }
                    showAlert(apiResponse.body.errorMessage)
                }
            }
            else {
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
                        AppUtilities.showLog("Network Error", "getUserMessages")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserMessages",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAdapter() {
        if (messageList == null || messageList!!.isEmpty()) {
            binding.tvMessage.visibility = View.VISIBLE
            if (mConversation != null && mConversation?.id != null && mConversation?.id.equals(
                    "EMPTY",
                    ignoreCase = true
                )
            ) {
                binding.tvMessage.text =
                    """Start a private conversation with ${mConversation?.userName} by sending a message."""
            }
            isLastPage = true
        }
        else {
            binding.tvMessage.visibility = View.GONE
        }
        messageAdapter = MessageAdapter(
            fragmentContext,
            preferenceHelper!!,
            messageList as ArrayList<Message>?
        ) { item: Pair<Int, Message> ->
            val titleText = "Choose Option"
            val actions = ArrayList<Action<*>>()
            if (item.first == MessageAdapter.VIEW_TYPE_NORMAL_LEFT) {
                actions.add(
                    Action(
                        getString(R.string.action_label_copy),
                        R.color.color_black,
                        ActionType.MESSAGE_COPY,
                        item.second
                    )
                )
            }
            else if (item.first == MessageAdapter.VIEW_TYPE_NORMAL_RIGHT) {
                actions.add(
                    Action(
                        getString(R.string.action_label_copy),
                        R.color.color_black,
                        ActionType.MESSAGE_COPY,
                        item.second
                    )
                )
                actions.add(
                    Action(
                        getString(R.string.action_label_delete),
                        R.color.color_black,
                        ActionType.MESSAGE_DELETE,
                        item.second
                    )
                )
            }
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                    titleText,
                    bundle,
                    ""
                )
            )
        }
        binding.rvMessages.adapter = messageAdapter

//        if (messageList != null && messageList.size() > 0 && mConversation.getReadStatus() != null && mConversation.getReadStatus().equalsIgnoreCase("1")) {
//            updateUserMessageReadStatus();
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mConversation?.id != null && !mConversation?.id.equals("EMPTY", ignoreCase = true)) {
            if (counter > 0) {
                viewModel.updateConversationReadStatus(mConversation)
            }
        }
    }
}