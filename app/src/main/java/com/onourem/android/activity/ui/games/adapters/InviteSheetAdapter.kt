package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemQuestionInviteSheetInfoBinding
import com.onourem.android.activity.models.InviteSheetInfo
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class InviteSheetAdapter(
    private val cardInfoList: ArrayList<InviteSheetInfo>,
    private val context: FragmentActivity,
    private val onItemClickListener: OnItemClickListener<Pair<Int, InviteSheetInfo>>?,
) : RecyclerView.Adapter<InviteSheetAdapter.InviteSheetViewHolder>() {
    val CLICK_SHARE = 5
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteSheetViewHolder {
        return InviteSheetViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question_invite_sheet_info, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InviteSheetViewHolder, position: Int) {
        val options = cardInfoList[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return cardInfoList.size
    }

    inner class InviteSheetViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemQuestionInviteSheetInfoBinding? = DataBindingUtil.bind(itemView)
        fun bind(activity: InviteSheetInfo) {

            if (activity.videoUrl!!.isNotEmpty()) {
                rowBinding!!.ivPlayVideo.visibility = View.VISIBLE
                Glide.with(rowBinding!!.root.context)
                    .load(R.drawable.english_thumbnail)
                    .into(rowBinding!!.ivQuestionImage)
            } else {
                rowBinding!!.ivPlayVideo.visibility = View.GONE
                Glide.with(rowBinding!!.root.context)
                    .load(activity.largeImageUrl)
                    .into(rowBinding!!.ivQuestionImage)
            }

        }

        init {
            rowBinding?.ivQuestionImage?.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    Pair(CLICK_SHARE, cardInfoList[bindingAdapterPosition])
                )
            })
        }
    }


}