package com.onourem.android.activity.ui.admin.posts.adapters

import android.animation.ArgbEvaluator
import androidx.navigation.NavController
import com.onourem.android.activity.models.FeedsList
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.R
import android.annotation.SuppressLint
import com.onourem.android.activity.models.UserList
import android.text.TextUtils
import com.onourem.android.activity.MobileNavigationDirections
import android.widget.PopupWindow
import com.onourem.android.activity.ui.games.adapters.MoreUserListAdapter
import android.view.View.OnTouchListener
import com.onourem.android.activity.ui.admin.posts.adapters.PostsAdapter
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
import com.onourem.android.activity.databinding.ItemLayoutPostBinding
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

class PostsAdapter(
    private val from : String,
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
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_post, parent, false)
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
            //userList.setStatus(item.getReceiverId().get(i + 3));
            userLists.add(userList)
            i += 4
        }
        //        ArrayList<String> receiverIds = (ArrayList<String>) item.getReceiverTypeId();
//        int counter = 0;
//        for (UserList user : userLists) {
//            if (receiverIds.get(counter) != null) {
//                user.setUserType(receiverIds.get(counter));
//            }
//            counter = counter + 1;
//        }
//
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
                val oldCount = feedsList.commentCount!!.toInt()
                if (oldCount != currentCount) {
                    feedsList.commentCount = currentCount.toString()
                    updatedItemIndex = i
                    notifyItemChanged(i)
                }
                break
            }
        }
    }

    inner class TaskMessageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemLayoutPostBinding
        private val profileOptions = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
        private val imageOptions = RequestOptions()
            .fitCenter()
            .transform(RoundedCorners(1))
            .placeholder(R.drawable.shape_image_video_placeholder)
            .error(R.drawable.shape_image_video_placeholder)

        fun animateChange(viewHolder: ItemLayoutPostBinding) {
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
            if (item.receiverId != null && item.receiverId!!.isNotEmpty() && item.isReceiverRequired.equals(
                    "Y",
                    ignoreCase = true
                )
            ) {
                itemBinding.tvReceiverName.text = item.receiverId!![2].split(" ").toTypedArray()[0]
                val receiverCount = item.receiverId!!.size / 4

//                ArrayList<UserList> userLists = new ArrayList<>();
//                for (int i = 0; i < item.getReceiverId().size(); i += 4) {
//                    UserList userList = new UserList();
//                    userList.setUserId(item.getReceiverId().get(i));
//                    userList.setProfilePicture(item.getReceiverId().get(i + 1));
//                    userList.setFirstName(item.getReceiverId().get(i + 2));
//                    //userList.setStatus(item.getReceiverId().get(i + 3));
//                    userLists.add(userList);
//                }

//                ArrayList<String> receiverIds = (ArrayList<String>) item.getReceiverTypeId();
//                int counter = 0;
//                for (UserList user : userLists) {
//                    if (receiverIds.get(counter) != null) {
//                        user.setUserType(receiverIds.get(counter));
//                    }
//                    counter = counter + 1;
//                }
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

                // Utilities.verifiedUserType(itemBinding.getRoot().getContext(), userLists.get(0).getUserType(), itemBinding.ivIconVerifiedReceiver);
            } else {
                itemBinding.tvReceiverName.visibility = View.GONE
                itemBinding.tvMore.visibility = View.GONE
                itemBinding.ivReceiverProfile.visibility = View.GONE
            }

            when (from){
                "0"->{
                    itemBinding.txtPushToSky.visibility = View.VISIBLE
                    itemBinding.txtRejectToSky.visibility = View.VISIBLE
                }
                "1"->{
                    itemBinding.txtPushToSky.visibility = View.GONE
                    itemBinding.txtRejectToSky.visibility = View.VISIBLE
                    itemBinding.txtRejectToSky.text = "Remove From Sky"

                }
                "2"->{
                    itemBinding.txtPushToSky.visibility = View.VISIBLE
                    itemBinding.txtRejectToSky.visibility = View.GONE
                }
            }

            if (item.message != null && item.message!!.isNotEmpty()) {
                itemBinding.clComment.visibility = View.VISIBLE
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
                itemBinding.clComment.visibility = View.GONE
            }
            itemBinding.tvCategoryName.text = item.categoryType
            itemBinding.tvDate.text = item.postCreationDate!!.substring(0, 10)
            if (!TextUtils.isEmpty(item.commentCount) && !item.commentCount.equals(
                    "0",
                    ignoreCase = true
                )
            ) {
                itemBinding.tvCommentsCount.visibility = View.VISIBLE
                itemBinding.cardCommentsCount.visibility = View.VISIBLE
                itemBinding.tvCommentsCount.text = item.commentCount
            } else {
                itemBinding.tvCommentsCount.visibility = View.GONE
                itemBinding.cardCommentsCount.visibility = View.GONE
            }
            Glide.with(itemBinding.root.context)
                .load(item.profilePicture)
                .apply(profileOptions)
                .into(itemBinding.ivSenderProfile)
            Utilities.verifiedUserType(
                itemBinding.root.context,
                item.userTypeId,
                itemBinding.ivIconVerified
            )
            val url: String
            val media: Int
            if (!TextUtils.isEmpty(item.videoURL)) {
                url = item.videoURL?:""
                media = 2
            } else {
                url = item.postLargeImageURL?:""
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
        }

        init {
            itemBinding = ItemLayoutPostBinding.bind(itemView)
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

            itemBinding.txtPushToSky.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_PUSH_TO_SKY, items[bindingAdapterPosition])
                )
            })

            itemBinding.txtRejectToSky.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_REJECT_TO_SKY, items[bindingAdapterPosition])
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
        var CLICK_MORE = 1
        var CLICK_COMMENT = 2
        var CLICK_SENDER_PROFILE = 3
        var CLICK_ATTACHMENT = 4
        var CLICK_COMMENT_COUNT = 5
        var CLICK_PUSH_TO_SKY = 6
        var CLICK_REJECT_TO_SKY = 7
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