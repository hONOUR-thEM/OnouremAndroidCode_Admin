package com.onourem.android.activity.ui.games.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemFriendsPlayingWatchListBinding
import com.onourem.android.activity.databinding.ItemFriendsPlayingWatchListEditAndInviteBinding
import com.onourem.android.activity.databinding.ItemFriendsPlayingWatchListImageBinding
import com.onourem.android.activity.databinding.ItemFriendsPlayingWatchListInviteBinding
import com.onourem.android.activity.models.UserWatchList
import com.onourem.android.activity.models.WatchListActions
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.SimpleDateFormat
import java.util.*

class FriendsPlayingWatchListAdapter(
    private val context: Context,
    private val userWatchLists: MutableList<UserWatchList>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val optionsFit = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    private val options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private val moodOptions = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var onItemClickListener: OnWatchListItemClickListener<Pair<Int, UserWatchList>>? = null

    override fun getItemViewType(position: Int): Int {
        return userWatchLists[position].viewType
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DRAWABLE_VIEW -> {
                SingleDrawableViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_friends_playing_watch_list_image, viewGroup, false)
                )
            }
            DRAWABLE_WITH_TEXT_VIEW -> {
                SingleDrawableWithTextViewHolder(
                    LayoutInflater.from(context)
                        .inflate(
                            R.layout.item_friends_playing_watch_list_edit_and_invite,
                            viewGroup,
                            false
                        )
                )
            }
            DRAWABLE_INVITE_VIEW -> {
                SingleDrawableInviteViewHolder(
                    LayoutInflater.from(context)
                        .inflate(
                            R.layout.item_friends_playing_watch_list_invite,
                            viewGroup,
                            false
                        )
                )
            }
            else -> {
                SingleViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_friends_playing_watch_list, viewGroup, false)
                )
            }
        }
    }

    override fun onBindViewHolder(singleViewHolder: RecyclerView.ViewHolder, position: Int) {
        when (userWatchLists[position].viewType) {
            DRAWABLE_VIEW -> {
                (singleViewHolder as SingleDrawableViewHolder).bind(userWatchLists[position])
            }
            DRAWABLE_WITH_TEXT_VIEW -> {
                (singleViewHolder as SingleDrawableWithTextViewHolder).bind(userWatchLists[position])
            }
            DRAWABLE_INVITE_VIEW -> {
                (singleViewHolder as SingleDrawableInviteViewHolder).bind(userWatchLists[position])
            }
            else -> {
                (singleViewHolder as SingleViewHolder).bind(userWatchLists[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return userWatchLists.size
    }

    fun setOnItemClickListener(onItemClickListener: OnWatchListItemClickListener<Pair<Int, UserWatchList>>?) {
        this.onItemClickListener = onItemClickListener
    }

    fun notifyItemChanged(userWatchList: UserWatchList) {
        val index = userWatchLists.indexOf(userWatchList)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }

    fun modifyItem(position: Int, model: UserWatchList) {
        userWatchLists[position] = model
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(item: UserWatchList) {
        userWatchLists.remove(item)
        notifyDataSetChanged()
    }

    inner class SingleDrawableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemFriendsPlayingWatchListImageBinding
        fun bind(userWatchList: UserWatchList) {
            if (userWatchList.drawable != null) {

                itemBinding.tvCenterText.text = context.getString(R.string.find_more_friends)
                itemBinding.constraintLayoutCenterText.visibility = View.INVISIBLE
                itemBinding.constraintLayoutWatchlist.visibility = View.INVISIBLE

                itemBinding.txtSubTitle.text = userWatchList.subText
                Glide.with(context)
                    .load(userWatchList.drawable)
                    .apply(optionsFit)
                    .into(itemBinding.rivProfile)
            }
        }

        init {
            itemBinding = ItemFriendsPlayingWatchListImageBinding.bind(itemView)

            itemBinding.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                val userWatchList = userWatchLists[bindingAdapterPosition]
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        WatchListActions.WATCH_LIST_INVITE_FRIENDS,
                        userWatchList
                    ), bindingAdapterPosition
                )
            })
        }
    }

    inner class SingleDrawableWithTextViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemFriendsPlayingWatchListEditAndInviteBinding
        fun bind(userWatchList: UserWatchList) {
            if (userWatchList.drawable != null) {
                itemBinding.txtSubTitle.text = userWatchList.subText
                if (userWatchLists[bindingAdapterPosition].subText == "InviteWatchList") {
                    itemBinding.txtSubTitle.visibility = View.GONE
                } else {
                    itemBinding.txtSubTitle.visibility = View.VISIBLE
                }

                Glide.with(context)
                    .load(userWatchList.drawable)
                    .apply(optionsFit)
                    .into(itemBinding.rivProfile)
            }
        }

        init {
            itemBinding = ItemFriendsPlayingWatchListEditAndInviteBinding.bind(itemView)

            itemBinding.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                val userWatchList = userWatchLists[bindingAdapterPosition]
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        WatchListActions.WATCH_LIST_EDIT,
                        userWatchList
                    ), bindingAdapterPosition
                )
            })
        }
    }

    inner class SingleDrawableInviteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemFriendsPlayingWatchListInviteBinding
        fun bind(userWatchList: UserWatchList) {
            if (userWatchList.drawable != null) {
                Glide.with(context)
                    .load(userWatchList.drawable)
                    .apply(optionsFit)
                    .into(itemBinding.rivProfile)
            }
        }

        init {
            itemBinding = ItemFriendsPlayingWatchListInviteBinding.bind(itemView)

            itemBinding.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                val userWatchList = userWatchLists[bindingAdapterPosition]
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        WatchListActions.WATCH_LIST_INVITE_FRIENDS,
                        userWatchList
                    ), bindingAdapterPosition
                )
            })
        }
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemFriendsPlayingWatchListBinding
        fun bind(userWatchList: UserWatchList) {
            var strUrl: String? = ""
            if (userWatchList.centerText != null && userWatchList.centerText.equals(
                    context.getString(R.string.find_more_friends), ignoreCase = true
                )
            ) {
                itemBinding.tvCenterText.text = context.getString(R.string.find_more_friends)
                itemBinding.constraintLayoutCenterText.visibility = View.VISIBLE
                itemBinding.constraintLayoutWatchlist.visibility = View.INVISIBLE
            } else {
                itemBinding.constraintLayoutCenterText.visibility = View.INVISIBLE
                itemBinding.constraintLayoutWatchlist.visibility = View.VISIBLE
                when (userWatchList.status) {
                    "Watching" -> {
                        strUrl = userWatchList.expressionImageUrl
//                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.GONE
                        itemBinding.textMood.visibility = View.GONE
                        itemBinding.tvMood.visibility = View.VISIBLE
                        itemBinding.tvMood.text = userWatchList.expressionName
                        itemBinding.tvMood.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_black
                            )
                        )
                        itemBinding.tvMood.backgroundTintList =
                            AppCompatResources.getColorStateList(
                                itemView.context,
                                R.color.white
                            )
                        itemBinding.textDate.text =
                            getDateMonth(userWatchList.createdOn!!)
                    }
                    "AcceptRejectInvitation" -> {

                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.VISIBLE
                        itemBinding.textMood.visibility = View.GONE
                        itemBinding.tvMood.visibility = View.GONE
                        itemBinding.ibAccept.text =
                            itemView.context.getString(R.string.label_tick_accept_request)
                        itemBinding.ibReject.text =
                            itemView.context.getString(R.string.label_cross_accept_request)
                        //                        itemBinding.ibAccept.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_green));
//                        itemBinding.ibReject.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_pink));
                        itemBinding.ibAccept.text = HtmlCompat.fromHtml(
                            itemView.context.getString(R.string.label_tick_accept_request),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                        itemBinding.ibReject.text = HtmlCompat.fromHtml(
                            itemView.context.getString(R.string.label_cross_accept_request),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                        itemBinding.ibAccept.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_white
                            )
                        )
                        itemBinding.ibReject.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_white
                            )
                        )
                        itemBinding.textDate.text = "invited you to be on Watch List"
                    }
                    "CancelInvitation" -> {
                        itemBinding.llAcceptReject.visibility = View.GONE
                        itemBinding.textMood.visibility = View.VISIBLE
                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.textMood.text = "Cancel"
                        itemBinding.tvMood.visibility = View.GONE
                        itemBinding.textMood.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_white
                            )
                        )
                        itemBinding.textMood.backgroundTintList =
                            AppCompatResources.getColorStateList(
                                itemView.context,
                                R.color.color_pink
                            )
                        itemBinding.textDate.text = "got invited by you"
                    }
                    "AddToWatchList" -> {
                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.GONE
                        itemBinding.textMood.visibility = View.VISIBLE
                        itemBinding.textMood.text = "Add"
                        itemBinding.tvMood.visibility = View.GONE
                        itemBinding.textMood.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_white
                            )
                        )
                        itemBinding.textMood.backgroundTintList =
                            AppCompatResources.getColorStateList(
                                itemView.context,
                                R.color.color_green
                            )
                        itemBinding.textDate.text = "isn't on your WatchList"
                    }
                }
                itemBinding.constraintLayoutCenterText.visibility = View.INVISIBLE
                itemBinding.constraintLayoutWatchlist.visibility = View.VISIBLE
                itemBinding.tvName.text = userWatchList.firstName

                Glide.with(context)
                    .load(strUrl)
                    .apply(
                        if (userWatchList.status.equals(
                                "Watching",
                                ignoreCase = true
                            )
                        ) moodOptions else options
                    )
                    .into(itemBinding.ivProfile)

                Utilities.verifiedUserType(
                    itemBinding.root.context,
                    userWatchList.userTypeId,
                    itemBinding.ivIconVerified
                )
            }
        }

        init {
            itemBinding = ItemFriendsPlayingWatchListBinding.bind(itemView)
            itemBinding.ibReject.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        WatchListActions.REJECT_INVITATION,
                        userWatchLists[bindingAdapterPosition]
                    ), bindingAdapterPosition
                )
            })
            itemBinding.ibAccept.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        WatchListActions.ACCEPT_INVITATION,
                        userWatchLists[bindingAdapterPosition]
                    ), bindingAdapterPosition
                )
            })
            itemBinding.textMood.setOnClickListener(ViewClickListener { v: View? ->
                val userWatchList = userWatchLists[bindingAdapterPosition]
                if (userWatchList.status.equals("CancelInvitation", ignoreCase = true)) {
                    onItemClickListener!!.onItemClick(
                        Pair.create(
                            WatchListActions.CANCEL_INVITATION,
                            userWatchList
                        ), bindingAdapterPosition
                    )
                } else if (userWatchList.status.equals("AddToWatchList", ignoreCase = true)) {
                    onItemClickListener!!.onItemClick(
                        Pair.create(
                            WatchListActions.ADD_TO_WATCH_LIST,
                            userWatchList
                        ), bindingAdapterPosition
                    )
                }
            })

            itemBinding.constraintLayoutWatchlist.setOnClickListener(ViewClickListener { v: View? ->
                val userWatchList = userWatchLists[bindingAdapterPosition]
                if (userWatchList.status.equals("Watching", ignoreCase = true)) {
                    onItemClickListener!!.onItemClick(
                        Pair.create(
                            WatchListActions.WATCHING,
                            userWatchList
                        ), bindingAdapterPosition
                    )
                }
            })

            itemBinding.constraintLayoutCenterText.setOnClickListener(ViewClickListener { v: View? ->
                val userWatchList = userWatchLists[bindingAdapterPosition]
                onItemClickListener!!.onItemClick(
                    Pair.create(
                        WatchListActions.WATCH_LIST_EDIT,
                        userWatchList
                    ), bindingAdapterPosition
                )
            })
        }
    }

    companion object {
        const val DRAWABLE_VIEW = 1
        const val WATCHLIST_VIEW = 2
        const val DRAWABLE_WITH_TEXT_VIEW = 3
        const val DRAWABLE_INVITE_VIEW = 4

        private fun getDateMonth(strDate: String): String {
            var str = ""
            try {
                @SuppressLint("SimpleDateFormat") val fmInput = SimpleDateFormat("yyyy-MM-dd")
                val date = fmInput.parse(strDate)
                val cal = Calendar.getInstance()
                cal.time = date
                val d = Date(cal.timeInMillis)
                val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(d)
                val day = cal[Calendar.DAY_OF_MONTH].toString()
                str = "$month $day"
                return str
            } catch (e: Exception) {
            }
            return ""
        }
    }

}