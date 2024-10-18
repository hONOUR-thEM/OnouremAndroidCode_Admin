package com.onourem.android.activity.ui.message.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.util.Linkify
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.models.Message
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Events.getServerTimeConversation
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.io.IOException


class MessageAdapter(
    private val context: Context,
    var preferenceHelper: SharedPreferenceHelper,
    var list: ArrayList<Message>?,
    private val onItemClickListener: OnItemClickListener<Pair<Int, Message>>?
) : MessagePaginationAdapter<Message>(
    list!! as MutableList<Message>
) {
    override fun emptyLoadingItem(): Message {
        return Message()
    }

    override fun emptyFooterItem(): Message {
        return Message()
    }

    val data: List<Message>?
        get() = list

    val options = RequestOptions().fitCenter().transform(FitCenter(), RoundedCorners(1)) //15


    override fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL_LEFT -> {
                MessageLeftViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_text_left, parent, false)
                )
            }

            VIEW_TYPE_NORMAL_RIGHT -> {
                MessageRightViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_text_right, parent, false)
                )
            }

            VIEW_TYPE_DELETED_LEFT -> {
                MessageDeletedLeftViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_text_deleted_left, parent, false)
                )
            }

            else -> {
                MessageDeletedRightViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_text_deleted_right, parent, false)
                )
            }
        }
    }

    inner class MessageLeftViewHolder internal constructor(itemView: View) :
        BaseViewHolder(itemView) {
        var messageTV: TextView
        var ivImage: AppCompatImageView
        var dateTV: TextView
        override fun onBind(position: Int) {
            val messageModel = list!![position]
            val messageText = Base64Utility.decode(messageModel.messageText)
            if (messageModel.status != null && messageModel.status.equals("2", ignoreCase = true)) {
                messageTV.text = messageModel.messageText
            } else {
                messageTV.text = messageText
                Linkify.addLinks(messageTV, Linkify.WEB_URLS)
            }
            dateTV.text =
                getServerTimeConversation(messageModel.createdDateTime!!)
//            if (messageText.contains("https://e859h")) {
//                val url = messageText.extractUrls()
//                if (url != "") {
//                    ivImage.visibility = View.VISIBLE
////                    fetchLinkMetadata(url, ivImage)
//                    GlideApp.with(context).load(url)
//                        .apply(options)
//                        .into(ivImage)
//
//                } else {
//                    ivImage.visibility = View.GONE
//                }
//            } else {
//                ivImage.visibility = View.GONE
//            }
            ivImage.visibility = View.GONE
        }

        init {

            val policy = ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)

            messageTV = itemView.findViewById(R.id.message_text)
            ivImage = itemView.findViewById(R.id.ivImage)
            dateTV = itemView.findViewById(R.id.date_text)
            itemView.setOnLongClickListener { view: View? ->
                if (onItemClickListener != null) {
                    val messageModel = list!![bindingAdapterPosition]
                    onItemClickListener.onItemClick(
                        Pair.create(
                            VIEW_TYPE_NORMAL_LEFT,
                            messageModel
                        )
                    )
                }
                true // returning true instead of false, works for me
            }
        }
    }

    inner class MessageDeletedLeftViewHolder internal constructor(itemView: View) :
        BaseViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        override fun onBind(position: Int) {
            val messageModel = list!![position]
            val messageText = Base64Utility.decode(messageModel.messageText)
            if (messageModel.status != null && messageModel.status.equals("2", ignoreCase = true)) {
                messageTV.text = messageModel.messageText + " "
            } else {
                messageTV.text = messageText
                Linkify.addLinks(messageTV, Linkify.WEB_URLS)
            }
            dateTV.text =
                getServerTimeConversation(messageModel.createdDateTime!!)
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
        }
    }

    inner class MessageDeletedRightViewHolder internal constructor(itemView: View) :
        BaseViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        override fun onBind(position: Int) {
            val messageModel = list!![position]
            val messageText = Base64Utility.decode(messageModel.messageText)
            if (messageModel.status != null && messageModel.status.equals("2", ignoreCase = true)) {
                messageTV.text = messageModel.messageText + " "
            } else {
                messageTV.text = messageText
                Linkify.addLinks(messageTV, Linkify.WEB_URLS)
            }
            dateTV.text =
                getServerTimeConversation(messageModel.createdDateTime!!)
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
        }
    }

    inner class MessageRightViewHolder internal constructor(itemView: View) :
        BaseViewHolder(itemView) {
        var messageTV: TextView
        var ivImage: AppCompatImageView
        var dateTV: TextView
        override fun onBind(position: Int) {
            val messageModel = list!![position]
            val messageText = Base64Utility.decode(messageModel.messageText)
            if (messageModel.status != null && messageModel.status.equals("2", ignoreCase = true)) {
                messageTV.text = messageModel.messageText
            } else {
                messageTV.text = messageText
                Linkify.addLinks(messageTV, Linkify.WEB_URLS)
            }
            messageTV.text = messageText
            dateTV.text =
                getServerTimeConversation(messageModel.createdDateTime!!)
//            if (messageText.contains("https://e859h")) {
//                val url = messageText.extractUrls()
//                if (url != "") {
//                    ivImage.visibility = View.VISIBLE
////                    fetchLinkMetadata(url, ivImage)
//                    GlideApp.with(context).load(url)
//                        .apply(options)
//                        .into(ivImage)
//
//                } else {
//                    ivImage.visibility = View.GONE
//                }
//            } else {
//                ivImage.visibility = View.GONE
//            }

            ivImage.visibility = View.GONE
        }

        init {
            val policy = ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)

            messageTV = itemView.findViewById(R.id.message_text)
            ivImage = itemView.findViewById(R.id.ivImage)
            dateTV = itemView.findViewById(R.id.date_text)
            itemView.setOnLongClickListener { view: View? ->
                if (onItemClickListener != null) {
                    val messageModel = list!![bindingAdapterPosition]
                    onItemClickListener.onItemClick(
                        Pair.create(
                            VIEW_TYPE_NORMAL_RIGHT,
                            messageModel
                        )
                    )
                }
                true // returning true instead of false, works for me
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: Message) {
        val position = 0
        if (list != null) {
            list!!.add(position, item)
            notifyDataSetChanged()
        }
    }

    // Example method to fetch metadata using JSoup
    private fun fetchLinkMetadata(url: String, messageIv: ImageView) {
        try {
//            val document: Document = Jsoup.connect(url).get()
//            val title: String = document.title()
//            val description: String = document.select("meta[name=description]").attr("content")
//            val imageUrl: String = document.select("meta[property=og:image]").attr("content")
//
//            // Now you have the metadata, you can display it in your chat window
//            displayLinkPreview(title, description, imageUrl)


            GlideApp.with(context).load(R.drawable.default_place_holder)
                .apply(options)
                .into(messageIv)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    companion object {
        private const val ITEM_LONG_PRESS = 0
        const val VIEW_TYPE_NORMAL_LEFT = 1
        const val VIEW_TYPE_NORMAL_RIGHT = 11
        const val VIEW_TYPE_DELETED_LEFT = 100
        const val VIEW_TYPE_DELETED_RIGHT = 101
    }
}