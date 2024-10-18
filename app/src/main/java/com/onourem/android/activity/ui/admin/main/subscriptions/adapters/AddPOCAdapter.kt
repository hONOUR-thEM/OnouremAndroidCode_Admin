package com.onourem.android.activity.ui.admin.main.subscriptions.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemPocAddBinding
import com.onourem.android.activity.models.POCUser
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class AddPOCAdapter(
    private val listFiltered: ArrayList<POCUser>,
    private val onItemClickListener: OnItemClickListener<POCUser>?,
) : RecyclerView.Adapter<AddPOCAdapter.SongsViewHolder>() {

    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SongsViewHolder {
        return SongsViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_poc_add, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: SongsViewHolder, position: Int) {
        val song = listFiltered[position]
        viewHolder.bind(song)
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    inner class SongsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemPocAddBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(user: POCUser) {

            rowBinding!!.tvName.text = user.name
            Glide.with(itemView.context)
                .load(user.imageUrl)
                .apply(options)
                .into(rowBinding!!.rivProfile)

        }

        init {
            rowBinding!!.parent.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(listFiltered[bindingAdapterPosition])
            })
        }
    }
}
