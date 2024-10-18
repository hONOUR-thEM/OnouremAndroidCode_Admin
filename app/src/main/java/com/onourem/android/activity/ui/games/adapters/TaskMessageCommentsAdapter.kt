package com.onourem.android.activity.ui.games.adapters

import android.animation.ArgbEvaluator
import androidx.navigation.NavController
import com.onourem.android.activity.models.FeedsList
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.games.adapters.TaskMessageCommentsAdapter.TaskMessageViewHolder
import com.onourem.android.activity.R
import android.annotation.SuppressLint
import com.onourem.android.activity.models.UserList
import android.text.TextUtils
import com.onourem.android.activity.MobileNavigationDirections
import android.widget.PopupWindow
import com.onourem.android.activity.ui.games.adapters.MoreUserListAdapter
import android.view.View.OnTouchListener
import com.onourem.android.activity.ui.games.adapters.TaskMessageCommentsAdapter
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import androidx.core.content.ContextCompat
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Activity
import android.graphics.Rect
import android.util.Pair
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.onourem.android.activity.databinding.ItemLayoutTaskMessageCommentViewBinding
import com.onourem.android.activity.databinding.LayoutShowMoreUsersBinding
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.zoomy.Zoomy
import com.onourem.android.activity.ui.utils.zoomy.TapListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.lang.NullPointerException
import java.util.*

