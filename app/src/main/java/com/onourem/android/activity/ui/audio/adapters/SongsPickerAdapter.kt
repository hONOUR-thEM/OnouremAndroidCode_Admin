package com.onourem.android.activity.ui.audio.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioPickerBinding
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import java.util.concurrent.TimeUnit

class SongsPickerAdapter(
    private val onItemClickListener: OnItemClickListener<Pair<Int, Song>>?,
) : RecyclerView.Adapter<SongsPickerAdapter.SongsViewHolder>(), Filterable {
    private var songsList = mutableListOf<Song>()
    private var songsListFiltered = mutableListOf<Song>()

    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SongsViewHolder {
        return SongsViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_audio_picker, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: SongsViewHolder, position: Int) {
        val song = songsListFiltered[position]
        viewHolder.bind(song)
    }

    override fun getItemCount(): Int {
        return songsListFiltered.size
    }

    fun addSongs(songs: MutableList<Song>) {
        songsList.clear()
        songsList.addAll(songs)
        songsListFiltered.clear()
        songsListFiltered.addAll(songs)
        notifyDataSetChanged()
    }

    inner class SongsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemAudioPickerBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(song: Song) {

            rowBinding!!.txtAudioTitle.text = song.title
            rowBinding!!.txtAudioDuration.text =
                formatTimeUnit(song.duration.toLong())
//                song.audioUrl.substring(song.audioUrl.lastIndexOf('/') + 1,
//                    song.audioUrl.lastIndexOf('.'))

        }

        init {
            rowBinding!!.parent.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    Pair(Constants.CLICK_WHOLE_ITEM, songsListFiltered[bindingAdapterPosition])
                )
            })

            rowBinding!!.ibPlay.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    Pair(Constants.CLICK_PROFILE, songsListFiltered[bindingAdapterPosition])
                )
            })

        }
    }

    private fun formatTimeUnit(timeInMilliseconds: Long): String {
        var value = ""
        try {
            if (TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) > 0) {
                value = String.format(
                    Locale.getDefault(),
                    "%2d min %2d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                    )
                )
            } else {
                value = String.format(
                    Locale.getDefault(),
                    "%d sec",
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                    )
                )
            }

        } catch (e: Exception) {
            value = "00 min 00 sec"
        }

        return value
    }

    fun updateItem(item: Song) {
        var position = 0
        if (songsListFiltered.isNotEmpty()) {
            position = songsListFiltered.indexOf(item)
            notifyItemChanged(position)
        }
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    songsListFiltered = songsList
                } else {
                    val filteredList: MutableList<Song> = ArrayList()
                    for (row in songsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.title.lowercase(Locale.ROOT).contains(
                                charString.lowercase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }
                    songsListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = songsListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                songsListFiltered = filterResults.values as ArrayList<Song>
                notifyDataSetChanged()
            }
        }
    }

}
