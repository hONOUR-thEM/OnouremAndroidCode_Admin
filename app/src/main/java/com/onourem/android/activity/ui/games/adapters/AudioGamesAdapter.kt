package com.onourem.android.activity.ui.games.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioBinding
import com.onourem.android.activity.models.AudioInfo
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener


class AudioGamesAdapter(
    private val audioInfoList: ArrayList<AudioInfo>,
    private val onItemClickListener: OnItemClickListener<Pair<Int, AudioInfo>>?,
) : RecyclerView.Adapter<AudioGamesAdapter.AudioGamesViewHolder>() {
    val CLICK_SHARE = 5
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGamesViewHolder {
        return AudioGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio, parent, false)
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
        private val rowBinding: ItemAudioBinding? = DataBindingUtil.bind(itemView)
        fun bind(activity: AudioInfo) {


            if (activity.isPlaying) {

                val unwrappedDrawable: Drawable = rowBinding!!.parent.background
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(rowBinding!!.root.context, R.color.gray_color_light)
                )

                rowBinding!!.parent.background = wrappedDrawable
            } else {

                val unwrappedDrawable: Drawable = rowBinding!!.parent.background
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(rowBinding!!.root.context, R.color.white)
                )

                rowBinding!!.parent.background = wrappedDrawable

            }

            Glide.with(itemView.context)
                .load(R.drawable.ic_logo)
                .apply(options)
                .into(rowBinding!!.rivProfile)

        }

        init {

        }
    }
}