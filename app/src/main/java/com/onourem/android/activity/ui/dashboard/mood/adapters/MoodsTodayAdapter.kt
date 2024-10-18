package com.onourem.android.activity.ui.dashboard.mood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemMoodTodayBinding
import com.onourem.android.activity.models.UserWatchList
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import java.text.SimpleDateFormat
import java.util.*

private class MoodsTodayAdapter
//    RequestOptions options = RequestOptions
//            .bitmapTransform(new GlideCircleBorderTransform(2, R.color.gray_color))
//            .placeholder(R.drawable.default_user_profile_image)
//            .error(R.drawable.default_user_profile_image)
//            .diskCacheStrategy(DiskCacheStrategy.DATA);
    (private val context: Context, private val userWatchLists: MutableList<UserWatchList>) :
    RecyclerView.Adapter<MoodsTodayAdapter.SingleViewHolder>() {
    private val options = RequestOptions()
        .centerInside()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private val moodOptions = RequestOptions()
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var onItemClickListener: OnWatchListItemClickListener<Pair<Int, UserWatchList>>? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_mood_today, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(userWatchLists[position])
    }

    override fun getItemCount(): Int {
        return userWatchLists.size
    }

    fun setOnItemClickListener(onItemClickListener: OnWatchListItemClickListener<Pair<Int, UserWatchList>>?) {
        this.onItemClickListener = onItemClickListener
    }

    fun modifyItem(position: Int, model: UserWatchList) {
        userWatchLists[position] = model
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        userWatchLists.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemMoodTodayBinding
        fun bind(userWatchList: UserWatchList?) {
            itemBinding.tvDate.text = "18 Mar"
            itemBinding.tvNameMood.text = "Sara is Happy"
            Glide.with(context)
                .load("https://dtv39ga8f8jxc.cloudfront.net/images/smallprofile/df494d1e-a573-4c83-a9fe-b763061ee3c21587636604702.jpeg")
                .apply(options)
                .into(itemBinding.ivProfile)
            Glide.with(context)
                .load("https://dtv39ga8f8jxc.cloudfront.net/images/appimages/surprised3.png")
                .apply(moodOptions)
                .into(itemBinding.ivMood)
        }

        init {
            itemBinding = ItemMoodTodayBinding.bind(itemView)
        }
    }

    companion object {
        private fun getDateMonth(strDate: String): String {
            var str = ""
            try {
                @SuppressLint("SimpleDateFormat") val fmInput = SimpleDateFormat("yyyy-MM-dd")
                val date = fmInput.parse(strDate)
                val cal = Calendar.getInstance()
                cal.time = date
                val d = Date(cal.timeInMillis)
                val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(d)
                val day = cal[Calendar.DAY_OF_MONTH].toString()
                str = "$month $day"
                return str
            } catch (e: Exception) {
            }
            return ""
        }
    }
}