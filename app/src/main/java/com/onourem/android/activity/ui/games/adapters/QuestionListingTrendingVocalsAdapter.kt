package com.onourem.android.activity.ui.games.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioQuestionListingBinding
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import java.util.concurrent.TimeUnit


class QuestionListingTrendingVocalsAdapter(
    items: List<Song>,
    private val onItemClickListener: OnItemClickListener<Pair<Int, Song>>,
) : PaginationRVAdapter<Song>(ArrayList(items)) {
    private var animateItemId: String? = null
    var options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    override fun emptyLoadingItem(): Song {
        return Song()
    }

    override fun emptyFooterItem(): Song {
        return Song()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return AudioGamesItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_question_listing, parent, false)
        )
    }

    fun notifyDataUpdated(data: Song?) {
        notifyItemChanged(items.indexOf(data))
    }

    override fun removeItem(item: Song?) {
        val position = items.indexOf(item)
        items.remove(item)

        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(songs: List<Song>) {
        items.clear()
        notifyDataSetChanged()
        items.addAll(songs)
        notifyDataSetChanged()
    }

    internal inner class AudioGamesItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val rowBinding: ItemAudioQuestionListingBinding =
            ItemAudioQuestionListingBinding.bind(itemView)
        private val context: Context = itemView.context

        @SuppressLint("RestrictedApi", "SetTextI18n")
        override fun onBind(position: Int) {
            val song = items[position]!!
            var numberOfLike = ""
            var audioDuration = song.audioDuration

//            numberOfLike = if (TextUtils.isEmpty(numberOfLike) || numberOfLike == "0") {
//                ""
//            } else {
////                val spannableHeartString = SpannableString("\uD83D\uDDA4")
////                val foregroundHeartColor = ForegroundColorSpan(Color.BLACK)
////                spannableHeartString.setSpan(foregroundHeartColor,
////                    0,
////                    spannableHeartString.length,
////                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                "  -  🖤 $numberOfLike"
//            }

            audioDuration = if (TextUtils.isEmpty(audioDuration)) {
                ""
            } else {
                formatTimeUnit(audioDuration.toLong() * 1000)
            }

            rowBinding!!.txtTitle.text = song.title
            rowBinding!!.tvDuration.text = audioDuration
            rowBinding!!.tvUserName.text = song.userName

            rowBinding!!.ivImage.setImageDrawable(song.drawable)

        }

        init {
            rowBinding!!.parent.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_WHOLE_ITEM, items[bindingAdapterPosition]!!)
                )
            })
        }
    }

    fun formatTimeUnit(timeInMilliseconds: Long): String {
        return String.format(
            Locale.getDefault(),
            "%2d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
            TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
            )
        )
    }


    fun updatePlayingItem(item: Song) {
        var position = 0
        if (items.isNotEmpty()) {
            items.forEach { song ->
                if (item.audioId == song.audioId) {
                    song.isPlaying = true
                    position = items.indexOf(song)
                    notifyItemChanged(position)
                } else {
                    song.isPlaying = false
                    position = items.indexOf(song)
                    notifyItemChanged(position)
                }
            }
        }
    }

    fun updateAudioTitleOfItem(item: Song) {
        var position = 0
        if (items.isNotEmpty()) {
            items.forEach { song ->
                if (item.audioId == song.audioId) {
                    position = items.indexOf(song)
                    notifyItemChanged(position)
                }
            }
        }
    }

    fun updateAnimatePlayingItem(item: Song?) {
        animateItemId = item?.audioId
    }

    fun getPlayingItemPosition(item: Song): Int {
        var position = 0
        if (items.isNotEmpty()) {
            items.forEach { song ->
                if (item.audioId == song.audioId) {
                    position = items.indexOf(song)
                }
            }
        }
        return position
    }

}