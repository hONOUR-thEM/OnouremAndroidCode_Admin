package com.onourem.android.activity.ui.admin.vocals

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAdminAudioBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AdminAudioAdapter(
    items: List<Song>,
    var preferenceHelper: SharedPreferenceHelper,
    private val onItemClickListener: OnItemClickListener<Pair<Int, Song>>,
) : PaginationRVAdapter<Song>(ArrayList(items)) {
    private var animateItemId: String? = null
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.ic_profile_placeholder)
        .error(R.drawable.ic_profile_placeholder)

    override fun emptyLoadingItem(): Song {
        return Song()
    }

    override fun emptyFooterItem(): Song {
        return Song()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return AudioGamesItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_audio, parent, false))
    }

    fun notifyDataUpdated(data: Song?) {
        notifyItemChanged(items.indexOf(data))
    }

    override fun removeItem(item: Song?) {
        //val position = items.indexOf(item)
        items.remove(item)
        //notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun updateItems(songs: List<Song>) {
        items.clear()
        notifyDataSetChanged()
        items.addAll(songs)
        notifyDataSetChanged()
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


    internal inner class AudioGamesItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val rowBinding: ItemAdminAudioBinding = ItemAdminAudioBinding.bind(itemView)
        private val context: Context = itemView.context

        @SuppressLint("RestrictedApi", "SetTextI18n")
        override fun onBind(position: Int) {
            //rowBinding!!.mRating.tag = song
            if (position != RecyclerView.NO_POSITION) {
                // Do your binding here
                val song = items[position]
                var numberOfLike = ""
                var audioDuration = song.audioDuration
                if (song.creatorId == preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID)) {
                    numberOfLike = song.numberOfLike
                }

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
                    "  - ${formatTimeUnit(audioDuration.toLong() * 1000)}"
                }

                rowBinding.txtTitle.text = song.title
                rowBinding.tvCategoryType.text = "${song.categoryName}${audioDuration}"
                rowBinding.tvUserName.text = song.userName


                rowBinding.txtDate.text = song.createdDate
//
                if (song.audioRatings != "") {
                    rowBinding.mRating.rating = song.audioRatings.toFloat()
                }

                if (song.isAudioLiked.isNotEmpty() && song.isAudioLiked == "Y") {
                    rowBinding.ibLike.setImageDrawable(ContextCompat.getDrawable(rowBinding.root.context,
                        R.drawable.ic_heart_selected))
                } else {
                    rowBinding.ibLike.setImageDrawable(ContextCompat.getDrawable(rowBinding.root.context,
                        R.drawable.ic_heart))
                }

                Glide.with(itemView.context)
                    .load(song.profilePictureUrl)
                    .apply(options)
                    .into(rowBinding.rivProfile)

                if (song.isPlaying) {
                    rowBinding.tvStatus.visibility = View.VISIBLE
                    val unwrappedDrawable: Drawable = rowBinding.parent.background
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        ContextCompat.getColor(rowBinding.parent.context, R.color.gray_color_light)
                    )

                    rowBinding.parent.background = wrappedDrawable
                } else {
                    rowBinding.tvStatus.visibility = View.GONE
                    val unwrappedDrawable: Drawable = rowBinding.parent.background
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        ContextCompat.getColor(rowBinding.parent.context, R.color.white)
                    )
                    rowBinding.parent.background = wrappedDrawable
                }

                if (song.isAudioPreparing) {
                    rowBinding.circularProgressView.visibility = View.VISIBLE
                } else {
                    rowBinding.circularProgressView.visibility = View.GONE
                }

                when (preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT)) {
                    "0" -> {
                        rowBinding.txtApprove.visibility = View.VISIBLE
                        rowBinding.txtReject.visibility = View.VISIBLE
                        rowBinding.txtSchedule.visibility = View.GONE
                        rowBinding.txtDate.visibility = View.GONE
                        rowBinding.ivUserMoreAction.visibility = View.VISIBLE
                        rowBinding.mRating.isClickable = false
                        rowBinding.mRating.isScrollable = false

                    }
                    "1" -> {
                        rowBinding.txtApprove.visibility = View.GONE
                        rowBinding.txtReject.visibility = View.GONE
                        rowBinding.txtDate.visibility = View.GONE
                        rowBinding.ivUserMoreAction.visibility = View.VISIBLE
                        rowBinding.txtSchedule.visibility = View.VISIBLE
                        rowBinding.txtSchedule.text = "Schedule"
                        rowBinding.txtSchedule.setBackgroundColor(ContextCompat.getColor(rowBinding.root.context,
                            R.color.colorAccent))
                        rowBinding.mRating.isClickable = false
                        rowBinding.mRating.isScrollable = false
                    }
                    "2" -> {
                        rowBinding.txtApprove.visibility = View.GONE
                        rowBinding.txtReject.visibility = View.GONE
                        rowBinding.ivUserMoreAction.visibility = View.VISIBLE
                        rowBinding.txtDate.visibility = View.VISIBLE
                        rowBinding.txtSchedule.visibility = View.VISIBLE
                        rowBinding.txtSchedule.text = "Re-Schedule"
                        rowBinding.txtSchedule.setBackgroundColor(ContextCompat.getColor(rowBinding.root.context,
                            R.color.color_dark_gray))
                        rowBinding.mRating.isClickable = false
                        rowBinding.mRating.isScrollable = false
                    }
                    "3" -> {
                        rowBinding.txtApprove.visibility = View.GONE
                        rowBinding.txtReject.visibility = View.GONE
                        rowBinding.ivUserMoreAction.visibility = View.VISIBLE
                        rowBinding.txtDate.visibility = View.VISIBLE
                        rowBinding.mRating.isClickable = false
                        rowBinding.mRating.isScrollable = false
                        rowBinding.txtSchedule.text = "Triggered"
                        rowBinding.txtSchedule.visibility = View.VISIBLE
                        rowBinding.txtSchedule.setBackgroundColor(ContextCompat.getColor(rowBinding.root.context,
                            R.color.green))
                    }
                    "4" -> {
                        rowBinding.txtApprove.visibility = View.GONE
                        rowBinding.txtReject.visibility = View.GONE
                        rowBinding.txtSchedule.visibility = View.GONE
                        rowBinding.txtDate.visibility = View.GONE
                        rowBinding.ivUserMoreAction.visibility = View.GONE
                        rowBinding.mRating.isClickable = false
                        rowBinding.mRating.isScrollable = false
                    }

                    else -> {
                        rowBinding.txtApprove.visibility = View.GONE
                        rowBinding.txtReject.visibility = View.GONE
                        rowBinding.txtSchedule.visibility = View.GONE
                        rowBinding.txtDate.visibility = View.GONE
                        rowBinding.mRating.isClickable = false
                        rowBinding.mRating.isScrollable = false
                    }
                }
                if (animateItemId != null && song.audioId == animateItemId) {
                    rowBinding.tvStatus.visibility = View.INVISIBLE
                    animateChange(rowBinding)
                }


            }
        }

        init {
            rowBinding.parent.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_WHOLE_ITEM, items[bindingAdapterPosition])
                )
            })

            rowBinding.rivProfile.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_PROFILE, items[bindingAdapterPosition])
                )
            })

            rowBinding.ivUserMoreAction.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_MORE, items[bindingAdapterPosition])
                )
            })

            rowBinding.ibLike.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_LIKE, items[bindingAdapterPosition])
                )
            })

            rowBinding.txtApprove.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_APPROVE, items[bindingAdapterPosition])
                )
            })

            rowBinding.txtReject.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_REJECT, items[bindingAdapterPosition])
                )
            })

            rowBinding.txtSchedule.setOnClickListener(ViewClickListener {

                if (rowBinding.txtSchedule.text == "Schedule") {
                    onItemClickListener.onItemClick(
                        Pair(Constants.CLICK_SCHEDULE, items[bindingAdapterPosition])
                    )
                } else if (rowBinding.txtSchedule.text == "Re-Schedule") {
                    onItemClickListener.onItemClick(
                        Pair(Constants.CLICK_RE_SCHEDULE, items[bindingAdapterPosition])
                    )
                }

            })


        }


    }

    private fun formatTimeUnit(timeInMilliseconds: Long): String {
        return String.format(
            Locale.getDefault(),
            "%2d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
            TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
            )
        )
    }

    @SuppressLint("NewApi", "WeekBasedYear")
    private fun replaceDateAndTimeFormat(date: Date): String? {
        var timeFormatted = ""
        val format: String = "DD-MM-YYYY"
        val formater = SimpleDateFormat(format, Locale.getDefault())
        timeFormatted = formater.format(date)
        return timeFormatted
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

    fun animateChange(viewHolder: ItemAdminAudioBinding) {
        val colorFrom: Int =
            viewHolder.parent.resources.getColor(R.color.color_highlight_game_post)
        val colorTo: Int = viewHolder.parent.resources.getColor(R.color.color_white)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.startDelay = 300 // milliseconds
        colorAnimation.addUpdateListener { animator: ValueAnimator ->
            viewHolder.parent.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.repeatCount = 1
        colorAnimation.start()
    }

}