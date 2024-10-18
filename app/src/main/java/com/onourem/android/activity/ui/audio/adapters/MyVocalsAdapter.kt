package com.onourem.android.activity.ui.audio.adapters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAudioBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import java.util.concurrent.TimeUnit

class MyVocalsAdapter(
    items: List<Song>,
    var preferenceHelper: SharedPreferenceHelper,
    private val onItemClickListener: OnItemClickListener<Pair<Int, Song>>,
) : PaginationRVAdapter<Song>(ArrayList(items)) {
    private var animateItemId: String? = null
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
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
                .inflate(R.layout.item_audio, parent, false)
        )
    }

    fun notifyDataUpdated(data: Song?) {
        notifyItemChanged(items.indexOf(data))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(songs: List<Song>) {
        items.clear()
        notifyDataSetChanged()
        items.addAll(songs)
        notifyDataSetChanged()
    }

    internal inner class AudioGamesItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val rowBinding: ItemAudioBinding = ItemAudioBinding.bind(itemView)
        private val context: Context = itemView.context

        @SuppressLint("RestrictedApi", "SetTextI18n")
        override fun onBind(position: Int) {
            val song = items[position]!!
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

            if (song.isAudioLiked.isNotEmpty() && song.isAudioLiked == "Y") {
                rowBinding.ibLike.setImageDrawable(
                    AppCompatResources.getDrawable(
                        rowBinding.root.context,
                        R.drawable.ic_heart_selected
                    )
                )
            } else {
                rowBinding.ibLike.setImageDrawable(
                    AppCompatResources.getDrawable(
                        rowBinding.root.context,
                        R.drawable.ic_heart
                    )
                )
            }

//            if (song.profilePictureUrl != null && song.profilePictureUrl.contains("userProfileDefaultPic.jpeg")) {
//                Glide.with(itemView.context)
//                    .load(R.drawable.ic_profile_placeholder)
//                    .apply(options)
//                    .into(rowBinding.rivProfile)
//            } else {
//                Glide.with(itemView.context)
//                    .load(song.profilePictureUrl)
//                    .apply(options)
//                    .into(rowBinding.rivProfile)
//            }

            AppUtilities.setUserProfile(
                rowBinding.root.context,
                rowBinding.rivProfile,
                song.profilePictureUrl
            )


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

//            if (song.audioStatus != null && song.audioStatus == "Active" && preferenceHelper.getString(
//                    Constants.KEY_SELECTED_FILTER_INT
//                ) == "2"
//            ) {
//                rowBinding.ibActive.visibility = View.VISIBLE
//            } else {
//                rowBinding.ibActive.visibility = View.GONE
//            }

            if (animateItemId != null && song.audioId == animateItemId) {
                rowBinding.tvStatus.visibility = View.GONE
                animateChange(rowBinding)
            }

            if (song.userType != null && song.userType != "1") {
                Utilities.verifiedUserType(
                    rowBinding.root.context,
                    song.userType,
                    rowBinding.ivIconVerified
                )
            } else {
                rowBinding.ivIconVerified.visibility = View.GONE
            }

            if (song.commentCount != null && song.commentCount != "0") {
                rowBinding.tvCommentsCount.visibility = View.VISIBLE
                rowBinding.tvCommentsCount.text = song.commentCount
                rowBinding.ivComment.visibility = View.GONE
            } else {
                rowBinding.tvCommentsCount.visibility = View.GONE
                rowBinding.ivComment.visibility = View.VISIBLE
            }


            if (preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT) == "5") {
                rowBinding.ivUserMoreAction.visibility = View.GONE
            } else {
                rowBinding.ivUserMoreAction.visibility = View.VISIBLE
            }

        }

        init {
            rowBinding.parent.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_WHOLE_ITEM, items[bindingAdapterPosition]!!)
                )
            })

            rowBinding.rivProfile.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_PROFILE, items[bindingAdapterPosition]!!)
                )
            })

            rowBinding.ivUserMoreAction.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_MORE, items[bindingAdapterPosition]!!)
                )
            })

            rowBinding.ibLike.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_LIKE, items[bindingAdapterPosition]!!)
                )
            })

            rowBinding.tvCommentsCount.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_COMMENT_LIST, items[bindingAdapterPosition]!!)
                )
            })

            rowBinding.ivComment.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair(Constants.CLICK_COMMENT, items[bindingAdapterPosition]!!)
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

    fun animateChange(viewHolder: ItemAudioBinding) {
        val colorFrom: Int =
            ContextCompat.getColor(viewHolder.root.context, R.color.color_highlight_game_post)
        val colorTo: Int = ContextCompat.getColor(viewHolder.root.context, R.color.color_white)
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