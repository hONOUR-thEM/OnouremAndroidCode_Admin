package com.onourem.android.activity.ui.audio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioCategoryBinding
import com.onourem.android.activity.ui.audio.models.AudioStats
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener


class AudioStatsAdapter(
    private val audioInfoList: ArrayList<AudioStats>,
    private val onItemClickListener: OnItemClickListener<AudioStats>?,
) : RecyclerView.Adapter<AudioStatsAdapter.AudioGamesViewHolder>() {
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGamesViewHolder {
        return AudioGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AudioGamesViewHolder, position: Int) {
        val options = audioInfoList[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return audioInfoList.size
    }

    inner class AudioGamesViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {


        private val rowBinding: ItemAudioCategoryBinding? = DataBindingUtil.bind(itemView)

        fun bind(category: AudioStats) {

            rowBinding!!.tvName.text = category.name + category.numbers

        }

        init {
            rowBinding?.parent?.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    audioInfoList[bindingAdapterPosition]
                )
            })
        }
    }
}