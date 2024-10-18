package com.onourem.android.activity.ui.message.adapter

import android.graphics.Typeface
import android.text.TextUtils
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemMessageRowBinding
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Events.getServerTimeConversation
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.SimpleDateFormat
import java.util.*

class ConversationsAdapter(
    val data: List<Conversation>,
    private val onItemClickListener: OnItemClickListener<Pair<Int, Conversation>>
) : PaginationRVAdapter<Conversation>(
    data
) {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    override fun emptyLoadingItem(): Conversation {
        return Conversation()
    }

    override fun emptyFooterItem(): Conversation {
        return Conversation()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ConversationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_row, parent, false)
        )
    }

    fun notifyDataUpdated(conversation: Conversation) {
        notifyItemChanged(data.indexOf(conversation))
    }

    fun notifyItemRemoved(conversation: Conversation) {
        notifyItemRemoved(data.indexOf(conversation))
    }


    internal inner class ConversationViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)
        var itemBinding: ItemMessageRowBinding
        override fun onBind(position: Int) {
            val item = items[position]!!
            itemBinding.tvName.text = item.userName
            if (item.lastMessage!!.contains(" ")) {
                itemBinding.tvMessageText.text = item.lastMessage
            } else {
                val messageText = Base64Utility.decode(item.lastMessage)
                itemBinding.tvMessageText.text = messageText
            }

            //itemBinding.tvMessageText.setText(item.getLastMessage());
            if (!TextUtils.isEmpty(item.readStatus) && Objects.requireNonNull(item.readStatus)
                    .equals("1", ignoreCase = true)
            ) {
                itemBinding.tvReadStatus.visibility = View.VISIBLE
                itemBinding.tvMessageText.setTypeface(
                    itemBinding.tvMessageText.typeface,
                    Typeface.BOLD
                )
            } else {
                itemBinding.tvReadStatus.visibility = View.INVISIBLE
                itemBinding.tvMessageText.setTypeface(
                    itemBinding.tvMessageText.typeface,
                    Typeface.NORMAL
                )
            }

            //itemBinding.tvDate.setText(Utilities.getRelatedTime(item.getUpdatedDateTime()));
            itemBinding.tvDate.text =
                getServerTimeConversation(
                    item.updatedDateTime!!
                )
            Glide.with(itemBinding.root.context)
                .load(item.profilePicture)
                .apply(options)
                .placeholder(R.drawable.ic_place_holder_image)
                .into(itemBinding.rivProfile)
            Utilities.verifiedUserType(
                itemBinding.root.context,
                item.userTypeId,
                itemBinding.ivIconVerified
            )
        }

        init {
            itemBinding = ItemMessageRowBinding.bind(itemView)
            itemBinding.root.setOnClickListener(ViewClickListener {
                val conversation = data[bindingAdapterPosition]
                onItemClickListener.onItemClick(Pair.create(CLICK_WHOLE, conversation))
            })
            itemBinding.rivProfile.setOnClickListener(ViewClickListener {
                val conversation = data[bindingAdapterPosition]
                onItemClickListener.onItemClick(Pair.create(CLICK_PROFILE, conversation))
            })
        }
    }

    companion object {
        var CLICK_ACCEPT = 0
        var CLICK_ACCEPT_WATCHLIST = 5
        var CLICK_IGNORE = 1
        var CLICK_IGNORE_WATCHLIST = 6
        var CLICK_READ_POST = 2
        var CLICK_PROFILE = 3
        var CLICK_WHOLE = 4
    }
}