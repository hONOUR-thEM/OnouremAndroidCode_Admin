package com.onourem.android.activity.ui.games.adapters.activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.*
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.*
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import com.onourem.android.activity.ui.dialog.SimpleAlertDialog
import com.onourem.android.activity.ui.games.adapters.*
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.tooltip.ViewTooltip
import com.onourem.android.activity.ui.utils.zoomy.Zoomy
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import kotlin.math.abs

class ActivityGamesAdapter(
    playGroup: PlayGroup,
    items: List<LoginDayActivityInfoList>?,
    sharedPreferenceHelper: SharedPreferenceHelper,
    private val onItemClickListener: OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?>,
    onWatchListItemClickListener: OnWatchListItemClickListener<android.util.Pair<Int, UserWatchList>>?,
    dashboardViewModel: DashboardViewModel,
    viewLifecycleOwner: LifecycleOwner,
    private val navController: NavController,
    private val alertDialog: SimpleAlertDialog
) : ActivityGamesPaginationAdapter<LoginDayActivityInfoList?>(
    ArrayList(items!!)
) {
    private val PlayGroupID: String?
    private val options = RequestOptions().fitCenter().transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.default_place_holder).error(R.drawable.default_place_holder)
    private val options30 = RequestOptions().placeholder(R.drawable.default_place_holder).error(R.drawable.default_place_holder)

    private val profileOptions = RequestOptions().fitCenter().placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private val imageOptions =
        RequestOptions().fitCenter().transform(RoundedCorners(1)).placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

    private val onWatchListItemClickListener: OnWatchListItemClickListener<android.util.Pair<Int, UserWatchList>>?
    private val dashboardViewModel: DashboardViewModel
    private val viewLifecycleOwner: LifecycleOwner

    var preferenceHelper: SharedPreferenceHelper

    override fun emptyLoadingItem(): LoginDayActivityInfoList {
        return LoginDayActivityInfoList()
    }

    override fun emptyFooterItem(): LoginDayActivityInfoList {
        return LoginDayActivityInfoList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_AUDIO -> {
                AudioViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_question_card_audio_listing, parent, false)
                )
            }
            VIEW_TYPE_FC_GAME -> {
                FriendsCircleViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_question_friends_circle_card, parent, false)
                )
            }
            VIEW_TYPE_WATCHLIST -> {
                WatchlistViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_watch_list_question_listing, parent, false)
                )
            }
            VIEW_TYPE_FUN_CARD -> {
                FunCardViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_question_fun_card, parent, false)
                )
            }
            VIEW_TYPE_POST -> {
                PostViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_layout_question_card_post_view, parent, false)
                )
            }
            VIEW_TYPE_TASK -> {
                TaskActivityItemViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_activity_task_message, parent, false)
                )
            }
            VIEW_TYPE_MESSAGE -> {
                MessageActivityItemViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_activity_task_message, parent, false)
                )
            }
            VIEW_TYPE_EXTERNAL -> {
                ExternalActivityItemViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_activity_external, parent, false)
                )
            }
            VIEW_TYPE_MOOD -> {
                MoodActivityItemViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_activity_external, parent, false)
                )
            }
            VIEW_TYPE_SURVEY -> {
                SurveyActivityItemViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_activity_survey, parent, false)
                )
            }
            else -> {
                PlaySoloGamesItemViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_question_card, parent, false)
                )
            }
        }
    }

    fun notifyDataUpdated(data: LoginDayActivityInfoList?) {
        notifyItemChanged(items.indexOf(data))
    }

    override fun removeItem(item: LoginDayActivityInfoList?) {
        val position = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(position)
    }

    fun removeItem(pos: Int) {
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(loginDayActivityInfoList: List<LoginDayActivityInfoList?>?) {
        items.clear()
        notifyDataSetChanged()
        items.addAll(loginDayActivityInfoList!!)
        notifyDataSetChanged()
    }

    inner class FriendsCircleViewHolder(view: View?) : BaseViewHolder(view) {
        private val rowBinding: ItemQuestionFriendsCircleCardBinding? = DataBindingUtil.bind(itemView)

        override fun onBind(position: Int) {
            val activity = items[position]!!

            rowBinding!!.tvQuestion.text = activity.activityText

            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                rowBinding.ivQuestionImage.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options30.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(rowBinding.ivQuestionImage)
                rowBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(rowBinding.root.context as Activity).target(rowBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
                            )
                        )
                    }

                builder.register()
            } else {
                rowBinding.ivQuestionImage.visibility = View.GONE
            }
        }

        init {
            assert(rowBinding != null)
            rowBinding!!.tvQuestion.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]!!
                onItemClickListener.onItemClick(
                    Triple(
                        item, CLICK_MEDIA, it.tag.toString()
                    )
                )
            })

            rowBinding.cardActivityRemove.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]!!
                onItemClickListener.onItemClick(
                    Triple(
                        item, CLICK_REMOVE, it.tag.toString()
                    )
                )
            })
        }
    }

    inner class PostViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemLayoutQuestionCardPostViewBinding

        fun animateChange(viewHolder: ItemLayoutQuestionCardPostViewBinding) {

            val colorFrom = ContextCompat.getColor(viewHolder.root.context, R.color.color_highlight_game_post)
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

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val item = items[position]!!.feedsInfo
            itemBinding.tvSenderName.text = item!!.firstName


            if (item.receiverId != null && item.receiverId!!.isNotEmpty() && item.isReceiverRequired.equals(
                    "Y", ignoreCase = true
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
                    itemBinding.tvMore.text = String.format(Locale.getDefault(), "+%d more", receiverCount - 1)
                } else itemBinding.tvMore.visibility = View.GONE
                GlideApp.with(itemBinding.root.context).load(item.receiverId!![1]).thumbnail(0.33f).apply(profileOptions)
                    .into(itemBinding.ivReceiverProfile)
                itemBinding.tvReceiverName.visibility = View.VISIBLE
                itemBinding.ivReceiverProfile.visibility = View.VISIBLE

                // Utilities.verifiedUserType(itemBinding.getRoot().getContext(), userLists.get(0).getUserType(), itemBinding.ivIconVerifiedReceiver);
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
                    "0", ignoreCase = true
                )
            ) {
                itemBinding.tvCommentsCount.visibility = View.VISIBLE
                itemBinding.cardCommentsCount.visibility = View.VISIBLE
                itemBinding.tvCommentsCount.text = item.commentCount
            } else {
                itemBinding.tvCommentsCount.visibility = View.GONE
                itemBinding.cardCommentsCount.visibility = View.GONE
            }
            GlideApp.with(itemBinding.root.context).load(item.profilePicture).thumbnail(0.33f).apply(profileOptions)
                .into(itemBinding.ivSenderProfile)
            Utilities.verifiedUserType(
                itemBinding.root.context, item.userTypeId, itemBinding.ivIconVerified
            )
            val url: String
            val media: Int
            if (!TextUtils.isEmpty(item.videoURL)) {
                url = item.videoURL ?: ""
                media = 2
            } else {
                url = item.postLargeImageURL ?: ""
                media = 1
            }
            if (!TextUtils.isEmpty(url)) {
                if (media == 2) {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                } else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
                GlideApp.with(itemBinding.root.context).load(url).thumbnail(0.33f)
                    .apply(imageOptions.override(item.imageWidth!!, item.imageHeight!!))
                    .into(itemBinding.ivCommentAttachment)
                itemBinding.ivCommentAttachment.visibility = View.VISIBLE
                itemBinding.ivCommentAttachment.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(itemBinding.root.context as Activity).target(itemBinding.ivCommentAttachment)
                    .enableImmersiveMode(false).tapListener { v: View? ->
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_ATTACHMENT, ""
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
//            if (updatedItemIndex != -1 && updatedItemIndex == position) {
//                itemBinding.root.postDelayed({
//                    updatedItemIndex = -1
//                    animateChange(itemBinding)
//                }, 1000)
//            }
        }

        init {
            itemBinding = ItemLayoutQuestionCardPostViewBinding.bind(itemView)

            itemBinding.ivSenderProfile.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_SENDER_PROFILE, ""
                    )
                )
            })

            itemBinding.tvCategoryName.setOnClickListener(ViewClickListener {
                showCategoryDetails(items[bindingAdapterPosition]!!.feedsInfo)
            })


            itemBinding.ivUserMoreAction.setOnClickListener(ViewClickListener {

                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MORE, ""
                    )
                )
            })

            itemBinding.tvWriteComment.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_COMMENT, ""
                    )
                )
            })

            itemBinding.ivCommentAttachment.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ATTACHMENT, ""
                    )
                )
            })

            itemBinding.cardCommentsCount.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_COMMENT_COUNT, ""
                    )
                )
            })

            itemBinding.tvCommentFull.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]!!.feedsInfo

                if (item!!.message!!.isNotEmpty()) {
                    val commentText = Base64Utility.decode(item.message)

                    if (commentText.length > Constants.COMMENT_MAX_LENGTH) {
                        itemBinding.tvCommentFull.visibility = View.GONE
                        itemBinding.tvComment.visibility = View.VISIBLE
                    }
                }

            })

            itemBinding.tvComment.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]!!.feedsInfo

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
                    itemBinding.tvMore, items[bindingAdapterPosition]!!.feedsInfo!!
                )
            }
            itemBinding.tvMore.setOnClickListener(receiverViewClickListener)
            itemBinding.ivReceiverProfile.setOnClickListener(receiverViewClickListener)
            itemBinding.tvReceiverName.setOnClickListener(receiverViewClickListener)
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
        val receiverIds = item.receiverTypeId as ArrayList<String?>
        var counter = 0
        for (user in userLists) {
            if (receiverIds[counter] != null) {
                user.userType = receiverIds[counter]
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
        val receiverIds = item.receiverTypeId as ArrayList<*>
        var counter = 0
        for (user in userLists) {
            if (receiverIds[counter] != null) {
                user.userType = receiverIds[counter] as String?
            }
            counter += 1
        }
        if (userLists.size == 1) {
            val clickedUser = userLists[0]
            if (!TextUtils.isEmpty(clickedUser.profilePicture) && !(clickedUser.profilePicture!!.contains(
                    "ANONYMOUS"
                ) || clickedUser.profilePicture!!.contains("EMAIL"))
            ) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavProfile(
                        item.activityId, userLists[0].userId
                    )
                )
            } else {
                alertDialog.showAlert("This person is not on Onourem yet")
            }
            return
        }
        val binding = LayoutShowMoreUsersBinding.inflate(LayoutInflater.from(anchorView.context))
        val popupWindow = PopupWindow(
            binding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val linearLayoutManager: LinearLayoutManager = object : LinearLayoutManager(anchorView.context, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        binding.rvMoreUsers.addItemDecoration(
            DividerItemDecoration(
                anchorView.context, DividerItemDecoration.VERTICAL
            )
        )
        binding.rvMoreUsers.layoutManager = linearLayoutManager
        val moreUserListAdapter = MoreUserListAdapter(
            userLists
        ) { clickedUser: UserList ->
            if (!TextUtils.isEmpty(clickedUser.profilePicture) && !(clickedUser.profilePicture!!.contains("ANONYMOUS") || clickedUser.profilePicture!!.contains(
                    "EMAIL"
                ))
            ) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavProfile(
                        item.activityId, clickedUser.userId
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
        if (rect == null) popupWindow.showAsDropDown(anchorView)
        else popupWindow.showAtLocation(
            anchorView, Gravity.TOP or Gravity.START, rect.left, rect.bottom
        )
    }

    private fun locateView(v: View?): Rect? {
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

    inner class AudioViewHolder(view: View?) : BaseViewHolder(view) {
        private val rowBinding: ItemQuestionCardAudioListingBinding? = DataBindingUtil.bind(itemView)
        private val layoutManager: LinearLayoutManager
        private val headerAdapter: HeaderAdapter
        private var preX = 0f
        private var preY = 0f
        private val Y_BUFFER: Float
        override fun onBind(position: Int) {
            val loginDayActivityInfoList = items[position]!!
            if (loginDayActivityInfoList.songsFromServer != null) {
                rowBinding!!.rvAudios.layoutManager = layoutManager
                rowBinding!!.rvAudios.setHasFixedSize(true)
                rowBinding!!.rvAudios.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        if (e.action == MotionEvent.ACTION_DOWN) {
                            rowBinding!!.rvAudios.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        if (e.action == MotionEvent.ACTION_MOVE) {
                            if (Math.abs(e.x - preX) > Math.abs(e.y - preY)) {
                                rowBinding!!.rvAudios.parent.requestDisallowInterceptTouchEvent(true)
                            } else if (Math.abs(e.y - preY) > Y_BUFFER) {
                                rowBinding!!.rvAudios.parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                        preX = e.x
                        preY = e.y
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                })

                val drawableArray: ArrayList<Drawable> = ArrayList()
                drawableArray.add(
                    ContextCompat.getDrawable(
                        rowBinding!!.root.context, R.drawable.bg_one
                    )!!
                )
                drawableArray.add(
                    ContextCompat.getDrawable(
                        rowBinding!!.root.context, R.drawable.bg_two
                    )!!
                )
                drawableArray.add(
                    ContextCompat.getDrawable(
                        rowBinding!!.root.context, R.drawable.bg_three
                    )!!
                )
                drawableArray.add(
                    ContextCompat.getDrawable(
                        rowBinding!!.root.context, R.drawable.bg_four
                    )!!
                )

                val songsItems = loginDayActivityInfoList.songsFromServer!!

                var count = 0
                songsItems.forEach { song ->
                    if (count <= drawableArray.size - 1) {
                        song.drawable = drawableArray[count]
                        count++
                        if (count > drawableArray.size - 1) {
                            count = 0
                        }
                    }
                }
                val adapter = QuestionListingTrendingVocalsAdapter(
                    songsItems
                ) { (_, second): Pair<Int?, Song> ->
                    val item = items[bindingAdapterPosition]!!
                    item.gameId = second.audioId
                    this@ActivityGamesAdapter.onItemClickListener.onItemClick(
                        Triple(
                            item, CLICK_AUDIO_ITEM, "Audio"
                        )
                    )
                }
                val concatAdapter = ConcatAdapter(headerAdapter, adapter)
                rowBinding!!.rvAudios.adapter = concatAdapter
            }
        }

        init {
            assert(rowBinding != null)
            layoutManager = LinearLayoutManager(rowBinding!!.root.context, RecyclerView.HORIZONTAL, false)
            Y_BUFFER = ViewConfiguration.get(rowBinding!!.root.context).scaledPagingTouchSlop.toFloat()
            headerAdapter = HeaderAdapter(
                "Vocals", "Short Audios That Make You Feel Better", ContextCompat.getDrawable(
                    rowBinding!!.root.context, R.drawable.mic_icon
                )!!
            )
        }
    }

    inner class WatchlistViewHolder(view: View?) : BaseViewHolder(view) {
        private val rowBinding: ItemWatchListQuestionListingBinding? = DataBindingUtil.bind(itemView)
        private val layoutManager: LinearLayoutManager
        private lateinit var headerAdapter: HeaderWatchlistAdapter
        private lateinit var headerSecondAdapter: SecondHeaderWatchlistAdapter
        private var preX = 0f
        private var preY = 0f
        private val Y_BUFFER: Float
        override fun onBind(position: Int) {
            val loginDayActivityInfoList = items[position]!!

            if (loginDayActivityInfoList.watchListResponse != null) {
                rowBinding!!.rvWatchlist.layoutManager = layoutManager

                if (preferenceHelper.getInt(Constants.WATCHLIST_POSITION_INDEX) > 0 && preferenceHelper.getInt(
                        Constants.WATCHLIST_POSITION_INDEX
                    ) < loginDayActivityInfoList.watchListResponse!!.size
                ) {
                    rowBinding!!.rvWatchlist.scrollToPosition(preferenceHelper.getInt(Constants.WATCHLIST_POSITION_INDEX))
                }
                rowBinding!!.rvWatchlist.setHasFixedSize(true)
                rowBinding!!.rvWatchlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val positionIndex = layoutManager.findFirstVisibleItemPosition()
                        preferenceHelper.putValue(Constants.WATCHLIST_POSITION_INDEX, positionIndex)
                    }
                })

                rowBinding!!.rvWatchlist.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        if (e.action == MotionEvent.ACTION_DOWN) {
                            rowBinding!!.rvWatchlist.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        if (e.action == MotionEvent.ACTION_MOVE) {
                            if (abs(e.x - preX) > abs(e.y - preY)) {
                                rowBinding!!.rvWatchlist.parent.requestDisallowInterceptTouchEvent(
                                    true
                                )
                            } else if (abs(e.y - preY) > Y_BUFFER) {
                                rowBinding!!.rvWatchlist.parent.requestDisallowInterceptTouchEvent(
                                    false
                                )
                            }
                        }
                        preX = e.x
                        preY = e.y
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                })

                val adapter = FriendsPlayingWatchListAdapter(
                    rowBinding!!.root.context, loginDayActivityInfoList.watchListResponse!!
                )
//                editAddWatchListAdapter!!.setOnItemClickListener(this)
//                val adapter = WatchListAdapter(
//                    rowBinding!!.root.context,
//                    loginDayActivityInfoList.watchListResponse!!
//                )
                adapter.setOnItemClickListener(object : OnWatchListItemClickListener<android.util.Pair<Int, UserWatchList>> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onItemClick(
                        item: android.util.Pair<Int, UserWatchList>, position: Int
                    ) {
                        if (onWatchListItemClickListener != null) {
                            onWatchListItemClickListener.onItemClick(item, position)
                            when (item.first) {
                                WatchListActions.ADD_TO_WATCH_LIST -> dashboardViewModel!!.addUserToWatchList(item.second.userId!!)
                                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AcceptPendingWatchResponse> ->
                                        if (apiResponse.isSuccess && apiResponse.body != null) {
//                                                hideProgress()
                                            if (apiResponse.body.errorCode.equals(
                                                    "000", ignoreCase = true
                                                )
                                            ) {
                                                item.second.status = "CancelInvitation"
                                                adapter.notifyItemChanged(
                                                    position
                                                )
                                            }
                                        }
                                    }

                                WatchListActions.ACCEPT_INVITATION -> {
                                    item.second.status = "Watching"
                                    adapter.notifyItemChanged(position)
                                    dashboardViewModel!!.acceptPendingWatchRequest(item.second.userId!!)
                                        .observe(viewLifecycleOwner) {}

                                }
                                WatchListActions.REJECT_INVITATION -> {
                                    //adapter.notifyDataSetChanged()
//                                    adapter.removeItem(position, adapter, rowBinding!!.root.context)
                                    adapter.removeItem(item.second)
                                    //adapter.notifyDataSetChanged()
                                    dashboardViewModel!!.cancelWatchListRequest(item.second.userId!!)
                                        .observe(viewLifecycleOwner) {}
                                }
                                WatchListActions.CANCEL_INVITATION -> {
                                    //adapter.notifyDataSetChanged()
//                                    adapter.removeItem(position, adapter, rowBinding!!.root.context)
                                    adapter.removeItem(item.second)
                                    //adapter.notifyDataSetChanged()
                                    dashboardViewModel!!.cancelWatchListPendingRequest(item.second.userId!!)
                                        .observe(viewLifecycleOwner) {}
                                }

                                WatchListActions.WATCHING -> {

                                }
                            }
                        }
                    }
                })


//                headerSecondAdapter = SecondHeaderWatchlistAdapter(
//                    "Watchlist", "Stay Aware How Your Close Ones Are Doing", ContextCompat.getDrawable(
//                        rowBinding!!.root.context, R.drawable.honeycomb
//                    )!!
//                )

                val concatAdapter = ConcatAdapter(headerAdapter, adapter)
                rowBinding.rvWatchlist.adapter = concatAdapter
            }


        }

        init {
            assert(rowBinding != null)
            layoutManager = LinearLayoutManager(rowBinding!!.root.context, RecyclerView.HORIZONTAL, false)
            Y_BUFFER = ViewConfiguration.get(rowBinding.root.context).scaledPagingTouchSlop.toFloat()

            headerAdapter = HeaderWatchlistAdapter(
                "Watchlist", "Stay Aware How Your Close Ones Are Doing", ContextCompat.getDrawable(
                    rowBinding.root.context, R.drawable.watchlist_icon
                )!!
            )

        }

    }

    internal inner class PlaySoloGamesItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemQuestionCardBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null && ActivityType.getActivityTypeIcon(activity.activityType) != -1) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }


            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }


            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context, R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context, R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                                    //highlight colors
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Card", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("External", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Task", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Message", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }

            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
            cardBinding.ibSecond.setIconTintResource(R.color.color_black)

        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemQuestionCardBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })

            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!)).textColor(Color.WHITE)
                    .textSize(1, 16f).autoHide(true, 2000).color(
                        ContextCompat.getColor(
                            itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                        )
                    ).position(ViewTooltip.Position.BOTTOM).show()
            })
        }
    }

    internal inner class ExternalActivityItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemActivityExternalBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null && ActivityType.getActivityTypeIcon(activity.activityType) != -1) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }


            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)

                if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                    cardBinding.ivPlayVideo.visibility = View.VISIBLE
                }else{
                    cardBinding.ivPlayVideo.visibility = View.GONE
                }
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }


            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context, R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context, R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                                    //highlight colors
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Card", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("External", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Task", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Message", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }

            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
            cardBinding.ibSecond.setIconTintResource(R.color.color_black)

        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemActivityExternalBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })

            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!)).textColor(Color.WHITE)
                    .textSize(1, 16f).autoHide(true, 2000).color(
                        ContextCompat.getColor(
                            itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                        )
                    ).position(ViewTooltip.Position.BOTTOM).show()
            })
        }
    }
    internal inner class MoodActivityItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemActivityExternalBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null && ActivityType.getActivityTypeIcon(activity.activityType) != -1) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "UserMoodCard", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "UserMoodCard", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }


            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)

                if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                    cardBinding.ivPlayVideo.visibility = View.VISIBLE
                }else if (activity.youTubeLink == "V") {
                    cardBinding.ivPlayVideo.visibility = View.VISIBLE
                }else{
                    cardBinding.ivPlayVideo.visibility = View.GONE
                }
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }


            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "UserMoodCard" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context, R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context, R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                                    //highlight colors
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount > 0) {
                cardBinding.cardTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.text = activity.friendCount.toString()
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Card", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("UserMoodCard", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Task", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Message", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }

            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
            cardBinding.ibSecond.setIconTintResource(R.color.color_black)

        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemActivityExternalBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })

            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!)).textColor(Color.WHITE)
                    .textSize(1, 16f).autoHide(true, 2000).color(
                        ContextCompat.getColor(
                            itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                        )
                    ).position(ViewTooltip.Position.BOTTOM).show()
            })
        }
    }

    internal inner class SurveyActivityItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemActivitySurveyBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null && ActivityType.getActivityTypeIcon(activity.activityType) != -1) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }


            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }


            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context, R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context, R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                                    //highlight colors
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Card", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("External", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Task", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Message", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }

            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
            cardBinding.ibSecond.setIconTintResource(R.color.color_black)

        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemActivitySurveyBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })

            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!)).textColor(Color.WHITE)
                    .textSize(1, 16f).autoHide(true, 2000).color(
                        ContextCompat.getColor(
                            itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                        )
                    ).position(ViewTooltip.Position.BOTTOM).show()
            })
        }
    }

    internal inner class TaskActivityItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemActivityTaskMessageBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null && ActivityType.getActivityTypeIcon(activity.activityType) != -1) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }


            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }


            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context, R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context, R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                                    //highlight colors
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Card", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("External", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Task", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Message", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }

            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
            cardBinding.ibSecond.setIconTintResource(R.color.color_black)

        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemActivityTaskMessageBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })

            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!)).textColor(Color.WHITE)
                    .textSize(1, 16f).autoHide(true, 2000).color(
                        ContextCompat.getColor(
                            itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                        )
                    ).position(ViewTooltip.Position.BOTTOM).show()
            })
        }
    }

    internal inner class MessageActivityItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemActivityTaskMessageBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null && ActivityType.getActivityTypeIcon(activity.activityType) != -1) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }


            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener.onItemClick(
                            Triple(
                                item, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }



            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context, R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context, R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                                    //highlight colors
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Card", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("External", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Task", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else if (activity.activityType.equals("Message", ignoreCase = true) && activity.playgroupId != null) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }

            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
            cardBinding.ibSecond.setIconTintResource(R.color.color_black)

        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemActivityTaskMessageBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })

            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!)).textColor(Color.WHITE)
                    .textSize(1, 16f).autoHide(true, 2000).color(
                        ContextCompat.getColor(
                            itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                        )
                    ).position(ViewTooltip.Position.BOTTOM).show()
            })
        }
    }

    internal inner class FunCardViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemQuestionFunCardBinding
        private val context: Context

        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card", ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External", ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }
            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity).target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false).tapListener { v: View ->

                        onItemClickListener.onItemClick(
                            Triple(
                                activity, CLICK_MEDIA, v.tag.toString()
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
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
                GlideApp.with(itemView.context).load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
            } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context, R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag = context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text = context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        if (activity.youTubeLink != null && activity.youTubeLink.equals(
                                "Y", ignoreCase = true
                            ) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }

                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
//                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
//                            //isTagLabelHidden = true;
//                            cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_experience)
//                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_experience)
//
//                            //highlight colors
//                            val drawableSecond = AppCompatResources.getDrawable(
//                                context, R.drawable.ic_answer_question
//                            )
//                            cardBinding.ibSecond.icon = drawableSecond
//                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
//                            val drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
//                            cardBinding.ibFirst.icon = drawableFirst
//                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                        } else {
//                            when (activity.userParticipationStatus) {
//                                "None" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_will_do_it)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_will_do_it)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_commit
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Pending" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_share_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_share_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_answer_question
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                                "Settled" -> {
//                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
//                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
//                                    //highlight colors
//                                    val drawableFirst = AppCompatResources.getDrawable(
//                                        context, R.drawable.ic_show_all
//                                    )!!
//                                    DrawableCompat.setTint(
//                                        drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                                    )
//                                    cardBinding.ibFirst.icon = drawableFirst
//                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
//                                }
//                            }

                        cardBinding.ibFirst.text = context.getString(R.string.click_action_read_experience)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_experience)
                        //highlight colors
                        val drawableFirst = AppCompatResources.getDrawable(
                            context, R.drawable.ic_show_all
                        )!!
                        DrawableCompat.setTint(
                            drawableFirst, ContextCompat.getColor(context, R.color.color_black)
                        )
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        cardBinding.ibFirst.visibility = View.VISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
//                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
//                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
//                            //isTagLabelHidden = true;
//                            cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
//                            cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
//                            cardBinding.ibFirst.icon = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
//                            cardBinding.ibSecond.text = context.getString(R.string.click_action_read_messages)
//                            cardBinding.ibSecond.tag = context.getString(R.string.click_action_read_messages)
//
//                            //highlight colors
//                            val drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
//                            cardBinding.ibSecond.icon = drawableSecond
//                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
//                            val drawableFirst = AppCompatResources.getDrawable(
//                                context, R.drawable.ic_ask_question
//                            )!!
//                            DrawableCompat.setTint(
//                                drawableFirst, ContextCompat.getColor(context, R.color.color_black)
//                            )
//                            cardBinding.ibFirst.icon = drawableFirst
//                            //ZZZ or YYY may not be getting used. @neeraj's comment
//                        } else {
////                            when (activity.userParticipationStatus) {
////                                "None" -> {
////                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_i_want_to_send)
////                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_i_want_to_send)
////                                    //highlight colors
////                                    val drawableFirst = AppCompatResources.getDrawable(
////                                        context, R.drawable.ic_commit
////                                    )
////                                    cardBinding.ibFirst.icon = drawableFirst
////                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
////                                }
////                                "Pending" -> {
////                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_send_now)
////                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_send_now)
////                                    //highlight colors
////                                    //highlight colors
////                                    val drawableFirst = AppCompatResources.getDrawable(
////                                        context, R.drawable.ic_answer_question
////                                    )
////                                    cardBinding.ibFirst.icon = drawableFirst
////                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
////                                }
////                                "Settled" -> {
////                                    cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
////                                    cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
////                                    //highlight colors
////                                    val drawableFirst = AppCompatResources.getDrawable(
////                                        context, R.drawable.ic_show_all
////                                    )
////                                    cardBinding.ibFirst.icon = drawableFirst
////                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
////                                }
////                            }
//
//
////                        if (activity.getUserParticipationStatus().equals("Settled")) {
////                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
////                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
////                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
////                        } else {
////                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
////                        }
//                        }

                        cardBinding.ibFirst.text = context.getString(R.string.click_action_read_messages)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_read_messages)
                        //highlight colors
                        val drawableFirst = AppCompatResources.getDrawable(
                            context, R.drawable.ic_show_all
                        )
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                        cardBinding.ibFirst.visibility = View.VISIBLE
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = ActivityTagUtils.getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList = AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals(
                        "1toM",
                        ignoreCase = true
                    ) && (activity.userParticipationStatus.equals(
                        "None",
                        ignoreCase = true
                    ) || activity.userParticipationStatus.equals(
                        "Pending", ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true) || activityPlayGroupId().equals(
                            "1",
                            ignoreCase = true
                        ) || activityPlayGroupId().equals("2", ignoreCase = true) || activityPlayGroupId().equals(
                            "3",
                            ignoreCase = true
                        ))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }
        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemQuestionFunCardBinding.bind(itemView)
            context = itemView.context


            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.tvQuestion.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {

                    if (item.activityType.equals("FriendCircleGame", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition], CLICK_WHOLE_ITEM, v!!.tag.toString()
                            )
                        )
                    } else {
                        if (item.activityType.equals("1toM", ignoreCase = true)) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibFirst.tag.toString()
                                    )
                                )
                            } else {
                                onItemClickListener.onItemClick(
                                    Triple(
                                        items[bindingAdapterPosition], CLICK_BUTTON, cardBinding.ibSecond.tag.toString()
                                    )
                                )
                            }
                        }
                    }

                }
            })
            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                if (ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!) != "") {
                    ViewTooltip.on(v).text(ActivityTagUtils.getTagInfoText(items[bindingAdapterPosition]!!))
                        .textColor(Color.WHITE).textSize(1, 16f).autoHide(true, 1000).color(
                            ContextCompat.getColor(
                                itemView.context, ActivityTagUtils.getTagColor(items[bindingAdapterPosition]!!)
                            )
                        ).position(ViewTooltip.Position.BOTTOM).show()
                }
            })
        }
    }

    companion object {
        const val CLICK_ACTIVITY = 0
        const val CLICK_WHOLE_ITEM = 1
        const val CLICK_BUTTON = 2
        const val CLICK_MENU = 3
        const val CLICK_MEDIA = 4
        const val CLICK_SHARE = 5
        const val CLICK_REMOVE = 10
        const val CLICK_AUDIO_ITEM = 11

        const val CLICK_MORE = 6
        const val CLICK_COMMENT = 7
        const val CLICK_SENDER_PROFILE = 8
        const val CLICK_ATTACHMENT = 9
        const val CLICK_COMMENT_COUNT = 12
        const val CLICK_SHOW_MORE = 13
    }

    init {
        PlayGroupID = playGroup.playGroupId
        preferenceHelper = sharedPreferenceHelper
        this.onWatchListItemClickListener = onWatchListItemClickListener
        this.dashboardViewModel = dashboardViewModel
        this.viewLifecycleOwner = viewLifecycleOwner
    }
}