package com.onourem.android.activity.ui.dashboard.watchlist

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
import com.onourem.android.activity.databinding.ItemEditAddWatchListBinding
import com.onourem.android.activity.models.UserWatchList
import com.onourem.android.activity.models.WatchListActions
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class EditAddWatchListAdapter(
    private val context: Context,
    private val userWatchLists: MutableList<UserWatchList>
) : RecyclerView.Adapter<EditAddWatchListAdapter.SingleViewHolder>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var onItemClickListener: OnWatchListItemClickListener<Pair<Int, UserWatchList>>? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_edit_add_watch_list, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(userWatchLists[position])
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

    fun removeItem(position: Int) {
        userWatchLists.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemEditAddWatchListBinding
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
                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.GONE
                        itemBinding.textMood.visibility = View.VISIBLE
                        itemBinding.textMood.text = "Watching"
                        itemBinding.textDate.text = "and you are"
                        itemBinding.textMood.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_white
                            )
                        )
                        itemBinding.textMood.backgroundTintList =
                            AppCompatResources.getColorStateList(
                                itemView.context,
                                R.color.color_blue
                            )
                    }
                    "AcceptRejectInvitation" -> {
                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.VISIBLE
                        itemBinding.textMood.visibility = View.GONE
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
                    .apply(options)
                    .into(itemBinding.ivProfile)
                Utilities.verifiedUserType(
                    itemBinding.root.context,
                    userWatchList.userTypeId,
                    itemBinding.ivIconVerified
                )
            }
        }

        init {
            itemBinding = ItemEditAddWatchListBinding.bind(itemView)
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
                } else if (userWatchList.status.equals("Watching", ignoreCase = true)) {
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
                        WatchListActions.WATCH_LIST_FIND_MORE_FRIENDS,
                        userWatchList
                    ), bindingAdapterPosition
                )
            })
        }
    }
}