class TaskMessageCommentsAdapter(
    private val navController: NavController,
    FeedsList: List<FeedsList?>?,
    private val alertDialog: SimpleAlertDialog,
    private val onItemClickListener: OnItemClickListener<Pair<Int, FeedsList>>
) : PaginationRVAdapter<FeedsList?>(
    FeedsList!!
) {
    private var updatedItemIndex = -1
    override fun emptyLoadingItem(): FeedsList {
        return FeedsList()
    }

    override fun emptyFooterItem(): FeedsList {
        return FeedsList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return TaskMessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout_task_message_comment_view, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showMoreUsers(anchorView: View, item: FeedsList) {
        val userLists = ArrayList<UserList>()
        var i = 0
        while (i < item.receiverId!!.size) {
            val userList = UserList()
            userList.userId = item.receiverId!![i]
            userList.profilePicture = item.receiverId!![i + 1]
            userList.firstName = item.receiverId!![i + 2]
            userList.status = item.receiverId!![i + 3]
            userLists.add(userList)
            i += 4
        }
        val receiverIds = item.receiverTypeId as ArrayList<String?>
        var counter = 0
        for (user in userLists) {
            if (receiverIds[counter] != null) {
                user.userType = receiverIds[counter]
            }
            counter = counter + 1
        }
        if (userLists.size == 1) {
            val clickedUser = userLists[0]
            if (!TextUtils.isEmpty(clickedUser.profilePicture) && !(clickedUser.profilePicture!!.contains(
                    "ANONYMOUS"
                ) || clickedUser.profilePicture!!.contains("EMAIL"))
            ) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavProfile(
                        item.activityId,
                        userLists[0].userId
                    )
                )
            } else {
                alertDialog.showAlert("This person is not on Onourem yet")
            }
            return
        }
        val binding = LayoutShowMoreUsersBinding.inflate(LayoutInflater.from(anchorView.context))
        val popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val linearLayoutManager: LinearLayoutManager =
            object : LinearLayoutManager(anchorView.context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return true
                }
            }
        binding.rvMoreUsers.addItemDecoration(
            DividerItemDecoration(
                anchorView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rvMoreUsers.layoutManager = linearLayoutManager
        val moreUserListAdapter = MoreUserListAdapter(userLists) { clickedUser: UserList ->
            if (!TextUtils.isEmpty(clickedUser.profilePicture) && !(clickedUser.profilePicture!!.contains(
                    "ANONYMOUS"
                ) || clickedUser.profilePicture!!.contains("EMAIL"))
            ) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavProfile(
                        item.activityId,
                        clickedUser.userId
                    )
                )
                popupWindow.dismiss()
            } else {
                alertDialog.showAlert("This person is not on Onourem yet")
            }
        }
        binding.rvMoreUsers.adapter = moreUserListAdapter
        popupWindow.elevation = 5.0f
        popupWindow.isOutsideTouchable = true
        popupWindow.setTouchInterceptor { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow.dismiss()
            }
            false
        }
        popupWindow.overlapAnchor = true
        val rect = locateView(anchorView)
        if (rect == null) popupWindow.showAsDropDown(anchorView) else popupWindow.showAtLocation(
            anchorView,
            Gravity.TOP or Gravity.START,
            rect.left,
            rect.bottom
        )
    }

    fun updateComment(postId: String?, count: Int) {
        for (i in items.indices) {
            val feedsList = items[i]!!
            if (feedsList.postId.equals(postId, ignoreCase = true)) {
                var currentCount = count
                if (currentCount <= 0) {
                    currentCount = 0
                }
                val oldCount = feedsList.commentCount?.toInt()
                if (oldCount != currentCount) {
                    feedsList.commentCount = currentCount.toString()
                    updatedItemIndex = i
                    notifyItemChanged(i)
                }
                break
            }
        }
    }

    private fun showCategoryDetails(item: FeedsList?) {
        val title = item!!.categoryType
        var popUpMessage = ""

        val senderName = "${item.firstName} ${item.lastName}"
        val numReceivers = item.receiverId!!.size / 4
        val userLists = ArrayList<UserList>()
        var i = 0
        while (i < item.receiverId!!.size) {
            val userList = UserList()
            userList.userId = item.receiverId!![i]
            userList.profilePicture = item.receiverId!![i + 1]
            userList.firstName = item.receiverId!![i + 2]
            userList.status = item.receiverId!![i + 3]
            userLists.add(userList)
            i += 4
        }
        val receiverIds = item.receiverTypeId as ArrayList<*>
        var counter = 0
        for (user in userLists) {
            if (receiverIds[counter] != null) {
                user.userType = receiverIds[counter] as String?
            }
            counter += 1
        }
        val receiverNames = ArrayList<String>()
        userLists.forEach {
            receiverNames.add(it.firstName!!)
        }

        popUpMessage = if (item.isReceiverRequired == "N") {
            "$senderName has shared a post of $title category. You can share such post from the bottom right + Create icon."
        } else {
            when (numReceivers) {
                1 -> {
                    senderName + " has sent a " + title + " message to " + receiverNames[0] + ". You can send such messages from 'Appreciate' section in the Menu. You can control who can see your messages."
                }
                else -> {
                    senderName + " has sent a " + title + " message to " + receiverNames[0] + " and others. You can send such messages from 'Appreciate' section in the Menu. You can control who can see your messages."
                }
            }
        }

        if (popUpMessage != "") {
            alertDialog.showAlert(item.categoryType!!, popUpMessage)
        }
    }

    inner class TaskMessageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemLayoutTaskMessageCommentViewBinding
        private val profileOptions = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)
        private val imageOptions = RequestOptions()
            .fitCenter()
            .transform(RoundedCorners(15))
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

        fun animateChange(viewHolder: ItemLayoutTaskMessageCommentViewBinding) {
//            Animation animBlink = AnimationUtils.loadAnimation(itemView.getContext(),
//                    R.anim.blink);
//            viewHolder.card.startAnimation(animBlink);
            val colorFrom =
                ContextCompat.getColor(viewHolder.root.context, R.color.color_highlight_game_post)
            val colorTo = ContextCompat.getColor(viewHolder.root.context, R.color.color_white)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 300 // milliseconds
            colorAnimation.addUpdateListener { animator: ValueAnimator ->
                viewHolder.card.setCardBackgroundColor(
                    animator.animatedValue as Int
                )
            }
            colorAnimation.repeatCount = 2
            colorAnimation.start()
        }

        override fun onBind(position: Int) {
            val item = items[position]!!
            itemBinding.tvSenderName.text = item.firstName
            if (item.receiverId != null && !item.receiverId!!.isEmpty() && item.isReceiverRequired.equals(
                    "Y",
                    ignoreCase = true
                )
            ) {
                itemBinding.tvReceiverName.text = item.receiverId!![2].split(" ").toTypedArray()[0]
                val receiverCount = item.receiverId!!.size / 4
                val userLists = ArrayList<UserList>()
                var i = 0
                while (i < item.receiverId!!.size) {
                    val userList = UserList()
                    userList.userId = item.receiverId!![i]
                    userList.profilePicture = item.receiverId!![i + 1]
                    userList.firstName = item.receiverId!![i + 2]
                    userList.status = item.receiverId!![i + 3]
                    userLists.add(userList)
                    i += 4
                }
                val receiverIds = item.receiverTypeId as ArrayList<String?>
                var counter = 0
                for (user in userLists) {
                    if (receiverIds[counter] != null) {
                        user.userType = receiverIds[counter]
                    }
                    counter += 1
                }
                if (receiverCount > 1) {
                    itemBinding.tvMore.visibility = View.VISIBLE
                    itemBinding.tvMore.text =
                        String.format(Locale.getDefault(), "+%d more", receiverCount - 1)
                } else itemBinding.tvMore.visibility = View.GONE
                Glide.with(itemBinding.root.context)
                    .load(item.receiverId!![1])
                    .apply(profileOptions)
                    .into(itemBinding.ivReceiverProfile)
                itemBinding.tvReceiverName.visibility = View.VISIBLE
                itemBinding.ivReceiverProfile.visibility = View.VISIBLE

                 Utilities.verifiedUserType(itemBinding.root.context, userLists[0].userType, itemBinding.ivIconVerifiedReceiver);
            } else {
                itemBinding.tvReceiverName.visibility = View.GONE
                itemBinding.tvMore.visibility = View.GONE
                itemBinding.ivReceiverProfile.visibility = View.GONE
            }

            if (item.message!!.isNotEmpty()) {
                itemBinding.tvComment.visibility = View.VISIBLE
                itemBinding.tvCommentFull.visibility = View.GONE
                val commentText = Base64Utility.decode(item.message)

                if (commentText.length > Constants.COMMENT_MAX_LENGTH) {
                    val commentShortText = commentText.substring(0, Constants.COMMENT_MAX_LENGTH)
                    itemBinding.tvComment.text = "$commentShortText ...Read More"
                } else {
                    itemBinding.tvComment.text = commentText
                }
                itemBinding.tvCommentFull.text = commentText
            } else {
                itemBinding.tvComment.visibility = View.GONE
                itemBinding.tvCommentFull.visibility = View.GONE
            }

            itemBinding.tvCategoryName.text = item.categoryType
            itemBinding.tvDate.text = item.postCreationDate!!.substring(0, 10)
            if (!TextUtils.isEmpty(item.commentCount) && !item.commentCount.equals(
                    "0",
                    ignoreCase = true
                )
            ) {
                itemBinding.tvCommentsCount.visibility = View.VISIBLE
                itemBinding.tvCommentsCount.text = item.commentCount
            } else {
                itemBinding.tvCommentsCount.visibility = View.GONE
            }
            Glide.with(itemBinding.root.context)
                .load(item.profilePicture)
                .apply(profileOptions)
                .into(itemBinding.ivSenderProfile)

            Utilities.verifiedUserType(itemBinding.root.context, item.userTypeId, itemBinding.ivIconVerified)

            val url: String
            val media: Int
            if (!TextUtils.isEmpty(item.videoURL)) {
                url = item.videoURL?: ""
                media = 2
            } else {
                url = item.postLargeImageURL?: ""
                media = 1
            }
            if (!TextUtils.isEmpty(url)) {
                if (media == 2) {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                } else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
                Glide.with(itemBinding.root.context)
                    .load(url)
                    .apply(imageOptions)
                    .into(itemBinding.ivCommentAttachment)
                itemBinding.ivCommentAttachment.visibility = View.VISIBLE
                itemBinding.ivCommentAttachment.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(itemBinding.root.context as Activity)
                    .target(itemBinding.ivCommentAttachment)
                    .enableImmersiveMode(false)
                    .tapListener { v: View? ->
                        onItemClickListener.onItemClick(
                            Pair.create(
                                CLICK_ATTACHMENT, items[bindingAdapterPosition]
                            )
                        )
                    }
                //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
                builder.register()

            } else {
                itemBinding.ivPlayVideo.visibility = View.GONE
                itemBinding.ivCommentAttachment.visibility = View.GONE
            }
            if (updatedItemIndex != -1 && updatedItemIndex == position) {
                itemBinding.root.postDelayed({
                    updatedItemIndex = -1
                    animateChange(itemBinding)
                }, 1000)
            }

            if (item.commentsEnabled != "" && item.commentsEnabled == "N") {
                itemBinding.tvCommentsCount.visibility = View.GONE
                itemBinding.tvWriteComment.visibility = View.GONE
            } else {
                val count = item.commentCount
                if (!TextUtils.isEmpty(count) && count != "0") {
                    itemBinding.tvCommentsCount.text = count
                    itemBinding.tvCommentsCount.visibility = View.VISIBLE
                } else {
                    itemBinding.tvCommentsCount.visibility = View.GONE
                }
                itemBinding.tvWriteComment.visibility = View.VISIBLE
            }
        }

        init {
            itemBinding = ItemLayoutTaskMessageCommentViewBinding.bind(itemView)
            itemBinding.ivSenderProfile.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_SENDER_PROFILE, items[bindingAdapterPosition])
                )
            })
            itemBinding.ivUserMoreAction.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_MORE, items[bindingAdapterPosition])
                )
            })

            itemBinding.tvCategoryName.setOnClickListener(ViewClickListener { v: View? ->
                showCategoryDetails(items[bindingAdapterPosition])
            })
            itemBinding.tvWriteComment.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_COMMENT, items[bindingAdapterPosition])
                )
            })

