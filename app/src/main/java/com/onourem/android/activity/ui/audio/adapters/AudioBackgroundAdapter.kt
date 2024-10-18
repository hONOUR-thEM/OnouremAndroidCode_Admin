package com.onourem.android.activity.ui.audio.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioPickerBinding
import com.onourem.android.activity.ui.audio.models.BackgroundAudio
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import java.util.concurrent.TimeUnit


class AudioBackgroundAdapter(
    private val audioInfoList: ArrayList<BackgroundAudio>,
    private val onItemClickListener: OnItemClickListener<BackgroundAudio>?,
) : RecyclerView.Adapter<AudioBackgroundAdapter.AudioGamesViewHolder>() {
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioGamesViewHolder {
        return AudioGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_picker, parent, false)
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

        private val rowBinding: ItemAudioPickerBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(category: BackgroundAudio) {

            if (category.id == -1) {
                rowBinding!!.txtAudioTitle.text = category.fileName
                category.audioDuration = "0"
                rowBinding!!.txtAudioDuration.visibility = View.GONE
                rowBinding!!.ibPlay.visibility = View.INVISIBLE
                //rowBinding!!.txtAudioDuration.text = formatTimeUnit(category.audioDuration.toLong())
            } else {
                rowBinding!!.txtAudioTitle.text =
                    category.fileName.substring(
                        category.fileName.lastIndexOf('/') + 1,
                        category.fileName.lastIndexOf('.')
                    )
                category.audioDuration = "0"
                rowBinding!!.txtAudioDuration.visibility = View.GONE
                rowBinding!!.ibPlay.visibility = View.VISIBLE
                //rowBinding!!.txtAudioDuration.text = formatTimeUnit(category.audioDuration.toLong())
            }



            itemView.setOnClickListener(ViewClickListener {
                checkedPosition = bindingAdapterPosition
                notifyDataSetChanged()
                onItemClickListener?.onItemClick(
                    getSelected()
                )
            })


        }

//        init {
//            rowBinding?.parent?.setOnClickListener(ViewClickListener { 
//                onItemClickListener?.onItemClick(
//                    getSelected()
//                )
//            })
//        }
    }

    fun getSelected(): BackgroundAudio? {
        return if (checkedPosition != -1) {
            audioInfoList[checkedPosition]
        } else null
    }

    private fun formatTimeUnit(timeInMilliseconds: Long): String {
        var value: String
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
                    "%2d sec",
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
}