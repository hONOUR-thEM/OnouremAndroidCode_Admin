package com.onourem.android.activity.ui.audio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioPlayerBinding
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener


class AudioPlayerAdapter(
    private val audioList: ArrayList<Song>,
    private val onItemClickListener: OnItemClickListener<Song>?,
) : RecyclerView.Adapter<AudioPlayerAdapter.AudioGamesViewHolder>() {
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGamesViewHolder {
        return AudioGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_player, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AudioGamesViewHolder, position: Int) {
        val options = audioList[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    inner class AudioGamesViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {


        private val rowBinding: ItemAudioPlayerBinding? = DataBindingUtil.bind(itemView)

        fun bind(song: Song) {

            rowBinding!!.songTitle.text = song.title
            rowBinding!!.txtName.text = song.userName

        }

        init {
            rowBinding?.clPlayRecording?.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    audioList[bindingAdapterPosition]
                )
            })
        }
    }

    fun updatePlayingItem(item: Song) {
        var position: Int
        if (audioList.isNotEmpty()) {
            audioList.forEach { song ->
                if (item.audioId == song.audioId) {
                    song.isPlaying = true
                    position = audioList.indexOf(song)
                    notifyItemChanged(position)
                } else {
                    song.isPlaying = false
                    position = audioList.indexOf(song)
                    notifyItemChanged(position)
                }
            }
        }
    }

    fun getPlayingItemPosition(item: Song): Int {
        var position = 0
        if (audioList.isNotEmpty()) {
            audioList.forEach { song ->
                if (item.audioId == song.audioId) {
                    position = audioList.indexOf(song)
                }
            }
        }
        return position
    }

    fun getPlayingItem(item: Int): Song {
        return audioList[item]
    }

    fun clearData() {
        audioList.clear()
        notifyDataSetChanged()
    }

    fun resetData(newData: List<Song>) {
        audioList.clear()
        notifyDataSetChanged()
        audioList.addAll(newData)
        notifyDataSetChanged()
    }

}