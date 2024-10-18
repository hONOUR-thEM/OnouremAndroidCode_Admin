package com.onourem.android.activity.ui.invite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.InviteGroupItemInfo

/**
 * Created by ashu on 6/2/17.
 */
internal class InviteInfoGroupAdapter(
    var items: ArrayList<InviteGroupItemInfo>,
    private val context: Context
) : RecyclerView.Adapter<InviteInfoGroupAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.fragment_info_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        holder.recyclerView.adapter = InviteAdapter(items[position].inviteInfoArrayList)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView

        init {
            recyclerView = itemView.findViewById<View>(R.id.rvItem) as RecyclerView
        }
    }

}