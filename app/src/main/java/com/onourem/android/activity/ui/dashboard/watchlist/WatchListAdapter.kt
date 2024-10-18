package com.onourem.android.activity.ui.dashboard.watchlist

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
import com.onourem.android.activity.databinding.ItemWatchListBinding
import com.onourem.android.activity.models.UserWatchList
import com.onourem.android.activity.models.WatchListActions
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.SimpleDateFormat
import java.util.*

class WatchListAdapter(
    private val context: Context,
    private val userWatchLists: MutableList<UserWatchList>
) : RecyclerView.Adapter<WatchListAdapter.SingleViewHolder>() {
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
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_watch_list, viewGroup, false)
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

    fun modifyItem(position: Int, model: UserWatchList) {
        userWatchLists[position] = model
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        userWatchLists.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(position: Int, watchListAdapter: WatchListAdapter, context: Context) {
        if (watchListAdapter.itemCount == 2) {
            val userWatchList = UserWatchList()
            userWatchList.centerText = context.getString(R.string.add_to_watch_list)
            watchListAdapter.modifyItem(1, userWatchList)
        }
        watchListAdapter.removeItem(position)
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemWatchListBinding
        fun bind(userWatchList: UserWatchList) {
            itemBinding.card.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color_white
                )
            )
            itemBinding.tvMood.visibility = View.GONE
            itemBinding.textMood.visibility = View.GONE
            itemBinding.tvCenterText.visibility = View.GONE
            itemBinding.container.visibility = View.INVISIBLE
            var strUrl: String? = ""
            if (userWatchList.centerText != null && userWatchList.centerText.equals(
                    context.getString(R.string.edit_watch_list), ignoreCase = true
                )
            ) {
                itemBinding.tvCenterText.text = context.getString(R.string.edit_watch_list)
                itemBinding.tvCenterText.visibility = View.VISIBLE
                itemBinding.tvMood.visibility = View.GONE
                itemBinding.textMood.visibility = View.INVISIBLE
                itemBinding.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorAccent
                    )
                )
            } else if (userWatchList.centerText != null && userWatchList.centerText.equals(
                    context.getString(R.string.add_to_watch_list), ignoreCase = true
                )
            ) {
                itemBinding.tvCenterText.text = context.getString(R.string.add_to_watch_list)
                itemBinding.tvCenterText.visibility = View.VISIBLE
                itemBinding.tvMood.visibility = View.GONE
                itemBinding.textMood.visibility = View.INVISIBLE
                itemBinding.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorAccent
                    )
                )
            } else {
                itemBinding.container.visibility = View.VISIBLE
                when (userWatchList.status) {
                    "Watching" -> {
                        strUrl = userWatchList.expressionImageUrl
                        itemBinding.tvMood.text = userWatchList.expressionName
                        itemBinding.llAcceptReject.visibility = View.GONE
                        itemBinding.tvMood.visibility = View.VISIBLE
                        itemBinding.tvMood.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_black
                            )
                        )
                        itemBinding.textDate.text = getDateMonth(userWatchList.createdOn!!)
                    }
                    "AcceptRejectInvitation" -> {
                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.VISIBLE
                        itemBinding.textMood.visibility = View.GONE
                        itemBinding.ibAccept.text = HtmlCompat.fromHtml(
                            itemView.context.getString(R.string.label_tick_accept_request),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                        itemBinding.ibReject.text = HtmlCompat.fromHtml(
                            itemView.context.getString(R.string.label_cross_accept_request),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                        itemBinding.ibAccept.backgroundTintList =
                            AppCompatResources.getColorStateList(
                                itemView.context,
                                R.color.color_green
                            )
                        itemBinding.ibReject.backgroundTintList =
                            AppCompatResources.getColorStateList(
                                itemView.context,
                                R.color.color_pink
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
                        strUrl = userWatchList.profilePictureUrl
                        itemBinding.llAcceptReject.visibility = View.GONE
                        itemBinding.textMood.visibility = View.VISIBLE
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
                        itemBinding.textDate.text = "you invited"
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
                        itemBinding.textDate.text =
                            context.resources.getString(R.string.isnt_on_ur_watchlist)
                    }
                }
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
            itemBinding = ItemWatchListBinding.bind(itemView)
            itemView.setOnClickListener(ViewClickListener { view: View? ->
                if (onItemClickListener != null) {
                    val userWatchList = userWatchLists[bindingAdapterPosition]
                    if (userWatchList.centerText.equals(
                            context.resources.getString(R.string.edit_watch_list),
                            ignoreCase = true
                        )
                    ) {
                        onItemClickListener!!.onItemClick(
                            Pair.create(
                                WatchListActions.WATCH_LIST_EDIT,
                                userWatchList
                            ), bindingAdapterPosition
                        )
                    } else if (userWatchList.centerText.equals(
                            context.resources.getString(R.string.add_to_watch_list),
                            ignoreCase = true
                        )
                    ) {
                        onItemClickListener!!.onItemClick(
                            Pair.create(
                                WatchListActions.WATCH_LIST_ADD,
                                userWatchList
                            ), bindingAdapterPosition
                        )
                    } else if (userWatchList.status.equals("Watching", ignoreCase = true)) {
                        onItemClickListener!!.onItemClick(
                            Pair.create(
                                WatchListActions.WHOLE_ITEM,
                                userWatchList
                            ), bindingAdapterPosition
                        )
                    }
                }
            })
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
        }
    }

    companion object {
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