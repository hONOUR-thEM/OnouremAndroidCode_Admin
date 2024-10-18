package com.onourem.android.activity.ui.dashboard.mood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemCardInviteInfoBinding
import com.onourem.android.activity.models.UserMoodReasonImage
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MoodInfoImagesAdapter(private val items: List<UserMoodReasonImage>) :
    RecyclerView.Adapter<MoodInfoImagesAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_invite_info, parent, false)
        return SettingsViewHolder(v)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = items[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemCardInviteInfoBinding = ItemCardInviteInfoBinding.bind(itemView)
        fun bind(settingsItem: UserMoodReasonImage) {
            Glide.with(rowBinding.imageView)
                .load(settingsItem.imageUrl) //                    .apply(options)
                .into(rowBinding.imageView)
        }

        // DisplayMetrics displaymetrics = new DisplayMetrics();
        //        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //        //if you need three fix imageview in width
        //        int devicewidth = displaymetrics.widthPixels / 3;
        //
        //        //if you need 4-5-6 anything fix imageview in height
        //        int deviceheight = displaymetrics.heightPixels / 4;
        //
        //        holder.image_view.getLayoutParams().width = devicewidth;
        //
        //        //if you need same height as width you can set devicewidth in holder.image_view.getLayoutParams().height
        //        holder.image_view.getLayoutParams().height = deviceheight;
        //        RequestOptions options = new RequestOptions()
        //                .fitCenter()
        //                .transform(new CenterInside(), new RoundedCorners(1));
        init {
            itemView.setOnClickListener(ViewClickListener { v: View? -> })
        }
    }
}