package com.onourem.android.activity.ui.dashboard.mood.adapters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemCardMoodNewBinding
import com.onourem.android.activity.models.UserMoodHistory
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MoodQuadrantAdapter(
    private val settingsItems: ArrayList<UserMoodHistory>,
    private val onItemClickListener: OnItemClickListener<UserMoodHistory>?
) : RecyclerView.Adapter<MoodQuadrantAdapter.SettingsViewHolder>() {
    private var moodToHighlight: String? = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_mood_new, parent, false)
        val lp = v.layoutParams as GridLayoutManager.LayoutParams
        lp.height = parent.measuredHeight / 5
        v.layoutParams = lp
        return SettingsViewHolder(v)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = settingsItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return settingsItems.size
    }

    fun moodToHighlight(i: Int, moodToHighlight: String?) {
        this.moodToHighlight = moodToHighlight
        notifyItemChanged(i)
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemCardMoodNewBinding = ItemCardMoodNewBinding.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(settingsItem: UserMoodHistory) {

//            Glide.with(rowBinding!!.imageView)
//                    .load(settingsItem.getMoodImageUrl())
//                    .apply(options)
//                    .into(rowBinding!!.imageView);
            rowBinding!!.textView.text =
                """
                   ${settingsItem.positivity},${settingsItem.energy}
                   ${settingsItem.expressionName}
                   """.trimIndent()
            if (moodToHighlight != null && !moodToHighlight.equals(
                    "",
                    ignoreCase = true
                ) && settingsItem.expressionName.equals(moodToHighlight, ignoreCase = true)
            ) {
                rowBinding!!.root.postDelayed({
                    animateChange(rowBinding)
                    moodToHighlight = null
                }, 1000)
            }
        }

        fun animateChange(viewHolder: ItemCardMoodNewBinding) {
            val colorFrom =
                ContextCompat.getColor(viewHolder.root.context, R.color.color_highlight_game_post)
            val colorTo = ContextCompat.getColor(viewHolder.root.context, R.color.color_white)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 500 // milliseconds
            colorAnimation.startDelay = 300 // milliseconds
            colorAnimation.addUpdateListener { animator: ValueAnimator ->
                viewHolder.parent.setCardBackgroundColor(
                    animator.animatedValue as Int
                )
            }
            colorAnimation.repeatCount = 1
            colorAnimation.start()
        }

        init {
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = settingsItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}