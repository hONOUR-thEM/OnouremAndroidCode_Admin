package com.onourem.android.activity.ui.circle.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemMobileNumberBinding
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MobileNumberAdapter(
    private var list: ArrayList<String>,
    private val onItemClickListener: OnItemClickListener<String>?,
) : RecyclerView.Adapter<MobileNumberAdapter.SongsViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SongsViewHolder {
        return SongsViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_mobile_number, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: SongsViewHolder, position: Int) {
        val song = list[position]
        viewHolder.bind(song)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNumber(number: String) {
        list.add(number)
        notifyDataSetChanged()
    }

    inner class SongsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemMobileNumberBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(number: String) {

            rowBinding!!.tvNumber.text = number
        }

        init {
            rowBinding!!.ivClose.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(list[bindingAdapterPosition])
            })
        }
    }

}