//            itemBinding.ivCommentAttachment.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_ATTACHMENT, items.get(getBindingAdapterPosition())))
//            ));
            itemBinding.tvCommentsCount.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_COMMENT_COUNT, items[bindingAdapterPosition])
                )
            })

            itemBinding.tvCommentFull.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]

                if (item!!.message!!.isNotEmpty()) {
                    val commentText = Base64Utility.decode(item.message)

                    if (commentText.length > Constants.COMMENT_MAX_LENGTH) {
                        itemBinding.tvCommentFull.visibility = View.GONE
                        itemBinding.tvComment.visibility = View.VISIBLE
                    }
                }

            })

            itemBinding.tvComment.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]

                if (item!!.message!!.isNotEmpty()) {
                    val commentText = Base64Utility.decode(item.message)

                    if (commentText.length > Constants.COMMENT_MAX_LENGTH) {
                        itemBinding.tvCommentFull.visibility = View.VISIBLE
                        itemBinding.tvComment.visibility = View.GONE
                    }
                }

            })
            val receiverViewClickListener = ViewClickListener { v: View? ->
                showMoreUsers(
                    itemBinding.tvMore,
                    items[bindingAdapterPosition]!!
                )
            }
            itemBinding.tvMore.setOnClickListener(receiverViewClickListener)
            itemBinding.ivReceiverProfile.setOnClickListener(receiverViewClickListener)
            itemBinding.tvReceiverName.setOnClickListener(receiverViewClickListener)
        }
    }

    companion object {
        @JvmField
        var CLICK_MORE = 1
        @JvmField
        var CLICK_COMMENT = 2
        @JvmField
        var CLICK_SENDER_PROFILE = 3
        @JvmField
        var CLICK_ATTACHMENT = 4
        var CLICK_COMMENT_COUNT = 5
        fun locateView(v: View?): Rect? {
            val loc_int = IntArray(2)
            if (v == null) return null
            try {
                v.getLocationOnScreen(loc_int)
            } catch (npe: NullPointerException) {
                //Happens when the view doesn't exist on screen anymore.
                return null
            }
            val location = Rect()
            location.left = loc_int[0]
            location.top = loc_int[1]
            location.right = location.left + v.width
            location.bottom = location.top + v.height
            return location
        }
    }
}