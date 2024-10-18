package com.onourem.android.activity.ui.games.adapters.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.FooterViewHolder
import com.onourem.android.activity.ui.survey.ProgressViewHolder
import com.onourem.android.activity.ui.utils.Constants

abstract class ActivityGamesPaginationAdapter<T> protected constructor(items: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val emptyLoadingItem: T
    private val emptyFooterItem: T
    protected var items: MutableList<T>
    private var isLoaderVisible = false
    private var isFooterVisible = false
    private var footerMessage = ""
    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == items.size - 1) {
                VIEW_TYPE_LOADING
            } else {
                checkActivityType(position, Constants.LOADER)
            }
        } else if (isFooterVisible) {
            if (position == items.size - 1) {
                VIEW_TYPE_FOOTER
            } else {
                checkActivityType(position, Constants.FOOTER)
            }
        } else {
            checkActivityType(position, "")
        }
    }

    private fun checkActivityType(position: Int, from: String): Int {
        val item = items[position] as LoginDayActivityInfoList
        return if (item.activityType != null && item.activityType == Constants.ACTIVITY_FRIEND_CIRCLE_GAME) {
            VIEW_TYPE_FC_GAME
        } else if (item.activityType != null && item.activityType == Constants.ACTIVITY_AUDIO) {
            VIEW_TYPE_AUDIO
        } else if (item.activityType != null && item.activityType == Constants.ACTIVITY_WATCHLIST) {
            VIEW_TYPE_WATCHLIST
        } else if (item.activityType != null && item.activityType == Constants.ACTIVITY_CARD) {
            VIEW_TYPE_FUN_CARD
        } else if (item.activityType != null && item.activityType == Constants.ACTIVITY_POST) {
            VIEW_TYPE_POST
        } else if (item.activityType != null && item.activityType == Constants.ACTIVITY_EXTERNAL) {
            VIEW_TYPE_EXTERNAL
        }else if (item.activityType != null && item.activityType == Constants.ACTIVITY_MOOD) {
            VIEW_TYPE_MOOD
        }else if (item.activityType != null && item.activityType == Constants.ACTIVITY_TASK) {
            VIEW_TYPE_TASK
        }else if (item.activityType != null && item.activityType == Constants.ACTIVITY_MESSAGE) {
            VIEW_TYPE_MESSAGE
        }else if (item.activityType != null && item.activityType == Constants.ACTIVITY_SURVEY) {
            VIEW_TYPE_SURVEY
        }else if (item.activityType == null && from == Constants.LOADER) {
            VIEW_TYPE_LOADING
        } else if (item.activityType == null && from == Constants.FOOTER) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    @JvmName("getItems1")
    fun getItems(): List<T> {
        return items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL,
            VIEW_TYPE_AUDIO,
            VIEW_TYPE_WATCHLIST,
            VIEW_TYPE_FC_GAME,
            VIEW_TYPE_FUN_CARD,
            VIEW_TYPE_POST,
            VIEW_TYPE_TASK,
            VIEW_TYPE_MESSAGE,
            VIEW_TYPE_EXTERNAL,
            VIEW_TYPE_MOOD,
            VIEW_TYPE_SURVEY -> onCreateItemViewHolder(
                parent,
                viewType
            )
            VIEW_TYPE_LOADING -> ProgressViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            )
            VIEW_TYPE_FOOTER -> FooterViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false),
                footerMessage
            )
            else -> throw IllegalArgumentException("VIEW TYPE is invalid")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_NORMAL,
            VIEW_TYPE_AUDIO,
            VIEW_TYPE_WATCHLIST,
            VIEW_TYPE_FC_GAME,
            VIEW_TYPE_FUN_CARD,
            VIEW_TYPE_POST,
            VIEW_TYPE_TASK,
            VIEW_TYPE_MESSAGE,
            VIEW_TYPE_EXTERNAL,
            VIEW_TYPE_MOOD,
            VIEW_TYPE_SURVEY,
            VIEW_TYPE_LOADING,
            VIEW_TYPE_FOOTER -> holder.onBind(
                position
            )
            else -> throw IllegalArgumentException("VIEW TYPE is invalid")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(postItems: List<T>?) {
        if (isLoaderVisible) {
            removeLoading()
            notifyDataSetChanged()
        }
        if (isFooterVisible) {
            removeFooter()
            notifyDataSetChanged()
        }
        items.addAll(postItems!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        items.add(emptyLoadingItem)
        notifyItemInserted(items.size - 1)
    }

    fun removeLoading() {
        if (!isLoaderVisible) return
        isLoaderVisible = false
        val position = items.indexOf(emptyLoadingItem)
        if (position >= 0) {
            items.remove(emptyLoadingItem)
            notifyItemRemoved(position)
        }
    }

    fun addFooter(footerMessage: String) {
        this.footerMessage = ""
        this.footerMessage = footerMessage
        if (isLoaderVisible) {
            removeLoading()
        }
        isFooterVisible = true
        if (!items.contains(emptyFooterItem)) {
            items.add(emptyFooterItem)
            notifyItemInserted(items.size - 1)
        } else {
            notifyItemChanged(items.size - 1)
        }
    }

    fun removeFooter() {
        //if (!isFooterVisible) return;
        isFooterVisible = false
        val position = items.indexOf(emptyFooterItem)
        if (position >= 0) {
            items.remove(emptyFooterItem)
            notifyItemRemoved(position)
        }
    }

    protected abstract fun emptyLoadingItem(): T
    protected abstract fun emptyFooterItem(): T
    protected abstract fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder
    open fun removeItem(item: T) {
        if (items.isNotEmpty()) {
            val position = items.indexOf(item)
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addItemAtPosition(pos: Int, item: T) {
        if (items.isNotEmpty() && items.size > pos) {
            items.add(pos, item)
            notifyItemInserted(pos)
        }
    }

    fun updateItem(item: T): Int {
        var position = 0
        if (items.isNotEmpty()) {
            position = items.indexOf(item)
            notifyItemChanged(position)
        }
        return position
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        items.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetData(newData: List<T>?) {
        isLoaderVisible = false
        isFooterVisible = false
        items.clear()
        notifyDataSetChanged()
        items.addAll(newData!!)
        notifyDataSetChanged()
    }

    fun hasValidItems(): Boolean {
        return !(items.contains(emptyLoadingItem) || items.contains(emptyFooterItem))
    }

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_NORMAL = 1
        const val VIEW_TYPE_FOOTER = 2
        const val VIEW_TYPE_AUDIO = 3
        const val VIEW_TYPE_WATCHLIST = 4
        const val VIEW_TYPE_FC_GAME = 5
        const val VIEW_TYPE_FUN_CARD = 6
        const val VIEW_TYPE_POST = 7
        const val VIEW_TYPE_TASK = 8
        const val VIEW_TYPE_MESSAGE = 9
        const val VIEW_TYPE_EXTERNAL = 10
        const val VIEW_TYPE_MOOD = 12
        const val VIEW_TYPE_SURVEY = 11
    }

    init {
        this.items = items
        emptyLoadingItem = this.emptyLoadingItem()
        emptyFooterItem = this.emptyFooterItem()
    }
}