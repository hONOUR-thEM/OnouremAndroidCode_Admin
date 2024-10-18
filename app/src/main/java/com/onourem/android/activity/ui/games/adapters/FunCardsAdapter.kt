package com.onourem.android.activity.ui.games.adapters

import android.text.TextUtils
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
import com.onourem.android.activity.databinding.ItemQuestionCardImageBinding
import com.onourem.android.activity.models.CardInfo
import com.onourem.android.activity.ui.games.adapters.FunCardsAdapter.FunCardViewHolder
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.zoomy.Zoomy

class FunCardsAdapter(
    private val cardInfoList: ArrayList<CardInfo>,
    private val context: FragmentActivity,
    private val onItemClickListener: OnItemClickListener<Pair<Int, CardInfo>>?,
) : RecyclerView.Adapter<FunCardViewHolder>() {
    val CLICK_SHARE = 5
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunCardViewHolder {
        return FunCardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question_card_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FunCardViewHolder, position: Int) {
        val options = cardInfoList[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return cardInfoList.size
    }

    inner class FunCardViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemQuestionCardImageBinding? = DataBindingUtil.bind(itemView)
        fun bind(activity: CardInfo) {

            if (!TextUtils.isEmpty(activity.largeImageUrl)) {
                rowBinding!!.ivQuestionImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(activity.largeImageUrl)
                    .apply(options)
                    .into(rowBinding!!.ivQuestionImage)
                rowBinding!!.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(context)
                    .target(rowBinding!!.ivQuestionImage)
                    .enableImmersiveMode(false)
                    .tapListener {
//                        val item = cardInfoList[bindingAdapterPosition]
//                    onItemClickListener!!.onItemClick(
//                        Pair(
//                            CLICK_SHARE,
//                            loginDayActivityInfoLists[bindingAdapterPosition]
//                        )
//                    )
                    }
                builder.register()
            }
        }

        init {
            rowBinding?.cvShare?.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(
                    Pair(CLICK_SHARE, cardInfoList[bindingAdapterPosition])
                )
            })
        }
    }


}