package com.onourem.android.activity.ui.message.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.FooterViewHolder
import com.onourem.android.activity.ui.survey.ProgressViewHolder

abstract class MessagePaginationAdapter<T> protected constructor(items: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val emptyLoadingItem: T
    private val emptyFooterItem: T
    protected var items: MutableList<T>?
    private var isLoaderVisible = false
    private var isFooterVisible = false
    private var footerMessage = ""
    override fun getItemViewType(position: Int): Int {
        val message = items!![position] as com.onourem.android.activity.models.Message
        return if (isLoaderVisible) {
            if (position == items!!.size - 1) {
                VIEW_TYPE_LOADING
            } else {
                if (message.isLoginUserSame.equals("Y", ignoreCase = true)) {
                    if (message.status.equals("2", ignoreCase = true)) {
                        VIEW_TYPE_DELETED_RIGHT
                    } else {
                        VIEW_TYPE_NORMAL_RIGHT
                    }
                } else {
                    if (message.status != null && message.status.equals("2", ignoreCase = true)) {
                        VIEW_TYPE_DELETED_LEFT
                    } else {
                        VIEW_TYPE_NORMAL_LEFT
                    }
                }
            }
        } else if (isFooterVisible) {
            if (position == items!!.size - 1) {
                VIEW_TYPE_FOOTER
            } else {
                if (message.isLoginUserSame.equals("Y", ignoreCase = true)) {
                    if (message.status.equals("2", ignoreCase = true)) {
                        VIEW_TYPE_DELETED_RIGHT
                    } else {
                        VIEW_TYPE_NORMAL_RIGHT
                    }
                } else {
                    if (message.status != null && message.status.equals("2", ignoreCase = true)) {
                        VIEW_TYPE_DELETED_LEFT
                    } else {
                        VIEW_TYPE_NORMAL_LEFT
                    }
                }
            }
        } else {
            if (message.isLoginUserSame.equals("Y", ignoreCase = true)) {
                if (message.status.equals("2", ignoreCase = true)) {
                    VIEW_TYPE_DELETED_RIGHT
                } else {
                    VIEW_TYPE_NORMAL_RIGHT
                }
            } else {
                if (message.status != null && message.status.equals("2", ignoreCase = true)) {
                    VIEW_TYPE_DELETED_LEFT
                } else {
                    VIEW_TYPE_NORMAL_LEFT
                }
            }
        }
    }

    fun getMessageItems(): List<T>? {
        return items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL_LEFT -> onCreateItemViewHolder(
                parent,
                VIEW_TYPE_NORMAL_LEFT
            )
            VIEW_TYPE_NORMAL_RIGHT -> onCreateItemViewHolder(
                parent,
                VIEW_TYPE_NORMAL_RIGHT
            )
            VIEW_TYPE_DELETED_LEFT -> onCreateItemViewHolder(
                parent,
                VIEW_TYPE_DELETED_LEFT
            )
            VIEW_TYPE_DELETED_RIGHT -> onCreateItemViewHolder(
                parent,
                VIEW_TYPE_DELETED_RIGHT
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
        if (holder.itemViewType == VIEW_TYPE_NORMAL_LEFT) {
            holder.onBind(position)
        } else if (holder.itemViewType == VIEW_TYPE_NORMAL_RIGHT) {
            holder.onBind(position)
        } else if (holder.itemViewType == VIEW_TYPE_DELETED_LEFT) {
            holder.onBind(position)
        } else if (holder.itemViewType == VIEW_TYPE_DELETED_RIGHT) {
            holder.onBind(position)
        } else if (holder.itemViewType == VIEW_TYPE_LOADING) {
            holder.onBind(position)
        } else if (holder.itemViewType == VIEW_TYPE_FOOTER) {
            holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return items!!.size
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
        items!!.addAll(postItems!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        items!!.add(emptyLoadingItem)
        notifyItemInserted(items!!.size - 1)
    }

    fun removeLoading() {
        if (!isLoaderVisible) return
        isLoaderVisible = false
        val position = items!!.indexOf(emptyLoadingItem)
        if (position >= 0) {
            items!!.remove(emptyLoadingItem)
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
        if (!items!!.contains(emptyFooterItem)) {
            items!!.add(emptyFooterItem)
            notifyItemInserted(items!!.size - 1)
        } else {
            notifyItemChanged(items!!.size - 1)
        }
    }

    fun removeFooter() {
        //if (!isFooterVisible) return;
        isFooterVisible = false
        val position = items!!.indexOf(emptyFooterItem)
        if (position >= 0) {
            items!!.remove(emptyFooterItem)
            notifyItemRemoved(position)
        }
    }

    protected abstract fun emptyLoadingItem(): T
    protected abstract fun emptyFooterItem(): T
    protected abstract fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder
    fun removeItem(item: T) {
        if (items != null && !items!!.isEmpty()) {
            val position = items!!.indexOf(item)
            items!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateItem(item: T, pos: Int) {
        if (items != null && !items!!.isEmpty()) {
            notifyItemChanged(pos, item)
        }
    }

    fun updateItem(item: T): Int {
        var position = 0
        if (items != null && !items!!.isEmpty()) {
            position = items!!.indexOf(item)
            notifyItemChanged(position)
        }
        return position
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        items!!.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetData(newData: List<T>?) {
        isLoaderVisible = false
        isFooterVisible = false
        items!!.clear()
        notifyDataSetChanged()
        items!!.addAll(newData!!)
        notifyDataSetChanged()
    }

    fun hasValidItems(): Boolean {
        return !(items!!.contains(emptyLoadingItem) || items!!.contains(emptyFooterItem))
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL_LEFT = 1
        private const val VIEW_TYPE_NORMAL_RIGHT = 11
        private const val VIEW_TYPE_DELETED_LEFT = 100
        private const val VIEW_TYPE_DELETED_RIGHT = 101
        private const val VIEW_TYPE_FOOTER = 2
    }

    init {
        this.items = items
        emptyLoadingItem = emptyLoadingItem()
        emptyFooterItem = emptyFooterItem()
    }
}