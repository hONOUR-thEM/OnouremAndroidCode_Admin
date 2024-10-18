package com.onourem.android.activity.ui.audio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioLanguageBinding
import com.onourem.android.activity.ui.audio.models.Language
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener


class AudioLanguageAdapter(
    private val audioInfoList: ArrayList<Language>,
    private val onItemClickListener: OnItemClickListener<Language>?,
) : RecyclerView.Adapter<AudioLanguageAdapter.AudioGamesViewHolder>() {
    val CLICK_SHARE = 5
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGamesViewHolder {
        return AudioGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_language, parent, false)
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

        private val rowBinding: ItemAudioLanguageBinding? = DataBindingUtil.bind(itemView)

        fun bind(category: Language) {

            rowBinding!!.tvName.text = category.languageName

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