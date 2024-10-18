package com.onourem.android.activity.ui.admin.admin_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAdminMenuBinding
import com.onourem.android.activity.models.MenuInfo
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class AdminMenuAdapter(
    private val menuList: ArrayList<MenuInfo>,
    private val onItemClickListener: OnItemClickListener<MenuInfo>?,
) : RecyclerView.Adapter<AdminMenuAdapter.AudioGamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGamesViewHolder {
        return AudioGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_admin_menu, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AudioGamesViewHolder, position: Int) {
        val options = menuList[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    inner class AudioGamesViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!) {

        private val rowBinding: ItemAdminMenuBinding? = DataBindingUtil.bind(itemView)

        fun bind(menu: MenuInfo) {

            rowBinding!!.tvName.text = menu.title

        }

        init {
            rowBinding?.parent?.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    menuList[bindingAdapterPosition]
                )
            })
        }
    }

}